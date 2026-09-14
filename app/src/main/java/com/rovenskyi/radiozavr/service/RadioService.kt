package com.rovenskyi.radiozavr.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import androidx.annotation.OptIn
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatDelegate
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.ForwardingPlayer
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.DefaultMediaNotificationProvider
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.rovenskyi.radiozavr.MainActivity
import com.rovenskyi.radiozavr.R
import com.rovenskyi.radiozavr.data.radio.PlayerEventReceiver
import com.rovenskyi.radiozavr.di.ProjectConfig
import com.rovenskyi.radiozavr.domain.analytics.AnalyticsTracker
import com.rovenskyi.radiozavr.domain.analytics.event.PlayerEvent
import com.rovenskyi.radiozavr.domain.analytics.model.ErrorType
import com.rovenskyi.radiozavr.domain.network.NetworkRepository
import com.rovenskyi.radiozavr.domain.network.NetworkStatus
import com.rovenskyi.radiozavr.domain.platform.PlatformRepository
import com.rovenskyi.radiozavr.domain.playback.PlaybackSettingsRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Media playback service using Media3's MediaSessionService.
 *
 * Key features:
 * - Automatic MediaStyle notification (lock screen, Quick Settings, Bluetooth)
 * - Proper audio focus handling via AudioAttributes
 * - System media controls integration (like iOS Control Center)
 * - TV screensaver prevention during playback
 */
@AndroidEntryPoint
class RadioService : MediaSessionService(), Player.Listener {

    @Inject
    lateinit var playerEventReceiver: PlayerEventReceiver

    @Inject
    lateinit var projectConfig: ProjectConfig

    @Inject
    lateinit var analyticsTracker: AnalyticsTracker

    @Inject
    lateinit var networkRepository: NetworkRepository

    @Inject
    lateinit var platformRepository: PlatformRepository

    @Inject
    lateinit var playbackSettingsRepository: PlaybackSettingsRepository

    private lateinit var exoPlayer: ExoPlayer
    private lateinit var mediaSession: MediaSession

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var wasPlayingBeforeBuffer = false
    private var autoStopJob: Job? = null
    private var wasStoppedByAutoStop = false

    @OptIn(UnstableApi::class)
    override fun onCreate() {
        super.onCreate()

        // Create notification channel (required for Android 8+)
        createNotificationChannel()

        // Set up Media3's notification provider for automatic MediaStyle notifications
        setMediaNotificationProvider(
            DefaultMediaNotificationProvider.Builder(this)
                .setChannelId(NOTIFICATION_CHANNEL_ID)
                .build(),
        )

        // Audio attributes for music streaming - enables proper audio focus
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(C.USAGE_MEDIA)
            .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
            .build()

        // Buffer configuration optimized for live HLS audio streaming
        val loadControl = DefaultLoadControl.Builder()
            .setBufferDurationsMs(
                15_000, // minBufferMs - 15 seconds (sufficient for live audio)
                30_000, // maxBufferMs - 30 seconds (limited for live to reduce latency)
                2_500, // bufferForPlaybackMs - fast playback start
                5_000, // bufferForPlaybackAfterRebufferMs - stability after rebuffer
            )
            .build()

        exoPlayer = ExoPlayer.Builder(this)
            .setLoadControl(loadControl)
            .setAudioAttributes(audioAttributes, true) // handleAudioFocus = true
            .setWakeMode(C.WAKE_MODE_NETWORK) // Keep WiFi/CPU during playback
            .build()
            .apply {
                val artworkBitmap = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher_foreground)

                val mediaMetadata = MediaMetadata.Builder()
                    .setTitle(getLocalizedString(R.string.notitification_content_title))
                    .setArtist(getLocalizedString(R.string.notitification_content_description))
                    .setArtworkData(bitmapToByteArray(artworkBitmap), MediaMetadata.PICTURE_TYPE_FRONT_COVER)
                    .setIsPlayable(true)
                    .setIsBrowsable(false)
                    .build()

                val mediaItem = MediaItem.Builder()
                    .setUri(projectConfig.streamUrl)
                    .setMediaMetadata(mediaMetadata)
                    .build()

                setMediaItem(mediaItem)
                addListener(this@RadioService)
            }

