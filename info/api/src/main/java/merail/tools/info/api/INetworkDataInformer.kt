package merail.tools.info.api

import kotlinx.coroutines.flow.Flow
import merail.tools.info.api.model.NetworkConnectionType

interface INetworkDataInformer {
    fun isConnected(): Flow<Boolean>

    fun getNetworkConnection(): Flow<NetworkConnectionType>

    fun getIpAddress(): Flow<String>

    fun startMonitoring()

    fun stopMonitoring()
}