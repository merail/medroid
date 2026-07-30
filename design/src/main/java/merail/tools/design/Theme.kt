package merail.tools.design

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF81C784),     // светлый зелёный
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF388E3C),
    onPrimaryContainer = Color.White,
    inversePrimary = Color(0xFF2E7D32),

    secondary = Color(0xFF66BB6A),   // мягкий зелёный
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF2E7D32),
    onSecondaryContainer = Color.White,

    tertiary = Color(0xFFAED581),    // лайм
    onTertiary = Color.Black,
    tertiaryContainer = Color(0xFF558B2F),
    onTertiaryContainer = Color.White,

    background = Color(0xFF121212),  // почти чёрный фон
    onBackground = Color.White,

    surface = Color(0xFF1E1E1E),     // тёмная поверхность
    onSurface = Color.White,
    surfaceVariant = Color(0xFF2C2C2C),
    onSurfaceVariant = Color(0xFFCCCCCC),
    surfaceTint = Color(0xFF81C784),

    inverseSurface = Color(0xFFE0E0E0),
    inverseOnSurface = Color(0xFF1E1E1E),

    error = Color(0xFFCF6679),
    onError = Color.Black,
    errorContainer = Color(0xFF8C1D18),
    onErrorContainer = Color.White,

    outline = Color(0xFF888888),
    outlineVariant = Color(0xFF444444),
    scrim = Color.Black,

    surfaceBright = Color(0xFF2C2C2C),
    surfaceDim = Color(0xFF0D0D0D),
    surfaceContainerLowest = Color(0xFF0A0A0A),
    surfaceContainerLow = Color(0xFF141414),
    surfaceContainer = Color(0xFF1E1E1E),
    surfaceContainerHigh = Color(0xFF2A2A2A),
    surfaceContainerHighest = Color(0xFF333333),
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF2E7D32),     // насыщенный зелёный
    onPrimary = Color.White,
    primaryContainer = Color(0xFFA5D6A7),
    onPrimaryContainer = Color(0xFF1B5E20),
    inversePrimary = Color(0xFF81C784),

    secondary = Color(0xFF66BB6A),   // мягкий зелёный
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFC8E6C9),
    onSecondaryContainer = Color(0xFF1B5E20),

    tertiary = Color(0xFFAED581),    // лайм
    onTertiary = Color.Black,
    tertiaryContainer = Color(0xFFDCE775),
    onTertiaryContainer = Color(0xFF33691E),

    background = Color(0xFFFFFFFF),  // белый фон
    onBackground = Color(0xFF1B1B1B),

    surface = Color(0xFFF5F5F5),     // светлая поверхность
    onSurface = Color(0xFF1B1B1B),
    surfaceVariant = Color(0xFFE0E0E0),
    onSurfaceVariant = Color(0xFF424242),
    surfaceTint = Color(0xFF2E7D32),

    inverseSurface = Color(0xFF2E2E2E),
    inverseOnSurface = Color.White,

    error = Color(0xFFB00020),
    onError = Color.White,
    errorContainer = Color(0xFFFCD8DF),
    onErrorContainer = Color(0xFF370B12),

    outline = Color(0xFF757575),
    outlineVariant = Color(0xFFBDBDBD),
    scrim = Color.Black,

    surfaceBright = Color(0xFFFFFFFF),
    surfaceDim = Color(0xFFF0F0F0),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFFAFAFA),
    surfaceContainer = Color(0xFFF5F5F5),
    surfaceContainerHigh = Color(0xFFEDEDED),
    surfaceContainerHighest = Color(0xFFE0E0E0),
)

@androidx.compose.runtime.Composable
fun MedroidTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @androidx.compose.runtime.Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) {
                dynamicDarkColorScheme(context)
            } else {
                dynamicLightColorScheme(context)
            }
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = MedroidTypography,
        content = content,
    )
}