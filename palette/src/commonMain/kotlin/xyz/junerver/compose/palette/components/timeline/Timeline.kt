package xyz.junerver.compose.palette.components.timeline

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

data class TimelineItemData(
    val content: @Composable () -> Unit,
    val dot: (@Composable () -> Unit)? = null,
    val color: Color? = null
)

@Composable
fun PTimeline(
    items: List<TimelineItemData>,
    modifier: Modifier = Modifier,
    lineColor: Color = TimelineDefaults.lineColor()
) {
    Column(modifier = modifier) {
        items.forEachIndexed { index, item ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(TimelineDefaults.dotToContent())
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.width(TimelineDefaults.dotSize())
                ) {
                    if (item.dot != null) {
                        item.dot.invoke()
                    } else {
                        Box(
                            modifier = Modifier
                                .size(TimelineDefaults.dotSize())
                                .clip(CircleShape)
                                .background(item.color ?: TimelineDefaults.dotColor())
                        )
                    }

                    if (index < items.size - 1) {
                        VerticalDivider(
                            modifier = Modifier.height(TimelineDefaults.itemSpacing()),
                            thickness = TimelineDefaults.lineWidth(),
                            color = lineColor
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(bottom = if (index < items.size - 1) TimelineDefaults.itemSpacing() else 0.dp)
                ) {
                    item.content()
                }
            }
        }
    }
}
