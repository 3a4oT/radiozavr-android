package com.rovenskyi.radio_lux_fm_lviv_streamer.service

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CheckNetworkService @Inject constructor(@ApplicationContext private val context: Context) {

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    // Initialize the flow with the *current* network state to avoid an initial false value.
    private val _networkStatus = MutableStateFlow(isNetworkAvailable(connectivityManager))
    val networkStatus: StateFlow<Boolean> = _networkStatus.asStateFlow()

    init {
        // Register a callback to listen for future network state changes.
        val builder = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        connectivityManager.registerNetworkCallback(
            builder.build(),
            object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    _networkStatus.value = true
                }

                override fun onLost(network: Network) {
                    _networkStatus.value = false
                }

                override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
                    val hasInternet = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                    _networkStatus.value = hasInternet
                }
            }
        )
    }

    private fun isNetworkAvailable(connectivityManager: ConnectivityManager): Boolean {
        val activeNetwork: Network? = connectivityManager.activeNetwork
        val capabilities: NetworkCapabilities? =
            connectivityManager.getNetworkCapabilities(activeNetwork)
        return capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    fun checkNetworkConnection() {
        if (!_networkStatus.value) {
            throw IOException("No network available")
        }
    }
}
