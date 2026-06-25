package xyz.junerver.compose.palette.components.cascaderpanel

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import xyz.junerver.compose.palette.components.cascader.CascaderOption
import xyz.junerver.compose.palette.core.theme.PaletteMaterialTheme
import kotlin.test.Test

class CascaderPanelUiTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun cascaderPanel_displaysOptions() {
        val options = listOf(
            CascaderOption(
                value = "1",
                label = "Option 1",
                children = listOf(
                    CascaderOption(value = "1-1", label = "Option 1-1"),
                    CascaderOption(value = "1-2", label = "Option 1-2"),
                ),
            ),
            CascaderOption(
                value = "2",
                label = "Option 2",
                children = listOf(
                    CascaderOption(value = "2-1", label = "Option 2-1"),
                ),
            ),
        )

        rule.setContent {
            PaletteMaterialTheme {
                PCascaderPanel(
                    options = options,
                    value = emptyList(),
                    onValueChange = {},
                )
            }
        }

        rule.onNodeWithText("Option 1").assertIsDisplayed()
        rule.onNodeWithText("Option 2").assertIsDisplayed()
    }

    @Test
    fun cascaderPanel_withValueShowsSelectedPath() {
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
                PCascaderPanel(
                    options = options,
                    value = listOf("1", "1-1"),
                    onValueChange = {},
                )
            }
        }

        rule.onNodeWithText("Option 1").assertIsDisplayed()
        rule.onNodeWithText("Option 1-1").assertIsDisplayed()
    }
}
