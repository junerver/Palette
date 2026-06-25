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
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.hooks.useCreation
import xyz.junerver.compose.palette.core.theme.PaletteTheme
import xyz.junerver.compose.palette.mermaid.ErEntity
import xyz.junerver.compose.palette.mermaid.ErRelationship
import xyz.junerver.compose.palette.mermaid.ErRelationshipKind
import xyz.junerver.compose.palette.mermaid.MermaidClassDefinition
import xyz.junerver.compose.palette.mermaid.MermaidClassMemberKind
import xyz.junerver.compose.palette.mermaid.MermaidClassRelationType
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
import xyz.junerver.compose.palette.mermaid.StateDefinition
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
                erRelationships = parsedDiagram?.erRelationships.orEmpty(),
            )

        MermaidDiagramType.StateDiagram ->
            StateDiagramMermaidDiagram(
                modifier = modifier,
                colors = colors,
                layout = resolvedLayout,
                stateDefinitions = parsedDiagram?.stateDefinitions.orEmpty(),
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
                val pathEffect =
                    if (edge.style == MermaidEdgeStyle.Dotted) {
                        PathEffect.dashPathEffect(floatArrayOf(4f, 5f))
                    } else {
                        null
                    }
                val offset = layout.stateEdgeOffsets[index] ?: 0f
                // Edges that share their endpoint pair (or double back) bow sideways by
                // `offset` so they separate instead of overlapping into a single line.
                if (offset != 0f) {
                    val s = endpoints.start
                    val e = endpoints.end
                    val perpX = -(e.y - s.y)
                    val perpY = e.x - s.x
                    val perpLen = sqrt(perpX * perpX + perpY * perpY).coerceAtLeast(1f)
                    val bow = offset.dp.toPx()
                    val ctrl = Offset(
                        (s.x + e.x) / 2f + perpX / perpLen * bow,
                        (s.y + e.y) / 2f + perpY / perpLen * bow,
                    )
                    val path = Path().apply {
                        moveTo(s.x, s.y)
                        quadraticTo(ctrl.x, ctrl.y, e.x, e.y)
                    }
                    drawPath(path = path, color = edgeColor, style = Stroke(width = edgeStrokeWidth, cap = StrokeCap.Round, pathEffect = pathEffect))
                    // Arrowhead follows the curve's final tangent (control point -> end).
                    drawMermaidEdgeEndMarker(
                        color = edgeColor,
                        start = ctrl,
                        end = e,
                        arrow = edge.arrow,
                        strokeWidth = edgeStrokeWidth,
                    )
                    drawMermaidEdgeEndMarker(
                        color = edgeColor,
                        start = ctrl,
                        end = s,
                        arrow = edge.startArrow,
                        strokeWidth = edgeStrokeWidth,
                    )
                    if (edge.arrow == MermaidEdgeArrow.Bidirectional) {
                        drawMermaidEdgeEndMarker(
                            color = edgeColor,
                            start = ctrl,
                            end = s,
                            arrow = MermaidEdgeArrow.Forward,
                            strokeWidth = edgeStrokeWidth,
                        )
                    }
                } else {
                    drawLine(
                        color = edgeColor,
                        start = endpoints.start,
                        end = endpoints.end,
                        strokeWidth = edgeStrokeWidth,
                        cap = StrokeCap.Round,
                        pathEffect = pathEffect,
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
                val strokeWidth = if (edge.style == MermaidEdgeStyle.Thick) 3.dp.toPx() else 2.dp.toPx()
                val pathEffect =
                    if (edge.style == MermaidEdgeStyle.Dotted) {
                        PathEffect.dashPathEffect(floatArrayOf(4f, 5f))
                    } else {
                        null
                    }

                if (edge.from == edge.to) {
                    // Self-message: a U-shaped arc leaving the participant's right side,
                    // looping out, and returning with an arrowhead on the same lifeline.
                    val loop = 22.dp.toPx()
                    val top = y
                    val bottom = y + messageGapPx * 0.7f
                    val rightX = startX + loop
                    val path = Path().apply {
                        moveTo(startX, top)
                        cubicTo(startX + loop, top, rightX, top, rightX, (top + bottom) / 2f)
                        cubicTo(rightX, bottom, startX + loop, bottom, startX, bottom)
                    }
                    drawPath(path = path, color = colors.edgeColor, style = Stroke(width = strokeWidth, cap = StrokeCap.Round, pathEffect = pathEffect))
                    // Arrowhead points back up into the lifeline at the bottom of the loop.
                    drawMermaidEdgeEndMarker(
                        color = colors.edgeColor,
                        start = Offset(startX + loop / 2f, bottom),
                        end = Offset(startX, bottom),
                        arrow = edge.arrow,
                        strokeWidth = strokeWidth,
                    )
                    return@forEachIndexed
                }

                val start = Offset(startX, y)
                val end = Offset(endX, y)
                drawLine(
                    color = colors.edgeColor,
                    start = start,
                    end = end,
                    strokeWidth = strokeWidth,
                    cap = StrokeCap.Round,
                    pathEffect = pathEffect,
                )
                drawMermaidEdgeEndMarker(
                    color = colors.edgeColor,
                    start = start,
                    end = end,
                    arrow = edge.arrow,
                    strokeWidth = strokeWidth,
                )
                drawMermaidEdgeEndMarker(
                    color = colors.edgeColor,
                    start = end,
                    end = start,
                    arrow = edge.startArrow,
                    strokeWidth = strokeWidth,
                )
                if (edge.arrow == MermaidEdgeArrow.Bidirectional) {
                    drawMermaidEdgeEndMarker(
                        color = colors.edgeColor,
                        start = end,
                        end = start,
                        arrow = MermaidEdgeArrow.Forward,
                        strokeWidth = strokeWidth,
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
            val strokeWidth = 2.dp.toPx()
            layout.edges.forEachIndexed { index, edge ->
                val from = layout.nodes[edge.from] ?: return@forEachIndexed
                val to = layout.nodes[edge.to] ?: return@forEachIndexed
                val startX = from.x.dp.toPx() + nodeWidth.toPx() / 2f
                val startY = from.y.dp.toPx() + (nodeHeights[edge.from] ?: headerHeight + padding * 2).toPx()
                val endX = to.x.dp.toPx() + nodeWidth.toPx() / 2f
                val endY = to.y.dp.toPx()
                val pathEffect = if (edge.style == MermaidEdgeStyle.Dotted) {
                    PathEffect.dashPathEffect(floatArrayOf(6f, 6f))
                } else {
                    null
                }
                // Unit vector along the edge (from -> to) and its perpendicular.
                val dx = endX - startX; val dy = endY - startY
                val len = sqrt(dx * dx + dy * dy)
                if (len <= 0f) return@forEachIndexed
                val ux = dx / len; val uy = dy / len
                val px = -uy; val py = ux

                val relType = layout.classRelationTypes[index]
                // How far the line should stop short of the node edge to make room for the marker.
                val marker = 10.dp.toPx()
                val lineEnd = when (relType) {
                    MermaidClassRelationType.Inheritance, MermaidClassRelationType.Realization -> endY - marker * 1.4f
                    else -> endY
                }
                drawLine(
                    color = colors.edgeColor,
                    start = Offset(startX, startY),
                    end = Offset(endX, lineEnd),
                    strokeWidth = strokeWidth,
                    cap = StrokeCap.Round,
                    pathEffect = pathEffect,
                )

                // UML end markers, keyed by relationship type.
                when (relType) {
                    MermaidClassRelationType.Inheritance, MermaidClassRelationType.Realization -> {
                        // Hollow triangle at the parent (to) end.
                        drawClassMarker(colors.edgeColor, Offset(endX, endY), ux, uy, px, py, marker, filled = false, kind = ClassMarkerKind.Triangle)
                    }
                    MermaidClassRelationType.Composition -> {
                        // Filled diamond at the whole (from) end.
                        drawClassMarker(colors.edgeColor, Offset(startX, startY), -ux, -uy, px, py, marker, filled = true, kind = ClassMarkerKind.Diamond)
                    }
                    MermaidClassRelationType.Aggregation -> {
                        // Hollow diamond at the whole (from) end.
                        drawClassMarker(colors.edgeColor, Offset(startX, startY), -ux, -uy, px, py, marker, filled = false, kind = ClassMarkerKind.Diamond)
                    }
                    MermaidClassRelationType.Dependency, MermaidClassRelationType.Association -> {
                        // Open V arrow at the to end.
                        drawClassMarker(colors.edgeColor, Offset(endX, endY), ux, uy, px, py, marker, filled = false, kind = ClassMarkerKind.Arrow)
                    }
                    else -> Unit // Link / DependencyLink: plain line, no marker.
                }
            }
        }

        layout.edges.forEach { edge ->
            val lbl = edge.label
            if (lbl.isNullOrEmpty()) return@forEach
            val from = layout.nodes[edge.from] ?: return@forEach
            val to = layout.nodes[edge.to] ?: return@forEach
            Text(text = lbl, color = colors.nodeContentColor, style = PaletteTheme.typography.label,
                modifier = Modifier.absoluteOffset(x = ((from.x + to.x) / 2f + 90f).dp, y = ((from.y + to.y) / 2f + 10f).dp))
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
    erRelationships: List<ErRelationship>,
) {
    val nodeWidth = 180.dp
    val attributeHeight = 22.dp
    val headerHeight = 32.dp
    val padding = 6.dp
    val cornerRadius = 6.dp

    // Pre-compute each entity height so edges anchor on the real bottom/top edges.
    val entityHeights = erEntities.associate { entity ->
        val h = headerHeight + attributeHeight * entity.attributes.size.coerceAtLeast(1) + padding * 2
        entity.name to h
    }
    fun heightOf(id: String): Dp = entityHeights[id] ?: (headerHeight + padding * 2)

    val nodeRight = (layout.nodes.values.maxOfOrNull { it.x } ?: 0f) + nodeWidth.value + 24f
    val nodeBottom = layout.nodes.entries.map { (_, n) -> n.y + heightOf(n.node.id).value + 24f }.maxOrNull() ?: 48f
    val width = nodeRight.dp
    val height = nodeBottom.dp

    Box(
        modifier = modifier.width(width).height(height),
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val widthPx = nodeWidth.toPx()
            erRelationships.forEach { rel ->
                val from = layout.nodes[rel.from] ?: return@forEach
                val to = layout.nodes[rel.to] ?: return@forEach
                val isDotted = rel.kind.name.startsWith("NonIdentifying")
                val startX = from.x.dp.toPx() + widthPx / 2f
                val startY = from.y.dp.toPx() + heightOf(from.node.id).toPx()
                val endX = to.x.dp.toPx() + widthPx / 2f
                val endY = to.y.dp.toPx()
                val pathEffect = if (isDotted) {
                    PathEffect.dashPathEffect(floatArrayOf(6f, 6f))
                } else {
                    null
                }
                drawLine(
                    color = colors.edgeColor,
                    start = Offset(startX, startY),
                    end = Offset(endX, endY),
                    strokeWidth = 1.5.dp.toPx(),
                    cap = StrokeCap.Round,
                    pathEffect = pathEffect,
                )
                val (fromMarker, toMarker) = rel.kind.crowsFootMarkers()
                drawErEndpointMarker(colors.edgeColor, Offset(startX, startY), Offset(endX, endY), fromMarker)
                drawErEndpointMarker(colors.edgeColor, Offset(endX, endY), Offset(startX, startY), toMarker)
            }
        }

        // Relationship labels sit at the line midpoint.
        erRelationships.forEach { rel ->
            val lbl = rel.label ?: return@forEach
            val from = layout.nodes[rel.from] ?: return@forEach
            val to = layout.nodes[rel.to] ?: return@forEach
            val fromHeight = heightOf(from.node.id).value
            val midX = ((from.x + to.x) / 2f + nodeWidth.value / 2f)
            val midY = ((from.y + fromHeight + to.y) / 2f)
            Text(
                text = lbl,
                color = colors.nodeContentColor,
                style = PaletteTheme.typography.label,
                modifier = Modifier
                    .absoluteOffset(x = midX.dp, y = (midY - 10f).dp)
                    .background(colors.nodeContainerColor, RoundedCornerShape(4.dp))
                    .padding(horizontal = 4.dp, vertical = 1.dp),
            )
        }

        erEntities.forEach { entity ->
            val positioned = layout.nodes[entity.name] ?: return@forEach
            val nodeHeight = heightOf(entity.name)
            val pkColor = if (colors.primaryKeyColor == Color.Unspecified) colors.nodeBorderColor else colors.primaryKeyColor
            val fkColor = if (colors.foreignKeyColor == Color.Unspecified) colors.nodeBorderColor else colors.foreignKeyColor

            Box(
                modifier = Modifier
                    .absoluteOffset(x = positioned.x.dp, y = positioned.y.dp)
                    .width(nodeWidth)
                    .height(nodeHeight)
                    .background(colors.nodeContainerColor, RoundedCornerShape(cornerRadius))
                    .border(1.dp, colors.nodeBorderColor, RoundedCornerShape(cornerRadius)),
            ) {
                Column {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(headerHeight)
                            .background(
                                if (colors.entityHeaderColor == Color.Unspecified) colors.nodeContainerColor.copy(alpha = 0.5f)
                                else colors.entityHeaderColor,
                                RoundedCornerShape(topStart = cornerRadius, topEnd = cornerRadius),
                            )
                            .padding(horizontal = 8.dp),
                        contentAlignment = Alignment.CenterStart,
                    ) {
                        Text(
                            text = entity.name,
                            color = colors.nodeContentColor,
                            style = PaletteTheme.typography.body,
                        )
                    }

                    entity.attributes.takeIf { it.isNotEmpty() }?.forEach { attr ->
                        val prefix = when {
                            attr.isPrimaryKey -> "PK"
                            attr.isForeignKey -> "FK"
                            else -> null
                        }
                        val keyColor = when {
                            attr.isPrimaryKey -> pkColor
                            attr.isForeignKey -> fkColor
                            else -> colors.nodeContentColor
                        }
                        val body = "${attr.type} ${attr.name}"
                        val annotated = buildAnnotatedString {
                            if (prefix != null) {
                                withStyle(SpanStyle(color = keyColor, fontWeight = FontWeight.Bold)) {
                                    append(prefix)
                                    append(' ')
                                }
                            }
                            append(body)
                        }
                        Text(
                            text = annotated,
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
private fun StateDiagramMermaidDiagram(
    modifier: Modifier,
    colors: MermaidColors,
    layout: MermaidLayout,
    stateDefinitions: List<StateDefinition>,
) {
    val nodeWidth = 140.dp
    val nodeHeight = 44.dp
    val circleSize = 24.dp
    val stateMap = stateDefinitions.associateBy { it.id }

    fun isCircle(id: String): Boolean {
        val s = stateMap[id]
        return s?.isStart == true || s?.isEnd == true
    }

    val nodeRight = (layout.nodes.values.maxOfOrNull { it.x + nodeWidth.value } ?: nodeWidth.value) + 24f
    val nodeBottom = (layout.nodes.values.maxOfOrNull { it.y + nodeHeight.value } ?: nodeHeight.value) + 24f
    val width = nodeRight.dp
    val height = nodeBottom.dp

    Box(
        modifier = modifier.width(width).height(height),
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val strokeWidth = 1.5.dp.toPx()
            val circlePx = circleSize.toPx()
            val boxWPx = nodeWidth.toPx()
            val boxHPx = nodeHeight.toPx()
            fun centerXPx(id: String, x: Float): Float =
                x + if (isCircle(id)) circlePx / 2f else boxWPx / 2f

            fun bottomPx(id: String, y: Float): Float =
                y + if (isCircle(id)) circlePx else boxHPx

            layout.edges.forEachIndexed { index, edge ->
                val from = layout.nodes[edge.from] ?: return@forEachIndexed
                val to = layout.nodes[edge.to] ?: return@forEachIndexed

                val startX = centerXPx(edge.from, from.x.dp.toPx())
                val startY = bottomPx(edge.from, from.y.dp.toPx())
                val endX = centerXPx(edge.to, to.x.dp.toPx())
                val endY = to.y.dp.toPx()
                val offsetX = layout.stateEdgeOffsets[index]?.dp?.toPx() ?: 0f

                // Cubic Bézier whose control points sit at the vertical midpoint, shifted
                // sideways by `offsetX`. With offset 0 the edge is a gentle S-curve; non-zero
                // offsets bow forward/backward links apart so they no longer overlap.
                val midY = (startY + endY) / 2f
                val path = Path().apply {
                    moveTo(startX, startY)
                    cubicTo(startX + offsetX, midY, endX + offsetX, midY, endX, endY)
                }
                drawPath(path = path, color = colors.edgeColor, style = Stroke(width = strokeWidth, cap = StrokeCap.Round))

                // Arrowhead follows the curve's final tangent (from the last control point
                // toward the tip), so up-going back-edges point up and down-going ones point down.
                drawMermaidEdgeEndMarker(
                    color = colors.edgeColor,
                    start = Offset(endX + offsetX, midY),
                    end = Offset(endX, endY),
                    arrow = MermaidEdgeArrow.Forward,
                    strokeWidth = strokeWidth,
                )
            }
        }

        layout.edges.forEachIndexed { index, edge ->
            val lbl = edge.label
            if (lbl.isNullOrEmpty()) return@forEachIndexed
            val from = layout.nodes[edge.from] ?: return@forEachIndexed
            val to = layout.nodes[edge.to] ?: return@forEachIndexed
            val halfWidth = nodeWidth.value / 2f
            val fromCx = from.x + halfWidth
            val toCx = to.x + halfWidth
            val midX = (fromCx + toCx) / 2f
            val midY = (from.y + nodeHeight.value + to.y) / 2f
            // Follow this edge's arc so labels of a bidirectional link sit on opposite sides.
            val offset = layout.stateEdgeOffsets[index] ?: 0f
            Text(
                text = lbl,
                color = colors.nodeContentColor,
                style = PaletteTheme.typography.label.copy(fontStyle = FontStyle.Italic),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .absoluteOffset(x = (midX - 40f + offset).dp, y = (midY - 18f).dp)
                    .width(80.dp)
                    .padding(horizontal = 2.dp),
            )
        }

        stateDefinitions.forEach { state ->
            val positioned = layout.nodes[state.id] ?: return@forEach

            when {
                state.isStart -> StateStartDot(
                    x = positioned.x.dp,
                    y = positioned.y.dp,
                    size = circleSize,
                    color = colors.edgeColor,
                )

                state.isEnd -> StateEndDoubleCircle(
                    x = positioned.x.dp,
                    y = positioned.y.dp,
                    size = circleSize,
                    borderColor = colors.edgeColor,
                    fillColor = colors.edgeColor,
                )

                state.isFork || state.isJoin -> Box(
                    modifier = Modifier
                        .absoluteOffset(x = positioned.x.dp, y = positioned.y.dp)
                        .size(width = nodeWidth, height = (nodeHeight / 2))
                        .background(colors.edgeColor)
                        .border(1.dp, colors.nodeBorderColor),
                )

                else -> Box(
                    modifier = Modifier
                        .absoluteOffset(x = positioned.x.dp, y = positioned.y.dp)
                        .width(nodeWidth)
                        .height(nodeHeight)
                        .background(colors.nodeContainerColor, RoundedCornerShape(MermaidDefaults.cornerRadius()))
                        .border(1.dp, colors.nodeBorderColor, RoundedCornerShape(MermaidDefaults.cornerRadius())),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = state.label ?: state.id,
                        color = colors.nodeContentColor,
                        style = PaletteTheme.typography.label,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }

        if (stateDefinitions.isEmpty()) {
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

// ── ER crow's foot notation ───────────────────────────────────────────

internal enum class ErCardinalityPart {
    One,   // vertical bar "|"
    Zero,  // circle "o"
    Many,  // crow's foot "{"
}

internal fun ErRelationshipKind.crowsFootMarkers(): Pair<Set<ErCardinalityPart>, Set<ErCardinalityPart>> =
    when (this) {
        ErRelationshipKind.OneToOne -> setOf(ErCardinalityPart.One) to setOf(ErCardinalityPart.One)
        ErRelationshipKind.OneToManyZeroOrMore ->
            setOf(ErCardinalityPart.One) to setOf(ErCardinalityPart.Many, ErCardinalityPart.Zero)
        ErRelationshipKind.OneToManyOneOrMore ->
            setOf(ErCardinalityPart.One) to setOf(ErCardinalityPart.Many, ErCardinalityPart.One)
        ErRelationshipKind.ManyToManyZeroOrMore ->
            setOf(ErCardinalityPart.Many, ErCardinalityPart.Zero) to setOf(ErCardinalityPart.Many, ErCardinalityPart.Zero)
        ErRelationshipKind.ManyToManyOneOrMore ->
            setOf(ErCardinalityPart.Many, ErCardinalityPart.One) to setOf(ErCardinalityPart.Many, ErCardinalityPart.One)
        ErRelationshipKind.ManyToOneZeroOrMore ->
            setOf(ErCardinalityPart.Many, ErCardinalityPart.Zero) to setOf(ErCardinalityPart.One)
        ErRelationshipKind.ManyToOneOneOrMore ->
            setOf(ErCardinalityPart.Many, ErCardinalityPart.One) to setOf(ErCardinalityPart.One)
        // Non-identifying variants share the same cardinality markers (rendered with a dotted line).
        ErRelationshipKind.NonIdentifyingOneToOne -> setOf(ErCardinalityPart.One) to setOf(ErCardinalityPart.One)
        ErRelationshipKind.NonIdentifyingOneToMany ->
            setOf(ErCardinalityPart.One) to setOf(ErCardinalityPart.Many, ErCardinalityPart.Zero)
        ErRelationshipKind.NonIdentifyingManyToOne ->
            setOf(ErCardinalityPart.Many, ErCardinalityPart.Zero) to setOf(ErCardinalityPart.One)
        ErRelationshipKind.NonIdentifyingManyToMany ->
            setOf(ErCardinalityPart.Many, ErCardinalityPart.Zero) to setOf(ErCardinalityPart.Many, ErCardinalityPart.Zero)
    }

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawErEndpointMarker(
    color: Color,
    point: Offset,
    other: Offset,
    parts: Set<ErCardinalityPart>,
) {
    if (parts.isEmpty()) return
    val dx = other.x - point.x
    val dy = other.y - point.y
    val length = sqrt(dx * dx + dy * dy)
    if (length == 0f) return
    val ux = dx / length
    val uy = dy / length
    val px = -uy
    val py = ux
    val strokeWidth = 1.5.dp.toPx()
    val gap = 6.dp.toPx()
    val radius = 3.dp.toPx()
    val foot = 7.dp.toPx()

    // Parts are stacked along the line from the node edge toward `other`.
    parts.forEachIndexed { index, _ ->
        val along = gap * (index + 1)
        val center = Offset(point.x + ux * along, point.y + uy * along)
        // Each index renders one of the markers (One/Zero/Many).
        val part = parts.elementAt(index)
        when (part) {
            ErCardinalityPart.One -> {
                val half = 6.dp.toPx()
                drawLine(
                    color = color,
                    start = Offset(center.x + px * half, center.y + py * half),
                    end = Offset(center.x - px * half, center.y - py * half),
                    strokeWidth = strokeWidth,
                    cap = StrokeCap.Round,
                )
            }
            ErCardinalityPart.Zero -> {
                drawCircle(
                    color = color,
                    radius = radius,
                    center = center,
                    style = Stroke(width = strokeWidth),
                )
            }
            ErCardinalityPart.Many -> {
                val tip = Offset(center.x + ux * foot, center.y + uy * foot)
                drawLine(color = color, start = Offset(center.x + px * foot, center.y + py * foot), end = tip, strokeWidth = strokeWidth, cap = StrokeCap.Round)
                drawLine(color = color, start = Offset(center.x - px * foot, center.y - py * foot), end = tip, strokeWidth = strokeWidth, cap = StrokeCap.Round)
            }
        }
    }
}

// ── Class diagram UML markers ─────────────────────────────────────────

internal enum class ClassMarkerKind {
    Triangle, // inheritance / realization (hollow triangle at the parent end)
    Diamond,  // composition (filled) / aggregation (hollow) at the whole end
    Arrow,    // dependency / association (open V arrow)
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawClassMarker(
    color: Color,
    tip: Offset,
    ux: Float,
    uy: Float,
    px: Float,
    py: Float,
    size: Float,
    filled: Boolean,
    kind: ClassMarkerKind,
) {
    val stroke = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
    when (kind) {
        ClassMarkerKind.Triangle -> {
            // Tip touches the node; base sits `1.4*size` back along the edge.
            val back = 1.4f * size
            val half = 0.7f * size
            val base = Offset(tip.x - ux * back, tip.y - uy * back)
            val left = Offset(base.x + px * half, base.y + py * half)
            val right = Offset(base.x - px * half, base.y - py * half)
            val path = Path().apply {
                moveTo(tip.x, tip.y)
                lineTo(left.x, left.y)
                lineTo(right.x, right.y)
                close()
            }
            drawPath(path = path, color = color, style = if (filled) Fill else stroke)
        }
        ClassMarkerKind.Diamond -> {
            // Long axis along the edge; one vertex at `tip` (node edge), extending outward.
            val lenOut = 1.6f * size
            val half = 0.55f * size
            val outer = Offset(tip.x - ux * lenOut, tip.y - uy * lenOut)
            val midBack = Offset(tip.x - ux * (lenOut / 2f), tip.y - uy * (lenOut / 2f))
            val left = Offset(midBack.x + px * half, midBack.y + py * half)
            val right = Offset(midBack.x - px * half, midBack.y - py * half)
            val path = Path().apply {
                moveTo(tip.x, tip.y)
                lineTo(left.x, left.y)
                lineTo(outer.x, outer.y)
                lineTo(right.x, right.y)
                close()
            }
            drawPath(path = path, color = color, style = if (filled) Fill else stroke)
        }
        ClassMarkerKind.Arrow -> {
            val half = 0.55f * size
            val back = Offset(tip.x - ux * size, tip.y - uy * size)
            drawLine(color = color, start = Offset(back.x + px * half, back.y + py * half), end = tip, strokeWidth = 2.dp.toPx(), cap = StrokeCap.Round)
            drawLine(color = color, start = Offset(back.x - px * half, back.y - py * half), end = tip, strokeWidth = 2.dp.toPx(), cap = StrokeCap.Round)
        }
    }
}

// ── State diagram terminal markers ────────────────────────────────────

@Composable
private fun StateStartDot(
    x: Dp,
    y: Dp,
    size: Dp,
    color: Color,
) {
    Canvas(
        modifier = Modifier
            .absoluteOffset(x = x, y = y)
            .size(size),
    ) {
        drawCircle(color = color, radius = size.toPx() / 2f)
    }
}

@Composable
private fun StateEndDoubleCircle(
    x: Dp,
    y: Dp,
    size: Dp,
    borderColor: Color,
    fillColor: Color,
) {
    Canvas(
        modifier = Modifier
            .absoluteOffset(x = x, y = y)
            .size(size),
    ) {
        val px = size.toPx()
        // Outer ring.
        drawCircle(
            color = borderColor,
            radius = px / 2f,
            style = Stroke(width = 1.5.dp.toPx()),
        )
        // Inner filled dot — the classic "bullseye" terminal state.
        drawCircle(color = fillColor, radius = px / 4.5f)
    }
}
