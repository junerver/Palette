package xyz.junerver.compose.palette.components.timeline

import androidx.compose.material3.Text
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import xyz.junerver.compose.palette.core.theme.PaletteMaterialTheme
import kotlin.test.Test

class TimelineUiTest {
    @get:Rule
    val rule = createComposeRule()

    private val items = listOf(
        TimelineItemData(content = { Text("Order Created") }),
        TimelineItemData(content = { Text("Order Paid") }),
        TimelineItemData(content = { Text("Order Shipped") }),
    )

    @Test
    fun timeline_shouldRenderAllTimelineContents() {
        rule.setContent {
            PaletteMaterialTheme {
                PTimeline(items = items)
            }
        }

        rule.onNodeWithText("Order Created").assertTextEquals("Order Created")
        rule.onNodeWithText("Order Paid").assertTextEquals("Order Paid")
        rule.onNodeWithText("Order Shipped").assertTextEquals("Order Shipped")
    }
}
