package xyz.junerver.compose.palette.components.loading

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import xyz.junerver.compose.palette.core.theme.PaletteMaterialTheme
import kotlin.test.Test

class LoadingUiTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun loadingVariants_shouldComposeWithoutErrors() {
        rule.setContent {
            PaletteMaterialTheme {
                Column {
                    Text("Default")
                    PLoading()
                    Text("Dots")
                    PLoadingDots(width = LoadingDefaults.MinDotsWidth)
                    Text("Bars")
                    PLoadingBars()
                    Text("Circle")
                    PLoadingCircle()
                    Text("Bounce")
                    PLoadingBounce(width = LoadingDefaults.MPWidth)
                }
            }
        }

        rule.onNodeWithText("Default").assertTextEquals("Default")
        rule.onNodeWithText("Dots").assertTextEquals("Dots")
        rule.onNodeWithText("Bars").assertTextEquals("Bars")
        rule.onNodeWithText("Circle").assertTextEquals("Circle")
        rule.onNodeWithText("Bounce").assertTextEquals("Bounce")
    }
}
