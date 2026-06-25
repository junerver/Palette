package xyz.junerver.compose.palette.components.mermaid

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.hooks.useCreation
import xyz.junerver.compose.palette.core.theme.PaletteTheme
import xyz.junerver.compose.palette.mermaid.ErEntity
import xyz.junerver.compose.palette.mermaid.MermaidClassDefinition
import xyz.junerver.compose.palette.mermaid.MermaidClassMemberKind
import xyz.junerver.compose.palette.mermaid.MermaidClassVisibility
import xyz.junerver.compose.palette.mermaid.MermaidDirection
import xyz.junerver.compose.palette.mermaid.MermaidDiagramType
import xyz.junerver.compose.palette.mermaid.MermaidEdgeArrow
import xyz.junerver.compose.palette.mermaid.MermaidEdgeStyle
import xyz.junerver.compose.palette.mermaid.MermaidLayout
import xyz.junerver.compose.palette.mermaid.MermaidLayoutEngine
import xyz.junerver.compose.palette.mermaid.MermaidNodeShape
import xyz.junerver.compose.palette.mermaid.MermaidNote
import xyz.junerver.compose.palette.mermaid.MermaidNotePosition
import xyz.junerver.compose.palette.mermaid.MermaidParser
import kotlin.math.abs
import kotlin.math.max
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
        MermaidNodeShape.Subroutine -> MermaidNodeContainerKind.Rectangle
        MermaidNodeShape.Database -> MermaidNodeContainerKind.Stadium
        MermaidNodeShape.Asymmetric -> MermaidNodeContainerKind.Rectangle
        MermaidNodeShape.Hexagon -> MermaidNodeContainerKind.Diamond
        MermaidNodeShape.Parallelogram -> MermaidNodeContainerKind.Rectangle
        MermaidNodeShape.ParallelogramAlt -> MermaidNodeContainerKind.Rectangle
        MermaidNodeShape.Trapezoid -> MermaidNodeContainerKind.Rectangle
        MermaidNodeShape.TrapezoidAlt -> MermaidNodeContainerKind.Rectangle
        MermaidNodeShape.DoubleCircle -> MermaidNodeContainerKind.Circle
    }

internal data class MermaidArrowHead(
    val tip: Offset,
    val left: Offset,
    val right: Offset,
)

internal data class MermaidEdgeEndpoints(
    val start: Offset,
    val end: Offset,
)

internal data class MermaidFlowchartNodeVisualStyle(
    val fill: Color? = null,
    val stroke: Color? = null,
    val color: Color? = null,
    val strokeWidth: Float? = null,
)

internal data class MermaidFlowchartEdgeVisualStyle(
    val stroke: Color? = null,
    val color: Color? = null,
    val strokeWidth: Float? = null,
)

internal fun resolveFlowchartNodeVisualStyles(layout: MermaidLayout): Map<String, MermaidFlowchartNodeVisualStyle> {
    if (
        layout.flowchartClassDefs.isEmpty() &&
        layout.flowchartClassAssignments.isEmpty() &&
        layout.flowchartNodeStyles.isEmpty()
    ) {
        return emptyMap()
    }

    val classDeclarations =
        layout.flowchartClassDefs.associate { classDef ->
            classDef.name to classDef.declarations.parseMermaidStyleDeclarations()
        }
    val styles = mutableMapOf<String, MermaidFlowchartNodeVisualStyle>()

    layout.flowchartClassAssignments.forEach { assignment ->
        val declarations = classDeclarations[assignment.className] ?: return@forEach
        assignment.nodeIds.forEach { nodeId ->
            styles[nodeId] = (styles[nodeId] ?: MermaidFlowchartNodeVisualStyle()).mergeNodeDeclarations(declarations)
        }
    }

    layout.flowchartNodeStyles.forEach { nodeStyle ->
        styles[nodeStyle.nodeId] =
            (styles[nodeStyle.nodeId] ?: MermaidFlowchartNodeVisualStyle())
                .mergeNodeDeclarations(nodeStyle.declarations.parseMermaidStyleDeclarations())
    }

    return styles
}

