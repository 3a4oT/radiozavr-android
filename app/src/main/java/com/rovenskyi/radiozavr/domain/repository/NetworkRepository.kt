package com.rovenskyi.radiozavr.domain.repository

import com.rovenskyi.radiozavr.domain.model.NetworkStatus
import kotlinx.coroutines.flow.Flow

interface NetworkRepository {
    fun getNetworkStatus(): Flow<NetworkStatus>
}
