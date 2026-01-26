package com.rovenskyi.radio_lux_fm_lviv_streamer.service

import android.Manifest
import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.annotation.OptIn
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
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
        instance = this

        // Buffer configuration optimized for live HLS audio streaming
        val loadControl = DefaultLoadControl.Builder()
            .setBufferDurationsMs(
                15_000,  // minBufferMs - 15 seconds (sufficient for live audio)
                30_000,  // maxBufferMs - 30 seconds (limited for live to reduce latency)
                2_500,   // bufferForPlaybackMs - fast playback start
                5_000    // bufferForPlaybackAfterRebufferMs - stability after rebuffer
            )
            .build()

        exoPlayer = ExoPlayer.Builder(this)
            .setLoadControl(loadControl)
            .build()
            .apply {
                val mediaMetadata = androidx.media3.common.MediaMetadata.Builder()
                    .setTitle(getString(R.string.notitification_content_title))
                    .setArtist(getString(R.string.notitification_content_description))
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
        startForeground(NOTIFICATION_ID, buildNotification(false))
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
                updateNotification(isPlaying = false)
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
        instance = null
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
            .setName(getString(R.string.notitification_content_title))
            .setDescription(getString(R.string.notitification_content_description))
            .build()
        NotificationManagerCompat.from(this).createNotificationChannel(channel)
    }

    private fun buildNotification(isPlaying: Boolean): Notification {
        val playPauseAction = if (isPlaying) {
            NotificationCompat.Action(
                android.R.drawable.ic_media_pause,
                getString(R.string.notitification_action_pause),
                PendingIntent.getService(this, 0, createPauseIntent(this), PendingIntent.FLAG_IMMUTABLE)
            )
        } else {
            NotificationCompat.Action(
                android.R.drawable.ic_media_play,
                getString(R.string.notitification_action_play),
                PendingIntent.getService(this, 0, createPlayIntent(this), PendingIntent.FLAG_IMMUTABLE)
            )
        }

        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.notitification_content_title))
            .setContentText(getString(R.string.notitification_content_description))
            .setSmallIcon(R.drawable.logo_lux)
            .setContentIntent(pendingIntent)
            .addAction(playPauseAction)
            .build()
    }

    private fun updateNotification(isPlaying: Boolean) {
        val notification = buildNotification(isPlaying)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        NotificationManagerCompat.from(this).notify(NOTIFICATION_ID, notification)
    }

    companion object {
        private const val CHANNEL_ID = "com.rovenskyi.radio_lux_fm_lviv_streamer.service.radio_playback_channel"
        private const val NOTIFICATION_ID = 1
        const val ACTION_PLAY = "com.rovenskyi.radio_lux_fm_lviv_streamer.service.action.PLAY"
        const val ACTION_PAUSE = "com.rovenskyi.radio_lux_fm_lviv_streamer.service.action.PAUSE"
        const val ACTION_STOP = "com.rovenskyi.radio_lux_fm_lviv_streamer.service.action.STOP"

        @Volatile
        private var instance: RadioService? = null

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

        fun isServicePlaying(): Boolean {
            val isPlaying = instance?.exoPlayer?.isPlaying ?: false
            return isPlaying
        }
    }
}