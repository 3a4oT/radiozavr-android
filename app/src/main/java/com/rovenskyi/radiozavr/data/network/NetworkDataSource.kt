package com.rovenskyi.radiozavr.data.network

import com.rovenskyi.radiozavr.data.network.CheckNetworkService
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkDataSource @Inject constructor(
    private val checkNetworkService: CheckNetworkService,
) {
    fun getNetworkStatus(): StateFlow<Boolean> = checkNetworkService.networkStatus
}
