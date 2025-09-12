package merail.tools.info

import android.net.NetworkCapabilities
import android.os.Build
import android.system.Os.stat
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class InfoViewModel @Inject constructor(
    displayMetricsInformer: DisplayMetricsInformer,
    dataInformer: DeviceDataInformer,
    private val networkDataInformer: NetworkDataInformer,
) : ViewModel() {

    val displayWidth = displayMetricsInformer.getDisplayWidth()
    val displayHeight = displayMetricsInformer.getDisplayHeight()
    val displayDpi = displayMetricsInformer.getDpi()
    val displayWidthDpi = (displayMetricsInformer.getDisplayWidth() / displayMetricsInformer.getDensity()).toInt()
    val displayHeightDpi = (displayMetricsInformer.getDisplayHeight() / displayMetricsInformer.getDensity()).toInt()
    val displayDensityQualifier = displayMetricsInformer.getDensityQualifier()
    val displayRefreshRate = displayMetricsInformer.getRefreshRate().toInt()

    val androidVersion = when(Build.VERSION.SDK_INT) {
        36 -> AndroidVersionType.BAKLAVA
        35 -> AndroidVersionType.VANILLA_ICE_CREAM
        34 -> AndroidVersionType.UPSIDE_DOWN_CAKE
        33 -> AndroidVersionType.TIRAMISU
        32 -> AndroidVersionType.SNOW_CONE_32
        31 -> AndroidVersionType.SNOW_CONE_31
        30 -> AndroidVersionType.RED_VELVET_CAKE
        29 -> AndroidVersionType.Q
        28 -> AndroidVersionType.PIE
        27 -> AndroidVersionType.OREO_27
        26 -> AndroidVersionType.OREO_26
        else -> AndroidVersionType.UNKNOWN
    }
    val patchLevel = Build.VERSION.SECURITY_PATCH
    val buildId = Build.ID

    val manufacturer = dataInformer.getManufacturer()
    val model = dataInformer.getModel()
    val brand = dataInformer.getBrand()
    val abis = dataInformer.getAbis()
    val coresCount = dataInformer.getCoresCount()
    val availableRam = dataInformer.getAvailableRam()
    val totalRam = dataInformer.getTotalRam()
    val availableStorage = dataInformer.getAvailableStorage()
    val totalStorage = dataInformer.getTotalStorage()

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
