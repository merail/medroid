package merail.tools.info.impl.repository

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import android.os.Environment
import android.os.StatFs
import dagger.hilt.android.qualifiers.ApplicationContext
import merail.tools.info.api.IDeviceDataInformer
import javax.inject.Inject
import kotlin.math.pow
import kotlin.math.roundToInt

internal const val BINARY_BASE = 1024F

internal class DeviceDataInformer @Inject constructor(
    @ApplicationContext private val context: Context,
): IDeviceDataInformer {
    private val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

    private val memoryInfo = ActivityManager.MemoryInfo().apply {
        activityManager.getMemoryInfo(this)
    }

    private val stat = StatFs(Environment.getDataDirectory().path)

    private val blockSize = stat.blockSizeLong

    override fun getManufacturer() = Build.MANUFACTURER

    override fun getModel() = Build.MODEL

    override fun getBrand() = Build.BRAND

    override fun getAbis() = Build.SUPPORTED_ABIS.joinToString()

    override fun getCoresCount() = Runtime.getRuntime().availableProcessors()

    override fun getAvailableRam() = memoryInfo.availMem.toGb().round()

    override fun getTotalRam() = memoryInfo.totalMem.toGb().round()

    override fun getAvailableStorage() = (stat.availableBlocksLong * blockSize).toGb().round()

    override fun getTotalStorage() = (stat.blockCountLong * blockSize).toGb().round()

    private fun Long.toGb() = this / (BINARY_BASE * BINARY_BASE * BINARY_BASE)

    private fun Float.round(decimals: Int = 1): Float {
        val factor = 10.0f.pow(decimals)
        return (this * factor).roundToInt() / factor
    }
}