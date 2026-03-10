package xyz.junerver.compose.palette.components.skeleton

import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import org.junit.Rule
import xyz.junerver.compose.palette.core.theme.PaletteMaterialTheme
import kotlin.test.Test

class SkeletonUiTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun skeleton_shouldComposeAllVariants() {
        rule.setContent {
            PaletteMaterialTheme {
                Box(Modifier.testTag("root")) {
                    PSkeletonCircle()
                    PSkeletonSquare()
                    PSkeletonRectangle()
                    PSkeletonLine()
                }
            }
        }

        rule.onNodeWithTag("root")
    }
}

