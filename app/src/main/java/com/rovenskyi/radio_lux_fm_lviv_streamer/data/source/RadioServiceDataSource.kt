package com.rovenskyi.radio_lux_fm_lviv_streamer.data.source

import com.rovenskyi.radio_lux_fm_lviv_streamer.service.CheckNetworkService
import com.rovenskyi.radio_lux_fm_lviv_streamer.service.PlayerEventReceiver
import kotlinx.coroutines.flow.StateFlow
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Data source for radio playback control.
 *
 * Uses MediaController to communicate with RadioService, which enables:
 * - Automatic MediaStyle notifications
 * - System media controls (lock screen, Quick Settings, Bluetooth)
 */
@Singleton
class RadioServiceDataSource @Inject constructor(
    private val playerEventReceiver: PlayerEventReceiver,
    private val checkNetworkService: CheckNetworkService,
    private val mediaControllerManager: MediaControllerManager,
) {
    fun getPlayerState(): StateFlow<Boolean> = playerEventReceiver.playerState
    fun getPlayerIsLoading(): StateFlow<Boolean> = playerEventReceiver.playerIsLoading
    fun getPlayerError(): StateFlow<String?> = playerEventReceiver.playerError
    fun getAudioSessionId(): StateFlow<Int?> = playerEventReceiver.audioSessionId

    suspend fun play() {
        try {
            // First, check for network connectivity. If it fails, an exception will be thrown.
            checkNetworkService.checkNetworkConnection()

            // Only if the network check is successful, clear any previous errors.
            playerEventReceiver.clearPlayerErrorMessage()

            // Use MediaController to start playback (triggers automatic notification)
            mediaControllerManager.play()
        } catch (_: IOException) {
            // If checkNetworkConnection throws an IOException, post a specific error.
            // The error state is never cleared, so the UI remains on the error screen.
            playerEventReceiver.postPlayerError("Network connection error. Please check your internet and try again.")
        }
    }

    suspend fun stop() {
        mediaControllerManager.stop()
    }

    fun clearError() {
        playerEventReceiver.clearPlayerErrorMessage()
    }
}
