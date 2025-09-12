package merail.tools.info

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

internal class NetworkDataInformer @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val networkCapabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)

    private val linkProperties = connectivityManager.getLinkProperties(connectivityManager.activeNetwork)

    private val isConnectedValue = MutableStateFlow(false)

    private val networkConnectionValue = MutableStateFlow(NetworkConnectionType.NONE)

    private val ipAddressValue = MutableStateFlow("")

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            isConnectedValue.value = networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
            networkConnectionValue.value = when {
                networkCapabilities == null -> NetworkConnectionType.NONE
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> NetworkConnectionType.MOBILE
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> NetworkConnectionType.WI_FI
                else -> NetworkConnectionType.OTHER
            }
            ipAddressValue.value = if (networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true) {
                val addresses = linkProperties?.linkAddresses
                if (addresses.isNullOrEmpty()) {
                    ""
                } else {
                    addresses[0].address.hostAddress
                }
            } else {
                ""
            }
        }

        override fun onLost(network: Network) {
            super.onLost(network)
        }
    }

    fun isConnected() = isConnectedValue

    fun getNetworkConnection() = networkConnectionValue

    fun getIpAddress() = ipAddressValue

    fun startMonitoring() {
        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        connectivityManager.registerNetworkCallback(request, networkCallback)
    }

    fun stopMonitoring() {
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }
}