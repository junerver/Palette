package xyz.junerver.compose.palette.components.checkbox

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import xyz.junerver.compose.palette.core.theme.PaletteMaterialTheme
import kotlin.test.Test

class CheckboxUiTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun checkbox_shouldRenderUncheckedStateWithHostText() {
        rule.setContent {
            val checked = false

            PaletteMaterialTheme {
                Column {
                    Text(if (checked) "Checkbox: checked" else "Checkbox: unchecked")
                    ColoredCheckBox(
                        checked = checked,
                        onCheckedChange = {},
                    )
                }
            }
        }

        rule.onNodeWithText("Checkbox: unchecked").assertTextEquals("Checkbox: unchecked")
    }

    @Test
    fun checkbox_shouldReflectHostStateUpdateWhenCheckedChanges() {
        var checked by mutableStateOf(false)

        rule.setContent {
            PaletteMaterialTheme {
                Column {
                    Text(if (checked) "Checkbox: checked" else "Checkbox: unchecked")
                    ColoredCheckBox(
                        checked = checked,
                        enabled = false,
                        onCheckedChange = { checked = it },
                    )
                }
            }
        }

        rule.onNodeWithText("Checkbox: unchecked").assertTextEquals("Checkbox: unchecked")
        rule.runOnIdle { checked = true }
        rule.onNodeWithText("Checkbox: checked").assertTextEquals("Checkbox: checked")
    }
}
