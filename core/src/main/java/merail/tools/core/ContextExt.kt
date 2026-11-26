package merail.tools.core

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.core.net.toUri

fun Context.openAppSettings(packageName: String) {
    val intent = Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        "$PACKAGE_SCHEME:$packageName".toUri(),
    )
    startActivity(intent)
}

fun Context.copyToClipboard(text: String) {
    val clipboard: ClipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    @Suppress("UsePropertyAccessSyntax")
    clipboard.setPrimaryClip(ClipData.newPlainText(CLIPBOARD_LABEL, text))
}

private const val PACKAGE_SCHEME = "package"
private const val CLIPBOARD_LABEL = "text"
