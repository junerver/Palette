package xyz.junerver.compose.palette.components.searchbar

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.milliseconds
import org.junit.Rule
import xyz.junerver.compose.palette.core.theme.PaletteMaterialTheme

class SearchBarUiTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun searchBar_shouldRenderPlaceholderAndAcceptInputImmediately() {
        var value by mutableStateOf("")

        rule.setContent {
            PaletteMaterialTheme {
                Column {
                    PSearchBar(
                        value = value,
                        onValueChange = { value = it },
                        placeholder = "Search components",
                    )
                    Text("Value: $value")
                }
            }
        }

        rule.onNodeWithText("Search components").assertExists()
        rule.onNode(hasSetTextAction()).performTextInput("Button")
        rule.onNodeWithText("Value: Button").assertTextEquals("Value: Button")
    }

    @Test
    fun searchBar_shouldClearValueWhenClearButtonClicked() {
        var value by mutableStateOf("hello")

        rule.setContent {
            PaletteMaterialTheme {
                Column {
                    PSearchBar(
                        value = value,
                        onValueChange = { value = it },
                    )
                    Text("Value: $value")
                }
            }
        }

        rule.onNodeWithText("Value: hello").assertTextEquals("Value: hello")
        rule.onNodeWithContentDescription("Clear").performClick()
        rule.onNodeWithText("Value: ").assertTextEquals("Value: ")
    }

    @Test
    fun searchBar_shouldSubmitDebouncedValueWhenEnabled() {
        var value by mutableStateOf("")
        val searches = mutableListOf<String>()

        rule.setContent {
            PaletteMaterialTheme {
                Column {
                    PSearchBar(
                        value = value,
                        onValueChange = { value = it },
                        debounce = true,
                        debounceWait = 0.milliseconds,
                        onSearch = { searches += it },
                    )
                    Text("Value: $value")
                }
            }
        }

        rule.runOnIdle {
            assertEquals(emptyList(), searches)
        }

        rule.onNode(hasSetTextAction()).performTextInput("Palette")
        rule.onNodeWithText("Value: Palette").assertTextEquals("Value: Palette")
        rule.waitForIdle()
        rule.runOnIdle {
            assertEquals(listOf("Palette"), searches)
        }
    }
}
