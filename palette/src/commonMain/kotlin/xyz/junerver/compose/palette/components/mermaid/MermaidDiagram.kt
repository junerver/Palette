package xyz.junerver.compose.palette.components.mermaid

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
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
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn
import xyz.junerver.compose.hooks.useCreation
import xyz.junerver.compose.palette.core.theme.PaletteTheme
import xyz.junerver.compose.palette.mermaid.ErEntity
import xyz.junerver.compose.palette.mermaid.ErRelationship
import xyz.junerver.compose.palette.mermaid.ErRelationshipKind
import xyz.junerver.compose.palette.mermaid.ClassEdgeAnchors
import xyz.junerver.compose.palette.mermaid.ClassEdgeGeometry
import xyz.junerver.compose.palette.mermaid.ClassMarkerSide
import xyz.junerver.compose.palette.mermaid.GanttConfig
import xyz.junerver.compose.palette.mermaid.GanttSection
import xyz.junerver.compose.palette.mermaid.GanttTask
import xyz.junerver.compose.palette.mermaid.GanttTaskStatus
import xyz.junerver.compose.palette.mermaid.GitBranch
import xyz.junerver.compose.palette.mermaid.GitCommit
import xyz.junerver.compose.palette.mermaid.GitCommitType
import xyz.junerver.compose.palette.mermaid.GitMerge
import xyz.junerver.compose.palette.mermaid.MermaidClassDefinition
import xyz.junerver.compose.palette.mermaid.MermaidClassMemberKind
import xyz.junerver.compose.palette.mermaid.MermaidClassRelationType
import xyz.junerver.compose.palette.mermaid.MermaidClassVisibility
import xyz.junerver.compose.palette.mermaid.MermaidDirection
import xyz.junerver.compose.palette.mermaid.MermaidDiagramType
import xyz.junerver.compose.palette.mermaid.MermaidEdgeArrow
import xyz.junerver.compose.palette.mermaid.MermaidEdge
import xyz.junerver.compose.palette.mermaid.MermaidEdgeStyle
import xyz.junerver.compose.palette.mermaid.MermaidLayout
import xyz.junerver.compose.palette.mermaid.MermaidLayoutEngine
import xyz.junerver.compose.palette.mermaid.MermaidNodeShape
import xyz.junerver.compose.palette.mermaid.MermaidNote
import xyz.junerver.compose.palette.mermaid.MermaidNotePosition
import xyz.junerver.compose.palette.mermaid.MermaidParser
import xyz.junerver.compose.palette.mermaid.MindmapNode
import xyz.junerver.compose.palette.mermaid.MindmapNodeShape
import xyz.junerver.compose.palette.mermaid.PieSlice
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
    fromShape: MermaidNodeShape = MermaidNodeShape.Rectangle,
    toShape: MermaidNodeShape = MermaidNodeShape.Rectangle,
): MermaidEdgeEndpoints {
    // Diamond fan-out: when a decision node branches to two children laid out left/right of
    // its center, mermaid.live starts the connectors at the diamond's lower-edge midpoints
    // (¼ and ¾ across), not its bottom vertex. We pick the side by comparing the target's
    // horizontal center to the source's — left target → left midpoint, right target → right
    // midpoint, directly-below target → bottom vertex.
    val diamondDownStart: Offset? =
        if (direction == MermaidDirection.TopDown && fromShape == MermaidNodeShape.Diamond) {
            val sourceCenterX = fromX + nodeWidth / 2f
            val targetCenterX = toX + nodeWidth / 2f
            when {
                targetCenterX < sourceCenterX - 1f -> Offset(fromX + nodeWidth / 4f, fromY + nodeHeight * 0.75f)
                targetCenterX > sourceCenterX + 1f -> Offset(fromX + nodeWidth * 0.75f, fromY + nodeHeight * 0.75f)
                else -> null // directly below → keep the bottom vertex
            }
        } else {
            null
        }

    return when (direction) {
        MermaidDirection.TopDown ->
            MermaidEdgeEndpoints(
                start = diamondDownStart ?: Offset(fromX + nodeWidth / 2f, fromY + nodeHeight),
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

        MermaidDiagramType.PieDiagram ->
            PieDiagramMermaidDiagram(
                modifier = modifier,
                colors = colors,
                title = parsedDiagram?.title,
                slices = parsedDiagram?.pieSlices.orEmpty(),
                showData = parsedDiagram?.pieShowData ?: false,
            )

        MermaidDiagramType.GanttDiagram ->
            GanttDiagramMermaidDiagram(
                modifier = modifier,
                colors = colors,
                config = parsedDiagram?.ganttConfig,
                sections = parsedDiagram?.ganttSections.orEmpty(),
            )

        MermaidDiagramType.GitGraphDiagram ->
            GitGraphDiagramMermaidDiagram(
                modifier = modifier,
                colors = colors,
                branches = parsedDiagram?.gitBranches.orEmpty(),
                commits = parsedDiagram?.gitCommits.orEmpty(),
                merges = parsedDiagram?.gitMerges.orEmpty(),
            )

        MermaidDiagramType.MindmapDiagram ->
            MindmapDiagramMermaidDiagram(
                modifier = modifier,
                colors = colors,
                layout = resolvedLayout,
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
            // mermaid.live draws clusters as dashed outlines around their members. A
            // dashed border needs a Canvas/DrawScope (Modifier.border only does solid),
            // so the box uses a faint fill + a dashed-stroke rectangle drawn behind it.
            Box(
                modifier =
                    Modifier
                        .absoluteOffset(x = subgraph.x.dp, y = subgraph.y.dp)
                        .size(width = subgraph.width.dp, height = subgraph.height.dp)
                        .background(colors.nodeContainerColor.copy(alpha = 0.18f), RoundedCornerShape(8.dp))
                        .drawWithContent {
                            drawContent()
                            val outline = colors.nodeBorderColor.copy(alpha = 0.7f)
                            drawRoundRect(
                                color = outline,
                                topLeft = Offset(0.5f, 0.5f),
                                size = androidx.compose.ui.geometry.Size(size.width - 1f, size.height - 1f),
                                cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx()),
                                style = Stroke(
                                    width = 1.dp.toPx(),
                                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 5f)),
                                ),
                            )
                        }
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
                        fromShape = from.node.shape,
                        toShape = to.node.shape,
                    )
                val pathEffect =
                    if (edge.style == MermaidEdgeStyle.Dotted) {
                        PathEffect.dashPathEffect(floatArrayOf(4f, 5f))
                    } else {
                        null
                    }
                // All flowchart edges render as cubic Bézier curves — same style dagre and
                // mermaid.live use, and the same style our state diagram uses. Control points
                // sit on the edge midpoint of the primary axis (so the curve is a gentle S),
                // with the perpendicular axis held at each endpoint's coordinate.
                //
                // `offset` (stateEdgeOffsets) fans out edges that share an endpoint pair:
                // with offset 0 the curve is a soft S; non-zero offsets bow forward/backward
                // links sideways so they no longer overlap into a single line.
                val s = endpoints.start
                val e = endpoints.end
                val bow = (layout.stateEdgeOffsets[index] ?: 0f).dp.toPx()
                val isHorizontalFlow = layout.direction == MermaidDirection.LeftRight || layout.direction == MermaidDirection.RightLeft
                val ctrl1: Offset
                val ctrl2: Offset
                if (isHorizontalFlow) {
                    // Primary axis is x: pull both control points to the horizontal midpoint,
                    // keep each end's y (plus a perpendicular bow on x).
                    val midX = (s.x + e.x) / 2f
                    ctrl1 = Offset(midX + bow, s.y)
                    ctrl2 = Offset(midX + bow, e.y)
                } else {
                    // Primary axis is y (TD/BT): pull both control points to the vertical
                    // midpoint, keep each end's x (plus a perpendicular bow on y).
                    val midY = (s.y + e.y) / 2f
                    ctrl1 = Offset(s.x, midY + bow)
                    ctrl2 = Offset(e.x, midY + bow)
                }
                val path = Path().apply {
                    moveTo(s.x, s.y)
                    cubicTo(ctrl1.x, ctrl1.y, ctrl2.x, ctrl2.y, e.x, e.y)
                }
                drawPath(path = path, color = edgeColor, style = Stroke(width = edgeStrokeWidth, cap = StrokeCap.Round, pathEffect = pathEffect))
                // Arrowheads follow the curve's tangents so they stay tangent to the arc
                // (ctrl2 -> end for the main arrow, ctrl1 -> start for the source arrow).
                drawMermaidEdgeEndMarker(
                    color = edgeColor,
                    start = ctrl2,
                    end = e,
                    arrow = edge.arrow,
                    strokeWidth = edgeStrokeWidth,
                )
                drawMermaidEdgeEndMarker(
                    color = edgeColor,
                    start = ctrl1,
                    end = s,
                    arrow = edge.startArrow,
                    strokeWidth = edgeStrokeWidth,
                )
                if (edge.arrow == MermaidEdgeArrow.Bidirectional) {
                    drawMermaidEdgeEndMarker(
                        color = edgeColor,
                        start = ctrl1,
                        end = s,
                        arrow = MermaidEdgeArrow.Forward,
                        strokeWidth = edgeStrokeWidth,
                    )
                }
            }
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

        // Edge labels are drawn LAST (above nodes) so a label sitting near a node boundary is
        // never erased by the node's fill — mermaid.live renders labels on top of nodes too.
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

