package merail.tools.packages.impl.repository

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.Signature
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import dagger.hilt.android.qualifiers.ApplicationContext
import merail.tools.core.ILogger
import merail.tools.packages.api.IPackagesRepository
import merail.tools.packages.api.model.AppDetails
import merail.tools.packages.api.model.AppPermission
import merail.tools.packages.api.model.InstalledAppInfo
import java.security.MessageDigest
import javax.inject.Inject
import androidx.core.graphics.createBitmap

internal class PackagesRepository @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val logger: ILogger,
) : IPackagesRepository {
    private val packageManager = context.packageManager

    override fun getInstalledApps(): List<InstalledAppInfo> {
        return packageManager.getInstalledApplications(PackageManager.GET_META_DATA).map {
            InstalledAppInfo(
                appName = packageManager.getApplicationLabel(it).toString(),
                packageName = it.packageName,
                icon = null,
            )
        }
    }

    override fun getAppIcon(packageName: String): Bitmap? {
        val iconSize = (LIST_ICON_SIZE_DP * context.resources.displayMetrics.density).toInt()
        return try {
            val applicationInfo = packageManager.getApplicationInfo(packageName, 0)
            packageManager.getApplicationIcon(applicationInfo).toBitmap(iconSize, iconSize)
        } catch (e: PackageManager.NameNotFoundException) {
            logger.error(TAG, "Failed to load icon for $packageName", e)
            null
        }
    }

    override fun getAppDetails(packageName: String): AppDetails? {
        val packageInfo = try {
            packageManager.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS)
        } catch (e: PackageManager.NameNotFoundException) {
            logger.error(TAG, "Failed to load details for $packageName", e)
            return null
        }

        val applicationInfo = packageInfo.applicationInfo ?: return null
        val signatures = getSignatures(packageName)
        val iconSize = (DETAIL_ICON_SIZE_DP * context.resources.displayMetrics.density).toInt()

        return AppDetails(
            appName = packageManager.getApplicationLabel(applicationInfo).toString(),
            packageName = packageName,
            icon = packageManager.getApplicationIcon(applicationInfo).toBitmap(iconSize, iconSize),
            apkPath = applicationInfo.sourceDir,
            launchActivity = packageManager
                .getLaunchIntentForPackage(packageName)
                ?.component
                ?.flattenToShortString(),
            versionName = packageInfo.versionName.orEmpty(),
            versionCode = packageInfo.longVersionCodeCompat(),
            minSdk = applicationInfo.minSdkVersion,
            targetSdk = applicationInfo.targetSdkVersion,
            debuggable = applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0,
            isSystemApp = applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0,
            installSource = getInstallSource(packageName),
            firstInstallTime = packageInfo.firstInstallTime,
            lastUpdateTime = packageInfo.lastUpdateTime,
            sha1 = digest(SHA_1, signatures.firstOrNull()?.toByteArray()),
            sha256 = digest(SHA_256, signatures.firstOrNull()?.toByteArray()),
            permissions = getPermissions(packageInfo),
        )
    }

    private fun getSignatures(packageName: String): Array<Signature> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val info = packageManager.getPackageInfo(
                packageName,
                PackageManager.GET_SIGNING_CERTIFICATES,
            )
            info.signingInfo?.apkContentsSigners ?: emptyArray()
        } else {
            @Suppress("DEPRECATION")
            val info = packageManager.getPackageInfo(
                packageName,
                PackageManager.GET_SIGNATURES,
            )
            @Suppress("DEPRECATION")
            info.signatures ?: emptyArray()
        }
    }

    private fun getInstallSource(packageName: String): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val info = packageManager.getInstallSourceInfo(packageName)
            info.installingPackageName ?: info.initiatingPackageName ?: ""
        } else {
            @Suppress("DEPRECATION")
            packageManager.getInstallerPackageName(packageName) ?: ""
        }
    }

    private fun getPermissions(packageInfo: PackageInfo): List<AppPermission> {
        val requested = packageInfo.requestedPermissions ?: return emptyList()
        val flags = packageInfo.requestedPermissionsFlags
        return requested.mapIndexed { index, name ->
            val granted = flags?.let {
                it[index] and PackageInfo.REQUESTED_PERMISSION_GRANTED != 0
            }
            AppPermission(
                name = name,
                granted = granted,
            )
        }
    }

    private fun digest(algorithm: String, bytes: ByteArray?): String {
        if (bytes == null) return ""
        return MessageDigest.getInstance(algorithm)
            .digest(bytes)
            .joinToString(":") { HEX_FORMAT.format(it.toInt() and 0xFF) }
    }

    private fun PackageInfo.longVersionCodeCompat(): Long {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            longVersionCode
        } else {
            @Suppress("DEPRECATION")
            versionCode.toLong()
        }
    }

    private fun Drawable.toBitmap(widthPx: Int, heightPx: Int): Bitmap {
        if (this is BitmapDrawable) {
            val bitmap = bitmap
            if (bitmap != null && bitmap.width == widthPx && bitmap.height == heightPx) {
                return bitmap
            }
        }

        val result = createBitmap(widthPx, heightPx)
        val canvas = Canvas(result)
        setBounds(0, 0, widthPx, heightPx)
        draw(canvas)
        return result
    }

    private companion object {
        const val TAG = "PackagesRepository"
        const val LIST_ICON_SIZE_DP = 40
        const val DETAIL_ICON_SIZE_DP = 64
        const val SHA_1 = "SHA-1"
        const val SHA_256 = "SHA-256"
        const val HEX_FORMAT = "%02X"
    }
}
