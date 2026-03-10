package xyz.junerver.compose.palette.components.image

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import org.junit.Rule
import xyz.junerver.compose.palette.core.theme.PaletteMaterialTheme
import kotlin.test.Test

class ImageUiTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun image_shouldRenderPainterAndSlotContent() {
        rule.setContent {
            PaletteMaterialTheme {
                PImage(
                    painter = ColorPainter(Color.Red),
                    contentDescription = "Profile image",
                    placeholder = { Text("Placeholder") },
                    error = { Text("Error overlay") },
                )
            }
        }

        rule.onAllNodesWithContentDescription("Profile image").assertCountEquals(1)
        rule.onNodeWithText("Placeholder").assertTextEquals("Placeholder")
        rule.onNodeWithText("Error overlay").assertTextEquals("Error overlay")
    }

    @Test
    fun image_shouldOpenPreviewPopupWhenPreviewable() {
        rule.setContent {
            PaletteMaterialTheme {
                PImage(
                    painter = ColorPainter(Color.Blue),
                    contentDescription = "Preview image",
                    previewable = true,
                    modifier = Modifier.size(64.dp),
                )
            }
        }

        rule.onAllNodesWithContentDescription("Preview image").assertCountEquals(1)
        rule.onNodeWithContentDescription("Preview image").performClick()
        rule.onAllNodesWithContentDescription("Preview image").assertCountEquals(2)
    }
}
