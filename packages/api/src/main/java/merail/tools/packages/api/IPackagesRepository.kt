package merail.tools.packages.api

import merail.tools.packages.api.model.InstalledAppInfo

interface IPackagesRepository {
    fun getInstalledApps(): List<InstalledAppInfo>
}