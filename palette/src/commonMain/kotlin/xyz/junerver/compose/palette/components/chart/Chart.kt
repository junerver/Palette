package xyz.junerver.compose.palette.components.chart

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.PointerType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.hooks.useState
import xyz.junerver.compose.palette.core.theme.PaletteTheme

/**
 * A themeable, dependency-free chart. Renders the chart described by [spec] from [data] using
 * Compose-native Canvas drawing (no WebView, no third-party libs) — consistent with the mermaid
 * module's self-rendering philosophy.
 *
 * To add a new chart type: declare a new [ChartSpec] subclass + add a `when` branch in
 * [ChartRenderer] + ship a renderer. Existing charts are untouched.
 *
 * **Tooltip**: hover (desktop) / press-drag (touch) the canvas to surface a floating value readout
 * and highlight the nearest data point. Toggle with [ChartOptions.showTooltip] (on by default).
 *
 * **Linked charts (P6-B)**: pass a non-null [controlledZoomRange] + [onZoomChange] to make the
 * data-zoom range externally controlled. Two `PChart`s sharing the same lifted state will link —
 * dragging one chart's zoom slider drives the other's view. When both are null the chart manages its
 * own zoom state internally (uncontrolled).
 *
 * @param spec which chart + its config (pie / bar / line / scatter / radar).
 * @param data series + optional categories.
 * @param modifier outer modifier.
 * @param options title, axes, grid, legend, tooltip, animation, dataZoom.
 * @param colors token-backed color bundle; defaults to [ChartDefaults.colors].
 * @param controlledZoomRange externally-owned zoom range; when non-null the chart is "controlled" and
 *   the internal state is bypassed (used for chart linking).
 * @param onZoomChange emitted when the user drags the zoom slider; the parent lifts it into state and
 *   feeds it back via [controlledZoomRange].
 */
@Composable
fun PChart(
    spec: ChartSpec,
    data: ChartData,
    modifier: Modifier = Modifier,
    options: ChartOptions = ChartOptions(),
    colors: ChartColors = ChartDefaults.colors(),
    controlledZoomRange: Pair<Float, Float>? = null,
    onZoomChange: ((Pair<Float, Float>) -> Unit)? = null,
) {
    val titleStyle = ChartDefaults.titleTextStyle()
    val emptyColor = colors.emptyStateColor
    val hasData = data.series.isNotEmpty() && data.series.any { it.values.isNotEmpty() }
    val hoverState = rememberChartHoverState()
    // Visibility filter for the legend toggle (P2-A): an entry per series, true = shown. Held as a
    // BooleanArray via useState so toggling a series allocates a fresh array → guaranteed recomposition.
    val (visibleSeries, setVisibleSeries) = useState(BooleanArray(data.series.size) { true })
    // Data-zoom range (P6-A). In CONTROLLED mode (controlledZoomRange != null) the parent owns the
    // range and we just read it; otherwise useState holds it locally (uncontrolled). The non-null
    // invariant is why useState takes a real Pair (compose-hooks assumes non-null state).
    val zoomConfig = options.dataZoom
    val (localZoomRange, setLocalZoomRange) = useState<Pair<Float, Float>>(
        zoomConfig?.let { it.start.coerceIn(0f, 1f) to it.end.coerceIn(0f, 1f) } ?: (0f to 1f),
    )
    val isControlled = controlledZoomRange != null
    val zoomRange = controlledZoomRange ?: localZoomRange
    val setZoomRange: (Pair<Float, Float>) -> Unit = { range ->
        if (onZoomChange != null) onZoomChange(range) else setLocalZoomRange(range)
    }
    var canvasSize by remember { mutableStateOf(IntSize.Zero) }
    val density = LocalDensity.current
    // Entrance animation (P3-A). Replays when the dataset/spec identity changes; pinned at 1 when
    // disabled so the renderers do zero extra work.
    val entrance = rememberChartEntranceAnimation(
        enabled = options.animationEnabled,
        replayKey = spec to visibleSeries.contentHashCode(),
    )

    Column(modifier = modifier) {
        if (options.title != null) {
            Text(
                text = options.title,
                color = colors.legendTextColor,
                style = titleStyle.copy(fontWeight = FontWeight.SemiBold),
                modifier = Modifier.padding(bottom = 4.dp),
            )
        }
        if (options.showLegend && options.legendPosition == ChartLegendPosition.Top && hasData) {
            ChartLegend(
                data = data,
                colors = colors,
                visible = visibleSeries,
                onToggle = { idx ->
                    setVisibleSeries(visibleSeries.copyOf().also { it[idx] = !it[idx] })
                },
                modifier = Modifier.padding(bottom = 4.dp),
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = true),
            contentAlignment = Alignment.Center,
        ) {
            if (!hasData) {
                Text(text = "No data", color = emptyColor, style = PaletteTheme.typography.body)
            } else {
                // Apply legend visibility, then the data-zoom category slice (P6-A). The slice is a
                // sub-range of the resolved categories; both the categories and each series' values
                // are trimmed so the renderer + axis layout see only the zoomed window.
                val legendFiltered = data.copy(series = data.series.filterIndexed { i, _ -> visibleSeries[i] })
                // Apply the zoom slice only when dataZoom is configured; otherwise pass null (no-op).
                val visibleData = applyZoomSlice(legendFiltered, if (zoomConfig != null) zoomRange else null)
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .onSizeChanged { canvasSize = it }
                        .then(
                            if (options.showTooltip) Modifier.pointerInput(spec, visibleData) {
                                detectChartHover(
                                    hoverState = hoverState,
                                )
                            } else Modifier,
                        ),
                ) {
                    ChartRenderer.render(
                        spec = spec,
                        data = visibleData,
                        modifier = Modifier.fillMaxSize(),
                        options = options,
                        colors = colors,
                        hoverState = hoverState,
                        canvasSize = canvasSize,
                        density = density,
                        entrance = entrance,
                    )
                    if (options.showTooltip) {
                        ChartTooltipOverlay(
                            state = hoverState,
                            data = visibleData,
                            colors = colors,
                            modifier = Modifier.fillMaxSize(),
                        )
                    }
                }
            }
        }

        // Data-zoom slider (P6-A): only for cartesian types whose category axis is meaningful to slice.
        if (zoomConfig != null && hasData && spec.supportsDataZoom()) {
            DataZoomSlider(
                startFraction = zoomRange.first,
                endFraction = zoomRange.second,
                minSpan = zoomConfig.minSpan,
                onChange = { s, e -> setZoomRange(s to e) },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            )
        }

        if (options.showLegend && options.legendPosition == ChartLegendPosition.Bottom && hasData) {
            ChartLegend(
                data = data,
                colors = colors,
                visible = visibleSeries,
                onToggle = { idx ->
                    setVisibleSeries(visibleSeries.copyOf().also { it[idx] = !it[idx] })
                },
                modifier = Modifier.padding(top = 4.dp),
            )
        }
    }
}

