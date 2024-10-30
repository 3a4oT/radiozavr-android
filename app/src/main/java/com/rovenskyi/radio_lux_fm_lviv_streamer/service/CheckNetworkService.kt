package com.rovenskyi.radio_lux_fm_lviv_streamer.service

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import java.io.IOException
import javax.inject.Inject

class CheckNetworkService @Inject constructor(private val context: Context) {
    fun checkNetworkConnection() {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: Network? = connectivityManager.activeNetwork
        val capabilities: NetworkCapabilities? =
            connectivityManager.getNetworkCapabilities(activeNetwork)
        if (capabilities == null || !capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)) {
            throw IOException("No network available")
        }
    }
}
