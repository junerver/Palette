package xyz.junerver.compose.palette.components.pageheader

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import xyz.junerver.compose.palette.core.theme.PaletteMaterialTheme
import kotlin.test.Test

class PageHeaderUiTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun pageHeader_displaysTitle() {
        rule.setContent {
            PaletteMaterialTheme {
                PPageHeader(
                    title = "Page Title",
                    onBack = {},
                )
            }
        }

        rule.onNodeWithText("Page Title").assertIsDisplayed()
    }

    @Test
    fun pageHeader_withSubtitleShowsSubtitle() {
        rule.setContent {
            PaletteMaterialTheme {
                PPageHeader(
                    title = "Page Title",
                    subtitle = "Page Subtitle",
                    onBack = {},
                )
            }
        }

        rule.onNodeWithText("Page Title").assertIsDisplayed()
        rule.onNodeWithText("Page Subtitle").assertIsDisplayed()
    }
}
