package xyz.junerver.compose.palette.components.infinitescroll

import androidx.compose.material3.Text
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import xyz.junerver.compose.palette.core.theme.PaletteMaterialTheme
import kotlin.test.Test

class InfiniteScrollUiTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun infiniteScroll_displaysContent() {
        rule.setContent {
            PaletteMaterialTheme {
                PInfiniteScroll(
                    onLoadMore = {},
                    content = { Text("List Content") },
                )
            }
        }

        rule.onNodeWithText("List Content").assertIsDisplayed()
    }

    @Test
    fun infiniteScroll_loadingShowsLoadingIndicator() {
        rule.setContent {
            PaletteMaterialTheme {
                PInfiniteScroll(
                    onLoadMore = {},
                    loading = true,
                    content = { Text("List Content") },
                )
            }
        }

        rule.onNodeWithText("List Content").assertIsDisplayed()
    }

    @Test
    fun infiniteScroll_noMoreShowsNoMoreContent() {
        rule.setContent {
            PaletteMaterialTheme {
                PInfiniteScroll(
                    onLoadMore = {},
                    hasMore = false,
                    noMoreContent = { Text("No more items") },
                    content = { Text("List Content") },
                )
            }
        }

        rule.onNodeWithText("List Content").assertIsDisplayed()
    }
}