        mediaSession = MediaSession.Builder(this, LiveRadioPlayer(exoPlayer))
            .setCallback(MediaSessionCallback())
            .apply {
                // On TV: don't set sessionActivity - system uses LEANBACK_LAUNCHER from manifest
                // On Phone: set sessionActivity for notification tap to open app
                if (!platformRepository.isTv) {
                    val sessionActivityIntent = PendingIntent.getActivity(
                        this@RadioService,
                        0,
                        Intent(this@RadioService, MainActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                        },
                        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
                    )
                    setSessionActivity(sessionActivityIntent)
                }
            }
            .build()

        observeNetworkStatus()
    }

    private fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
        val stream = java.io.ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
    }

    private fun observeNetworkStatus() {
        serviceScope.launch {
            networkRepository.getNetworkStatus().collect { status ->
                if (status == NetworkStatus.UNAVAILABLE && exoPlayer.isPlaying) {
                    analyticsTracker.track(PlayerEvent.NetworkError(wasPlaying = true))
                }
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        // Playback is controlled via MediaController (not custom actions)
        // Only handle app lifecycle events for TV auto-stop feature
        when (intent?.action) {
            ACTION_APP_BACKGROUND -> handleAppBackground()
            ACTION_APP_FOREGROUND -> handleAppForeground()
        }
        return START_STICKY
    }

    /**
     * Schedules auto-stop when app goes to background (TV only).
     * Stops playback after [AUTO_STOP_DELAY_MS] if still in background.
     */
    private fun handleAppBackground() {
        if (!platformRepository.isTv) return

        autoStopJob = serviceScope.launch {
            val autoStopEnabled = playbackSettingsRepository.autoStopOnBackgroundEnabled.first()
            if (autoStopEnabled && exoPlayer.isPlaying) {
                delay(AUTO_STOP_DELAY_MS)
                wasStoppedByAutoStop = true
                exoPlayer.stop()
                // State changes are propagated via onIsPlayingChanged
            }
        }
    }

    /**
     * Cancels pending auto-stop and resumes if was stopped by auto-stop.
     */
    private fun handleAppForeground() {
        // Cancel pending auto-stop
        autoStopJob?.cancel()
        autoStopJob = null

        // Auto-resume if was stopped by auto-stop
        if (wasStoppedByAutoStop) {
            wasStoppedByAutoStop = false
            // Post loading state immediately to ensure UI updates before async prepare
            playerEventReceiver.postPlayerIsLoading(true)
            exoPlayer.prepare()
            exoPlayer.play()
            // Further state changes propagated via onPlaybackStateChanged/onIsPlayingChanged
        }
    }

    override fun onDestroy() {
        serviceScope.cancel()
        exoPlayer.removeListener(this)
        exoPlayer.release()
        mediaSession.release()
        super.onDestroy()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession = mediaSession

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        super.onIsPlayingChanged(isPlaying)
        playerEventReceiver.postPlayerState(isPlaying)
        if (!isPlaying && exoPlayer.playbackState == Player.STATE_IDLE) {
            playerEventReceiver.postAudioSessionId(null)
        }
    }

    /**
     * Note: For live streaming, ExoPlayer continuously buffers ahead, so isLoading
     * toggles frequently even during playback. We intentionally DO NOT propagate
     * isLoading here - instead we use onPlaybackStateChanged for loading UI state.
     * STATE_BUFFERING = loading, STATE_READY = not loading.
     */
    override fun onIsLoadingChanged(isLoading: Boolean) {
        super.onIsLoadingChanged(isLoading)
        // Intentionally empty - loading state is managed via onPlaybackStateChanged
    }

    @OptIn(UnstableApi::class)
    override fun onPlaybackStateChanged(state: Int) {
        super.onPlaybackStateChanged(state)
        when (state) {
            Player.STATE_READY -> {
                playerEventReceiver.postPlayerIsLoading(false)
                playerEventReceiver.postAudioSessionId(exoPlayer.audioSessionId)
                wasPlayingBeforeBuffer = true
            }
            Player.STATE_BUFFERING -> {
                playerEventReceiver.postPlayerIsLoading(true)
                analyticsTracker.track(PlayerEvent.BufferingStarted(isRebuffer = wasPlayingBeforeBuffer))
            }
            Player.STATE_IDLE, Player.STATE_ENDED -> {
                wasPlayingBeforeBuffer = false
            }
        }
    }

    override fun onPlayerError(error: PlaybackException) {
        super.onPlayerError(error)

        // The buffered position fell out of the HLS live window (~10 s for this stream), e.g.
        // after ExoPlayer auto-paused on audio focus loss. Rejoin the live edge instead of
        // surfacing an error the user can only fix by pressing play again.
        if (error.errorCode == PlaybackException.ERROR_CODE_BEHIND_LIVE_WINDOW) {
            exoPlayer.seekToDefaultPosition()
            exoPlayer.prepare()
            return
        }

        playerEventReceiver.postPlayerError(error.message)

        analyticsTracker.track(
            PlayerEvent.PlaybackError(
                errorType = ErrorType.PLAYBACK,
                errorCode = error.errorCode,
                errorMessage = error.message,
            ),
        )
        analyticsTracker.logError(
            throwable = error,
            context = mapOf(
                "stream_url" to projectConfig.streamUrl,
                "error_code" to error.errorCode.toString(),
            ),
        )
    }

    /**
     * Creates notification channel for media playback.
     */
    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            getLocalizedString(R.string.notification_channel_name),
            NotificationManager.IMPORTANCE_LOW, // Low importance = no sound
        ).apply {
            description = getLocalizedString(R.string.notification_channel_description)
        }
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }

    /**
     * Gets a string using the app's selected locale (for per-app language support).
     */
    private fun getLocalizedString(@StringRes resId: Int): String {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return getString(resId)
        }
        val appLocales = AppCompatDelegate.getApplicationLocales()
        if (appLocales.isEmpty) {
            return getString(resId)
        }
        val locale = appLocales[0] ?: return getString(resId)
        val config = resources.configuration.apply {
            setLocale(locale)
        }
        return createConfigurationContext(config).getString(resId)
    }

    /**
     * Live radio has no meaningful resume point.
     *
     * While paused, ExoPlayer keeps the last buffered seconds, but the stream's HLS live window
     * only holds a handful of segments. Resuming after a long pause therefore plays the stale
     * buffer for a few seconds and then fails with a source error, because the segments that
     * would follow are long gone from the playlist. Every play() rebuilds the stream from the
     * live edge instead.
     */
    @OptIn(UnstableApi::class)
    private class LiveRadioPlayer(private val exoPlayer: ExoPlayer) : ForwardingPlayer(exoPlayer) {

        override fun play() {
            restartFromLiveEdge()
        }

        override fun setPlayWhenReady(playWhenReady: Boolean) {
            if (playWhenReady) restartFromLiveEdge() else super.setPlayWhenReady(false)
        }

        private fun restartFromLiveEdge() {
            if (exoPlayer.playWhenReady) return
            exoPlayer.stop()
            exoPlayer.prepare()
            exoPlayer.play()
        }
    }

    /**
     * MediaSession callback for handling custom commands.
     */
    @OptIn(UnstableApi::class)
    private inner class MediaSessionCallback : MediaSession.Callback {
        override fun onConnect(
            session: MediaSession,
            controller: MediaSession.ControllerInfo,
        ): MediaSession.ConnectionResult {
            return MediaSession.ConnectionResult.AcceptedResultBuilder(session)
                .setAvailableSessionCommands(
                    MediaSession.ConnectionResult.DEFAULT_SESSION_COMMANDS.buildUpon()
                        .build(),
                )
                .build()
        }
    }

    companion object {
        private const val AUTO_STOP_DELAY_MS = 5000L
        private const val NOTIFICATION_CHANNEL_ID = "radio_playback_channel"

        // App lifecycle actions for TV auto-stop feature
        private const val ACTION_APP_BACKGROUND =
            "com.rovenskyi.radiozavr.service.action.APP_BACKGROUND"
        private const val ACTION_APP_FOREGROUND =
            "com.rovenskyi.radiozavr.service.action.APP_FOREGROUND"

        fun createAppBackgroundIntent(context: Context): Intent {
            return Intent(context, RadioService::class.java).apply {
                action = ACTION_APP_BACKGROUND
            }
        }

        fun createAppForegroundIntent(context: Context): Intent {
            return Intent(context, RadioService::class.java).apply {
                action = ACTION_APP_FOREGROUND
            }
        }
    }
}
