package xyz.junerver.compose.palette.components.badge

import androidx.compose.material3.Text
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import xyz.junerver.compose.palette.core.theme.PaletteMaterialTheme
import kotlin.test.Test

class BadgeUiTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun badge_shouldRenderContentWithHolder() {
        rule.setContent {
            PaletteMaterialTheme {
                PBadge(
                    content = "8",
                    holder = { Text("Inbox") },
                )
            }
        }

        rule.onNodeWithText("Inbox").assertTextEquals("Inbox")
        rule.onNodeWithText("8").assertTextEquals("8")
    }

    @Test
    fun badge_shouldRenderStandaloneLabel() {
        rule.setContent {
            PaletteMaterialTheme {
                PBadge(content = "New")
            }
        }

        rule.onNodeWithText("New").assertTextEquals("New")
    }
}
