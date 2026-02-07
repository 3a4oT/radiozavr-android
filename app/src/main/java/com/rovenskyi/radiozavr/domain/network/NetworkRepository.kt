package com.rovenskyi.radiozavr.domain.network

import kotlinx.coroutines.flow.Flow

interface NetworkRepository {
    fun getNetworkStatus(): Flow<NetworkStatus>
}
