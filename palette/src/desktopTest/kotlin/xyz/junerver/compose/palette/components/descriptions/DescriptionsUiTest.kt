package xyz.junerver.compose.palette.components.descriptions

import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import xyz.junerver.compose.palette.core.theme.PaletteMaterialTheme
import kotlin.test.Test

class DescriptionsUiTest {
    @get:Rule
    val rule = createComposeRule()

    private val items = listOf(
        DescriptionItem(label = "Username", content = "Zhang San"),
        DescriptionItem(label = "Email", content = "zhangsan@example.com"),
        DescriptionItem(label = "Address", content = "Chaoyang District"),
    )

    @Test
    fun descriptions_shouldRenderAllLabelsAndContents() {
        rule.setContent {
            PaletteMaterialTheme {
                PDescriptions(
                    items = items,
                    column = 2,
                )
            }
        }

        rule.onNodeWithText("Username").assertTextEquals("Username")
        rule.onNodeWithText("Zhang San").assertTextEquals("Zhang San")
        rule.onNodeWithText("Email").assertTextEquals("Email")
        rule.onNodeWithText("zhangsan@example.com").assertTextEquals("zhangsan@example.com")
        rule.onNodeWithText("Address").assertTextEquals("Address")
        rule.onNodeWithText("Chaoyang District").assertTextEquals("Chaoyang District")
    }
}
