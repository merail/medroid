package merail.tools.packages.impl.repository

import android.content.Context
import android.content.pm.PackageManager
import dagger.hilt.android.qualifiers.ApplicationContext
import merail.tools.packages.api.IPackagesRepository
import merail.tools.packages.api.model.InstalledAppInfo
import javax.inject.Inject

internal class PackagesRepository @Inject constructor(
    @ApplicationContext context: Context,
): IPackagesRepository {
    private val packageManager = context.packageManager

    override fun getInstalledApps() = packageManager.getInstalledApplications(
        PackageManager.GET_META_DATA,
    ).map {
        InstalledAppInfo(
            appName = packageManager.getApplicationLabel(it).toString(),
            packageName = it.packageName,
            icon = packageManager.getApplicationIcon(it),
        )
    }
}