internal fun resolveFlowchartEdgeVisualStyles(layout: MermaidLayout): Map<Int, MermaidFlowchartEdgeVisualStyle> {
    if (layout.flowchartLinkStyles.isEmpty()) return emptyMap()

    val styles = mutableMapOf<Int, MermaidFlowchartEdgeVisualStyle>()
    layout.flowchartLinkStyles.forEach { linkStyle ->
        val declarations = linkStyle.declarations.parseMermaidStyleDeclarations()
        val edgeIndexes = linkStyle.edgeIndexes ?: layout.edges.indices.toList()
        edgeIndexes.forEach { index ->
            if (index in layout.edges.indices) {
                styles[index] = (styles[index] ?: MermaidFlowchartEdgeVisualStyle()).mergeEdgeDeclarations(declarations)
            }
        }
    }

    return styles
}

private fun MermaidFlowchartNodeVisualStyle.mergeNodeDeclarations(
    declarations: Map<String, String>,
): MermaidFlowchartNodeVisualStyle =
    copy(
        fill = declarations["fill"]?.parseMermaidCssColor() ?: declarations["background-color"]?.parseMermaidCssColor() ?: fill,
        stroke = declarations["stroke"]?.parseMermaidCssColor() ?: stroke,
        color = declarations["color"]?.parseMermaidCssColor() ?: color,
        strokeWidth = declarations["stroke-width"]?.parseMermaidStrokeWidth() ?: strokeWidth,
    )

private fun MermaidFlowchartEdgeVisualStyle.mergeEdgeDeclarations(
    declarations: Map<String, String>,
): MermaidFlowchartEdgeVisualStyle =
    copy(
        stroke = declarations["stroke"]?.parseMermaidCssColor() ?: stroke,
        color = declarations["color"]?.parseMermaidCssColor() ?: color,
        strokeWidth = declarations["stroke-width"]?.parseMermaidStrokeWidth() ?: strokeWidth,
    )

internal fun String.parseMermaidStyleDeclarations(): Map<String, String> =
    splitMermaidStyleDeclarations()
        .mapNotNull { declaration ->
            val separator = declaration.indexOf(':')
            if (separator <= 0) return@mapNotNull null
            val key = declaration.substring(0, separator).trim().lowercase()
            val value = declaration.substring(separator + 1).trim()
            if (key.isEmpty() || value.isEmpty()) null else key to value
        }.toMap()

private fun String.splitMermaidStyleDeclarations(): List<String> {
    val parts = mutableListOf<String>()
    val current = StringBuilder()
    var parenDepth = 0
    var inQuote = false
    var escaped = false

    for (char in this) {
        when {
            escaped -> {
                current.append(char)
                escaped = false
            }

            char == '\\' -> {
                current.append(char)
                escaped = true
            }

            char == '"' -> {
                current.append(char)
                inQuote = !inQuote
            }

            !inQuote && char == '(' -> {
                current.append(char)
                parenDepth += 1
            }

            !inQuote && char == ')' -> {
                current.append(char)
                parenDepth = (parenDepth - 1).coerceAtLeast(0)
            }

            !inQuote && parenDepth == 0 && (char == ',' || char == ';') -> {
                parts += current.toString()
                current.clear()
            }

            else -> current.append(char)
        }
    }

    parts += current.toString()
    return parts.map { it.trim() }.filter { it.isNotEmpty() }
}

private fun String.parseMermaidStrokeWidth(): Float? =
    trim()
        .removeSuffix("px")
        .removeSuffix("PX")
        .trim()
        .toFloatOrNull()
        ?.takeIf { it >= 0f }

