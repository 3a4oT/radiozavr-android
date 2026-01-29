package com.rovenskyi.radio_lux_fm_lviv_streamer.domain.repository

import com.rovenskyi.radio_lux_fm_lviv_streamer.domain.model.NetworkStatus
import kotlinx.coroutines.flow.Flow

interface NetworkRepository {
    fun getNetworkStatus(): Flow<NetworkStatus>
}
