package merail.tools.packages.api.model

import android.graphics.Bitmap

data class InstalledAppInfo(
    val appName: String,
    val packageName: String,
    val icon: Bitmap?,
    val isSystemApp: Boolean,
    val debuggable: Boolean,
)
