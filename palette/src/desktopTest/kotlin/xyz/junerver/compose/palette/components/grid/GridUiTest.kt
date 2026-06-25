package xyz.junerver.compose.palette.components.grid

import androidx.compose.material3.Text
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.unit.dp
import org.junit.Rule
import xyz.junerver.compose.palette.core.theme.PaletteMaterialTheme
import kotlin.test.Test

class GridUiTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun grid_rowWithColumnsDisplaysContent() {
        rule.setContent {
            PaletteMaterialTheme {
                PRow {
                    PCol(span = 12) { Text("Left") }
                    PCol(span = 12) { Text("Right") }
                }
            }
        }

        rule.onNodeWithText("Left").assertIsDisplayed()
        rule.onNodeWithText("Right").assertIsDisplayed()
    }

    @Test
    fun grid_rowWithGutterDisplaysContent() {
        rule.setContent {
            PaletteMaterialTheme {
                PRow(gutter = 16.dp) {
                    PCol(span = 8) { Text("Col 1") }
                    PCol(span = 8) { Text("Col 2") }
                    PCol(span = 8) { Text("Col 3") }
                }
            }
        }

        rule.onNodeWithText("Col 1").assertIsDisplayed()
        rule.onNodeWithText("Col 2").assertIsDisplayed()
        rule.onNodeWithText("Col 3").assertIsDisplayed()
    }
}
