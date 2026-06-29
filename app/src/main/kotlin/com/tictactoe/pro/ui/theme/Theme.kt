package com.tictactoe.pro.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.tictactoe.pro.domain.model.ThemeStyle

private val MaterialYouDark = darkColorScheme(
    primary = Purple80, secondary = PurpleGrey80, tertiary = Pink80
)
private val MaterialYouLight = lightColorScheme(
    primary = Purple40, secondary = PurpleGrey40, tertiary = Pink40
)
private val NeonDark = darkColorScheme(
    primary = NeonPrimary, secondary = NeonSecondary, background = NeonBackground,
    surface = NeonSurface, tertiary = NeonAccent
)
private val RetroDark = darkColorScheme(
    primary = RetroPrimary, secondary = RetroSecondary, background = RetroBackground,
    surface = RetroSurface
)
private val GalaxyDark = darkColorScheme(
    primary = GalaxyPrimary, secondary = GalaxySecondary, background = GalaxyBackground
)
private val GoldDark = darkColorScheme(
    primary = GoldPrimary, secondary = GoldSecondary, background = GoldBackground
)
private val NatureDark = darkColorScheme(
    primary = NaturePrimary, secondary = NatureSecondary, background = NatureBackground
)
private val CyberpunkDark = darkColorScheme(
    primary = CyberpunkPrimary, secondary = CyberpunkSecondary, background = CyberpunkBackground
)
private val AmoledDark = darkColorScheme(
    primary = Purple80, secondary = PurpleGrey80, background = androidx.compose.ui.graphics.Color.Black,
    surface = androidx.compose.ui.graphics.Color(0xFF0D0D0D)
)
private val MinimalLight = lightColorScheme(
    primary = androidx.compose.ui.graphics.Color(0xFF212121),
    secondary = androidx.compose.ui.graphics.Color(0xFF757575),
    background = androidx.compose.ui.graphics.Color(0xFFFAFAFA)
)

@Composable
fun TicTacToeTheme(
    themeStyle: ThemeStyle = ThemeStyle.MATERIAL_YOU,
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeStyle) {
        ThemeStyle.MINIMAL -> false
        else -> true
    }
    val forceDark = themeStyle !in listOf(ThemeStyle.MATERIAL_YOU, ThemeStyle.MINIMAL)

    val colorScheme = when {
        dynamicColor && themeStyle == ThemeStyle.MATERIAL_YOU && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val ctx = LocalContext.current
            if (isSystemInDarkTheme()) dynamicDarkColorScheme(ctx) else dynamicLightColorScheme(ctx)
        }
        else -> when (themeStyle) {
            ThemeStyle.MATERIAL_YOU -> if (isSystemInDarkTheme()) MaterialYouDark else MaterialYouLight
            ThemeStyle.NEON -> NeonDark
            ThemeStyle.RETRO -> RetroDark
            ThemeStyle.GALAXY -> GalaxyDark
            ThemeStyle.GOLD -> GoldDark
            ThemeStyle.NATURE -> NatureDark
            ThemeStyle.CYBERPUNK -> CyberpunkDark
            ThemeStyle.AMOLED -> AmoledDark
            ThemeStyle.MINIMAL -> MinimalLight
            ThemeStyle.GLASSMORPHISM -> darkColorScheme(
                primary = GlassPrimary, secondary = GlassSecondary
            )
        }
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars =
                !darkTheme && !forceDark
        }
    }

    MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
