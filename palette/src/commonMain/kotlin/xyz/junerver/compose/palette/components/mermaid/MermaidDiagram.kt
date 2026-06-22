package xyz.junerver.compose.palette.components.mermaid

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.core.theme.PaletteTheme
import xyz.junerver.compose.palette.mermaid.MermaidEdgeStyle
import xyz.junerver.compose.palette.mermaid.MermaidLayout
import xyz.junerver.compose.palette.mermaid.MermaidLayoutEngine
import xyz.junerver.compose.palette.mermaid.MermaidParser

@Composable
fun PMermaidDiagram(
    source: String,
    modifier: Modifier = Modifier,
    colors: MermaidColors = MermaidDefaults.colors(),
    layout: MermaidLayout = MermaidLayoutEngine.layout(MermaidParser.parse(source)),
) {
    val nodeWidth = 132.dp
    val nodeHeight = 44.dp
    val width = ((layout.nodes.values.maxOfOrNull { it.x } ?: 0f) + 156f).dp
    val height = ((layout.nodes.values.maxOfOrNull { it.y } ?: 0f) + 72f).dp

    Box(
        modifier =
            modifier
                .width(width)
                .height(height),
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            layout.edges.forEach { edge ->
                val from = layout.nodes[edge.from] ?: return@forEach
                val to = layout.nodes[edge.to] ?: return@forEach
                val start = Offset(from.x + nodeWidth.toPx(), from.y + nodeHeight.toPx() / 2f)
                val end = Offset(to.x, to.y + nodeHeight.toPx() / 2f)
                drawLine(
                    color = colors.edgeColor,
                    start = start,
                    end = end,
                    strokeWidth = if (edge.style == MermaidEdgeStyle.Thick) 3.dp.toPx() else 2.dp.toPx(),
                    cap = StrokeCap.Round,
                    pathEffect =
                        if (edge.style == MermaidEdgeStyle.Dotted) {
                            PathEffect.dashPathEffect(floatArrayOf(4f, 5f))
                        } else {
                            null
                        },
                )
            }
        }

        layout.edges.forEachIndexed { index, edge ->
            val label = edge.label ?: return@forEachIndexed
            val from = layout.nodes[edge.from] ?: return@forEachIndexed
            val to = layout.nodes[edge.to] ?: return@forEachIndexed
            val labelX = ((from.x + to.x) / 2f + 44f).coerceAtLeast(0f)
            val labelY = ((from.y + to.y) / 2f + 8f + index * 18f).coerceAtLeast(0f)

            Text(
                text = label,
                color = colors.nodeContentColor,
                style = PaletteTheme.typography.label,
                modifier =
                    Modifier
                        .absoluteOffset(x = labelX.dp, y = labelY.dp)
                        .background(colors.nodeContainerColor, RoundedCornerShape(4.dp))
                        .border(1.dp, colors.edgeColor, RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp),
            )
        }

        layout.nodes.values.forEach { item ->
            Box(
                modifier =
                    Modifier
                        .absoluteOffset(x = item.x.dp, y = item.y.dp)
                        .size(width = nodeWidth, height = nodeHeight)
                        .background(colors.nodeContainerColor, RoundedCornerShape(MermaidDefaults.cornerRadius()))
                        .border(1.dp, colors.nodeBorderColor, RoundedCornerShape(MermaidDefaults.cornerRadius())),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = item.node.label,
                    color = colors.nodeContentColor,
                    style = PaletteTheme.typography.label,
                    textAlign = TextAlign.Center,
                )
            }
        }

        if (layout.nodes.isEmpty()) {
            Text(
                text = "Empty diagram",
                color = Color.Unspecified,
                style = PaletteTheme.typography.body,
                modifier = Modifier.align(Alignment.Center),
            )
        }
    }
}