/** Whether this chart type can be sliced by the data-zoom (cartesian with a category axis). */
private fun ChartSpec.supportsDataZoom(): Boolean = this is ChartSpec.Bar || this is ChartSpec.Line

/**
 * Drives [hoverState] from pointer motion. Uses a raw `awaitPointerEventScope` loop on the
 * `Initial` pass (the earliest stage, so hover/drag events arrive before any consumer can swallow
 * them — the fix for tooltips not showing under mouse hover).
 *
 * Works for BOTH:
 * - **mouse hover** on desktop/web — `PointerEventType.Move` events with no buttons; the tooltip
 *   follows the cursor and clears when it leaves the canvas,
 * - **touch drag** on Android/iOS — `PointerEventType.Down/Move` while pressed; clears on `Up`.
 *
 * Only the pointer position + active flag are recorded here; each renderer resolves which data
 * point is under the cursor inside its [DrawScope] (where the exact plot rectangle is known) and
 * writes the [HitTarget] back into [hoverState] for the tooltip overlay to read.
 */
private suspend fun androidx.compose.ui.input.pointer.PointerInputScope.detectChartHover(
    hoverState: ChartHoverState,
) {
    awaitPointerEventScope {
        while (true) {
            // Initial pass → we observe the event even if a descendant would later consume it.
            val event = awaitPointerEvent(PointerEventPass.Initial)
            val change = event.changes.firstOrNull() ?: continue
            val pos = change.position
            val w = size.width.toFloat()
            val h = size.height.toFloat()
            val inside = pos.x in 0f..w && pos.y in 0f..h

            when (event.type) {
                PointerEventType.Move, PointerEventType.Enter, PointerEventType.Exit -> {
                    // Mouse hover (no buttons). Track position while inside; clear on exit.
                    if (inside) {
                        hoverState.active = true
                        hoverState.anchor = pos
                    } else {
                        hoverState.active = false
                        hoverState.target = null
                    }
                }
                PointerEventType.Press -> {
                    // Touch/press: begin inspecting.
                    if (inside) {
                        hoverState.active = true
                        hoverState.anchor = pos
                    }
                }
                PointerEventType.Release -> {
                    // Release → clear (touch). For mouse this is a click-release; hover continues via Move.
                    if (change.type != PointerType.Mouse) {
                        hoverState.active = false
                        hoverState.target = null
                    }
                }
                else -> {
                    // PointerEventType.Move also covers pressed-move (drag) — update the anchor.
                    if (change.pressed && inside) {
                        hoverState.active = true
                        hoverState.anchor = pos
                    }
                }
            }
        }
    }
}

/**
 * Renders the categorical legend: a colored dot + series label per series. Each dot is tappable to
 * toggle that series' visibility (P2-A); hidden entries render dimmed (at the chart token's
 * `legendHiddenAlpha`).
 */
@Composable
private fun ChartLegend(
    data: ChartData,
    colors: ChartColors,
    visible: BooleanArray,
    onToggle: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val tokens = PaletteTheme.componentThemes.chart
    val symbolSize = ChartDefaults.legendSymbolSize()
    val legendStyle = ChartDefaults.legendTextStyle()
    val accentFallback = PaletteTheme.colors.textPrimary

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        data.series.forEachIndexed { index, series ->
            if (index > 0) Box(modifier = Modifier.size(12.dp))
            val isShown = visible.getOrNull(index) ?: true
            val color = resolveSeriesColor(series, index, colors.categoricalColors, accentFallback)
            Row(
                modifier = Modifier
                    .clip(CircleShape)
                    .pointerInput(index) {
                        detectTapGestures { onToggle(index) }
                    }
                    .padding(vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(symbolSize)
                        .clip(CircleShape)
                        .background(color.copy(alpha = if (isShown) 1f else tokens.legendHiddenAlpha)),
                )
                Text(
                    text = series.label,
                    color = colors.legendTextColor.copy(alpha = if (isShown) 1f else tokens.legendHiddenAlpha),
                    style = legendStyle,
                    modifier = Modifier.padding(start = 4.dp, end = 8.dp),
                )
            }
        }
    }
}