// Half of the uniform flowchart node footprint (132×44). Edge-label midpoint math uses node
// centres, which are these offsets away from the stored top-left coordinates.
private const val FlowchartLabelNodeHalfWidth = 66f
private const val FlowchartLabelNodeHalfHeight = 22f
// Two labels collide only when their anchors are within this horizontal distance. Tuned to
// the real label width so diverging fan-out labels (~90px apart) are not falsely merged.
private const val FlowchartLabelCollisionX = 60f

private fun calculateFlowchartEdgeLabelPositions(layout: MermaidLayout): Map<Int, MermaidEdgeLabelPosition> {
    val placed = mutableListOf<MermaidEdgeLabelPosition>()
    val positions = mutableMapOf<Int, MermaidEdgeLabelPosition>()
    // Label is anchored at its top-left; nudge it so its center sits at the edge midpoint.
    val labelHalfWidth = 18f
    val labelHalfHeight = 8f

    layout.edges.forEachIndexed { index, edge ->
        if (edge.label == null) return@forEachIndexed
        val from = layout.nodes[edge.from] ?: return@forEachIndexed
        val to = layout.nodes[edge.to] ?: return@forEachIndexed
        // Use node centres (not top-left corners) so the label sits on the connector's true
        // midpoint rather than drifting toward whichever node has the smaller x/y.
        val midX = (from.x + to.x) / 2f + FlowchartLabelNodeHalfWidth
        var midY = (from.y + to.y) / 2f + FlowchartLabelNodeHalfHeight
        var labelX = midX - labelHalfWidth
        var labelY = midY - labelHalfHeight

        // Avoid stacking labels on top of each other. The x threshold matches the real label
        // width (~60px) so diverging fan-out labels (e.g. a diamond branching left/right) —
        // which sit ~90px apart but never actually overlap — aren't forced down into nodes.
        while (placed.any { abs(it.x - labelX) < FlowchartLabelCollisionX && abs(it.y - labelY) < 24f }) {
            labelY += 18f
        }

        val position = MermaidEdgeLabelPosition(labelX.coerceAtLeast(0f), labelY.coerceAtLeast(0f))
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
            // Keep the label pill narrow (text-sized) so it doesn't blanket the arrow line;
            // a full-span width would erase the connector entirely.
            val labelWidth = (right - left).coerceIn(132f, 200f)
            // Arrow runs horizontally at this y between the two lifeline centres. The label
            // sits ABOVE the line (so the line stays visible) but is horizontally centred on
            // the arrow's midpoint — mirroring mermaid.live's alignment.
            val arrowY = messageStartY + edge.sequenceIndex * messageGap
            val arrowCenterX = (from.x + to.x) / 2f + 66f

            Text(
                text = label,
                color = colors.nodeContentColor,
                style = PaletteTheme.typography.label,
                textAlign = TextAlign.Center,
                modifier =
                    Modifier
                        .absoluteOffset(
                            x = (arrowCenterX - labelWidth / 2f).coerceAtLeast(0f).dp,
                            y = (arrowY - SequenceLabelLift).coerceAtLeast(48f).dp,
                        )
                        .width(labelWidth.dp)
                        // No border (mermaid.live renders message labels as plain text on the
                        // line); keep a solid background so the connector is masked behind it.
                        .background(colors.nodeContainerColor, RoundedCornerShape(4.dp))
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
            // Top participant box (at item.y = 0).
            SequenceParticipantBox(
                x = item.x,
                y = item.y,
                label = item.node.label,
                nodeWidth = nodeWidth,
                nodeHeight = nodeHeight,
                nodeShape = nodeShape,
                colors = colors,
            )
            // Bottom participant box: mermaid.live repeats each participant at the foot of its
            // lifeline. Dock it just above the canvas bottom, mirroring the top box.
            SequenceParticipantBox(
                x = item.x,
                y = (height.value - nodeHeight.value - SequenceBottomBoxMargin).coerceAtLeast(item.y),
                label = item.node.label,
                nodeWidth = nodeWidth,
                nodeHeight = nodeHeight,
                nodeShape = nodeShape,
                colors = colors,
            )
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

    val defaultNodeHeight = headerHeight + padding * 2

    val nodeHeights = classDefinitions.associate { cls ->
        cls.id to (headerHeight + memberHeight * cls.members.size + padding * 2)
    }
    fun heightOf(id: String): Dp = nodeHeights[id] ?: defaultNodeHeight

    val nodeRight = (layout.nodes.values.maxOfOrNull { it.x } ?: 0f) + nodeWidth.value + 24f
    val nodeBottom = layout.nodes.entries.maxOfOrNull { (_, n) -> n.y + heightOf(n.node.id).value + 24f } ?: 48f
    val width = nodeRight.dp
    val height = nodeBottom.dp

    // Fan-out: when several children inherit from / associate to one parent, their parent-side
    // anchors must spread across the parent's bottom edge instead of stacking on the center.
    // `edge.from` is the parent (mermaid `Parent <|-- Child`). Group siblings, then assign each
    // edge a (index, count) pair the geometry helper fans out with.
    val parentFanGroups: Map<String, List<Int>> = layout.edges.withIndex()
        .groupBy { it.value.from }
        .mapValues { (_, list) -> list.map { it.index } }
    val edgeFanIndex = HashMap<Int, Int>()
    val edgeFanCount = HashMap<Int, Int>()
    parentFanGroups.values.forEach { indices ->
        indices.forEachIndexed { pos, edgeIdx ->
            edgeFanIndex[edgeIdx] = pos
            edgeFanCount[edgeIdx] = indices.size
        }
    }

    /**
     * Edge anchors in pixels for a class-diagram edge: child's top-center → parent's bottom
     * edge (already fanned out), plus the marker direction vectors. Delegates to
     * [ClassEdgeGeometry] so the UML contract is unit-tested, not duplicated inline.
     */
    fun anchorsForEdge(index: Int, edge: MermaidEdge, relType: MermaidClassRelationType): ClassEdgeAnchors? {
        val parent = layout.nodes[edge.from] ?: return null
        val child = layout.nodes[edge.to] ?: return null
        return ClassEdgeGeometry.anchorsFor(
            child = child,
            parent = parent,
            childWidth = nodeWidth.value,
            childHeight = heightOf(edge.to).value,
            parentHeight = heightOf(edge.from).value,
            fanIndex = edgeFanIndex[index] ?: 0,
            fanCount = edgeFanCount[index] ?: 1,
            nodeWidth = nodeWidth.value,
            relationType = relType,
        )
    }

    Box(
        modifier = modifier.width(width).height(height),
    ) {
        // Pass 1: draw Bezier curves (behind everything).
        // Edge direction: child top → parent bottom (child→parent). In mermaid `A <|-- B`,
        // from=A (parent), to=B (child); we draw from the child's top edge up to the parent's
        // bottom edge, with the parent anchor fanned out across siblings.
        Canvas(modifier = Modifier.matchParentSize()) {
            val strokeWidth = 2.dp.toPx()
            val marker = 10.dp.toPx()
            layout.edges.forEachIndexed { index, edge ->
                val relType = layout.classRelationTypes[index] ?: return@forEachIndexed
                val anchors = anchorsForEdge(index, edge, relType) ?: return@forEachIndexed
                val pathEffect = if (edge.style == MermaidEdgeStyle.Dotted) {
                    PathEffect.dashPathEffect(floatArrayOf(6f, 6f))
                } else {
                    null
                }

                // Start = child top-center (px); end = parent bottom edge, fanned out (px).
                val startX = anchors.startX.dp.toPx()
                val startY = anchors.startY.dp.toPx()
                val endX = anchors.endX.dp.toPx()
                val endY = anchors.endY.dp.toPx()

                // Pull the curve back at the marker end so it meets the marker base, not its tip.
                // Marker shape length: diamonds are a touch longer than triangles/arrows.
                val shorten = when (relType) {
                    MermaidClassRelationType.Composition, MermaidClassRelationType.Aggregation -> 1.6f
                    else -> 1.4f
                }
                val side = ClassEdgeGeometry.markerSide(relType)
                // Marker body points down (u=(0,-1)); `tip - u*size` lands below — shorten toward child.
                val drawEndY = if (side == ClassMarkerSide.Parent) endY - anchors.endUy * marker * shorten else endY
                val drawStartY = if (side == ClassMarkerSide.Child) startY - anchors.startUy * marker * shorten else startY

                // Cubic Bézier: vertical control points give a smooth S-curve between the two edges.
                val shortMidY = (drawStartY + drawEndY) / 2f
                val shortPath = Path().apply {
                    moveTo(startX, drawStartY)
                    cubicTo(startX, shortMidY, endX, shortMidY, endX, drawEndY)
                }
                drawPath(
                    path = shortPath,
                    color = colors.edgeColor,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round, pathEffect = pathEffect),
                )
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

        // Pass 2: draw UML markers ON TOP of node boxes (so they're never hidden by backgrounds).
        // The marker sits on whichever end [ClassEdgeGeometry.markerSide] picks, body pointing
        // DOWN: inheritance/realization/composition/aggregation → parent's bottom edge;
        // association/dependency → child's top edge.
        Canvas(modifier = Modifier.matchParentSize()) {
            layout.edges.forEachIndexed { index, edge ->
                val relType = layout.classRelationTypes[index] ?: return@forEachIndexed
                val anchors = anchorsForEdge(index, edge, relType) ?: return@forEachIndexed
                val marker = 10.dp.toPx()
                val side = ClassEdgeGeometry.markerSide(relType)

                // tip + u/p vectors (anchors hold dp-valued floats; convert via .dp.toPx()).
                val tipX: Float
                val tipY: Float
                val ux: Float
                val uy: Float
                val px: Float
                val py: Float
                if (side == ClassMarkerSide.Parent) {
                    tipX = anchors.endX; tipY = anchors.endY
                    ux = anchors.endUx; uy = anchors.endUy
                    px = anchors.endPx; py = anchors.endPy
                } else {
                    tipX = anchors.startX; tipY = anchors.startY
                    ux = anchors.startUx; uy = anchors.startUy
                    px = anchors.startPx; py = anchors.startPy
                }
                val tipXpx = tipX.dp.toPx()
                val tipYpx = tipY.dp.toPx()

                when (relType) {
                    MermaidClassRelationType.Inheritance, MermaidClassRelationType.Realization -> {
                        // Hollow triangle, apex on the parent edge, base hanging down into the gap.
                        drawClassMarker(colors.edgeColor, Offset(tipXpx, tipYpx), ux, uy, px, py, marker, filled = false, kind = ClassMarkerKind.Triangle)
                    }
                    MermaidClassRelationType.Composition -> {
                        // Filled diamond at the whole (parent).
                        drawClassMarker(colors.edgeColor, Offset(tipXpx, tipYpx), ux, uy, px, py, marker, filled = true, kind = ClassMarkerKind.Diamond)
                    }
                    MermaidClassRelationType.Aggregation -> {
                        // Hollow diamond at the whole (parent).
                        drawClassMarker(colors.edgeColor, Offset(tipXpx, tipYpx), ux, uy, px, py, marker, filled = false, kind = ClassMarkerKind.Diamond)
                    }
                    MermaidClassRelationType.Dependency, MermaidClassRelationType.Association -> {
                        // Open V arrow at the target (child), pointing down into it.
                        drawClassMarker(colors.edgeColor, Offset(tipXpx, tipYpx), ux, uy, px, py, marker, filled = false, kind = ClassMarkerKind.Arrow)
                    }
                    else -> Unit
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

// Vertical margin between the bottom participant box and the canvas floor.
private const val SequenceBottomBoxMargin = 8f
// How far an edge label is lifted above its arrow line, so the connector stays visible.
private const val SequenceLabelLift = 24f
// Approx half-height of a note pill; used to vertically centre a note on its own row.
private const val SequenceNoteHalfHeight = 14f

/** A participant box (top or bottom of a lifeline). Shared by both copies so they stay identical. */
@Composable
private fun SequenceParticipantBox(
    x: Float,
    y: Float,
    label: String,
    nodeWidth: Dp,
    nodeHeight: Dp,
    nodeShape: Shape,
    colors: MermaidColors,
) {
    Box(
        modifier =
            Modifier
                .absoluteOffset(x = x.dp, y = y.dp)
                .size(width = nodeWidth, height = nodeHeight)
                .background(colors.nodeContainerColor, nodeShape)
                .border(1.dp, colors.nodeBorderColor, nodeShape)
                .padding(horizontal = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            color = colors.nodeContentColor,
            style = PaletteTheme.typography.label,
            textAlign = TextAlign.Center,
        )
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
    // A note occupies its own row (no arrow runs through it), so centre it vertically on that
    // row instead of lifting it above — matching mermaid.live's inline note placement.
    val noteY = messageStartY + note.sequenceIndex * messageGap - SequenceNoteHalfHeight
    // mermaid.live notes have a distinctive folded top-right corner (dog-ear) and a warm fill,
    // not the plain rectangle used for participants.
    val noteShape = SequenceNoteShape
    val noteFill = colors.noteColor.takeIf { it != Color.Unspecified } ?: colors.nodeContainerColor
    val noteStroke = colors.noteBorderColor.takeIf { it != Color.Unspecified } ?: colors.nodeBorderColor

    Text(
        text = note.text,
        color = colors.nodeContentColor,
        style = PaletteTheme.typography.label,
        textAlign = TextAlign.Center,
        modifier =
            Modifier
                .absoluteOffset(x = frame.x.dp, y = noteY.dp)
                .width(frame.width.dp)
                .background(noteFill, noteShape)
                .border(1.dp, noteStroke, noteShape)
                .padding(horizontal = 8.dp, vertical = 6.dp)
                // Reserve a little room on the right so text clears the folded corner.
                .padding(end = 6.dp),
    )
}

/** A note shape with a folded top-right corner (dog-ear), the mermaid.live note silhouette.
 *  The fold is a fixed fraction of the box so it scales with the note size. */
private val SequenceNoteShape: Shape =
    GenericShape { size, _ ->
        val w = size.width
        val h = size.height
        val fold = minOf(w, h) * 0.22f
        moveTo(0f, 0f)
        lineTo(w - fold, 0f)
        lineTo(w, fold)
        lineTo(w, h)
        lineTo(0f, h)
        close()
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
    // Lifelines in display order (left→right) so a "right of X" note can find X's neighbour.
    val lifelines = layout.nodes.values.sortedBy { it.x }
    val subject = participantPositions.first()
    val subjectIndex = lifelines.indexOfFirst { it.node.id == subject.node.id }

    val noteWidth =
        when (position) {
            MermaidNotePosition.Over -> {
                val left = participantPositions.minOf { it.x }
                val right = participantPositions.maxOf { it.x }
                (right - left + nodeWidth).coerceAtLeast(132f)
            }

            MermaidNotePosition.LeftOf,
            MermaidNotePosition.RightOf,
            // Narrower than a full node so it fits in the gap between two adjacent lifelines.
            -> 96f
        }
    val noteX =
        when (position) {
            MermaidNotePosition.Over -> participantPositions.minOf { it.x }
            // Centre the note on the midpoint between the subject lifeline and its nearest
            // neighbour on the chosen side — mirroring mermaid.live, which parks the note in
            // that gap. No hard clamp: a centred note may overlap the subject box edge, which
            // is fine (the note renders above the lifeline connectors).
            MermaidNotePosition.RightOf -> {
                val neighbour = lifelines.getOrNull(subjectIndex + 1)
                val subjectCenter = subject.x + nodeWidth / 2f
                val rightBound = neighbour?.let { it.x + nodeWidth / 2f } ?: (subject.x + nodeWidth)
                (subjectCenter + rightBound) / 2f - noteWidth / 2f
            }
            MermaidNotePosition.LeftOf -> {
                val neighbour = lifelines.getOrNull(subjectIndex - 1)
                val subjectCenter = subject.x + nodeWidth / 2f
                val leftBound = neighbour?.let { it.x + nodeWidth / 2f } ?: 0f
                ((leftBound + subjectCenter) / 2f - noteWidth / 2f).coerceAtLeast(0f)
            }
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

// ── Pie chart rendering ───────────────────────────────────────────────

@Composable
private fun PieDiagramMermaidDiagram(
    modifier: Modifier,
    colors: MermaidColors,
    title: String?,
    slices: List<PieSlice>,
    showData: Boolean,
) {
    if (slices.isEmpty()) {
        Box(modifier = modifier, contentAlignment = Alignment.Center) {
            Text("Empty pie chart", color = colors.nodeContentColor, style = PaletteTheme.typography.body)
        }
        return
    }

    val total = slices.sumOf { it.value }.coerceAtLeast(1.0)
    val diameter = 240.dp
    val padding = 16.dp
    val titleHeight: Dp = if (title != null) 32.dp else 0.dp
    val legendWidth = 180.dp
    val width = diameter + legendWidth + padding * 3
    val pieHeight = diameter + titleHeight + padding * 2
    val legendHeight = (slices.size * 28).dp + padding * 2
    val height = if (pieHeight >= legendHeight) pieHeight else legendHeight

    // A reusable palette of slice colors derived from the global theme tokens, cycled when
    // there are more slices than colors.
    val sliceColors = rememberSliceColors()
    val arcCenter = Offset(
        x = padding.value + diameter.value / 2f,
        y = titleHeight.value + padding.value + diameter.value / 2f,
    )

    Box(modifier = modifier.width(width).height(height)) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val radius = diameter.toPx() / 2f
            val centerPx = Offset(arcCenter.x.dp.toPx(), arcCenter.y.dp.toPx())
            var startAngle = -90f // start at 12 o'clock, like mermaid
            slices.forEachIndexed { index, slice ->
                val sweep = (slice.value / total * 360f).toFloat()
                val color = sliceColors[index % sliceColors.size]
                drawArc(
                    color = color,
                    startAngle = startAngle,
                    sweepAngle = sweep,
                    useCenter = true,
                    topLeft = Offset(centerPx.x - radius, centerPx.y - radius),
                    size = androidx.compose.ui.geometry.Size(diameter.toPx(), diameter.toPx()),
                )
                // Slice separator stroke.
                drawArc(
                    color = colors.nodeContainerColor,
                    startAngle = startAngle,
                    sweepAngle = sweep,
                    useCenter = true,
                    topLeft = Offset(centerPx.x - radius, centerPx.y - radius),
                    size = androidx.compose.ui.geometry.Size(diameter.toPx(), diameter.toPx()),
                    style = Stroke(width = 2.dp.toPx()),
                )
                startAngle += sweep
            }
        }

        if (title != null) {
            Text(
                text = title,
                color = colors.nodeContentColor,
                style = PaletteTheme.typography.body,
                modifier = Modifier
                    .absoluteOffset(x = padding, y = padding)
                    .width(diameter + legendWidth),
                textAlign = TextAlign.Center,
            )
        }

        // Legend on the right.
        Column(
            modifier = Modifier
                .absoluteOffset(x = padding + diameter + padding, y = titleHeight + padding)
                .width(legendWidth),
        ) {
            slices.forEachIndexed { index, slice ->
                val color = sliceColors[index % sliceColors.size]
                val percentage = "%.1f%%".format(slice.value / total * 100)
                val label = if (showData) {
                    "${slice.label} : ${slice.value} ($percentage)"
                } else {
                    "${slice.label} ($percentage)"
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(color),
                    )
                    Text(
                        text = label,
                        color = colors.nodeContentColor,
                        style = PaletteTheme.typography.label,
                        modifier = Modifier.padding(start = 8.dp),
                    )
                }
            }
        }
    }
}

/**
 * A deterministic cycle of slice colors derived from the global theme so pie charts stay
 * on-brand and adapt to light/dark mode.
 */
@Composable
private fun rememberSliceColors(): List<Color> {
    val c = PaletteTheme.colors
    // Base palette from existing semantic tokens, extended with alpha variants so charts
    // with many slices still get distinct colors without introducing new top-level tokens.
    val base = listOf(c.primary, c.success, c.warning, c.error)
        .filter { it != Color.Unspecified }
        .ifEmpty { listOf(c.textPrimary) }
    return base + base.map { it.copy(alpha = 0.65f) } + base.map { it.copy(alpha = 0.40f) }
}

// ── Gantt chart rendering ─────────────────────────────────────────────

/**
 * A task resolved to a real date span on the global timeline. [start] and [end] are calendar
 * dates; the renderer maps them to pixels via the timeline's day-offset and pixels-per-day.
 */
private data class PositionedGanttTask(
    val task: GanttTask,
    val start: LocalDate,
    val end: LocalDate,
)

/** A vertical band of the timeline used to draw alternating-column shading behind bars. */
private data class GanttTickColumn(
    val date: LocalDate,
    /** Pixel x (relative to timeline origin) where this column's left edge sits. */
    val x: Float,
    val width: Float,
)

/**
 * Parse a mermaid gantt date token into a [LocalDate]. Mermaid's canonical `dateFormat
 * YYYY-MM-DD` is ISO-8601, so `LocalDate.parse` handles it directly; non-ISO / unrecognised
 * tokens fall back to null and the caller resolves them as durations or dependency offsets.
 */
private fun parseGanttDate(token: String?): LocalDate? {
    if (token.isNullOrBlank()) return null
    // `after` deps are resolved by the caller (they reference another task's end), not here.
    if (token.equals("after", ignoreCase = true)) return null
    return runCatching { LocalDate.parse(token.trim()) }.getOrNull()
}

/**
 * Format a [LocalDate] for the time-axis label per mermaid's `axisFormat` (`strftime`-style
 * placeholders). Supported tokens: `%Y` (4-digit year), `%m` (zero-padded month), `%d`
 * (zero-padded day), `%b` (short English month name), `%B` (full English month name). An
 * absent or unrecognised format falls back to ISO (`date.toString()`).
 */
private fun formatGanttAxisLabel(date: LocalDate, axisFormat: String?): String {
    val fmt = axisFormat?.takeIf { it.isNotBlank() } ?: return date.toString()
    if (!fmt.contains('%')) return date.toString()
    val monthsShort = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
    val monthsLong = listOf(
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December",
    )
    val sb = StringBuilder()
    var i = 0
    while (i < fmt.length) {
        val ch = fmt[i]
        if (ch == '%' && i + 1 < fmt.length) {
            when (fmt[i + 1]) {
                'Y' -> sb.append(date.year)
                'm' -> sb.append(date.monthNumber.toString().padStart(2, '0'))
                'd' -> sb.append(date.dayOfMonth.toString().padStart(2, '0'))
                'b' -> sb.append(monthsShort.getOrElse(date.monthNumber - 1) { date.monthNumber.toString() })
                'B' -> sb.append(monthsLong.getOrElse(date.monthNumber - 1) { date.monthNumber.toString() })
                else -> { sb.append('%').append(fmt[i + 1]) }
            }
            i += 2
        } else {
            sb.append(ch)
            i += 1
        }
    }
    return sb.toString()
}

/**
 * Decide the axis tick step (in days) from the timeline's total span so labels stay legible
 * without crowding. Short charts tick daily, week-scale charts weekly, long charts monthly.
 */
private fun ganttTickStepDays(totalDays: Int): Int = when {
    totalDays <= 14 -> 1 // day-by-day
    totalDays <= 90 -> 7 // weekly
    totalDays <= 730 -> 30 // ~monthly
    else -> 90 // quarterly
}

/** Pixels per day, scaled so wide timelines still fit a reasonable width. */
private fun ganttPixelsPerDay(totalDays: Int): Float = when {
    totalDays <= 14 -> 22f
    totalDays <= 60 -> 11f
    totalDays <= 365 -> 5f
    else -> 2.5f
}

/** Index positioned tasks by identity for O(1) lookup during section rendering. */
private fun positionedByTask(positioned: List<PositionedGanttTask>): Map<GanttTask, PositionedGanttTask> =
    positioned.associateBy { it.task }

@Composable
private fun GanttDiagramMermaidDiagram(
    modifier: Modifier,
    colors: MermaidColors,
    config: GanttConfig?,
    sections: List<GanttSection>,
) {
    val allTasks = sections.flatMap { it.tasks }
    if (allTasks.isEmpty()) {
        Box(modifier = modifier, contentAlignment = Alignment.Center) {
            Text("Empty gantt chart", color = colors.nodeContentColor, style = PaletteTheme.typography.body)
        }
        return
    }

    // ── Pass 1: resolve each task to a real (start,end) date span ──────────────────
    // Mermaid semantics: a task's start is its declared date, or the end of an `after`
    // dependency; its end is start + duration (or a declared end date). Tasks with no date
    // and no dep fall back to chaining after the previous task in their section, matching
    // mermaid.live's implicit "next" behaviour.
    val taskEndDate = mutableMapOf<String, LocalDate>()
    val positioned = mutableListOf<PositionedGanttTask>()
    // A GLOBAL chain cursor: a task with no explicit date and no `after` dep chains after the
    // previous task's end (even across section boundaries) — this is mermaid.live's actual
    // semantics. The very first such task falls back to "today" (mermaid's default start date).
    var chainCursor: LocalDate? = null

    sections.forEach { section ->
        section.tasks.forEach { task ->
            // Start: explicit date > `after` dep > global chain cursor > today.
            val explicitStart = parseGanttDate(task.startToken)
            val depEnd = task.dependsOn.mapNotNull { taskEndDate[it] }.maxOrNull()
            val start: LocalDate = when {
                explicitStart != null -> explicitStart
                depEnd != null -> depEnd
                else -> chainCursor ?: kotlin.time.Clock.System.todayIn(TimeZone.currentSystemDefault())
            }
            // End: declared end date > start + duration; milestones are zero-width (start==end).
            val durationDays = task.durationDays
            val explicitEnd = parseGanttDate(task.endToken?.takeIf { durationDays == null })
            val end: LocalDate = when {
                task.isMilestone -> start
                explicitEnd != null -> explicitEnd
                durationDays != null -> start.plus(durationDays.toLong(), DateTimeUnit.DAY)
                else -> start.plus(1, DateTimeUnit.DAY)
            }
            // Ensure end >= start so degenerate tokens never produce negative-width bars.
            val safeEnd = if (end >= start) end else start.plus(1, DateTimeUnit.DAY)
            task.id?.let { taskEndDate[it] = safeEnd }
            positioned.add(PositionedGanttTask(task = task, start = start, end = safeEnd))
            chainCursor = safeEnd
        }
    }

    // ── Timeline bounds & scale ────────────────────────────────────────────────────
    val timelineMin = positioned.minOf { it.start }
    val timelineMax = positioned.maxOf { it.end }
    val totalDays = timelineMin.daysUntil(timelineMax).coerceAtLeast(1)
    val pixelsPerDay = ganttPixelsPerDay(totalDays)
    val tickStep = ganttTickStepDays(totalDays)
    val timelineWidth = totalDays * pixelsPerDay + pixelsPerDay // +1 day of trailing padding

    // Layout constants (geometric — kept as dp like the other diagram renderers).
    val labelColumn = 140.dp
    val padding = 16.dp
    val rowHeight = 24.dp
    val sectionHeaderHeight = 28.dp
    val sectionGap = 8.dp
    val axisHeight = 28.dp
    val titleHeight: Dp = if (config?.title != null) 32.dp else 0.dp

    // Pre-compute the pixel x for a date relative to the timeline's left edge.
    fun dateToX(date: LocalDate): Float = timelineMin.daysUntil(date).coerceAtLeast(0) * pixelsPerDay

    // Build the tick columns (for both axis labels and vertical grid + zebra shading).
    val tickColumns = buildList {
        var d = timelineMin
        while (d <= timelineMax) {
            val x = dateToX(d)
            val next = d.plus(tickStep, DateTimeUnit.DAY)
            val nextClamped = if (next > timelineMax) timelineMax.plus(1, DateTimeUnit.DAY) else next
            val w = (timelineMin.daysUntil(nextClamped) * pixelsPerDay - x).coerceAtLeast(1f)
            add(GanttTickColumn(date = d, x = x, width = w))
            d = next
        }
    }

    // Total canvas height: section headers + task rows + inter-section gaps.
    val sectionCount = sections.count { it.name.isNotEmpty() }
    val bodyHeight = titleHeight + axisHeight +
        (rowHeight * allTasks.size) +
        (sectionHeaderHeight * sectionCount) +
        (sectionGap * sections.size) +
        padding * 2
    val width = labelColumn + timelineWidth.dp + padding * 2
    val c = PaletteTheme.colors
    val statusColor = mapOf(
        GanttTaskStatus.Done to c.success,
        GanttTaskStatus.Active to c.warning,
        GanttTaskStatus.Todo to c.primary,
    )
    // Grid/shading tokens (all derived from top-level PaletteColors, no hard-coded hues).
    val gridColor = c.divider
    val axisLabelColor = c.hint
    val altShadeColor = c.bgHover
    val sectionHeaderBg = colors.entityHeaderColor.let { if (it == Color.Unspecified) c.bgHover else it }

    // Y offset where the timeline grid origin sits (top of axis labels).
    val gridOriginY = padding.value + titleHeight.value
    // Y offset where the task body starts (below the axis).
    val bodyOriginY = gridOriginY + axisHeight.value
    // Total vertical span the grid lines must cover (axis + all rows).
    val gridSpanY = bodyHeight.value - gridOriginY - padding.value

    Box(modifier = modifier.width(width).height(bodyHeight)) {
        // ── Grid + zebra shading + axis (drawn behind everything) ─────────────────
        Canvas(modifier = Modifier.matchParentSize()) {
            val timelineX0 = padding.value + labelColumn.value
            // Alternating-column shading spanning the full task body, like mermaid.live.
            tickColumns.forEachIndexed { i, col ->
                if (i % 2 == 1) {
                    drawRect(
                        color = altShadeColor,
                        topLeft = Offset(timelineX0 + col.x, gridOriginY),
                        size = androidx.compose.ui.geometry.Size(col.width, gridSpanY),
                    )
                }
            }
            // Vertical grid lines at each tick, spanning the whole task body.
            tickColumns.forEach { col ->
                drawLine(
                    color = gridColor,
                    start = Offset(timelineX0 + col.x, gridOriginY),
                    end = Offset(timelineX0 + col.x, gridOriginY + gridSpanY),
                    strokeWidth = 1.dp.toPx(),
                )
            }
            // Main horizontal axis line at the top of the task body.
            drawLine(
                color = gridColor,
                start = Offset(timelineX0, bodyOriginY),
                end = Offset(timelineX0 + timelineWidth, bodyOriginY),
                strokeWidth = 1.dp.toPx(),
            )
        }

        // ── Axis date labels (Composables, so they get real text shaping) ────────
        tickColumns.forEach { col ->
            val label = formatGanttAxisLabel(col.date, config?.axisFormat)
            Text(
                text = label,
                color = axisLabelColor,
                style = PaletteTheme.typography.label,
                maxLines = 1,
                modifier = Modifier.absoluteOffset(
                    x = (padding.value + labelColumn.value + col.x + 2f).dp,
                    y = (gridOriginY + 4f).dp,
                ),
            )
        }

        // ── Title ────────────────────────────────────────────────────────────────
        val titleText = config?.title
        if (titleText != null) {
            Text(
                text = titleText,
                color = colors.nodeContentColor,
                style = PaletteTheme.typography.body,
                modifier = Modifier.absoluteOffset(x = padding, y = padding).width(width - padding * 2),
            )
        }

        // ── Section groups + task bars ───────────────────────────────────────────
        val byTask = positionedByTask(positioned)
        Column(
            modifier = Modifier.absoluteOffset(x = padding, y = bodyOriginY.dp),
        ) {
            sections.forEach { section ->
                if (section.name.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(sectionHeaderHeight)
                            .background(sectionHeaderBg, RoundedCornerShape(4.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        contentAlignment = Alignment.CenterStart,
                    ) {
                        Text(
                            text = section.name,
                            color = colors.nodeContentColor,
                            style = PaletteTheme.typography.label.copy(fontWeight = FontWeight.Bold),
                        )
                    }
                }
                section.tasks.forEach { task ->
                    val pos = byTask[task] ?: return@forEach
                    Row(
                        modifier = Modifier.height(rowHeight).fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = task.title,
                            color = colors.nodeContentColor,
                            style = PaletteTheme.typography.label,
                            modifier = Modifier.width(labelColumn),
                            maxLines = 1,
                        )
                        // The bar lives inside the timeline region; offset by its date span.
                        val barX = dateToX(pos.start)
                        val barWidth = (dateToX(pos.end) - barX)
                            .coerceAtLeast(if (task.isMilestone) 8f else 4f)
                        val barColor = (if (task.isCritical) c.error else statusColor[task.status] ?: c.primary)
                            .let { if (it == Color.Unspecified) colors.edgeColor else it }
                        Box(
                            modifier = Modifier
                                .absoluteOffset(x = barX.dp, y = 0.dp)
                                .width(barWidth.dp)
                                .height(rowHeight - 6.dp)
                                .background(barColor, RoundedCornerShape(3.dp)),
                            contentAlignment = Alignment.Center,
                        ) {
                            if (task.isMilestone) {
                                Text("◆", color = Color.White, style = PaletteTheme.typography.label)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(sectionGap))
            }
        }
    }
}

// ── GitGraph rendering ────────────────────────────────────────────────

@Composable
private fun GitGraphDiagramMermaidDiagram(
    modifier: Modifier,
    colors: MermaidColors,
    branches: List<GitBranch>,
    commits: List<GitCommit>,
    merges: List<GitMerge>,
) {
    if (commits.isEmpty()) {
        Box(modifier = modifier, contentAlignment = Alignment.Center) {
            Text("Empty git graph", color = colors.nodeContentColor, style = PaletteTheme.typography.body)
        }
        return
    }

    val padding = 24.dp
    val branchLabelWidth = 90.dp
    val commitStep = 48.dp
    val rowHeight = 56.dp
    val commitRadius = 8.dp

    val maxSeq = commits.maxOf { it.seq }
    val branchRow = branches.withIndex().associate { (i, b) -> b.name to i }
    val width = (branchLabelWidth.value + (maxSeq + 1) * commitStep.value + padding.value * 2).dp
    val height = (branches.size * rowHeight.value + padding.value * 2 + 24f).dp

    val c = PaletteTheme.colors
    val branchColors = rememberSliceColors()

    Box(modifier = modifier.width(width).height(height)) {
        Canvas(modifier = Modifier.matchParentSize()) {
            // Branch lanes: horizontal lines spanning the timeline.
            branches.forEachIndexed { rowIndex, branch ->
                val y = padding.value + rowIndex * rowHeight.value + rowHeight.value / 2f
                val color = branchColors[rowIndex % branchColors.size]
                val startX = padding.value + branchLabelWidth.value
                val endX = padding.value + branchLabelWidth.value + maxSeq * commitStep.value
                drawLine(
                    color = color,
                    start = Offset(startX, y),
                    end = Offset(endX, y),
                    strokeWidth = 2.dp.toPx(),
                    cap = StrokeCap.Round,
                )
            }

            // Merge edges: connect the source branch's last commit to the merge commit.
            merges.forEach { merge ->
                val mergeCommit = commits.first { it.id == merge.mergeCommitId }
                val fromCommit = commits.filter { it.branch == merge.from }.lastOrNull()
                if (fromCommit == null) return@forEach
                val fromY = padding.value + branchRow.getValue(merge.from) * rowHeight.value + rowHeight.value / 2f
                val toY = padding.value + branchRow.getValue(merge.into) * rowHeight.value + rowHeight.value / 2f
                val fromX = padding.value + branchLabelWidth.value + fromCommit.seq * commitStep.value
                val toX = padding.value + branchLabelWidth.value + mergeCommit.seq * commitStep.value
                drawLine(
                    color = colors.edgeColor,
                    start = Offset(fromX, fromY),
                    end = Offset(toX, toY),
                    strokeWidth = 1.5.dp.toPx(),
                    cap = StrokeCap.Round,
                )
            }

            // Commits as dots on their branch lane.
            commits.forEach { commit ->
                val rowIndex = branchRow.getValue(commit.branch)
                val x = padding.value + branchLabelWidth.value + commit.seq * commitStep.value
                val y = padding.value + rowIndex * rowHeight.value + rowHeight.value / 2f
                val color = branchColors[rowIndex % branchColors.size]
                when (commit.type) {
                    GitCommitType.Normal -> {
                        if (commit.isMerge) {
                            // Merge commit: outer ring + inner filled dot.
                            drawCircle(color = color, radius = commitRadius.toPx(), center = Offset(x, y), style = Stroke(width = 2.dp.toPx()))
                            drawCircle(color = color, radius = commitRadius.toPx() / 2f, center = Offset(x, y))
                        } else {
                            drawCircle(color = color, radius = commitRadius.toPx(), center = Offset(x, y))
                        }
                    }
                    GitCommitType.Highlight -> {
                        // Highlight: filled rectangle.
                        val s = commitRadius.toPx()
                        drawRect(
                            color = color,
                            topLeft = Offset(x - s, y - s),
                            size = androidx.compose.ui.geometry.Size(s * 2, s * 2),
                        )
                    }
                    GitCommitType.Reverse -> {
                        // Reverse: circle with a cross.
                        drawCircle(color = color, radius = commitRadius.toPx(), center = Offset(x, y), style = Stroke(width = 2.dp.toPx()))
                        val s = commitRadius.toPx() * 0.6f
                        drawLine(color = color, start = Offset(x - s, y - s), end = Offset(x + s, y + s), strokeWidth = 1.5.dp.toPx())
                        drawLine(color = color, start = Offset(x + s, y - s), end = Offset(x - s, y + s), strokeWidth = 1.5.dp.toPx())
                    }
                }
            }
        }

        // Branch labels on the left.
        branches.forEachIndexed { rowIndex, branch ->
            val color = branchColors[rowIndex % branchColors.size]
            Text(
                text = branch.name,
                color = color,
                style = PaletteTheme.typography.label.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.absoluteOffset(x = padding, y = padding + (rowIndex * rowHeight.value).dp),
            )
        }

        // Commit id/tag labels under commits.
        commits.forEach { commit ->
            val rowIndex = branchRow.getValue(commit.branch)
            val x = padding.value + branchLabelWidth.value + commit.seq * commitStep.value
            val y = padding.value + rowIndex * rowHeight.value
            val label = commit.tag ?: commit.id
            Text(
                text = label,
                color = colors.nodeContentColor,
                style = PaletteTheme.typography.label,
                modifier = Modifier
                    .absoluteOffset(x = (x - 14f).dp, y = (y + rowHeight.value - 4f).dp)
                    .width(60.dp),
                textAlign = TextAlign.Center,
                maxLines = 1,
            )
        }
    }
}

// ── Mindmap rendering ─────────────────────────────────────────────────
// The layout stores each node's (x, y) as its center. Connectors run from a parent's
// right edge to its child's left edge (the tree grows left→right), drawn as soft cubic
// curves so branches stay visually distinct from the straight flowchart edges.

@Composable
private fun MindmapDiagramMermaidDiagram(
    modifier: Modifier,
    colors: MermaidColors,
    layout: MermaidLayout,
) {
    if (layout.nodes.isEmpty()) {
        Box(modifier = modifier, contentAlignment = Alignment.Center) {
            Text("Empty mindmap", color = colors.nodeContentColor, style = PaletteTheme.typography.body)
        }
        return
    }

    val nodeWidth = 132.dp
    val nodeHeight = 44.dp
    val cornerRadius = MermaidDefaults.cornerRadius()
    val maxCenterX = layout.nodes.values.maxOf { it.x }
    val maxCenterY = layout.nodes.values.maxOf { it.y }
    // Total canvas: room for the deepest node's right half + horizontal/vertical padding.
    val width = (maxCenterX + nodeWidth.value / 2f + 24f).dp
    val height = (maxCenterY + nodeHeight.value / 2f + 24f).dp

    Box(modifier = modifier.width(width).height(height)) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val halfW = nodeWidth.toPx() / 2f
            val halfH = nodeHeight.toPx() / 2f
            layout.edges.forEach { edge ->
                val from = layout.nodes[edge.from] ?: return@forEach
                val to = layout.nodes[edge.to] ?: return@forEach
                val start = Offset(from.x + halfW, from.y)
                val end = Offset(to.x - halfW, to.y)
                // Cubic with control points pulled horizontally — a gentle S-curve connector.
                val ctrl1 = Offset(start.x + (end.x - start.x) * 0.5f, start.y)
                val ctrl2 = Offset(start.x + (end.x - start.x) * 0.5f, end.y)
                val path = Path().apply {
                    moveTo(start.x, start.y)
                    cubicTo(ctrl1.x, ctrl1.y, ctrl2.x, ctrl2.y, end.x, end.y)
                }
                drawPath(
                    path = path,
                    color = colors.edgeColor,
                    style = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round),
                )
            }
        }

        layout.nodes.values.forEach { item ->
            val shape = item.node.shape.toComposeShape()
            // Circle/bang render as filled dots (mermaid mindmap convention); boxes keep the
            // container/border look shared with the rest of the diagram family.
            val isDot = item.node.shape == MermaidNodeShape.Circle
            Box(
                modifier = Modifier
                    .absoluteOffset(x = (item.x - nodeWidth.value / 2f).dp, y = (item.y - nodeHeight.value / 2f).dp)
                    .size(width = nodeWidth, height = nodeHeight)
                    .then(
                        if (isDot) {
                            Modifier.background(colors.nodeContainerColor, shape)
                        } else {
                            Modifier
                                .background(colors.nodeContainerColor, shape)
                                .border(1.dp, colors.nodeBorderColor, shape)
                        },
                    )
                    .padding(horizontal = 8.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = item.node.label,
                    color = colors.nodeContentColor,
                    style = PaletteTheme.typography.label,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                )
            }
        }
    }
}
