package xyz.junerver.compose.palette.components.mentions

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import xyz.junerver.compose.palette.core.theme.PaletteMaterialTheme
import kotlin.test.Test

class MentionsUiTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun mentions_displaysTextField() {
        val options = listOf(
            MentionsOption(value = "user1", label = "User 1"),
            MentionsOption(value = "user2", label = "User 2"),
        )

        rule.setContent {
            PaletteMaterialTheme {
                PMentions(
                    value = "",
                    onValueChange = {},
                    options = options,
                    placeholder = "Type @ to mention",
                )
            }
        }

        rule.onNodeWithText("Type @ to mention").assertIsDisplayed()
    }
}
