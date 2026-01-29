package com.rovenskyi.radio_lux_fm_lviv_streamer.domain.usecase

import com.rovenskyi.radio_lux_fm_lviv_streamer.domain.model.NetworkStatus
import com.rovenskyi.radio_lux_fm_lviv_streamer.domain.repository.NetworkRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveNetworkStatusUseCase @Inject constructor(private val networkRepository: NetworkRepository) {
    operator fun invoke(): Flow<NetworkStatus> = networkRepository.getNetworkStatus()
}
