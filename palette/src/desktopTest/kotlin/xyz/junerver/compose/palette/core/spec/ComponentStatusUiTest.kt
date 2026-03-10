package xyz.junerver.compose.palette.core.spec

import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import xyz.junerver.compose.palette.core.i18n.PaletteStrings
import xyz.junerver.compose.palette.core.theme.PaletteTheme
import xyz.junerver.compose.palette.core.tokens.PaletteColors
import kotlin.test.Test

class ComponentStatusUiTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun componentStatus_shouldResolveThemeColorsForEachState() {
        val colors =
            PaletteColors(
                primary = Color(0xFF123456),
                onPrimary = Color.White,
                border = Color(0xFF777777),
                surface = Color(0xFFF5F5F5),
                onSurface = Color.Black,
                hint = Color(0xFF888888),
                error = Color(0xFFE53935),
                onError = Color.White,
                success = Color(0xFF10B981),
                warning = Color(0xFFF59E0B),
            )

        rule.setContent {
            PaletteTheme(
                colors = colors,
                strings = PaletteStrings.enUS(),
                darkTheme = true,
            ) {
                Text("Default border: ${ComponentStatus.Default.borderColor() == colors.border}")
                Text("Success border: ${ComponentStatus.Success.borderColor() == colors.success}")
                Text("Warning border: ${ComponentStatus.Warning.borderColor() == colors.warning}")
                Text("Error border: ${ComponentStatus.Error.borderColor() == colors.error}")
                Text("Default background: ${ComponentStatus.Default.backgroundColor() == colors.surface}")
                Text("Success shadow: ${ComponentStatus.Success.shadowColor() == colors.success.copy(alpha = 0.2f)}")
                Text("Warning shadow: ${ComponentStatus.Warning.shadowColor() == colors.warning.copy(alpha = 0.2f)}")
                Text("Error shadow: ${ComponentStatus.Error.shadowColor() == colors.error.copy(alpha = 0.2f)}")
                Text("Theme dark: ${PaletteTheme.isDark}")
            }
        }

        rule.onNodeWithText("Default border: true").assertTextEquals("Default border: true")
        rule.onNodeWithText("Success border: true").assertTextEquals("Success border: true")
        rule.onNodeWithText("Warning border: true").assertTextEquals("Warning border: true")
        rule.onNodeWithText("Error border: true").assertTextEquals("Error border: true")
        rule.onNodeWithText("Default background: true").assertTextEquals("Default background: true")
        rule.onNodeWithText("Success shadow: true").assertTextEquals("Success shadow: true")
        rule.onNodeWithText("Warning shadow: true").assertTextEquals("Warning shadow: true")
        rule.onNodeWithText("Error shadow: true").assertTextEquals("Error shadow: true")
    }
}
