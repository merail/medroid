package merail.tools.info.impl.repository

import android.content.Context
import android.hardware.display.DisplayManager
import android.os.Build
import android.util.DisplayMetrics
import android.view.Display
import android.view.WindowManager
import androidx.annotation.RequiresApi
import dagger.hilt.android.qualifiers.ApplicationContext
import merail.tools.info.api.IDisplayDataInformer
import merail.tools.info.api.model.DensityQualifierType
import javax.inject.Inject

internal class DisplayDataInformer @Inject constructor(
    @ApplicationContext context: Context,
): IDisplayDataInformer {
    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

    private val displayManager = context.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager

    private val displayMetrics = context.resources.displayMetrics

    @delegate:RequiresApi(Build.VERSION_CODES.R)
    private val windowBounds by lazy {
        windowManager.currentWindowMetrics.bounds
    }

    private val realMetrics by lazy {
        windowManager.defaultDisplay.run {
            val displayMetrics = DisplayMetrics()
            getRealMetrics(displayMetrics)
            return@run displayMetrics
        }
    }

    private val display = displayManager.getDisplay(Display.DEFAULT_DISPLAY)

    override fun getDisplayWidth() = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
        windowBounds.width()
    } else {
        realMetrics.widthPixels
    }

    override fun getDisplayHeight() = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
        windowBounds.height()
    } else {
        realMetrics.heightPixels
    }

    override fun getDpi() = displayMetrics.densityDpi

    override fun getDensity() = displayMetrics.density

    override fun getDensityQualifier() = when (getDpi()) {
        in 0..120 -> DensityQualifierType.LDPI
        in 121..160 -> DensityQualifierType.MDPI
        in 161..240 -> DensityQualifierType.HDPI
        in 241..320 -> DensityQualifierType.XHDPI
        in 321..480 -> DensityQualifierType.XXHDPI
        in 481..640 -> DensityQualifierType.XXXHDPI
        else -> DensityQualifierType.UNKNOWN
    }

    override fun getRefreshRate() = display.refreshRate
}