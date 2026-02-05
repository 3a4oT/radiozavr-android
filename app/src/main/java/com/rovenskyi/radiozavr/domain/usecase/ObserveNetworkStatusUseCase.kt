package com.rovenskyi.radiozavr.domain.usecase

import com.rovenskyi.radiozavr.domain.model.NetworkStatus
import com.rovenskyi.radiozavr.domain.repository.NetworkRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveNetworkStatusUseCase @Inject constructor(private val networkRepository: NetworkRepository) {
    operator fun invoke(): Flow<NetworkStatus> = networkRepository.getNetworkStatus()
}
