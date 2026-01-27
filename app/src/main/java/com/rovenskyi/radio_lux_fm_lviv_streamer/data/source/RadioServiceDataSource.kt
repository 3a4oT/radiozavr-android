package com.rovenskyi.radio_lux_fm_lviv_streamer.data.source

import android.content.Context
import com.rovenskyi.radio_lux_fm_lviv_streamer.service.CheckNetworkService
import com.rovenskyi.radio_lux_fm_lviv_streamer.service.PlayerEventReceiver
import com.rovenskyi.radio_lux_fm_lviv_streamer.service.RadioService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.StateFlow
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RadioServiceDataSource @Inject constructor(
    private val playerEventReceiver: PlayerEventReceiver,
    private val checkNetworkService: CheckNetworkService,
    @param:ApplicationContext private val context: Context
) {
    fun getPlayerState(): StateFlow<Boolean> = playerEventReceiver.playerState
    fun getPlayerIsLoading(): StateFlow<Boolean> = playerEventReceiver.playerIsLoading
    fun getPlayerError(): StateFlow<String?> = playerEventReceiver.playerError

    fun play() {
        try {
            // First, check for network connectivity. If it fails, an exception will be thrown.
            checkNetworkService.checkNetworkConnection()

            // Only if the network check is successful, clear any previous errors.
            playerEventReceiver.clearPlayerErrorMessage()
            
            // And then start the radio service.
            context.startService(RadioService.createPlayIntent(context))
        } catch (e: IOException) {
            // If checkNetworkConnection throws an IOException, post a specific error.
            // The error state is never cleared, so the UI remains on the error screen.
            playerEventReceiver.postPlayerError("Network connection error. Please check your internet and try again.")
        }
    }

    fun stop() {
        context.startService(RadioService.createStopIntent(context))
    }

    fun refreshState() {
        val isPlaying = RadioService.isServicePlaying()
        playerEventReceiver.postPlayerState(isPlaying)
    }

    fun clearError() {
        playerEventReceiver.clearPlayerErrorMessage()
    }
} 