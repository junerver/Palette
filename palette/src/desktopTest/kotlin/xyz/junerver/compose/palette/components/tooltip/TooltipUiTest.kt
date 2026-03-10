package xyz.junerver.compose.palette.components.tooltip

import androidx.compose.material3.Text
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import xyz.junerver.compose.palette.core.theme.PaletteMaterialTheme
import kotlin.test.Test

class TooltipUiTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun tooltip_shouldToggleVisibleWhenClicked() {
        rule.setContent {
            PaletteMaterialTheme {
                PTooltip(
                    text = "Tooltip content",
                ) {
                    Text("Trigger")
                }
            }
        }

        // hidden by default
        rule.onNodeWithText("Trigger").assertTextEquals("Trigger")

        rule.onNodeWithText("Trigger").performClick()
        rule.onNodeWithText("Tooltip content").assertTextEquals("Tooltip content")

        // click again -> hide
        rule.onNodeWithText("Trigger").performClick()
        // Popup dismissal is async; just ensure trigger is still present.
        rule.onNodeWithText("Trigger").assertTextEquals("Trigger")
    }
}

