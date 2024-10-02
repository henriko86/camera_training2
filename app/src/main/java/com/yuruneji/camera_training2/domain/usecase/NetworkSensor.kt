package com.yuruneji.camera_training2.domain.usecase

import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.net.ConnectivityManager
import android.net.LinkProperties
import android.net.Network
import android.net.NetworkCapabilities
import java.net.Inet4Address
import javax.inject.Inject

/**
 * @author toru
 * @version 1.0
 */
class NetworkSensor @Inject constructor(
    context: Context
) {

    // private val _networkState = MutableStateFlow(isNetworkAvailable())
    // val networkState: StateFlow<Boolean> = _networkState

    private val connectivityManager = context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager

    fun checkNetworkAvailable(): Boolean {
        return isNetworkAvailable()
    }

    fun getIpAddress(callback: (String) -> Unit) {
        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onLinkPropertiesChanged(network: Network, linkProperties: LinkProperties) {
                super.onLinkPropertiesChanged(network, linkProperties)

                callback(linkProperties.linkAddresses.filter {
                    it.address is Inet4Address
                }[0].address.hostName)
            }
        }
        connectivityManager.registerDefaultNetworkCallback(networkCallback)
    }

    private fun isNetworkAvailable(): Boolean {
        val nw = connectivityManager.activeNetwork ?: return false
        val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
        return when {
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
            else -> false
        }
    }
}
