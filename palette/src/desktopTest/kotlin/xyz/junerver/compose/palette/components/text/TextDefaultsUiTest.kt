package xyz.junerver.compose.palette.components.text

import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import org.junit.Rule
import xyz.junerver.compose.palette.core.i18n.PaletteStrings
import xyz.junerver.compose.palette.core.theme.PaletteTheme
import xyz.junerver.compose.palette.core.tokens.PaletteColors
import xyz.junerver.compose.palette.core.tokens.PaletteTypography
import kotlin.test.Test

class TextDefaultsUiTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun textDefaults_shouldResolveThemeColorsAndStyle() {
        val onSurface = Color(0xFF112233)
        val hint = Color(0xFF445566)
        val typography = PaletteTypography(body = TextStyle(fontSize = 15.sp))

        rule.setContent {
            PaletteTheme(
                colors = PaletteColors(onSurface = onSurface, hint = hint),
                typography = typography,
                strings = PaletteStrings.enUS(),
                darkTheme = true,
            ) {
                Text("primary=${TextDefaults.color() == onSurface}")
                Text("secondary=${TextDefaults.secondaryColor() == onSurface.copy(alpha = 0.6f)}")
                Text("disabled=${TextDefaults.disabledColor() == hint}")
                Text("style=${TextDefaults.style() == typography.body}")
            }
        }

        rule.onNodeWithText("primary=true").assertTextEquals("primary=true")
        rule.onNodeWithText("secondary=true").assertTextEquals("secondary=true")
        rule.onNodeWithText("disabled=true").assertTextEquals("disabled=true")
        rule.onNodeWithText("style=true").assertTextEquals("style=true")
    }
}

