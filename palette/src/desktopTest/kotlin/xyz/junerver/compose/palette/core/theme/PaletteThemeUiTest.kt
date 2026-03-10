package xyz.junerver.compose.palette.core.theme

import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import xyz.junerver.compose.palette.core.i18n.PaletteStrings
import xyz.junerver.compose.palette.core.tokens.PaletteColors
import xyz.junerver.compose.palette.core.tokens.PaletteShapes
import xyz.junerver.compose.palette.core.tokens.PaletteSpacing
import xyz.junerver.compose.palette.core.tokens.PaletteTypography
import kotlin.test.Test

class PaletteThemeUiTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun paletteTheme_shouldExposeCustomLocals() {
        rule.setContent {
            PaletteTheme(
                colors =
                    PaletteColors(
                        primary = Color(0xFF123456),
                        onPrimary = Color.White,
                        border = Color(0xFF654321),
                        surface = Color(0xFFF0F0F0),
                        onSurface = Color.Black,
                        hint = Color(0xFF999999),
                        error = Color(0xFFE53935),
                        onError = Color.White,
                        success = Color(0xFF10B981),
                        warning = Color(0xFFF59E0B),
                    ),
                spacing = PaletteSpacing.default(),
                shapes = PaletteShapes.default(),
                typography = PaletteTypography.default(),
                strings = PaletteStrings.enUS(),
                darkTheme = true,
            ) {
                Text("Dark: ${PaletteTheme.isDark}")
                Text("Expand: ${PaletteTheme.strings.commonExpand}")
                Text("Spacing: ${PaletteTheme.spacing.medium.value}")
                Text("BodySize: ${PaletteTheme.typography.body.fontSize.value}")
            }
        }

        rule.onNodeWithText("Dark: true").assertTextEquals("Dark: true")
        rule.onNodeWithText("Expand: Expand").assertTextEquals("Expand: Expand")
        rule.onNodeWithText("Spacing: 16.0").assertTextEquals("Spacing: 16.0")
        rule.onNodeWithText("BodySize: 14.0").assertTextEquals("BodySize: 14.0")
    }

    @Test
    fun paletteMaterialTheme_shouldExposeMaterialLocals() {
        rule.setContent {
            PaletteMaterialTheme(strings = PaletteStrings.enUS(), darkTheme = true) {
                Text("Material dark: ${PaletteTheme.isDark}")
                Text("Material expand: ${PaletteTheme.strings.commonExpand}")
                Text("Material body size: ${PaletteMaterialTheme.typography.bodyLarge.fontSize.value}")
            }
        }

        rule.onNodeWithText("Material dark: true").assertTextEquals("Material dark: true")
        rule.onNodeWithText("Material expand: Expand").assertTextEquals("Material expand: Expand")
        rule.onNodeWithText("Material body size: 16.0").assertTextEquals("Material body size: 16.0")
    }
}
