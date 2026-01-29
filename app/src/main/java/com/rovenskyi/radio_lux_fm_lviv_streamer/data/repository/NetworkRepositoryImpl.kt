package com.rovenskyi.radio_lux_fm_lviv_streamer.data.repository

import com.rovenskyi.radio_lux_fm_lviv_streamer.data.source.NetworkDataSource
import com.rovenskyi.radio_lux_fm_lviv_streamer.domain.model.NetworkStatus
import com.rovenskyi.radio_lux_fm_lviv_streamer.domain.repository.NetworkRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class NetworkRepositoryImpl @Inject constructor(
    private val networkDataSource: NetworkDataSource,
) : NetworkRepository {
    override fun getNetworkStatus(): Flow<NetworkStatus> {
        return networkDataSource.getNetworkStatus().map { isAvailable ->
            if (isAvailable) NetworkStatus.AVAILABLE else NetworkStatus.UNAVAILABLE
        }
    }
}
