package com.rovenskyi.radiozavr.data.source

import com.rovenskyi.radiozavr.service.CheckNetworkService
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkDataSource @Inject constructor(
    private val checkNetworkService: CheckNetworkService,
) {
    fun getNetworkStatus(): StateFlow<Boolean> = checkNetworkService.networkStatus
}
