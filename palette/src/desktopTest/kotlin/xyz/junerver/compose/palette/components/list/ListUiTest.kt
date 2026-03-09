package xyz.junerver.compose.palette.components.list

import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import xyz.junerver.compose.palette.components.empty.PEmpty
import xyz.junerver.compose.palette.core.theme.PaletteMaterialTheme
import kotlin.test.Test

class ListUiTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun list_shouldRenderCustomEmptyContentWhenNoData() {
        rule.setContent {
            PaletteMaterialTheme {
                PList(
                    data = emptyList<String>(),
                    emptyContent = {
                        PEmpty(title = "Custom Empty", description = "Nothing here")
                    },
                ) { item ->
                    androidx.compose.material3.Text(item)
                }
            }
        }

        rule.onNodeWithText("Custom Empty").assertTextEquals("Custom Empty")
        rule.onNodeWithText("Nothing here").assertTextEquals("Nothing here")
    }
}
