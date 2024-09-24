package com.yuruneji.camera_training2.domain.usecase

import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import javax.inject.Inject

/**
 * @author toru
 * @version 1.0
 */
class NetworkSensor @Inject constructor(
    private val context: Context
) {

    // private val _networkState = MutableStateFlow(isNetworkAvailable())
    // val networkState: StateFlow<Boolean> = _networkState

    fun checkNetworkAvailable(): Boolean {
        return isNetworkAvailable()
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            context.getSystemService(CONNECTIVITY_SERVICE) as? ConnectivityManager
        connectivityManager?.let {
            val nw = it.activeNetwork ?: return false
            val actNw = it.getNetworkCapabilities(nw) ?: return false
            return when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
                else -> false
            }
        } ?: run {
            return false
        }
    }
}
