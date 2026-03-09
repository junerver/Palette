package xyz.junerver.compose.palette.components.pagination

import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import xyz.junerver.compose.palette.core.i18n.PaletteStrings
import xyz.junerver.compose.palette.core.theme.PaletteMaterialTheme
import kotlin.test.Test
import kotlin.test.assertEquals

class PaginationUiTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun pagination_shouldEmitClickedPageNumber() {
        var currentPage = 1

        rule.setContent {
            PaletteMaterialTheme {
                PPagination(
                    currentPage = currentPage,
                    totalPages = 3,
                    onPageChange = { currentPage = it },
                )
            }
        }

        rule.onNodeWithText("2").performClick()

        assertEquals(2, currentPage)
    }

    @Test
    fun pagination_shouldRenderSimpleLocalizedSummary() {
        rule.setContent {
            PaletteMaterialTheme(strings = PaletteStrings.enUS()) {
                PPagination(
                    currentPage = 2,
                    totalPages = 9,
                    simple = true,
                    onPageChange = {},
                )
            }
        }

        rule.onNodeWithText("Page 2 of 9").assertTextEquals("Page 2 of 9")
    }
}
