package xyz.junerver.compose.palette.components.select

import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import xyz.junerver.compose.palette.core.theme.PaletteMaterialTheme
import kotlin.test.Test

class SelectUiTest {
    @get:Rule
    val rule = createComposeRule()

    private val options =
        listOf(
            SelectOption(label = "Pending", value = "pending"),
            SelectOption(label = "Approved", value = "approved"),
            SelectOption(label = "Archived", value = "archived", disabled = true),
        )

    @Test
    fun select_shouldRenderPlaceholderBeforeSelection() {
        rule.setContent {
            PaletteMaterialTheme {
                PSelect(
                    options = options,
                    value = null,
                    onValueChange = {},
                    placeholder = "Choose status",
                )
            }
        }

        rule.onNodeWithText("Choose status").assertTextEquals("Choose status")
    }

    @Test
    fun select_shouldRenderSelectedOptionLabel() {
        rule.setContent {
            PaletteMaterialTheme {
                PSelect(
                    options = options,
                    value = "approved",
                    onValueChange = {},
                    placeholder = "Choose status",
                )
            }
        }

        rule.onNodeWithText("Approved").assertTextEquals("Approved")
    }

    @Test
    fun select_shouldFallbackToPlaceholderWhenValueMissing() {
        rule.setContent {
            PaletteMaterialTheme {
                PSelect(
                    options = options,
                    value = "unknown",
                    onValueChange = {},
                    placeholder = "Choose status",
                )
            }
        }

        rule.onNodeWithText("Choose status").assertTextEquals("Choose status")
    }
}
