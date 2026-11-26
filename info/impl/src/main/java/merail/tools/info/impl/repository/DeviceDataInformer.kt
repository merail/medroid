package merail.tools.info.impl.repository

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import android.os.Environment
import android.os.StatFs
import dagger.hilt.android.qualifiers.ApplicationContext
import merail.tools.core.round
import merail.tools.core.toGb
import merail.tools.info.api.IDeviceDataInformer
import javax.inject.Inject

internal class DeviceDataInformer @Inject constructor(
    @param:ApplicationContext private val context: Context,
): IDeviceDataInformer {
    private val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

    private val memoryInfo = ActivityManager.MemoryInfo().apply {
        activityManager.getMemoryInfo(this)
    }

    private val stat = StatFs(Environment.getDataDirectory().path)

    private val blockSize = stat.blockSizeLong

    override fun getManufacturer(): String = Build.MANUFACTURER

    override fun getModel(): String = Build.MODEL

    override fun getBrand(): String = Build.BRAND

    override fun getAbis(): String = Build.SUPPORTED_ABIS.joinToString()

    override fun getCoresCount() = Runtime.getRuntime().availableProcessors()

    override fun getAvailableRam() = memoryInfo.availMem.toGb().round()

    override fun getTotalRam() = memoryInfo.totalMem.toGb().round()

    override fun getAvailableStorage() = (stat.availableBlocksLong * blockSize).toGb().round()

    override fun getTotalStorage() = (stat.blockCountLong * blockSize).toGb().round()
}