internal fun String.parseMermaidCssColor(): Color? {
    val value = trim().lowercase()
    if (value == "transparent") return Color.Transparent

    MermaidNamedCssColors[value]?.let { return it }

    if (value.startsWith("#")) {
        val hex = value.drop(1)
        val expanded =
            when (hex.length) {
                3 -> hex.flatMap { listOf(it, it) }.joinToString("")
                6 -> hex
                else -> return null
            }
        val rgb = expanded.toLongOrNull(radix = 16) ?: return null
        return Color(0xFF000000 or rgb)
    }

    val rgba = CssRgbRegex.matchEntire(value) ?: return null
    val red = rgba.groupValues[1].toIntOrNull()?.coerceIn(0, 255) ?: return null
    val green = rgba.groupValues[2].toIntOrNull()?.coerceIn(0, 255) ?: return null
    val blue = rgba.groupValues[3].toIntOrNull()?.coerceIn(0, 255) ?: return null
    val alpha = rgba.groupValues.getOrNull(4)?.takeIf { it.isNotEmpty() }?.toFloatOrNull()?.coerceIn(0f, 1f) ?: 1f
    return Color(red = red / 255f, green = green / 255f, blue = blue / 255f, alpha = alpha)
}

internal fun calculateFlowchartEdgeEndpoints(
    direction: MermaidDirection,
    fromX: Float,
    fromY: Float,
    toX: Float,
    toY: Float,
    nodeWidth: Float,
    nodeHeight: Float,
): MermaidEdgeEndpoints =
    when (direction) {
        MermaidDirection.TopDown ->
            MermaidEdgeEndpoints(
                start = Offset(fromX + nodeWidth / 2f, fromY + nodeHeight),
                end = Offset(toX + nodeWidth / 2f, toY),
            )

        MermaidDirection.BottomTop ->
            MermaidEdgeEndpoints(
                start = Offset(fromX + nodeWidth / 2f, fromY),
                end = Offset(toX + nodeWidth / 2f, toY + nodeHeight),
            )

        MermaidDirection.LeftRight ->
            MermaidEdgeEndpoints(
                start = Offset(fromX + nodeWidth, fromY + nodeHeight / 2f),
                end = Offset(toX, toY + nodeHeight / 2f),
            )

        MermaidDirection.RightLeft ->
            MermaidEdgeEndpoints(
                start = Offset(fromX, fromY + nodeHeight / 2f),
                end = Offset(toX + nodeWidth, toY + nodeHeight / 2f),
            )
    }

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
    layout: MermaidLayout? = null,
) {
    val (parsedDiagram, resolvedLayout) =
        useCreation(layout, source) {
            if (layout != null) {
                null to layout
            } else {
                val diagram = MermaidParser.parse(source)
                diagram to MermaidLayoutEngine.layout(diagram)
            }
        }.current

    when (resolvedLayout.type) {
        MermaidDiagramType.Flowchart ->
            FlowchartMermaidDiagram(
                modifier = modifier,
                colors = colors,
                layout = resolvedLayout,
            )

        MermaidDiagramType.Sequence ->
            SequenceMermaidDiagram(
                modifier = modifier,
                colors = colors,
                layout = resolvedLayout,
            )

        MermaidDiagramType.ClassDiagram ->
            ClassDiagramMermaidDiagram(
                modifier = modifier,
                colors = colors,
                layout = resolvedLayout,
                classDefinitions = parsedDiagram?.classDefinitions.orEmpty(),
            )

        MermaidDiagramType.ErDiagram ->
            ErDiagramMermaidDiagram(
                modifier = modifier,
                colors = colors,
                layout = resolvedLayout,
                erEntities = parsedDiagram?.erEntities.orEmpty(),
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
    val nodeRight = (layout.nodes.values.maxOfOrNull { it.x } ?: 0f) + 156f
    val nodeBottom = (layout.nodes.values.maxOfOrNull { it.y } ?: 0f) + 72f
    val subgraphRight = layout.subgraphs.maxOfOrNull { it.x + it.width } ?: 0f
    val subgraphBottom = layout.subgraphs.maxOfOrNull { it.y + it.height } ?: 0f
    val width = max(nodeRight, subgraphRight).dp
    val height = max(nodeBottom, subgraphBottom).dp
    val labelPositions = calculateFlowchartEdgeLabelPositions(layout)
    val nodeVisualStyles = resolveFlowchartNodeVisualStyles(layout)
    val edgeVisualStyles = resolveFlowchartEdgeVisualStyles(layout)

    Box(
        modifier =
            modifier
                .width(width)
                .height(height),
    ) {
        layout.subgraphs.forEach { subgraph ->
            Box(
                modifier =
                    Modifier
                        .absoluteOffset(x = subgraph.x.dp, y = subgraph.y.dp)
                        .size(width = subgraph.width.dp, height = subgraph.height.dp)
                        .background(colors.nodeContainerColor.copy(alpha = 0.32f), RoundedCornerShape(8.dp))
                        .border(1.dp, colors.nodeBorderColor.copy(alpha = 0.56f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                contentAlignment = Alignment.TopStart,
            ) {
                Text(
                    text = subgraph.subgraph.label,
                    color = colors.nodeContentColor,
                    style = PaletteTheme.typography.label,
                )
            }
        }

        Canvas(modifier = Modifier.matchParentSize()) {
            layout.edges.forEachIndexed { index, edge ->
                if (!edge.visible) return@forEachIndexed
                val from = layout.nodes[edge.from] ?: return@forEachIndexed
                val to = layout.nodes[edge.to] ?: return@forEachIndexed
                val edgeVisualStyle = edgeVisualStyles[index]
                val edgeColor = edgeVisualStyle?.stroke ?: colors.edgeColor
                val edgeStrokeWidth = edgeVisualStyle?.strokeWidth?.dp?.toPx()
                    ?: if (edge.style == MermaidEdgeStyle.Thick) 3.dp.toPx() else 2.dp.toPx()
                val endpoints =
                    calculateFlowchartEdgeEndpoints(
                        direction = layout.direction,
                        fromX = from.x,
                        fromY = from.y,
                        toX = to.x,
                        toY = to.y,
                        nodeWidth = nodeWidth.toPx(),
                        nodeHeight = nodeHeight.toPx(),
                    )
                drawLine(
                    color = edgeColor,
                    start = endpoints.start,
                    end = endpoints.end,
                    strokeWidth = edgeStrokeWidth,
                    cap = StrokeCap.Round,
                    pathEffect =
                        if (edge.style == MermaidEdgeStyle.Dotted) {
                            PathEffect.dashPathEffect(floatArrayOf(4f, 5f))
                        } else {
                            null
                        },
                )
                drawMermaidEdgeEndMarker(
                    color = edgeColor,
                    start = endpoints.start,
                    end = endpoints.end,
                    arrow = edge.arrow,
                    strokeWidth = edgeStrokeWidth,
                )
                drawMermaidEdgeEndMarker(
                    color = edgeColor,
                    start = endpoints.end,
                    end = endpoints.start,
                    arrow = edge.startArrow,
                    strokeWidth = edgeStrokeWidth,
                )
                if (edge.arrow == MermaidEdgeArrow.Bidirectional) {
                    drawMermaidEdgeEndMarker(
                        color = edgeColor,
                        start = endpoints.end,
                        end = endpoints.start,
                        arrow = MermaidEdgeArrow.Forward,
                        strokeWidth = edgeStrokeWidth,
                    )
                }
            }
        }

        layout.edges.forEachIndexed { index, edge ->
            if (!edge.visible) return@forEachIndexed
            val label = edge.label ?: return@forEachIndexed
            val labelPosition = labelPositions[index] ?: return@forEachIndexed
            val edgeVisualStyle = edgeVisualStyles[index]

            Text(
                text = label,
                color = edgeVisualStyle?.color ?: colors.nodeContentColor,
                style = PaletteTheme.typography.label,
                modifier =
                    Modifier
                        .absoluteOffset(x = labelPosition.x.dp, y = labelPosition.y.dp)
                        .background(colors.nodeContainerColor, RoundedCornerShape(4.dp))
                        .border(
                            width = (edgeVisualStyle?.strokeWidth ?: 1f).dp,
                            color = edgeVisualStyle?.stroke ?: colors.edgeColor,
                            shape = RoundedCornerShape(4.dp),
                        )
                        .padding(horizontal = 6.dp, vertical = 2.dp),
            )
        }

        layout.nodes.values.forEach { item ->
            val nodeShape = item.node.shape.toComposeShape()
            val nodeVisualStyle = nodeVisualStyles[item.node.id]
            Box(
                modifier =
                    Modifier
                        .absoluteOffset(x = item.x.dp, y = item.y.dp)
                        .size(width = nodeWidth, height = nodeHeight)
                        .background(nodeVisualStyle?.fill ?: colors.nodeContainerColor, nodeShape)
                        .border(
                            width = (nodeVisualStyle?.strokeWidth ?: 1f).dp,
                            color = nodeVisualStyle?.stroke ?: colors.nodeBorderColor,
                            shape = nodeShape,
                        )
                        .padding(horizontal = if (item.node.shape == MermaidNodeShape.Diamond) 20.dp else 8.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = item.node.label,
                    color = nodeVisualStyle?.color ?: colors.nodeContentColor,
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

private data class MermaidEdgeLabelPosition(
    val x: Float,
    val y: Float,
)

private fun calculateFlowchartEdgeLabelPositions(layout: MermaidLayout): Map<Int, MermaidEdgeLabelPosition> {
    val placed = mutableListOf<MermaidEdgeLabelPosition>()
    val positions = mutableMapOf<Int, MermaidEdgeLabelPosition>()

    layout.edges.forEachIndexed { index, edge ->
        if (edge.label == null) return@forEachIndexed
        val from = layout.nodes[edge.from] ?: return@forEachIndexed
        val to = layout.nodes[edge.to] ?: return@forEachIndexed
        val labelX = ((from.x + to.x) / 2f + 44f).coerceAtLeast(0f)
        var labelY = ((from.y + to.y) / 2f + 8f).coerceAtLeast(0f)

        while (placed.any { abs(it.x - labelX) < 96f && abs(it.y - labelY) < 24f }) {
            labelY += 18f
        }

        val position = MermaidEdgeLabelPosition(labelX, labelY)
        placed += position
        positions[index] = position
    }

    return positions
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
                drawMermaidEdgeEndMarker(
                    color = colors.edgeColor,
                    start = start,
                    end = end,
                    arrow = edge.arrow,
                    strokeWidth = if (edge.style == MermaidEdgeStyle.Thick) 3.dp.toPx() else 2.dp.toPx(),
                )
                drawMermaidEdgeEndMarker(
                    color = colors.edgeColor,
                    start = end,
                    end = start,
                    arrow = edge.startArrow,
                    strokeWidth = if (edge.style == MermaidEdgeStyle.Thick) 3.dp.toPx() else 2.dp.toPx(),
                )
                if (edge.arrow == MermaidEdgeArrow.Bidirectional) {
                    drawMermaidEdgeEndMarker(
                        color = colors.edgeColor,
                        start = end,
                        end = start,
                        arrow = MermaidEdgeArrow.Forward,
                        strokeWidth = if (edge.style == MermaidEdgeStyle.Thick) 3.dp.toPx() else 2.dp.toPx(),
                    )
                }
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
private fun ClassDiagramMermaidDiagram(
    modifier: Modifier,
    colors: MermaidColors,
    layout: MermaidLayout,
    classDefinitions: List<MermaidClassDefinition>,
) {
    val nodeWidth = 180.dp
    val memberHeight = 20.dp
    val headerHeight = 48.dp
    val padding = 8.dp

    val nodeRight = (layout.nodes.values.maxOfOrNull { it.x } ?: 0f) + 204f
    val nodeBottom = (layout.nodes.values.maxOfOrNull { it.y } ?: 0f) + 120f
    val width = nodeRight.dp
    val height = nodeBottom.dp

    val nodeHeights = classDefinitions.associate { cls ->
        cls.id to (headerHeight + memberHeight * cls.members.size + padding * 2)
    }

    Box(
        modifier = modifier.width(width).height(height),
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            layout.edges.forEach { edge ->
                val from = layout.nodes[edge.from] ?: return@forEach
                val to = layout.nodes[edge.to] ?: return@forEach
                val startX = from.x.dp.toPx() + nodeWidth.toPx() / 2f
                val startY = from.y.dp.toPx() + (nodeHeights[edge.from] ?: headerHeight + padding * 2).toPx()
                val endX = to.x.dp.toPx() + nodeWidth.toPx() / 2f
                val endY = to.y.dp.toPx()
                val pathEffect = if (edge.style == MermaidEdgeStyle.Dotted) {
                    PathEffect.dashPathEffect(floatArrayOf(6f, 6f))
                } else {
                    null
                }
                drawLine(
                    color = colors.edgeColor,
                    start = Offset(startX, startY),
                    end = Offset(endX, endY),
                    strokeWidth = 2.dp.toPx(),
                    cap = StrokeCap.Round,
                    pathEffect = pathEffect,
                )
            }
        }

        classDefinitions.forEach { cls ->
            val positioned = layout.nodes[cls.id] ?: return@forEach
            val memberCount = cls.members.size
            val nodeHeight = headerHeight + memberHeight * memberCount + padding * 2

            Box(
                modifier = Modifier
                    .absoluteOffset(x = positioned.x.dp, y = positioned.y.dp)
                    .width(nodeWidth)
                    .height(nodeHeight)
                    .background(colors.nodeContainerColor, RoundedCornerShape(4.dp))
                    .border(1.dp, colors.nodeBorderColor, RoundedCornerShape(4.dp)),
            ) {
                Column {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(headerHeight)
                            .background(colors.nodeContainerColor.copy(alpha = 0.5f))
                            .padding(horizontal = 8.dp),
                        contentAlignment = Alignment.CenterStart,
                    ) {
                        Column {
                            if (cls.annotation != null) {
                                Text(
                                    text = "<<${cls.annotation}>>",
                                    color = colors.nodeContentColor.copy(alpha = 0.7f),
                                    style = PaletteTheme.typography.label,
                                )
                            }
                            Text(
                                text = cls.label,
                                color = colors.nodeContentColor,
                                style = PaletteTheme.typography.body,
                            )
                        }
                    }

                    cls.members.forEach { member ->
                        val visibility = when (member.visibility) {
                            MermaidClassVisibility.Public -> "+"
                            MermaidClassVisibility.Private -> "-"
                            MermaidClassVisibility.Protected -> "#"
                            MermaidClassVisibility.Package -> "~"
                        }
                        val prefix = buildString {
                            append(visibility)
                            if (member.isAbstract) append("*")
                            if (member.isStatic) append("$")
                        }
                        val memberText = if (member.kind == MermaidClassMemberKind.Method) {
                            "$prefix${member.name}()${member.type?.let { ": $it" } ?: ""}"
                        } else {
                            "$prefix${member.name}${member.type?.let { ": $it" } ?: ""}"
                        }
                        Text(
                            text = memberText,
                            color = colors.nodeContentColor,
                            style = PaletteTheme.typography.label,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(memberHeight)
                                .padding(horizontal = 8.dp),
                        )
                    }
                }
            }
        }

        if (classDefinitions.isEmpty()) {
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
private fun ErDiagramMermaidDiagram(
    modifier: Modifier,
    colors: MermaidColors,
    layout: MermaidLayout,
    erEntities: List<ErEntity>,
) {
    val nodeWidth = 180.dp
    val attributeHeight = 20.dp
    val headerHeight = 32.dp
    val padding = 8.dp

    val nodeRight = (layout.nodes.values.maxOfOrNull { it.x } ?: 0f) + 204f
    val nodeBottom = (layout.nodes.values.maxOfOrNull { it.y } ?: 0f) + 120f
    val width = nodeRight.dp
    val height = nodeBottom.dp

    Box(
        modifier = modifier.width(width).height(height),
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            layout.edges.forEach { edge ->
                val from = layout.nodes[edge.from] ?: return@forEach
                val to = layout.nodes[edge.to] ?: return@forEach
                val startX = from.x.dp.toPx() + nodeWidth.toPx() / 2f
                val startY = from.y.dp.toPx() + 44f
                val endX = to.x.dp.toPx() + nodeWidth.toPx() / 2f
                val endY = to.y.dp.toPx()
                val pathEffect = if (edge.style == MermaidEdgeStyle.Dotted) {
                    PathEffect.dashPathEffect(floatArrayOf(6f, 6f))
                } else {
                    null
                }
                drawLine(
                    color = colors.edgeColor,
                    start = Offset(startX, startY),
                    end = Offset(endX, endY),
                    strokeWidth = 2.dp.toPx(),
                    cap = StrokeCap.Round,
                    pathEffect = pathEffect,
                )
            }
        }

        erEntities.forEach { entity ->
            val positioned = layout.nodes[entity.name] ?: return@forEach
            val attributeCount = entity.attributes.size
            val nodeHeight = headerHeight + attributeHeight * attributeCount + padding * 2

            Box(
                modifier = Modifier
                    .absoluteOffset(x = positioned.x.dp, y = positioned.y.dp)
                    .width(nodeWidth)
                    .height(nodeHeight)
                    .background(colors.nodeContainerColor, RoundedCornerShape(4.dp))
                    .border(1.dp, colors.nodeBorderColor, RoundedCornerShape(4.dp)),
            ) {
                Column {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(headerHeight)
                            .background(colors.nodeContainerColor.copy(alpha = 0.5f))
                            .padding(horizontal = 8.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = entity.name,
                            color = colors.nodeContentColor,
                            style = PaletteTheme.typography.body,
                        )
                    }

                    entity.attributes.forEach { attr ->
                        val prefix = if (attr.isPrimaryKey) "PK" else "FK"
                        val attrText = "$prefix ${attr.type} ${attr.name}"
                        Text(
                            text = attrText,
                            color = colors.nodeContentColor,
                            style = PaletteTheme.typography.label,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(attributeHeight)
                                .padding(horizontal = 8.dp),
                        )
                    }
                }
            }
        }

        if (erEntities.isEmpty()) {
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

private val CssRgbRegex =
    Regex("""rgba?\(\s*([0-9]{1,3})\s*,\s*([0-9]{1,3})\s*,\s*([0-9]{1,3})(?:\s*,\s*([0-9.]+))?\s*\)""")

private val MermaidNamedCssColors =
    mapOf(
        "black" to Color.Black,
        "white" to Color.White,
        "red" to Color.Red,
        "green" to Color.Green,
        "blue" to Color.Blue,
        "yellow" to Color.Yellow,
        "cyan" to Color.Cyan,
        "magenta" to Color.Magenta,
        "gray" to Color.Gray,
        "grey" to Color.Gray,
        "darkgray" to Color.DarkGray,
        "darkgrey" to Color.DarkGray,
        "lightgray" to Color.LightGray,
        "lightgrey" to Color.LightGray,
    )

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawMermaidEdgeEndMarker(
    color: Color,
    start: Offset,
    end: Offset,
    arrow: MermaidEdgeArrow,
    strokeWidth: Float,
) {
    when (arrow) {
        MermaidEdgeArrow.Forward,
        MermaidEdgeArrow.Open,
        MermaidEdgeArrow.Bidirectional,
        ->
            drawMermaidArrowHead(
                color = color,
                arrowHead = calculateMermaidArrowHead(start = start, end = end, size = 10.dp.toPx()),
                strokeWidth = strokeWidth,
            )

        MermaidEdgeArrow.Circle ->
            drawCircle(
                color = color,
                radius = 5.dp.toPx(),
                center = end,
                style = Stroke(width = strokeWidth),
            )

        MermaidEdgeArrow.Cross -> {
            val size = 5.dp.toPx()
            drawLine(
                color = color,
                start = Offset(end.x - size, end.y - size),
                end = Offset(end.x + size, end.y + size),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round,
            )
            drawLine(
                color = color,
                start = Offset(end.x - size, end.y + size),
                end = Offset(end.x + size, end.y - size),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round,
            )
        }

        MermaidEdgeArrow.None -> Unit
    }
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
