package xyz.junerver.compose.palette.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

val LocalIsDarkTheme = compositionLocalOf { false }

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryLight,
    onPrimary = Color.White,
    primaryContainer = PrimaryDark,
    secondary = PurpleGrey80,
    tertiary = Pink80,
    background = SurfaceDark,
    surface = CardBackgroundDark,
    surfaceVariant = CodeBackgroundDark,
    onBackground = TextPrimaryDark,
    onSurface = TextPrimaryDark,
    onSurfaceVariant = TextSecondaryDark,
    outline = BorderDark,
)

private val LightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = Color.White,
    primaryContainer = PrimaryLight,
    secondary = PurpleGrey40,
    tertiary = Pink40,
    background = Surface,
    surface = CardBackground,
    surfaceVariant = CodeBackground,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    onSurfaceVariant = TextSecondary,
    outline = Border,
)

@Composable
fun PaletteTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    CompositionLocalProvider(LocalIsDarkTheme provides darkTheme) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
