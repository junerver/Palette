package xyz.junerver.compose.palette.components.watermark

import androidx.compose.material3.Text
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import xyz.junerver.compose.palette.core.theme.PaletteMaterialTheme
import kotlin.test.Test

class WatermarkUiTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun watermark_rendersContent() {
        rule.setContent {
            PaletteMaterialTheme {
                PWatermark(text = "Watermark") {
                    Text("Content")
                }
            }
        }

        rule.onNodeWithText("Content").assertIsDisplayed()
    }

    @Test
    fun watermark_customTextShowsContent() {
        rule.setContent {
            PaletteMaterialTheme {
                PWatermark(
                    text = "Confidential",
                    rotate = -30f,
                ) {
                    Text("Secret Document")
                }
            }
        }

        rule.onNodeWithText("Secret Document").assertIsDisplayed()
    }
}
