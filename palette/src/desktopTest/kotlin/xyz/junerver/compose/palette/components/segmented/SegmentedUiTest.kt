package xyz.junerver.compose.palette.components.segmented

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import xyz.junerver.compose.palette.core.theme.PaletteMaterialTheme
import kotlin.test.Test
import kotlin.test.assertEquals

class SegmentedUiTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun segmented_displaysAllOptions() {
        val options = listOf(
            SegmentedOption(value = "a", label = "Option A"),
            SegmentedOption(value = "b", label = "Option B"),
        )

        rule.setContent {
            PaletteMaterialTheme {
                PSegmented(
                    options = options,
                    value = "a",
                    onValueChange = {},
                )
            }
        }

        rule.onNodeWithText("Option A").assertIsDisplayed()
        rule.onNodeWithText("Option B").assertIsDisplayed()
    }

    @Test
    fun segmented_clickOptionCallsOnValueChange() {
        val options = listOf(
            SegmentedOption(value = "a", label = "Option A"),
            SegmentedOption(value = "b", label = "Option B"),
        )
        var selectedValue = "a"

        rule.setContent {
            PaletteMaterialTheme {
                PSegmented(
                    options = options,
                    value = selectedValue,
                    onValueChange = { selectedValue = it },
                )
            }
        }

        rule.onNodeWithText("Option B").performClick()
        assertEquals("b", selectedValue)
    }

    @Test
    fun segmented_disabledOptionNotClickable() {
        val options = listOf(
            SegmentedOption(value = "a", label = "Option A"),
            SegmentedOption(value = "b", label = "Option B", disabled = true),
        )
        var selectedValue = "a"

        rule.setContent {
            PaletteMaterialTheme {
                PSegmented(
                    options = options,
                    value = selectedValue,
                    onValueChange = { selectedValue = it },
                )
            }
        }

        rule.onNodeWithText("Option B").performClick()
        assertEquals("a", selectedValue)
    }
}
