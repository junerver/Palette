package xyz.junerver.compose.palette.components.floatbutton

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import xyz.junerver.compose.palette.core.theme.PaletteMaterialTheme
import kotlin.test.Test
import kotlin.test.assertTrue

class FloatButtonUiTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun floatButton_withTextShowsText() {
        rule.setContent {
            PaletteMaterialTheme {
                PFloatButton(
                    onClick = {},
                    text = "Add",
                )
            }
        }

        rule.onNodeWithText("Add").assertIsDisplayed()
    }

    @Test
    fun floatButton_clickCallsOnClick() {
        var clicked = false

        rule.setContent {
            PaletteMaterialTheme {
                PFloatButton(
                    onClick = { clicked = true },
                    text = "Click Me",
                )
            }
        }

        rule.onNodeWithText("Click Me").performClick()
        assertTrue(clicked)
    }

    @Test
    fun floatButton_circleShapeShowsText() {
        rule.setContent {
            PaletteMaterialTheme {
                PFloatButton(
                    onClick = {},
                    shape = FloatButtonShape.Circle,
                    text = "Circle",
                )
            }
        }

        rule.onNodeWithText("Circle").assertIsDisplayed()
    }

    @Test
    fun floatButton_squareShapeShowsText() {
        rule.setContent {
            PaletteMaterialTheme {
                PFloatButton(
                    onClick = {},
                    shape = FloatButtonShape.Square,
                    text = "Square",
                )
            }
        }

        rule.onNodeWithText("Square").assertIsDisplayed()
    }
}
