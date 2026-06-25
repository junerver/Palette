package xyz.junerver.compose.palette.components.code

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import xyz.junerver.compose.palette.core.theme.PaletteMaterialTheme
import kotlin.test.Test

class CodeBlockUiTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun codeBlock_displaysCode() {
        val code = "val x = 42"

        rule.setContent {
            PaletteMaterialTheme {
                PCodeBlock(
                    code = code,
                    language = "kotlin",
                )
            }
        }

        rule.onNodeWithText("val x = 42").assertIsDisplayed()
    }

    @Test
    fun codeBlock_withTitleShowsTitle() {
        val code = "val x = 42"

        rule.setContent {
            PaletteMaterialTheme {
                PCodeBlock(
                    code = code,
                    title = "Example",
                )
            }
        }

        rule.onNodeWithText("Example").assertIsDisplayed()
        rule.onNodeWithText("val x = 42").assertIsDisplayed()
    }

    @Test
    fun codeBlock_withLineNumbersShowsLineNumbers() {
        val code = "line 1\nline 2\nline 3"

        rule.setContent {
            PaletteMaterialTheme {
                PCodeBlock(
                    code = code,
                    showLineNumbers = true,
                )
            }
        }

        rule.onNodeWithText("line 1").assertIsDisplayed()
    }
}
