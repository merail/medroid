package merail.tools.packages.impl

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import merail.tools.core.copyToClipboard
import merail.tools.core.openAppSettings
import merail.tools.packages.api.model.AppDetails
import merail.tools.packages.api.model.AppPermission
import java.text.DateFormat
import java.util.Date

private const val ADB_PM_PATH = "adb shell pm path %s"
private const val ADB_PM_CLEAR = "adb shell pm clear %s"
private const val ADB_PM_UNINSTALL_KEEP = "adb shell pm uninstall -k %s"
private const val ADB_AM_START = "adb shell am start -n %s"
private const val ADB_DUMPSYS_PACKAGE = "adb shell dumpsys package %s"

@Composable
internal fun AppDetailsScreen(
    viewModel: AppDetailsViewModel = hiltViewModel<AppDetailsViewModel>(),
) {
    val appDetails = viewModel.appDetails

    if (appDetails == null) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .systemBarsPadding()
                .fillMaxSize()
                .padding(16.dp),
        ) {
            Text(
                text = stringResource(R.string.packages_app_details_not_found),
                style = MaterialTheme.typography.titleMedium,
            )
        }
        return
    }

    LazyColumn(
        modifier = Modifier
            .systemBarsPadding()
            .fillMaxSize(),
    ) {
        item { AppDetailsHeader(appDetails) }

        item {
            ActionButtons(
                appDetails = appDetails,
                onShareApk = { title -> viewModel.shareApk(appDetails, title) },
            )
        }

        item { AdbCommandsBlock(appDetails) }

        item { SignaturesBlock(appDetails) }

        item { VersionBlock(appDetails) }

        if (!appDetails.isSystemApp) {
            item { InstallBlock(appDetails) }
        }

        item { PermissionsBlock(appDetails) }
    }
}

