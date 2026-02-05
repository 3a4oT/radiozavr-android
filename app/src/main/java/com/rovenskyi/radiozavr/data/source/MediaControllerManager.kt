package com.rovenskyi.radiozavr.data.source

import android.content.ComponentName
import android.content.Context
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import com.rovenskyi.radiozavr.service.RadioService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.guava.await
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages MediaController connection to RadioService.
 *
 * Using MediaController (instead of startService with custom actions) enables:
 * - Automatic MediaStyle notifications
 * - System media controls integration (lock screen, Quick Settings)
 * - Bluetooth/headphone controls
 * - Proper foreground service lifecycle
 */
@Singleton
class MediaControllerManager @Inject constructor(
    @param:ApplicationContext private val context: Context,
) {
    private var controllerFuture: ListenableFuture<MediaController>? = null
    private var mediaController: MediaController? = null

    /**
     * Connects to RadioService's MediaSession.
     * Should be called when app starts (e.g., in MainActivity.onCreate).
     */
    fun connect() {
        if (controllerFuture != null) return

        val sessionToken = SessionToken(
            context,
            ComponentName(context, RadioService::class.java),
        )
        controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
        controllerFuture?.addListener(
            { mediaController = controllerFuture?.get() },
            MoreExecutors.directExecutor(),
        )
    }

    /**
     * Awaits controller connection with timeout.
     * Returns connected controller or null if timeout/failure.
     */
    private suspend fun awaitController(): MediaController? {
        // If already connected, return immediately
        mediaController?.let { if (it.isConnected) return it }

        // Ensure connect() was called
        val future = controllerFuture ?: return null

        return withTimeoutOrNull(CONNECTION_TIMEOUT_MS) {
            try {
                future.await().also { mediaController = it }
            } catch (_: Exception) {
                null
            }
        }
    }

    /**
     * Starts playback. Awaits controller connection if needed.
     * Prepares player if in IDLE state.
     */
    suspend fun play() {
        val controller = awaitController() ?: return
        if (controller.playbackState == Player.STATE_IDLE) {
            controller.prepare()
        }
        controller.play()
    }

    /**
     * Pauses playback. Awaits controller connection if needed.
     */
    suspend fun pause() {
        awaitController()?.pause()
    }

    /**
     * Stops playback. Awaits controller connection if needed.
     */
    suspend fun stop() {
        awaitController()?.stop()
    }

    /**
     * Releases the MediaController connection.
     * Should be called when app is destroyed.
     */
    fun release() {
        controllerFuture?.let { MediaController.releaseFuture(it) }
        mediaController = null
        controllerFuture = null
    }

    /**
     * Returns true if controller is connected and ready.
     */
    val isConnected: Boolean
        get() = mediaController?.isConnected == true

    companion object {
        private const val CONNECTION_TIMEOUT_MS = 5000L
    }
}
