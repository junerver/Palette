package xyz.junerver.compose.palette.components.cascader

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import xyz.junerver.compose.palette.core.theme.PaletteMaterialTheme
import kotlin.test.Test

class CascaderUiTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun cascader_displaysPlaceholder() {
        val options = listOf(
            CascaderOption(
                value = "1",
                label = "Option 1",
                children = listOf(
                    CascaderOption(value = "1-1", label = "Option 1-1"),
                ),
            ),
        )

        rule.setContent {
            PaletteMaterialTheme {
                PCascader(
                    options = options,
                    value = emptyList(),
                    onValueChange = {},
                    placeholder = "Please select",
                )
            }
        }

        rule.onNodeWithText("Please select").assertIsDisplayed()
    }

    @Test
    fun cascader_withValueShowsSelectedLabel() {
        val options = listOf(
            CascaderOption(
                value = "1",
                label = "Option 1",
                children = listOf(
                    CascaderOption(value = "1-1", label = "Option 1-1"),
                ),
            ),
        )

        rule.setContent {
            PaletteMaterialTheme {
                PCascader(
                    options = options,
                    value = listOf("1", "1-1"),
                    onValueChange = {},
                )
            }
        }

        rule.onNodeWithText("Option 1 / Option 1-1").assertIsDisplayed()
    }
}
