package xyz.junerver.compose.palette.components.breadcrumb

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import xyz.junerver.compose.palette.core.theme.PaletteMaterialTheme
import kotlin.test.Test
import kotlin.test.assertEquals

class BreadcrumbUiTest {
    @get:Rule
    val rule = createComposeRule()

    private val items = listOf(
        BreadcrumbItem(key = "home", label = "Home"),
        BreadcrumbItem(key = "library", label = "Library"),
        BreadcrumbItem(key = "book", label = "Book"),
    )

    @Test
    fun breadcrumb_shouldRenderLabelsAndCustomSeparator() {
        rule.setContent {
            PaletteMaterialTheme {
                PBreadcrumb(
                    items = items,
                    separator = ">",
                )
            }
        }

        rule.onNodeWithText("Home").assertTextEquals("Home")
        rule.onNodeWithText("Library").assertTextEquals("Library")
        rule.onNodeWithText("Book").assertTextEquals("Book")
        rule.onAllNodesWithText(">", useUnmergedTree = true).assertCountEquals(2)
    }

    @Test
    fun breadcrumb_shouldInvokeClickForIntermediateItem() {
        var selectedKey: String? = null

        rule.setContent {
            PaletteMaterialTheme {
                PBreadcrumb(
                    items = items,
                    onClick = { selectedKey = it },
                )
            }
        }

        rule.onNodeWithText("Library").performClick()
        assertEquals("library", selectedKey)
    }
}
