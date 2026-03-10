package xyz.junerver.compose.palette.components.progress

import androidx.compose.foundation.layout.Column
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import xyz.junerver.compose.palette.core.theme.PaletteMaterialTheme
import kotlin.test.Test

class ProgressUiTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun progress_shouldClampPercentToHundred() {
        rule.setContent {
            PaletteMaterialTheme {
                PProgress(percent = 120f)
            }
        }

        rule.onNodeWithText("100%").assertTextEquals("100%")
    }

    @Test
    fun circleProgress_shouldRenderCustomFormatterText() {
        rule.setContent {
            PaletteMaterialTheme {
                Column {
                    PProgress(
                        percent = 42f,
                        formatter = { value -> "Done ${value.toInt()}" },
                    )
                }
            }
        }

        rule.onNodeWithText("Done 42").assertTextEquals("Done 42")
    }

    @Test
    fun progress_shouldRenderCustomFormatterWithClampedValue() {
        rule.setContent {
            PaletteMaterialTheme {
                PProgress(
                    percent = 180f,
                    formatter = { value -> "${value.toInt()}/100" },
                )
            }
        }

        rule.onNodeWithText("100/100").assertTextEquals("100/100")
    }

    @Test
    fun progress_shouldClampNegativePercentToZero() {
        rule.setContent {
            PaletteMaterialTheme {
                PProgress(percent = -25f)
            }
        }

        rule.onNodeWithText("0%").assertTextEquals("0%")
    }

    @Test
    fun progress_shouldHideLabelWhenFormatterIsNull() {
        rule.setContent {
            PaletteMaterialTheme {
                PProgress(
                    percent = 40f,
                    formatter = null,
                )
            }
        }

        rule.onAllNodesWithText("40%").assertCountEquals(0)
    }
}
