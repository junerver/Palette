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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.core.theme.PaletteTheme
import xyz.junerver.compose.palette.mermaid.MermaidDiagramType
import xyz.junerver.compose.palette.mermaid.MermaidEdgeStyle
import xyz.junerver.compose.palette.mermaid.MermaidLayout
import xyz.junerver.compose.palette.mermaid.MermaidLayoutEngine
import xyz.junerver.compose.palette.mermaid.MermaidNodeShape
import xyz.junerver.compose.palette.mermaid.MermaidNote
import xyz.junerver.compose.palette.mermaid.MermaidNotePosition
import xyz.junerver.compose.palette.mermaid.MermaidParser
import kotlin.math.sqrt

internal enum class MermaidNodeContainerKind {
    Rectangle,
    Rounded,
    Stadium,
    Diamond,
    Circle,
}

internal fun MermaidNodeShape.toContainerKind(): MermaidNodeContainerKind =
    when (this) {
        MermaidNodeShape.Rectangle -> MermaidNodeContainerKind.Rectangle
        MermaidNodeShape.Rounded -> MermaidNodeContainerKind.Rounded
        MermaidNodeShape.Stadium -> MermaidNodeContainerKind.Stadium
        MermaidNodeShape.Diamond -> MermaidNodeContainerKind.Diamond
        MermaidNodeShape.Circle -> MermaidNodeContainerKind.Circle
    }

internal data class MermaidArrowHead(
    val tip: Offset,
    val left: Offset,
    val right: Offset,
)

internal fun calculateMermaidArrowHead(
    start: Offset,
    end: Offset,
    size: Float = 10f,
    spread: Float = 0.55f,
): MermaidArrowHead {
    val dx = end.x - start.x
    val dy = end.y - start.y
    val length = sqrt(dx * dx + dy * dy)
    if (length == 0f) return MermaidArrowHead(tip = end, left = end, right = end)

    val unitX = dx / length
    val unitY = dy / length
    val base = Offset(
        x = end.x - unitX * size,
        y = end.y - unitY * size,
    )
    val perpendicular = Offset(x = -unitY, y = unitX)
    return MermaidArrowHead(
        tip = end,
        left =
            Offset(
                x = base.x + perpendicular.x * size * spread,
                y = base.y + perpendicular.y * size * spread,
            ),
        right =
            Offset(
                x = base.x - perpendicular.x * size * spread,
                y = base.y - perpendicular.y * size * spread,
            ),
    )
}

@Composable
fun PMermaidDiagram(
    source: String,
    modifier: Modifier = Modifier,
    colors: MermaidColors = MermaidDefaults.colors(),
    layout: MermaidLayout = MermaidLayoutEngine.layout(MermaidParser.parse(source)),
) {
    when (layout.type) {
        MermaidDiagramType.Flowchart ->
            FlowchartMermaidDiagram(
                modifier = modifier,
                colors = colors,
                layout = layout,
            )

        MermaidDiagramType.Sequence ->
            SequenceMermaidDiagram(
                modifier = modifier,
                colors = colors,
                layout = layout,
            )
    }
}

