package merail.tools.info.impl.repository

import android.content.Context
import android.hardware.display.DisplayManager
import android.os.Build
import android.util.DisplayMetrics
import android.view.Display
import android.view.WindowManager
import dagger.hilt.android.qualifiers.ApplicationContext
import merail.tools.info.api.IDisplayDataInformer
import merail.tools.info.api.model.DensityQualifierType
import javax.inject.Inject

internal class DisplayDataInformer @Inject constructor(
    @param:ApplicationContext private val context: Context,
) : IDisplayDataInformer {
    private val windowManager: WindowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

    private val displayManager: DisplayManager = context.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager

    private val displayMetrics = context.resources.displayMetrics

    private val display = displayManager.getDisplay(Display.DEFAULT_DISPLAY)

    private val realMetrics: DisplayMetrics by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val bounds = windowManager.maximumWindowMetrics.bounds
            DisplayMetrics().apply {
                widthPixels = bounds.width()
                heightPixels = bounds.height()
            }
        } else {
            @Suppress("DEPRECATION")
            DisplayMetrics().apply {
                display.getRealMetrics(this)
            }
        }
    }

    override fun getDisplayWidth() = realMetrics.widthPixels

    override fun getDisplayHeight() = realMetrics.heightPixels

    override fun getDpi() = displayMetrics.densityDpi

    override fun getDensity() = displayMetrics.density

    override fun getDensityQualifier() = when (getDpi()) {
        in 0..LDPI_MAX_DPI -> DensityQualifierType.LDPI
        in (LDPI_MAX_DPI + 1)..MDPI_MAX_DPI -> DensityQualifierType.MDPI
        in (MDPI_MAX_DPI + 1)..HDPI_MAX_DPI -> DensityQualifierType.HDPI
        in (HDPI_MAX_DPI + 1)..XHDPI_MAX_DPI -> DensityQualifierType.XHDPI
        in (XHDPI_MAX_DPI + 1)..XXHDPI_MAX_DPI -> DensityQualifierType.XXHDPI
        in (XXHDPI_MAX_DPI + 1)..XXXHDPI_MAX_DPI -> DensityQualifierType.XXXHDPI
        else -> DensityQualifierType.UNKNOWN
    }

    override fun getRefreshRate() = display.refreshRate

    private companion object {
        const val LDPI_MAX_DPI = 120
        const val MDPI_MAX_DPI = 160
        const val HDPI_MAX_DPI = 240
        const val XHDPI_MAX_DPI = 320
        const val XXHDPI_MAX_DPI = 480
        const val XXXHDPI_MAX_DPI = 640
    }
}
