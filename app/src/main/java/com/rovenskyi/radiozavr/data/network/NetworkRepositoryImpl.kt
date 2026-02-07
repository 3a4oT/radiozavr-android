package com.rovenskyi.radiozavr.data.network

import com.rovenskyi.radiozavr.domain.network.NetworkRepository
import com.rovenskyi.radiozavr.domain.network.NetworkStatus
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
