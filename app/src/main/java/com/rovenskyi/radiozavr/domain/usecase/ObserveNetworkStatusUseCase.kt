package com.rovenskyi.radiozavr.domain.usecase

import com.rovenskyi.radiozavr.domain.network.NetworkRepository
import com.rovenskyi.radiozavr.domain.network.NetworkStatus
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveNetworkStatusUseCase @Inject constructor(private val networkRepository: NetworkRepository) {
    operator fun invoke(): Flow<NetworkStatus> = networkRepository.getNetworkStatus()
}
