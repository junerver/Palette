package xyz.junerver.compose.palette.components.affix

import androidx.compose.material3.Text
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import xyz.junerver.compose.palette.core.theme.PaletteMaterialTheme
import kotlin.test.Test

class AffixUiTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun affix_rendersContent() {
        rule.setContent {
            PaletteMaterialTheme {
                PAffix {
                    Text("Affixed Content")
                }
            }
        }

        rule.onNodeWithText("Affixed Content").assertIsDisplayed()
    }

    @Test
    fun affix_withTopPositionRendersContent() {
        rule.setContent {
            PaletteMaterialTheme {
                PAffix(position = AffixPosition.Top) {
                    Text("Top Affix")
                }
            }
        }

        rule.onNodeWithText("Top Affix").assertIsDisplayed()
    }
}
