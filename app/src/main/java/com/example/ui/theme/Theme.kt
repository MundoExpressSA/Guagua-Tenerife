package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF33DA93),
    onPrimary = Color(0xFF00381F),
    primaryContainer = Color(0xFF00522E),
    onPrimaryContainer = Color(0xFF85F8BE),
    secondary = Color(0xFF81CFFF),
    secondaryContainer = Color(0xFF004B73),
    onSecondaryContainer = Color(0xFFCBE6FF),
    tertiary = Color(0xFFFFB59D),
    background = DarkBackground,
    surface = DarkSurface,
    surfaceVariant = DarkSurfaceVariant,
    onBackground = Color(0xFFE1E3DF),
    onSurface = Color(0xFFE1E3DF)
)

private val LightColorScheme = lightColorScheme(
    primary = TitsaGreenPrimary,
    onPrimary = Color.White,
    primaryContainer = TitsaGreenContainer,
    onPrimaryContainer = OnTitsaGreenContainer,
    secondary = OceanBlueSecondary,
    secondaryContainer = OceanBlueContainer,
    onSecondaryContainer = OnOceanBlueContainer,
    tertiary = WarningOrange,
    tertiaryContainer = WarningOrangeContainer,
    background = Color(0xFFF6FBF7),
    surface = Color.White,
    surfaceVariant = Color(0xFFE8F3ED),
    onBackground = Color(0xFF191C1A),
    onSurface = Color(0xFF191C1A)
)

@Composable
fun GuaguasTenerifeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
