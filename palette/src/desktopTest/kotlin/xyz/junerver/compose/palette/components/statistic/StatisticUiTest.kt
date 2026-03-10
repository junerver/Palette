package xyz.junerver.compose.palette.components.statistic

import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import xyz.junerver.compose.palette.core.theme.PaletteMaterialTheme
import kotlin.test.Test

class StatisticUiTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun statistic_shouldRenderTitleValueAndTrend() {
        rule.setContent {
            PaletteMaterialTheme {
                PStatistic(
                    title = "Completion",
                    value = "98.5",
                    suffix = "%",
                    trend = TrendType.Up,
                )
            }
        }

        rule.onNodeWithText("Completion").assertTextEquals("Completion")
        rule.onNodeWithText("98.5").assertTextEquals("98.5")
        rule.onNodeWithText("%").assertTextEquals("%")
        rule.onNodeWithText("▲").assertTextEquals("▲")
    }

    @Test
    fun statistic_shouldRenderPrefixAndValue() {
        rule.setContent {
            PaletteMaterialTheme {
                PStatistic(
                    title = "Revenue",
                    prefix = "¥",
                    value = "9,876",
                )
            }
        }

        rule.onNodeWithText("Revenue").assertTextEquals("Revenue")
        rule.onNodeWithText("¥").assertTextEquals("¥")
        rule.onNodeWithText("9,876").assertTextEquals("9,876")
    }

    @Test
    fun statistic_shouldRenderDownTrendWithoutPrefixSuffix() {
        rule.setContent {
            PaletteMaterialTheme {
                PStatistic(
                    title = "New Users",
                    value = "567",
                    trend = TrendType.Down,
                )
            }
        }

        rule.onNodeWithText("New Users").assertTextEquals("New Users")
        rule.onNodeWithText("567").assertTextEquals("567")
        rule.onNodeWithText("▼").assertTextEquals("▼")
    }
}
