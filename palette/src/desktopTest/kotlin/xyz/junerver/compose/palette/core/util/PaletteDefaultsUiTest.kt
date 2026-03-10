package xyz.junerver.compose.palette.core.util

import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import xyz.junerver.compose.palette.core.i18n.PaletteStrings
import xyz.junerver.compose.palette.core.theme.PaletteMaterialTheme
import xyz.junerver.compose.palette.core.tokens.PaletteColors
import kotlin.test.Test

class PaletteDefaultsUiTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun paletteDefaults_shouldExposeSnapshotsAndMaterialLocals() {
        val colors =
            PaletteColors(
                primary = Color(0xFF223344),
                onPrimary = Color.White,
                border = Color(0xFF667788),
                surface = Color(0xFFF8F8F8),
                onSurface = Color.Black,
                hint = Color(0xFF999999),
                error = Color(0xFFE53935),
                onError = Color.White,
                success = Color(0xFF10B981),
                warning = Color(0xFFF59E0B),
            )

        rule.setContent {
            PaletteMaterialTheme(colors = colors, strings = PaletteStrings.enUS(), darkTheme = true) {
                Text("Defaults dark: ${PaletteDefaults.isDark}")
                Text("Defaults primary: ${PaletteDefaults.colors.primary == colors.primary}")
                Text("Defaults spacing: ${PaletteDefaults.spacing.medium.value}")
                Text("Defaults material primary: ${PaletteDefaults.materialColors.primary == colors.primary}")
                Text("Defaults material body: ${PaletteDefaults.materialTypography.bodyLarge.fontSize.value}")
            }
        }

        rule.onNodeWithText("Defaults dark: true").assertTextEquals("Defaults dark: true")
        rule.onNodeWithText("Defaults primary: true").assertTextEquals("Defaults primary: true")
        rule.onNodeWithText("Defaults spacing: 16.0").assertTextEquals("Defaults spacing: 16.0")
        rule.onNodeWithText("Defaults material primary: true").assertTextEquals("Defaults material primary: true")
    }
}
