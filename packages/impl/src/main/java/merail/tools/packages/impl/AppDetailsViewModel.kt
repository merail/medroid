package merail.tools.packages.impl

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import merail.tools.core.ILogger
import merail.tools.packages.api.IPackagesRepository
import merail.tools.packages.api.model.AppDetails
import java.io.File
import javax.inject.Inject

@HiltViewModel
internal class AppDetailsViewModel @Inject constructor(
    @param:ApplicationContext private val context: Context,
    savedStateHandle: SavedStateHandle,
    packagesRepository: IPackagesRepository,
    private val logger: ILogger,
) : ViewModel() {

    val appDetails: AppDetails? = savedStateHandle
        .get<String>(PACKAGE_NAME)
        ?.let(packagesRepository::getAppDetails)

    fun shareApk(appDetails: AppDetails, chooserTitle: String) {
        try {
            val source = File(appDetails.apkPath)
            val targetDir = File(context.cacheDir, SHARED_APKS_DIR)
            targetDir.mkdirs()
            val target = File(targetDir, appDetails.packageName + APK_EXTENSION)
            source.copyTo(target, overwrite = true)

            val uri = FileProvider.getUriForFile(
                context,
                context.packageName + FILE_PROVIDER_SUFFIX,
                target,
            )

            val sendIntent = Intent(Intent.ACTION_SEND).apply {
                type = APK_MIME_TYPE
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            val chooser = Intent.createChooser(sendIntent, chooserTitle).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            context.startActivity(chooser)
        } catch (e: Exception) {
            logger.error(TAG, "Failed to share APK for ${appDetails.packageName}", e)
        }
    }

    private companion object {
        const val TAG = "AppDetailsViewModel"
        const val PACKAGE_NAME = "packageName"
        const val SHARED_APKS_DIR = "shared_apks"
        const val APK_EXTENSION = ".apk"
        const val APK_MIME_TYPE = "application/vnd.android.package-archive"
        const val FILE_PROVIDER_SUFFIX = ".fileprovider"
    }
}
