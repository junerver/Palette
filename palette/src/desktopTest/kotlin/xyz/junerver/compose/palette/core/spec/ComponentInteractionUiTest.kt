package xyz.junerver.compose.palette.core.spec

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import xyz.junerver.compose.palette.core.theme.PaletteMaterialTheme
import kotlin.test.Test

class ComponentInteractionUiTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun rememberComponentInteraction_shouldUseProvidedInteractionSource() {
        val provided = MutableInteractionSource()

        rule.setContent {
            PaletteMaterialTheme {
                val interaction =
                    rememberComponentInteraction(
                        enabled = false,
                        interactionSource = provided,
                    )

                Text("enabled=${interaction.enabled}")
                Text("sameSource=${interaction.interactionSource === provided}")
            }
        }

        rule.onNodeWithText("enabled=false").assertTextEquals("enabled=false")
        rule.onNodeWithText("sameSource=true").assertTextEquals("sameSource=true")
    }

    @Test
    fun rememberComponentInteraction_shouldRememberCreatedSourceWhenNull() {
        val tick = mutableStateOf(0)

        rule.setContent {
            PaletteMaterialTheme {
                val interaction =
                    rememberComponentInteraction(
                        enabled = true,
                        interactionSource = null,
                    )

                val firstSource = remember { interaction.interactionSource }
                Text("sameSource=${firstSource === interaction.interactionSource} tick=${tick.value}")
            }
        }

        rule.onNodeWithText("sameSource=true tick=0").assertTextEquals("sameSource=true tick=0")

        rule.runOnIdle { tick.value++ }

        rule.onNodeWithText("sameSource=true tick=1").assertTextEquals("sameSource=true tick=1")
    }
}

