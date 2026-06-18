package xyz.junerver.compose.palette.components.swipe

import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import xyz.junerver.compose.palette.components.text.PText
import xyz.junerver.compose.palette.core.theme.PaletteMaterialTheme
import kotlin.test.Test

class SwipeToDismissBoxUiTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun swipeToDismissBox_shouldRenderContent() {
        rule.setContent {
            PaletteMaterialTheme {
                PSwipeToDismissBox(
                    onDismiss = { false },
                    backgroundContent = { PText("Delete") },
                    content = { PText("History item") }
                )
            }
        }

        rule.onNodeWithText("History item").assertTextEquals("History item")
    }
}
