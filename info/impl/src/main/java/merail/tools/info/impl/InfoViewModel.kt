package merail.tools.info.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import merail.tools.info.api.IDeviceDataInformer
import merail.tools.info.api.IDisplayDataInformer
import merail.tools.info.api.INetworkDataInformer
import merail.tools.info.api.IOsDataInformer
import merail.tools.info.api.model.NetworkConnectionType
import javax.inject.Inject

@HiltViewModel
internal class InfoViewModel @Inject constructor(
    displayMetricsInformer: IDisplayDataInformer,
    osDataInformer: IOsDataInformer,
    deviceDataInformer: IDeviceDataInformer,
    private val networkDataInformer: INetworkDataInformer,
) : ViewModel() {

    val displayWidth = displayMetricsInformer.getDisplayWidth()
    val displayHeight = displayMetricsInformer.getDisplayHeight()
    val displayDpi = displayMetricsInformer.getDpi()
    val displayWidthDpi = (displayMetricsInformer.getDisplayWidth() / displayMetricsInformer.getDensity()).toInt()
    val displayHeightDpi = (displayMetricsInformer.getDisplayHeight() / displayMetricsInformer.getDensity()).toInt()
    val displayDensityQualifier =
        displayMetricsInformer.getDensityQualifier()
    val displayRefreshRate = displayMetricsInformer.getRefreshRate().toInt()

    val androidVersion = osDataInformer.getAndroidVersion()
    val patchLevel = osDataInformer.getPatchLevel()
    val buildId = osDataInformer.getBuildId()

    val manufacturer = deviceDataInformer.getManufacturer()
    val model = deviceDataInformer.getModel()
    val brand = deviceDataInformer.getBrand()
    val abis = deviceDataInformer.getAbis()
    val coresCount = deviceDataInformer.getCoresCount()
    val availableRam = deviceDataInformer.getAvailableRam()
    val totalRam = deviceDataInformer.getTotalRam()
    val availableStorage = deviceDataInformer.getAvailableStorage()
    val totalStorage = deviceDataInformer.getTotalStorage()

    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected

    private val _networkConnection = MutableStateFlow(NetworkConnectionType.NONE)
    val networkConnection = _networkConnection

    private val _ipAddress = MutableStateFlow("")
    val ipAddress = _ipAddress

    init {
        networkDataInformer.startMonitoring()

        viewModelScope.launch {
            networkDataInformer.isConnected().collect {
                _isConnected.value = it
            }
        }

        viewModelScope.launch {
            networkDataInformer.getNetworkConnection().collect {
                _networkConnection.value = it
            }
        }

        viewModelScope.launch {
            networkDataInformer.getIpAddress().collect {
                _ipAddress.value = it
            }
        }
    }

    override fun onCleared() {
        super.onCleared()

        networkDataInformer.stopMonitoring()
    }
}
