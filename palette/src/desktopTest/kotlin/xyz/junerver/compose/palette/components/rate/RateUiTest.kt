package xyz.junerver.compose.palette.components.rate

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.platform.testTag
import org.junit.Rule
import xyz.junerver.compose.palette.core.theme.PaletteMaterialTheme
import kotlin.test.Test

class RateUiTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun rate_shouldRenderHostStateText() {
        rule.setContent {
            PaletteMaterialTheme {
                Column {
                    PRate(
                        value = 3.5f,
                        allowHalf = true,
                        modifier = Modifier.testTag("rate"),
                    )
                    Text("Rating: 3.5")
                }
            }
        }

        rule.onNodeWithTag("rate")
        rule.onNodeWithText("Rating: 3.5").assertTextEquals("Rating: 3.5")
    }

    @Test
    fun rate_shouldSupportDifferentStarCounts() {
        rule.setContent {
            PaletteMaterialTheme {
                Column {
                    PRate(
                        value = 7f,
                        count = 10,
                        modifier = Modifier.testTag("rate10"),
                    )
                    Text("Count: 10")
                }
            }
        }

        rule.onNodeWithTag("rate10")
        rule.onNodeWithText("Count: 10").assertTextEquals("Count: 10")
    }

    @Test
    fun rate_whenOnChangeNull_shouldStillCompose() {
        rule.setContent {
            PaletteMaterialTheme {
                Column {
                    PRate(
                        value = 1f,
                        onChange = null,
                        modifier = Modifier.testTag("rateReadonly"),
                    )
                    Text("Readonly")
                }
            }
        }

        rule.onNodeWithTag("rateReadonly")
        rule.onAllNodesWithText("Readonly").assertCountEquals(1)
    }
}

