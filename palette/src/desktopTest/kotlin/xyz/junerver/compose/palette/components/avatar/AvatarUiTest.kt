package xyz.junerver.compose.palette.components.avatar

import androidx.compose.material3.Text
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import xyz.junerver.compose.palette.core.theme.PaletteMaterialTheme
import kotlin.test.Test

class AvatarUiTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun avatar_shouldRenderUppercaseInitialFromText() {
        rule.setContent {
            PaletteMaterialTheme {
                PAvatar(text = "alice")
            }
        }

        rule.onNodeWithText("A").assertTextEquals("A")
    }

    @Test
    fun avatar_shouldPreferCustomContentOverFallbackText() {
        rule.setContent {
            PaletteMaterialTheme {
                PAvatar(
                    text = "alice",
                    content = { Text("VIP") },
                )
            }
        }

        rule.onNodeWithText("VIP").assertTextEquals("VIP")
    }
}
