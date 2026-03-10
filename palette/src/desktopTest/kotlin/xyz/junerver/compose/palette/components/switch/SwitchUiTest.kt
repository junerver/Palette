package xyz.junerver.compose.palette.components.switch

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import xyz.junerver.compose.palette.core.theme.PaletteMaterialTheme
import kotlin.test.Test

class SwitchUiTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun switch_shouldToggleHostStateWhenClicked() {
        rule.setContent {
            var checked by mutableStateOf(false)

            PaletteMaterialTheme {
                Column {
                    Text(if (checked) "Switch: on" else "Switch: off")
                    PSwitch(
                        checked = checked,
                        onChange = { checked = it },
                    )
                }
            }
        }

        rule.onNodeWithText("Switch: off").assertTextEquals("Switch: off")
        rule.onNode(hasClickAction()).performClick()
        rule.onNodeWithText("Switch: on").assertTextEquals("Switch: on")
    }
}
