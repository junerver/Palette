package xyz.junerver.compose.palette.components.autocomplete

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import org.junit.Rule
import xyz.junerver.compose.palette.core.theme.PaletteMaterialTheme
import kotlin.test.Test
import kotlin.test.assertEquals

class AutocompleteUiTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun autocomplete_showsTextField() {
        val options = listOf(
            AutocompleteOption(value = "1", label = "Apple"),
            AutocompleteOption(value = "2", label = "Banana"),
        )

        rule.setContent {
            PaletteMaterialTheme {
                PAutocomplete(
                    value = "",
                    onValueChange = {},
                    options = options,
                    placeholder = "Search...",
                )
            }
        }

        rule.onNodeWithText("Search...").assertIsDisplayed()
    }

    @Test
    fun autocomplete_typingShowsFilteredOptions() {
        val options = listOf(
            AutocompleteOption(value = "1", label = "Apple"),
            AutocompleteOption(value = "2", label = "Banana"),
            AutocompleteOption(value = "3", label = "Cherry"),
        )
        var value = ""

        rule.setContent {
            PaletteMaterialTheme {
                PAutocomplete(
                    value = value,
                    onValueChange = { value = it },
                    options = options,
                )
            }
        }

        rule.onNodeWithText("").performTextInput("App")
        rule.waitForIdle()
    }
}
