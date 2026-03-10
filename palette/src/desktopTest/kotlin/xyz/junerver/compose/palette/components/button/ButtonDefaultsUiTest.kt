package xyz.junerver.compose.palette.components.button

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

class ButtonDefaultsUiTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun buttonDefaults_shouldResolveLightThemeColors() {
        val colors =
            PaletteColors(
                primary = Color(0xFF123456),
                onPrimary = Color.White,
                border = Color(0xFF999999),
                surface = Color(0xFFF5F5F5),
                onSurface = Color.Black,
                hint = Color(0xFF888888),
                error = Color(0xFFE53935),
                onError = Color.White,
                success = Color(0xFF10B981),
                warning = Color(0xFFF59E0B),
            )

        rule.setContent {
            PaletteMaterialTheme(colors = colors, strings = PaletteStrings.enUS(), darkTheme = false) {
                Text("Primary container: ${ButtonDefaults.primaryContainerColor() == colors.primary}")
                Text("Primary content: ${ButtonDefaults.primaryContentColor() == colors.onPrimary}")
                Text("Danger content: ${ButtonDefaults.dangerContentColor() == colors.error}")
                Text("Plain content: ${ButtonDefaults.plainContentColor() == colors.onSurface}")
            }
        }

        rule.onNodeWithText("Primary container: true").assertTextEquals("Primary container: true")
        rule.onNodeWithText("Primary content: true").assertTextEquals("Primary content: true")
        rule.onNodeWithText("Danger content: true").assertTextEquals("Danger content: true")
        rule.onNodeWithText("Plain content: true").assertTextEquals("Plain content: true")
    }

    @Test
    fun buttonDefaults_shouldResolveDarkThemeBranches() {
        val colors =
            PaletteColors(
                primary = Color(0xFF123456),
                onPrimary = Color.White,
                border = Color(0xFF999999),
                surface = Color(0xFF1E1E1E),
                onSurface = Color.White,
                hint = Color(0xFF888888),
                error = Color(0xFFE53935),
                onError = Color(0xFFFAFAFA),
                success = Color(0xFF10B981),
                warning = Color(0xFFF59E0B),
            )

        rule.setContent {
            PaletteMaterialTheme(colors = colors, strings = PaletteStrings.enUS(), darkTheme = true) {
                Text("Danger container dark: ${ButtonDefaults.dangerContainerColor() == colors.error}")
                Text("Danger content dark: ${ButtonDefaults.dangerContentColor() == colors.onError}")
                Text("Plain container dark: ${ButtonDefaults.plainContainerColor() == colors.surface}")
            }
        }

        rule.onNodeWithText("Danger container dark: true").assertTextEquals("Danger container dark: true")
        rule.onNodeWithText("Danger content dark: true").assertTextEquals("Danger content dark: true")
        rule.onNodeWithText("Plain container dark: true").assertTextEquals("Plain container dark: true")
    }
}
