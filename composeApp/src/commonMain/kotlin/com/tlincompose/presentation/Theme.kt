package com.tlincompose.presentation

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.sp
import com.tlincompose.presentation.accessibility.AccessibilitySettingsUiState
import com.tlincompose.presentation.accessibility.ThemeModeUiState

private val tlInComposeColors = lightColorScheme(
    primary = Color(0xFF145C4C),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFDDEFE9),
    onPrimaryContainer = Color(0xFF0A342B),
    secondary = Color(0xFFB55A2B),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFF9E1D2),
    onSecondaryContainer = Color(0xFF4F2108),
    tertiary = Color(0xFF295D79),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFD9EBF5),
    onTertiaryContainer = Color(0xFF103346),
    background = Color(0xFFF6F2EA),
    onBackground = Color(0xFF1F1B17),
    surface = Color(0xFFFFFBF6),
    onSurface = Color(0xFF1F1B17),
    surfaceVariant = Color(0xFFE7DDD2),
    onSurfaceVariant = Color(0xFF4D463E),
    outline = Color(0xFF7C746B),
)

private val tlInComposeDarkColors = darkColorScheme(
    primary = Color(0xFF8ED6C3),
    onPrimary = Color(0xFF00382D),
    primaryContainer = Color(0xFF0E4C3F),
    onPrimaryContainer = Color(0xFFD7F0E8),
    secondary = Color(0xFFFFB68D),
    onSecondary = Color(0xFF5B2300),
    secondaryContainer = Color(0xFF7A360D),
    onSecondaryContainer = Color(0xFFFFDCC7),
    tertiary = Color(0xFF9CCCE9),
    onTertiary = Color(0xFF00344D),
    tertiaryContainer = Color(0xFF184C66),
    onTertiaryContainer = Color(0xFFD5ECFB),
    background = Color(0xFF141310),
    onBackground = Color(0xFFE8E1D8),
    surface = Color(0xFF1B1916),
    onSurface = Color(0xFFE8E1D8),
    surfaceVariant = Color(0xFF4A453E),
    onSurfaceVariant = Color(0xFFD0C5BA),
    outline = Color(0xFF9A9188),
)

private val tlInComposeHighContrastColors = lightColorScheme(
    primary = Color(0xFF053B31),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFD5F0E8),
    onPrimaryContainer = Color(0xFF04251E),
    secondary = Color(0xFF8A3B00),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFFFDEC5),
    onSecondaryContainer = Color(0xFF3D1800),
    tertiary = Color(0xFF0E426B),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFD8ECFB),
    onTertiaryContainer = Color(0xFF07253D),
    background = Color(0xFFFFFFFF),
    onBackground = Color(0xFF111111),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF111111),
    surfaceVariant = Color(0xFFE7E2D9),
    onSurfaceVariant = Color(0xFF26221E),
    outline = Color(0xFF49433C),
)

private val tlInComposeHighContrastDarkColors = darkColorScheme(
    primary = Color(0xFFB8F5E4),
    onPrimary = Color(0xFF00251D),
    primaryContainer = Color(0xFF0C4A3C),
    onPrimaryContainer = Color(0xFFE4FFF6),
    secondary = Color(0xFFFFC9A8),
    onSecondary = Color(0xFF421900),
    secondaryContainer = Color(0xFF8F4105),
    onSecondaryContainer = Color(0xFFFFE7D7),
    tertiary = Color(0xFFB8E0FA),
    onTertiary = Color(0xFF00273B),
    tertiaryContainer = Color(0xFF14516E),
    onTertiaryContainer = Color(0xFFE4F5FF),
    background = Color(0xFF000000),
    onBackground = Color(0xFFFFFFFF),
    surface = Color(0xFF141414),
    onSurface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFF313131),
    onSurfaceVariant = Color(0xFFF2EEE8),
    outline = Color(0xFFD4CDC4),
)

private val tlInComposeTypography = Typography(
    headlineLarge = TextStyle(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Bold,
        fontSize = 34.sp,
        lineHeight = 40.sp,
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp,
        lineHeight = 34.sp,
    ),
    headlineSmall = TextStyle(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 30.sp,
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        lineHeight = 24.sp,
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 17.sp,
        lineHeight = 24.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        lineHeight = 22.sp,
    ),
    bodySmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        lineHeight = 18.sp,
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 15.sp,
        lineHeight = 20.sp,
    ),
    labelMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 13.sp,
        lineHeight = 18.sp,
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
    ),
)

@Composable
fun TLInComposeTheme(
    accessibilitySettings: AccessibilitySettingsUiState,
    content: @Composable () -> Unit,
) {
    val systemIsDark = isSystemInDarkTheme()
    val useDarkTheme = remember(accessibilitySettings.themeMode, systemIsDark) {
        when (accessibilitySettings.themeMode) {
            ThemeModeUiState.SYSTEM -> systemIsDark
            ThemeModeUiState.LIGHT -> false
            ThemeModeUiState.DARK -> true
        }
    }
    val density = LocalDensity.current
    val adjustedDensity = remember(
        density.density,
        density.fontScale,
        accessibilitySettings.textScale,
    ) {
        Density(
            density = density.density,
            fontScale = density.fontScale * accessibilitySettings.textScale.fontScale,
        )
    }

    CompositionLocalProvider(LocalDensity provides adjustedDensity) {
        MaterialTheme(
            colorScheme = when {
                accessibilitySettings.highContrast && useDarkTheme -> tlInComposeHighContrastDarkColors
                accessibilitySettings.highContrast -> tlInComposeHighContrastColors
                useDarkTheme -> tlInComposeDarkColors
                else -> tlInComposeColors
            },
            typography = tlInComposeTypography,
            content = content,
        )
    }
}
