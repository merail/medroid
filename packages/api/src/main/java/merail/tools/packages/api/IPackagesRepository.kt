package merail.tools.packages.api

import android.graphics.Bitmap
import merail.tools.packages.api.model.AppDetails
import merail.tools.packages.api.model.InstalledAppInfo

interface IPackagesRepository {
    fun getInstalledApps(): List<InstalledAppInfo>

    fun getAppIcon(packageName: String): Bitmap?

    fun getAppDetails(packageName: String): AppDetails?
}