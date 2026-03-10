package xyz.junerver.compose.palette.components.tour

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import xyz.junerver.compose.palette.core.theme.PaletteMaterialTheme
import kotlin.test.Test
import kotlin.test.assertEquals

class TourUiTest {
    @get:Rule
    val rule = createComposeRule()

    private val steps =
        listOf(
            TourStep(id = "intro", title = "Welcome", description = "Start here"),
            TourStep(id = "finish", title = "Done"),
        )

    @Test
    fun tour_shouldRenderCurrentStepAndInvokePreviousNextCallbacks() {
        var previousClicks = 0
        var nextClicks = 0

        rule.setContent {
            PaletteMaterialTheme {
                PTour(
                    steps = steps,
                    currentIndex = 0,
                    onPrevious = { previousClicks++ },
                    onNext = { nextClicks++ },
                )
            }
        }

        rule.onNodeWithText("Welcome").assertTextEquals("Welcome")
        rule.onNodeWithText("Start here").assertTextEquals("Start here")
        rule.onNodeWithText("上一步").performClick()
        rule.onNodeWithText("下一步").performClick()

        assertEquals(1, previousClicks)
        assertEquals(1, nextClicks)
    }

    @Test
    fun tour_shouldUseFinishAndCloseActionsOnLastStep() {
        var finishClicks = 0

        rule.setContent {
            PaletteMaterialTheme {
                PTour(
                    steps = steps,
                    currentIndex = 1,
                    onFinish = { finishClicks++ },
                )
            }
        }

        rule.onNodeWithText("Done").assertTextEquals("Done")
        rule.onNodeWithText("完成").performClick()
        rule.onNodeWithText("关闭").performClick()

        assertEquals(2, finishClicks)
    }

    @Test
    fun tour_shouldRenderNothingWhenIndexOutOfBounds() {
        rule.setContent {
            PaletteMaterialTheme {
                PTour(
                    steps = steps,
                    currentIndex = 9,
                )
            }
        }

        rule.onAllNodesWithText("Welcome").assertCountEquals(0)
        rule.onAllNodesWithText("Done").assertCountEquals(0)
    }
}
