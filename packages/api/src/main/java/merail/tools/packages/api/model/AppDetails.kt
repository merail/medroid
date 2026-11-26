package merail.tools.packages.api.model

import android.graphics.Bitmap

data class AppDetails(
    val appName: String,
    val packageName: String,
    val icon: Bitmap?,
    val apkPath: String,
    val launchActivity: String?,
    val versionName: String,
    val versionCode: Long,
    val minSdk: Int,
    val targetSdk: Int,
    val debuggable: Boolean,
    val isSystemApp: Boolean,
    val installSource: String,
    val firstInstallTime: Long,
    val lastUpdateTime: Long,
    val sha1: String,
    val sha256: String,
    val permissions: List<AppPermission>,
)

data class AppPermission(
    val name: String,
    val granted: Boolean?,
)
