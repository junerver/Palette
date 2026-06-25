package xyz.junerver.compose.palette.components.popup

import androidx.compose.material3.Text
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import xyz.junerver.compose.palette.core.theme.PaletteMaterialTheme
import kotlin.test.Test

class PopupUiTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun popup_visibleShowsContent() {
        rule.setContent {
            PaletteMaterialTheme {
                PPopup(
                    visible = true,
                    onClose = {},
                    content = { Text("Popup Content") },
                )
            }
        }

        rule.onNodeWithText("Popup Content").assertIsDisplayed()
    }

    @Test
    fun popup_visibleWithTitleShowsTitle() {
        rule.setContent {
            PaletteMaterialTheme {
                PPopup(
                    visible = true,
                    onClose = {},
                    title = "Test Title",
                    content = { Text("Popup Content") },
                )
            }
        }

        rule.onNodeWithText("Test Title").assertIsDisplayed()
        rule.onNodeWithText("Popup Content").assertIsDisplayed()
    }
}
