package com.rovenskyi.radio_lux_fm_lviv_streamer.service

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.io.IOException
import javax.inject.Inject

class CheckNetworkService @Inject constructor(private val context: Context) {
    private val _networkStatusLiveData = MutableLiveData<Boolean>()
    val networkStatusLiveData: LiveData<Boolean> get() = _networkStatusLiveData

    init {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        // Register callback for network state changes
        val builder = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        connectivityManager.registerNetworkCallback(
            builder.build(),
            object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    _networkStatusLiveData.postValue(true)
                }

                override fun onLost(network: Network) {
                    _networkStatusLiveData.postValue(false)
                }

                override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
                    val hasInternet = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                    _networkStatusLiveData.postValue(hasInternet)
                }
            }
        )

        // Initialize the current status
        _networkStatusLiveData.postValue(isNetworkAvailable(connectivityManager))
    }

    private fun isNetworkAvailable(connectivityManager: ConnectivityManager): Boolean {
        val activeNetwork: Network? = connectivityManager.activeNetwork
        val capabilities: NetworkCapabilities? =
            connectivityManager.getNetworkCapabilities(activeNetwork)
        return capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    fun checkNetworkConnection() {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (!isNetworkAvailable(connectivityManager)) {
            throw IOException("No network available")
        }
    }
}
