package com.rovenskyi.radio_lux_fm_lviv_streamer.service

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.annotation.OptIn
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.upstream.DefaultAllocator
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.rovenskyi.radio_lux_fm_lviv_streamer.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class RadioService : MediaSessionService(), Player.Listener {

    @Inject
    lateinit var playerEventReceiver: PlayerEventReceiver

    @Inject
    lateinit var projectConfig: ProjectConfig

    private lateinit var exoPlayer: ExoPlayer
    private lateinit var mediaSession: MediaSession

    @OptIn(UnstableApi::class)
    override fun onCreate() {
        super.onCreate()

        val loadControl = DefaultLoadControl.Builder()
            .setAllocator(DefaultAllocator(true, 16))
            .setBufferDurationsMs(
                32 * 1024, // minBufferMs
                64 * 1024, // maxBufferMs
                1024, // bufferForPlaybackMs
                1024 // bufferForPlaybackAfterRebufferMs
            )
            .setTargetBufferBytes(-1)
            .setPrioritizeTimeOverSizeThresholds(true)
            .build()

        exoPlayer = ExoPlayer.Builder(this)
            .setLoadControl(loadControl)
            .build()
            .apply {
            val mediaItem = MediaItem.fromUri(projectConfig.streamUrl)
            setMediaItem(mediaItem)
            prepare()
            addListener(this@RadioService)
        }

        mediaSession = MediaSession.Builder(this, exoPlayer).build()

        createNotificationChannel()
        startForeground(NOTIFICATION_ID, buildNotification())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        when (intent?.action) {
            ACTION_PLAY -> {
                exoPlayer.play()
            }
            ACTION_PAUSE -> {
                exoPlayer.pause()
            }
            ACTION_STOP -> {
                exoPlayer.stop()
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }
        }
        return START_STICKY
    }

    override fun onDestroy() {
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

    override fun onPlaybackStateChanged(state: Int) {
        super.onPlaybackStateChanged(state)
        if (state == Player.STATE_READY) {
            playerEventReceiver.postPlayerIsLoading(false)
        } else if (state == Player.STATE_BUFFERING) {
            playerEventReceiver.postPlayerIsLoading(true)
        }
    }

    // Override listener methods to handle network errors
    override fun onPlayerError(error: PlaybackException) {
        super.onPlayerError(error)
        playerEventReceiver.postPlayerError(error.message)
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannelCompat.Builder(CHANNEL_ID, NotificationManagerCompat.IMPORTANCE_LOW)
            .setName("Radio Playback")
            .setDescription("Notification for radio playback control")
            .build()
        NotificationManagerCompat.from(this).createNotificationChannel(channel)
    }

    private fun buildNotification(): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Radio Stream")
            .setContentText("Playing Radio")
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setContentIntent(pendingIntent)
            .build()
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