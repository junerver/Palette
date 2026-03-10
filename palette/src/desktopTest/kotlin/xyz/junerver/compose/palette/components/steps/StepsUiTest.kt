package xyz.junerver.compose.palette.components.steps

import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import xyz.junerver.compose.palette.core.theme.PaletteMaterialTheme
import kotlin.test.Test

class StepsUiTest {
    @get:Rule
    val rule = createComposeRule()

    private val items = listOf(
        StepItem(title = "Create account", description = "Basic profile"),
        StepItem(title = "Verify email", description = "Check your inbox"),
        StepItem(title = "Finish", description = "Start using Palette"),
    )

    @Test
    fun steps_shouldRenderTitlesDescriptionsAndIndexes() {
        rule.setContent {
            PaletteMaterialTheme {
                PSteps(
                    items = items,
                    currentStep = 1,
                )
            }
        }

        rule.onNodeWithText("1").assertTextEquals("1")
        rule.onNodeWithText("2").assertTextEquals("2")
        rule.onNodeWithText("3").assertTextEquals("3")
        rule.onNodeWithText("Create account").assertTextEquals("Create account")
        rule.onNodeWithText("Verify email").assertTextEquals("Verify email")
        rule.onNodeWithText("Finish").assertTextEquals("Finish")
        rule.onNodeWithText("Basic profile").assertTextEquals("Basic profile")
        rule.onNodeWithText("Check your inbox").assertTextEquals("Check your inbox")
        rule.onNodeWithText("Start using Palette").assertTextEquals("Start using Palette")
    }

    @Test
    fun steps_shouldRenderSingleStepWithoutDescription() {
        rule.setContent {
            PaletteMaterialTheme {
                PSteps(
                    items = listOf(StepItem(title = "Only step")),
                    currentStep = 0,
                )
            }
        }

        rule.onNodeWithText("1").assertTextEquals("1")
        rule.onNodeWithText("Only step").assertTextEquals("Only step")
    }
}
