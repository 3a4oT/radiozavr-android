package com.rovenskyi.radio_lux_fm_lviv_streamer.service

import android.Manifest
import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.os.Build
import androidx.annotation.OptIn
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.ServiceCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.rovenskyi.radio_lux_fm_lviv_streamer.MainActivity
import com.rovenskyi.radio_lux_fm_lviv_streamer.R
import com.rovenskyi.radio_lux_fm_lviv_streamer.domain.analytics.AnalyticsTracker
import com.rovenskyi.radio_lux_fm_lviv_streamer.domain.analytics.event.PlayerEvent
import com.rovenskyi.radio_lux_fm_lviv_streamer.domain.analytics.model.ErrorType
import com.rovenskyi.radio_lux_fm_lviv_streamer.domain.model.NetworkStatus
import com.rovenskyi.radio_lux_fm_lviv_streamer.domain.repository.NetworkRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

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

    private lateinit var exoPlayer: ExoPlayer
    private lateinit var mediaSession: MediaSession

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var wasPlayingBeforeBuffer = false

    @OptIn(UnstableApi::class)
    override fun onCreate() {
        super.onCreate()

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
            .build()
            .apply {
                val mediaMetadata = androidx.media3.common.MediaMetadata.Builder()
                    .setTitle(getLocalizedString(R.string.notitification_content_title))
                    .setArtist(getLocalizedString(R.string.notitification_content_description))
                    .build()

                val mediaItem = MediaItem.Builder()
                    .setUri(projectConfig.streamUrl)
                    .setMediaMetadata(mediaMetadata)
                    .build()

                setMediaItem(mediaItem)
                prepare()
                addListener(this@RadioService)
            }

        mediaSession = MediaSession.Builder(this, exoPlayer).build()

        createNotificationChannel()
        ServiceCompat.startForeground(
            this,
            NOTIFICATION_ID,
            buildNotification(false),
            ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK,
        )

        observeNetworkStatus()
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
        when (intent?.action) {
            ACTION_PLAY -> {
                exoPlayer.play()
                playerEventReceiver.postPlayerState(true)
                updateNotification(isPlaying = true)
            }
            ACTION_PAUSE -> {
                exoPlayer.pause()
                playerEventReceiver.postPlayerState(false)
                updateNotification(isPlaying = false)
            }
            ACTION_STOP -> {
                exoPlayer.stop()
                playerEventReceiver.postPlayerState(false)
                playerEventReceiver.postAudioSessionId(null)
                updateNotification(isPlaying = false)
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }
        }
        return START_STICKY
    }

    override fun onDestroy() {
        serviceScope.cancel()
        exoPlayer.removeListener(this)
        exoPlayer.release()
        mediaSession.release()
        stopForeground(STOP_FOREGROUND_REMOVE)
        super.onDestroy()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession = mediaSession

    override fun onIsLoadingChanged(isLoading: Boolean) {
        super.onIsLoadingChanged(isLoading)
        playerEventReceiver.postPlayerIsLoading(isLoading)
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

    // Override listener methods to handle network errors
    override fun onPlayerError(error: PlaybackException) {
        super.onPlayerError(error)
        playerEventReceiver.postPlayerError(error.message)

        // Track error in analytics and Crashlytics
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

    private fun createNotificationChannel() {
        val channel = NotificationChannelCompat.Builder(CHANNEL_ID, NotificationManagerCompat.IMPORTANCE_LOW)
            .setName(getLocalizedString(R.string.notitification_content_title))
            .setDescription(getLocalizedString(R.string.notitification_content_description))
            .build()
        NotificationManagerCompat.from(this).createNotificationChannel(channel)
    }

    private fun buildNotification(isPlaying: Boolean): Notification {
        val playPauseAction = if (isPlaying) {
            NotificationCompat.Action(
                android.R.drawable.ic_media_pause,
                getLocalizedString(R.string.notitification_action_pause),
                PendingIntent.getService(this, 0, createPauseIntent(this), PendingIntent.FLAG_IMMUTABLE),
            )
        } else {
            NotificationCompat.Action(
                android.R.drawable.ic_media_play,
                getLocalizedString(R.string.notitification_action_play),
                PendingIntent.getService(this, 0, createPlayIntent(this), PendingIntent.FLAG_IMMUTABLE),
            )
        }

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getLocalizedString(R.string.notitification_content_title))
            .setContentText(getLocalizedString(R.string.notitification_content_description))
            .setSmallIcon(R.mipmap.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .addAction(playPauseAction)
            .build()
    }

    private fun updateNotification(isPlaying: Boolean) {
        val notification = buildNotification(isPlaying)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS,
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        NotificationManagerCompat.from(this).notify(NOTIFICATION_ID, notification)
    }

    /**
     * Gets a string using the app's selected locale (for per-app language support).
     * On Android 13+, the system handles this automatically.
     * On Android 12 and below, we need to manually create a localized context.
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

    companion object {
        private const val CHANNEL_ID = "com.rovenskyi.radio_lux_fm_lviv_streamer.service.radio_playback_channel"
        private const val NOTIFICATION_ID = 1
        const val ACTION_PLAY = "com.rovenskyi.radio_lux_fm_lviv_streamer.service.action.PLAY"
        const val ACTION_PAUSE = "com.rovenskyi.radio_lux_fm_lviv_streamer.service.action.PAUSE"
        const val ACTION_STOP = "com.rovenskyi.radio_lux_fm_lviv_streamer.service.action.STOP"

        fun createPlayIntent(context: Context): Intent {
            return Intent(context, RadioService::class.java).apply {
                action = ACTION_PLAY
            }
        }

        fun createPauseIntent(context: Context): Intent {
            return Intent(context, RadioService::class.java).apply {
                action = ACTION_PAUSE
            }
        }

        fun createStopIntent(context: Context): Intent {
            return Intent(context, RadioService::class.java).apply {
                action = ACTION_STOP
            }
        }
    }
}