@Composable
private fun AppDetailsHeader(appDetails: AppDetails) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
    ) {
        appDetails.icon?.let { bitmap ->
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = appDetails.appName,
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(12.dp)),
            )
        }

        Text(
            text = appDetails.appName,
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp),
        )

        Text(
            text = appDetails.packageName,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
            fontFamily = FontFamily.Monospace,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun ActionButtons(
    appDetails: AppDetails,
    onShareApk: (String) -> Unit,
) {
    val context = LocalContext.current
    val shareTitle = stringResource(R.string.packages_app_details_share_apk)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
    ) {
        OutlinedButton(
            onClick = { context.openAppSettings(appDetails.packageName) },
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text = stringResource(R.string.packages_app_details_settings),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }

        Spacer(modifier = Modifier.size(8.dp))

        Button(
            onClick = { onShareApk(shareTitle) },
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text = shareTitle,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun AdbCommandsBlock(appDetails: AppDetails) {
    val packageName = appDetails.packageName
    val commands = buildList {
        add(
            stringResource(R.string.packages_app_details_adb_path) to
                ADB_PM_PATH.format(packageName),
        )
        add(
            stringResource(R.string.packages_app_details_adb_clear) to
                ADB_PM_CLEAR.format(packageName),
        )
        add(
            stringResource(R.string.packages_app_details_adb_uninstall) to
                ADB_PM_UNINSTALL_KEEP.format(packageName),
        )
        appDetails.launchActivity?.let {
            add(
                stringResource(R.string.packages_app_details_adb_start) to
                    ADB_AM_START.format(it),
            )
        }
        add(
            stringResource(R.string.packages_app_details_adb_dump) to
                ADB_DUMPSYS_PACKAGE.format(packageName),
        )
    }

    Section(title = stringResource(R.string.packages_app_details_adb)) {
        DividedRows(
            commands.map { (label, command) ->
                {
                    CopyableRow(
                        label = label,
                        value = command,
                    )
                }
            },
        )
    }
}

@Composable
private fun SignaturesBlock(appDetails: AppDetails) {
    Section(title = stringResource(R.string.packages_app_details_signatures)) {
        DividedRows(
            listOf(
                {
                    CopyableRow(
                        label = stringResource(R.string.packages_app_details_sha1),
                        value = appDetails.sha1,
                    )
                },
                {
                    CopyableRow(
                        label = stringResource(R.string.packages_app_details_sha256),
                        value = appDetails.sha256,
                    )
                },
            ),
        )
    }
}

@Composable
private fun VersionBlock(appDetails: AppDetails) {
    Section(title = stringResource(R.string.packages_app_details_version)) {
        InfoRow(stringResource(R.string.packages_app_details_version_name, appDetails.versionName))
        InfoRow(stringResource(R.string.packages_app_details_version_code, appDetails.versionCode))
        InfoRow(stringResource(R.string.packages_app_details_min_sdk, appDetails.minSdk))
        InfoRow(stringResource(R.string.packages_app_details_target_sdk, appDetails.targetSdk))
        InfoRow(
            stringResource(
                R.string.packages_app_details_debuggable,
                stringResource(
                    if (appDetails.debuggable) {
                        R.string.packages_app_details_yes
                    } else {
                        R.string.packages_app_details_no
                    },
                ),
            ),
        )
    }
}

@Composable
private fun InstallBlock(appDetails: AppDetails) {
    val dateFormat = remember {
        DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM)
    }
    val unknown = stringResource(R.string.packages_app_details_unknown)

    fun formatDate(timestamp: Long): String {
        return if (timestamp > 0) dateFormat.format(Date(timestamp)) else unknown
    }

    Section(title = stringResource(R.string.packages_app_details_install)) {
        InfoRow(
            stringResource(
                R.string.packages_app_details_install_source,
                appDetails.installSource.ifBlank { unknown },
            ),
        )
        InfoRow(
            stringResource(
                R.string.packages_app_details_first_install,
                formatDate(appDetails.firstInstallTime),
            ),
        )
        InfoRow(
            stringResource(
                R.string.packages_app_details_last_update,
                formatDate(appDetails.lastUpdateTime),
            ),
        )
    }
}

@Composable
private fun PermissionsBlock(appDetails: AppDetails) {
    Section(title = stringResource(R.string.packages_app_details_permissions)) {
        if (appDetails.permissions.isEmpty()) {
            InfoRow(stringResource(R.string.packages_app_details_no_permissions))
        } else {
            DividedRows(
                appDetails.permissions.map { permission ->
                    { PermissionRow(permission) }
                },
            )
        }
    }
}

@Composable
private fun PermissionRow(permission: AppPermission) {
    val status = when (permission.granted) {
        true -> stringResource(R.string.packages_app_details_permission_granted)
        false -> stringResource(R.string.packages_app_details_permission_denied)
        null -> stringResource(R.string.packages_app_details_permission_unknown)
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
    ) {
        Text(
            text = permission.name,
            style = MaterialTheme.typography.bodyMedium,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.weight(1f),
        )

        Text(
            text = status,
            style = MaterialTheme.typography.labelMedium,
            textAlign = TextAlign.End,
            maxLines = 1,
            color = when (permission.granted) {
                true -> MaterialTheme.colorScheme.primary
                false -> MaterialTheme.colorScheme.error
                null -> MaterialTheme.colorScheme.onSurfaceVariant
            },
            modifier = Modifier.width(80.dp),
        )
    }
}

@Composable
private fun Section(
    title: String,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 16.dp,
                vertical = 4.dp,
            )
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(16.dp),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
        )

        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
        )

        HorizontalDivider(
            thickness = 2.dp,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
        )

        content()
    }
}

@Composable
private fun DividedRows(
    rows: List<@Composable () -> Unit>,
) {
    rows.forEachIndexed { index, row ->
        row()
        if (index != rows.lastIndex) {
            HorizontalDivider(
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
            )
        }
    }
}

@Composable
private fun InfoRow(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
    )
}

@Composable
private fun CopyableRow(
    label: String,
    value: String,
) {
    val context = LocalContext.current

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                fontFamily = FontFamily.Monospace,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }

        Text(
            text = stringResource(R.string.packages_app_details_copy),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .clickable { context.copyToClipboard(value) }
                .padding(
                    horizontal = 8.dp,
                    vertical = 4.dp,
                ),
        )
    }
}

