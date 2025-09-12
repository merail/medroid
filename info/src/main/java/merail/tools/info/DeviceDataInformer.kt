package merail.tools.info

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import android.os.Environment
import android.os.StatFs
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlin.math.pow
import kotlin.math.roundToInt

internal const val BINARY_BASE = 1024F

internal class DeviceDataInformer @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

    private val memoryInfo = ActivityManager.MemoryInfo().apply {
        activityManager.getMemoryInfo(this)
    }

    private val stat = StatFs(Environment.getDataDirectory().path)

    private val blockSize = stat.blockSizeLong

    fun getManufacturer() = Build.MANUFACTURER

    fun getModel() = Build.MODEL

    fun getBrand() = Build.BRAND

    fun getAbis() = Build.SUPPORTED_ABIS.joinToString()

    fun getCoresCount() = Runtime.getRuntime().availableProcessors()

    fun getAvailableRam() = memoryInfo.availMem.toGb().round()

    fun getTotalRam() = memoryInfo.totalMem.toGb().round()

    fun getAvailableStorage() = (stat.availableBlocksLong * blockSize).toGb().round()

    fun getTotalStorage() = (stat.blockCountLong * blockSize).toGb().round()

    private fun Long.toGb() = this / (BINARY_BASE * BINARY_BASE * BINARY_BASE)

    private fun Float.round(decimals: Int = 1): Float {
        val factor = 10.0f.pow(decimals)
        return (this * factor).roundToInt() / factor
    }
}