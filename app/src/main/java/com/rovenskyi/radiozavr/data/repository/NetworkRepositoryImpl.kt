package com.rovenskyi.radiozavr.data.repository

import com.rovenskyi.radiozavr.data.source.NetworkDataSource
import com.rovenskyi.radiozavr.domain.model.NetworkStatus
import com.rovenskyi.radiozavr.domain.repository.NetworkRepository
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
