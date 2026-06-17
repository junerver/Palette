package xyz.junerver.compose.palette.components.avatar

import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
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

    @Test
    fun avatar_shouldRenderPainterImage() {
        rule.setContent {
            PaletteMaterialTheme {
                PAvatar(
                    painter = ColorPainter(Color.Red),
                    contentDescription = "Profile image",
                    text = "alice",
                )
            }
        }

        rule.onAllNodesWithContentDescription("Profile image").assertCountEquals(1)
    }

    @Test
    fun avatar_shouldPreferCustomContentOverPainterImage() {
        rule.setContent {
            PaletteMaterialTheme {
                PAvatar(
                    painter = ColorPainter(Color.Red),
                    contentDescription = "Profile image",
                    text = "alice",
                    content = { Text("VIP") },
                )
            }
        }

        rule.onAllNodesWithContentDescription("Profile image").assertCountEquals(0)
        rule.onNodeWithText("VIP").assertTextEquals("VIP")
    }

    @Test
    fun avatar_shouldRenderWithBuiltInShapes() {
        rule.setContent {
            PaletteMaterialTheme {
                PAvatar(text = "circle", shape = AvatarDefaults.shape(AvatarShape.Circle))
                PAvatar(text = "square", shape = AvatarDefaults.shape(AvatarShape.Square))
                PAvatar(text = "rounded", shape = AvatarDefaults.shape(AvatarShape.RoundedRectangle))
            }
        }

        rule.onNodeWithText("C").assertTextEquals("C")
        rule.onNodeWithText("S").assertTextEquals("S")
        rule.onNodeWithText("R").assertTextEquals("R")
    }
}