@Composable
private fun FlowchartMermaidDiagram(
    modifier: Modifier,
    colors: MermaidColors,
    layout: MermaidLayout,
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
                drawMermaidArrowHead(
                    color = colors.edgeColor,
                    arrowHead =
                        calculateMermaidArrowHead(
                            start = start,
                            end = end,
                            size = 10.dp.toPx(),
                        ),
                    strokeWidth = if (edge.style == MermaidEdgeStyle.Thick) 3.dp.toPx() else 2.dp.toPx(),
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
            val nodeShape = item.node.shape.toComposeShape()
            Box(
                modifier =
                    Modifier
                        .absoluteOffset(x = item.x.dp, y = item.y.dp)
                        .size(width = nodeWidth, height = nodeHeight)
                        .background(colors.nodeContainerColor, nodeShape)
                        .border(1.dp, colors.nodeBorderColor, nodeShape)
                        .padding(horizontal = if (item.node.shape == MermaidNodeShape.Diamond) 20.dp else 8.dp),
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

@Composable
private fun SequenceMermaidDiagram(
    modifier: Modifier,
    colors: MermaidColors,
    layout: MermaidLayout,
) {
    val nodeWidth = 132.dp
    val nodeHeight = 44.dp
    val messageStartY = 88f
    val messageGap = 56f
    val sequenceItemCount =
        (
            layout.edges.map { it.sequenceIndex } +
                layout.notes.map { it.sequenceIndex }
        ).maxOrNull()?.plus(1) ?: 1
    val maxNodeRight = (layout.nodes.values.maxOfOrNull { it.x } ?: 0f) + 156f
    val maxNoteRight =
        layout.notes
            .mapNotNull { it.sequenceNoteFrame(layout = layout, nodeWidth = 132f) }
            .maxOfOrNull { it.x + it.width + 24f }
            ?: 0f
    val width = maxOf(maxNodeRight, maxNoteRight).dp
    val height = (messageStartY + sequenceItemCount.coerceAtLeast(1) * messageGap + 56f).dp

    Box(
        modifier =
            modifier
                .width(width)
                .height(height),
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val nodeWidthPx = nodeWidth.toPx()
            val nodeHeightPx = nodeHeight.toPx()
            val messageStartYPx = messageStartY.dp.toPx()
            val messageGapPx = messageGap.dp.toPx()

            layout.nodes.values.forEach { item ->
                val x = item.x.dp.toPx() + nodeWidthPx / 2f
                drawLine(
                    color = colors.edgeColor.copy(alpha = 0.45f),
                    start = Offset(x, nodeHeightPx + 10.dp.toPx()),
                    end = Offset(x, size.height),
                    strokeWidth = 1.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f)),
                )
            }

            layout.edges.forEachIndexed { index, edge ->
                val from = layout.nodes[edge.from] ?: return@forEachIndexed
                val to = layout.nodes[edge.to] ?: return@forEachIndexed
                val y = messageStartYPx + edge.sequenceIndex * messageGapPx
                val startX = from.x.dp.toPx() + nodeWidthPx / 2f
                val endX = to.x.dp.toPx() + nodeWidthPx / 2f
                val start = Offset(startX, y)
                val end = Offset(endX, y)
                val pathEffect =
                    if (edge.style == MermaidEdgeStyle.Dotted) {
                        PathEffect.dashPathEffect(floatArrayOf(4f, 5f))
                    } else {
                        null
                    }

                drawLine(
                    color = colors.edgeColor,
                    start = start,
                    end = end,
                    strokeWidth = if (edge.style == MermaidEdgeStyle.Thick) 3.dp.toPx() else 2.dp.toPx(),
                    cap = StrokeCap.Round,
                    pathEffect = pathEffect,
                )
                drawMermaidArrowHead(
                    color = colors.edgeColor,
                    arrowHead =
                        calculateMermaidArrowHead(
                            start = start,
                            end = end,
                            size = 10.dp.toPx(),
                        ),
                    strokeWidth = if (edge.style == MermaidEdgeStyle.Thick) 3.dp.toPx() else 2.dp.toPx(),
                )
            }
        }

        layout.edges.forEachIndexed { index, edge ->
            val label = edge.label ?: return@forEachIndexed
            val from = layout.nodes[edge.from] ?: return@forEachIndexed
            val to = layout.nodes[edge.to] ?: return@forEachIndexed
            val left = minOf(from.x, to.x)
            val right = maxOf(from.x, to.x)
            val labelWidth = (right - left).coerceAtLeast(132f)

            Text(
                text = label,
                color = colors.nodeContentColor,
                style = PaletteTheme.typography.label,
                textAlign = TextAlign.Center,
                modifier =
                    Modifier
                        .absoluteOffset(
                            x = (left + 66f - labelWidth / 2f).coerceAtLeast(0f).dp,
                            y = (messageStartY + edge.sequenceIndex * messageGap - 30f).coerceAtLeast(48f).dp,
                        )
                        .width(labelWidth.dp)
                        .background(colors.nodeContainerColor, RoundedCornerShape(4.dp))
                        .border(1.dp, colors.edgeColor, RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp),
            )
        }

        layout.notes.forEach { note ->
            SequenceNote(
                note = note,
                layout = layout,
                colors = colors,
                nodeWidth = 132f,
                messageStartY = messageStartY,
                messageGap = messageGap,
            )
        }

        layout.nodes.values.forEach { item ->
            val nodeShape = item.node.shape.toComposeShape()
            Box(
                modifier =
                    Modifier
                        .absoluteOffset(x = item.x.dp, y = item.y.dp)
                        .size(width = nodeWidth, height = nodeHeight)
                        .background(colors.nodeContainerColor, nodeShape)
                        .border(1.dp, colors.nodeBorderColor, nodeShape)
                        .padding(horizontal = 8.dp),
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

@Composable
private fun SequenceNote(
    note: MermaidNote,
    layout: MermaidLayout,
    colors: MermaidColors,
    nodeWidth: Float,
    messageStartY: Float,
    messageGap: Float,
) {
    val frame = note.sequenceNoteFrame(layout = layout, nodeWidth = nodeWidth) ?: return
    val noteY = messageStartY + note.sequenceIndex * messageGap - 24f

    Text(
        text = note.text,
        color = colors.nodeContentColor,
        style = PaletteTheme.typography.label,
        textAlign = TextAlign.Center,
        modifier =
            Modifier
                .absoluteOffset(x = frame.x.dp, y = noteY.dp)
                .width(frame.width.dp)
                .background(colors.nodeContainerColor, RoundedCornerShape(4.dp))
                .border(1.dp, colors.nodeBorderColor, RoundedCornerShape(4.dp))
                .padding(horizontal = 8.dp, vertical = 6.dp),
    )
}

private data class SequenceNoteFrame(
    val x: Float,
    val width: Float,
)

private fun MermaidNote.sequenceNoteFrame(
    layout: MermaidLayout,
    nodeWidth: Float,
): SequenceNoteFrame? {
    val participantPositions =
        participants
            .mapNotNull { layout.nodes[it] }
            .takeIf { it.isNotEmpty() }
            ?: return null
    val noteWidth =
        when (position) {
            MermaidNotePosition.Over -> {
                val left = participantPositions.minOf { it.x }
                val right = participantPositions.maxOf { it.x }
                (right - left + nodeWidth).coerceAtLeast(132f)
            }

            MermaidNotePosition.LeftOf,
            MermaidNotePosition.RightOf,
            -> 132f
        }
    val noteX =
        when (position) {
            MermaidNotePosition.Over -> participantPositions.minOf { it.x }
            MermaidNotePosition.LeftOf -> (participantPositions.first().x - noteWidth - 16f).coerceAtLeast(0f)
            MermaidNotePosition.RightOf -> participantPositions.first().x + nodeWidth + 16f
        }
    return SequenceNoteFrame(x = noteX, width = noteWidth)
}

@Composable
private fun MermaidNodeShape.toComposeShape(): Shape =
    when (toContainerKind()) {
        MermaidNodeContainerKind.Rectangle -> RoundedCornerShape(0.dp)
        MermaidNodeContainerKind.Rounded -> RoundedCornerShape(MermaidDefaults.cornerRadius())
        MermaidNodeContainerKind.Stadium -> RoundedCornerShape(percent = 50)
        MermaidNodeContainerKind.Circle -> CircleShape
        MermaidNodeContainerKind.Diamond -> DiamondShape
    }

private val DiamondShape =
    GenericShape { size, _ ->
        moveTo(size.width / 2f, 0f)
        lineTo(size.width, size.height / 2f)
        lineTo(size.width / 2f, size.height)
        lineTo(0f, size.height / 2f)
        close()
    }

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawMermaidArrowHead(
    color: Color,
    arrowHead: MermaidArrowHead,
    strokeWidth: Float,
) {
    drawLine(
        color = color,
        start = arrowHead.tip,
        end = arrowHead.left,
        strokeWidth = strokeWidth,
        cap = StrokeCap.Round,
    )
    drawLine(
        color = color,
        start = arrowHead.tip,
        end = arrowHead.right,
        strokeWidth = strokeWidth,
        cap = StrokeCap.Round,
    )
}
