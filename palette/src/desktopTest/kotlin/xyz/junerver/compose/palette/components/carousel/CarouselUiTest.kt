package xyz.junerver.compose.palette.components.carousel

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import org.junit.Rule
import xyz.junerver.compose.palette.core.theme.PaletteMaterialTheme
import kotlin.test.Test

class CarouselUiTest {
    @get:Rule
    val rule = createComposeRule()

    private val slides = listOf("Slide 1", "Slide 2", "Slide 3")

    @Test
    fun carousel_shouldRenderIndicatorsAndArrowControls() {
        rule.setContent {
            PaletteMaterialTheme {
                Box(modifier = Modifier.size(320.dp, 180.dp)) {
                    PCarousel(
                        items = slides,
                        autoPlay = false,
                    ) { slide ->
                        Box(modifier = Modifier.fillMaxSize()) {
                            Text(slide)
                        }
                    }
                }
            }
        }

        rule.onAllNodesWithText("Slide 1").assertCountEquals(1)
        rule.onNodeWithContentDescription("Previous")
        rule.onNodeWithContentDescription("Next")
        rule.onNodeWithContentDescription("Go to slide 1")
        rule.onNodeWithContentDescription("Go to slide 2")
        rule.onNodeWithContentDescription("Go to slide 3")
    }

    @Test
    fun carousel_shouldNavigateToFarSlideWhenIndicatorClicked() {
        rule.setContent {
            PaletteMaterialTheme {
                Box(modifier = Modifier.size(320.dp, 180.dp)) {
                    PCarousel(
                        items = slides,
                        autoPlay = false,
                        showArrows = false,
                    ) { slide ->
                        Box(modifier = Modifier.fillMaxSize()) {
                            Text(slide)
                        }
                    }
                }
            }
        }

        rule.onAllNodesWithText("Slide 3").assertCountEquals(0)
        rule.onNodeWithContentDescription("Go to slide 3").performClick()
        rule.waitForIdle()
        rule.onAllNodesWithText("Slide 3").assertCountEquals(1)
    }

    @Test
    fun carousel_shouldHideControlsWhenOnlyOneItem() {
        rule.setContent {
            PaletteMaterialTheme {
                Box(modifier = Modifier.size(320.dp, 180.dp)) {
                    PCarousel(
                        items = listOf("Only Slide"),
                        autoPlay = false,
                    ) { slide ->
                        Box(modifier = Modifier.fillMaxSize()) {
                            Text(slide)
                        }
                    }
                }
            }
        }

        rule.onAllNodesWithText("Only Slide").assertCountEquals(1)
        rule.onAllNodesWithContentDescription("Previous").assertCountEquals(0)
        rule.onAllNodesWithContentDescription("Next").assertCountEquals(0)
        rule.onAllNodesWithContentDescription("Go to slide 1").assertCountEquals(0)
    }

    @Test
    fun carousel_shouldRenderNothingWhenItemsEmpty() {
        rule.setContent {
            PaletteMaterialTheme {
                Box(modifier = Modifier.size(320.dp, 180.dp)) {
                    PCarousel(
                        items = emptyList<String>(),
                        autoPlay = false,
                    ) { slide ->
                        Box(modifier = Modifier.fillMaxSize()) {
                            Text(slide)
                        }
                    }
                }
            }
        }

        rule.onAllNodesWithText("Slide 1").assertCountEquals(0)
        rule.onAllNodesWithContentDescription("Previous").assertCountEquals(0)
        rule.onAllNodesWithContentDescription("Next").assertCountEquals(0)
    }
}
