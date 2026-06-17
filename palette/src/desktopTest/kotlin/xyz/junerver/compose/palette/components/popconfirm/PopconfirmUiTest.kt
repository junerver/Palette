package xyz.junerver.compose.palette.components.popconfirm

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.click
import org.junit.Rule
import xyz.junerver.compose.palette.components.button.ButtonType
import xyz.junerver.compose.palette.components.button.PButton
import xyz.junerver.compose.palette.core.theme.PaletteMaterialTheme
import kotlin.test.Test
import kotlin.test.assertEquals

class PopconfirmUiTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun popconfirm_shouldOpenWhenTriggerIsClickableButton() {
        var confirmCount = 0

        rule.setContent {
            PaletteMaterialTheme {
                PPopconfirm(
                    title = "Delete item?",
                    description = "This cannot be undone",
                    onConfirm = { confirmCount += 1 },
                ) {
                    PButton(text = "Delete", type = ButtonType.DANGER) {}
                }
            }
        }

        rule.onNodeWithText("Delete").performTouchInput { click(center) }

        rule.onNodeWithText("Delete item?").assertTextEquals("Delete item?")
        rule.onNodeWithText("This cannot be undone").assertTextEquals("This cannot be undone")

        rule.onNodeWithText("确定").performClick()

        assertEquals(1, confirmCount)
        rule.onAllNodesWithText("Delete item?").assertCountEquals(0)
    }
}
