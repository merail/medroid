package merail.tools.info.impl.repository

import android.os.Build
import merail.tools.info.api.IOsDataInformer
import merail.tools.info.api.model.AndroidVersionType
import javax.inject.Inject

internal class OsDataInformer @Inject constructor(): IOsDataInformer {
    override fun getAndroidVersion() = when(Build.VERSION.SDK_INT) {
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

    override fun getPatchLevel() = Build.VERSION.SECURITY_PATCH

    override fun getBuildId() = Build.ID
}