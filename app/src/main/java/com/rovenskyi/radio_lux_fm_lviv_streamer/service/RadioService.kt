package com.rovenskyi.radio_lux_fm_lviv_streamer.service

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.rovenskyi.radio_lux_fm_lviv_streamer.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class RadioService : MediaSessionService(), Player.Listener {

    @Inject
    lateinit var networkErrorReceiver: NetworkErrorReceiver

    private lateinit var exoPlayer: ExoPlayer
    private lateinit var mediaSession: MediaSession

    override fun onCreate() {
        super.onCreate()

        exoPlayer = ExoPlayer.Builder(this).build().apply {
            val luxFmStreamUri = "http://streamvideo.luxnet.ua/luxlviv/luxlviv.stream/chunklist.m3u8"
            val mediaItem = MediaItem.fromUri(luxFmStreamUri)
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

    // Override listener methods to handle network errors
    override fun onPlayerError(error: PlaybackException) {
        super.onPlayerError(error)
        networkErrorReceiver.postNetworkError(error.message)
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
        const val ACTION_NETWORK_ERROR = "com.rovenskyi.radio_lux_fm_lviv_streamer.service.action.NETWORK_ERROR"
        const val EXTRA_ERROR_MSG = "com.rovenskyi.radio_lux_fm_lviv_streamer.service.extra.ERROR_MSG"

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