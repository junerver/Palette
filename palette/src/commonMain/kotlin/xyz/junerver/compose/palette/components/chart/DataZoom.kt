package xyz.junerver.compose.palette.components.chart

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import kotlin.math.abs
import xyz.junerver.compose.palette.core.theme.PaletteTheme

/**
 * A data-zoom slider rendered below a cartesian chart. Drag the selection window's left/right
 * handles (or its body) to choose a sub-range of categories; the chart redraws only that slice.
 *
 * The slider is **controlled**: the parent owns the `(start, end)` range and feeds it back via the
 * params, so two linked charts can share one lifted range. All visuals derive from `PaletteTheme`.
 *
 * **Why a raw `awaitPointerEventScope` loop (not `detectDragGestures`)**: the slider must track the
 * cursor with 1:1 fidelity. `detectDragGestures.onDrag` delivers a per-**frame** `dragAmount` delta;
 * accumulating it across recompositions is lossy (a frame whose `onChange`-triggered recomposition
 * hasn't settled yet applies the next delta to a stale range, so the window lags far behind the
 * cursor — the "moved more, slider moved little" bug). Instead this tracks the **absolute** pointer
 * position from the press origin: each move computes `current - pressStart` as a TOTAL displacement
 * and applies it to the range captured at press time. That is recomposition-proof and tracks the
 * cursor exactly, regardless of frame rate or how slowly the parent reflows.
 *
 * @param startFraction current selection start, clamped to `[0, endFraction]`.
 * @param endFraction current selection end, clamped to `[startFraction, 1]`.
 * @param minSpan minimum window width (fraction of the full range).
 * @param onChange emits the new `(start, end)` pair as the user drags.
 */
@Composable
internal fun DataZoomSlider(
    startFraction: Float,
    endFraction: Float,
    minSpan: Float,
    onChange: (Float, Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    val tokens = PaletteTheme.componentThemes.chart
    val density = LocalDensity.current
    val trackColor = tokens.gridColor
    val selectionColor = PaletteTheme.colors.primary
    val handleColor = tokens.seriesLabelColor
    val trackHeightPx = with(density) { 6.dp.toPx() }
    val handleWidthPx = with(density) { 14.dp.toPx() }
    val handleHeightPx = with(density) { 22.dp.toPx() }
    // Touch-target radius (px) for grabbing a handle — generous so the handle is easy to grab.
    val handleGrabRadiusPx = with(density) { 20.dp.toPx() }

    var sizePx by remember { mutableStateOf(IntSize.Zero) }

    // Latest props read inside the gesture coroutine via stable refs.
    val startRef = remember { mutableStateOf(startFraction) }
    val endRef = remember { mutableStateOf(endFraction) }
    val minSpanRef = remember { mutableStateOf(minSpan) }
    val onChangeRef = remember { mutableStateOf(onChange) }
    startRef.value = startFraction
    endRef.value = endFraction
    minSpanRef.value = minSpan
    onChangeRef.value = onChange

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(with(density) { (handleHeightPx + 4.dp.toPx()).toDp() })
            .onSizeChanged { sizePx = it }
            // STABLE key (Unit) → the gesture loop launches ONCE and never restarts mid-drag.
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    // Per-drag session state — captured fresh on each press.
                    var dragging = false
                    var dragMode = 0 // -1 = left, +1 = right, +2 = body pan
                    var pressStartX = 0f // pointer X (px) at the moment of press
                    var baseStart = 0f // the range (start,end) frozen at press time
                    var baseEnd = 0f
                    while (true) {
                        val event = awaitPointerEvent(PointerEventPass.Initial)
                        val change = event.changes.firstOrNull() ?: continue
                        val x = change.position.x
                        when (event.type) {
                            PointerEventType.Press -> {
                                val wPx = sizePx.width.toFloat().coerceAtLeast(1f)
                                val s = startRef.value
                                val e = endRef.value
                                val leftHandleX = s * wPx
                                val rightHandleX = e * wPx
                                // Handle hit-test in PIXELS, handles checked BEFORE body-pan so a
                                // press on a handle is never mistaken for a body pan.
                                dragMode = when {
                                    abs(x - leftHandleX) <= handleGrabRadiusPx -> -1
                                    abs(x - rightHandleX) <= handleGrabRadiusPx -> 1
                                    x in leftHandleX..rightHandleX -> 2
                                    x < leftHandleX -> -1
                                    else -> 1
                                }
                                // FREEZE the range at press time → subsequent moves are absolute.
                                pressStartX = x
                                baseStart = s
                                baseEnd = e
                                dragging = true
                                change.consume()
                            }
                            PointerEventType.Move -> {
                                if (dragging && change.pressed) {
                                    val wPx = sizePx.width.toFloat().coerceAtLeast(1f)
                                    // ABSOLUTE displacement from the press origin (not a per-frame delta).
                                    val totalDeltaPx = x - pressStartX
                                    val (ns, ne) = computeZoom(
                                        start = baseStart,
                                        end = baseEnd,
                                        mode = dragMode,
                                        deltaPx = totalDeltaPx,
                                        widthPx = wPx,
                                        minSpan = minSpanRef.value,
                                    )
                                    onChangeRef.value(ns, ne)
                                    change.consume()
                                }
                            }
                            PointerEventType.Release -> {
                                if (dragging) {
                                    dragging = false
                                    dragMode = 0
                                    change.consume()
                                }
                            }
                            else -> {}
                        }
                    }
                }
            },
    ) {
        Canvas(modifier = Modifier.fillMaxWidth().height(with(density) { handleHeightPx.toDp() })) {
            val w = size.width
            val h = size.height
            val trackY = (h - trackHeightPx) / 2f
            // Track (full range).
            drawRect(
                color = trackColor,
                topLeft = Offset(0f, trackY),
                size = Size(w, trackHeightPx),
            )
            // Selection window fill + outline.
            val selX = startFraction * w
            val selW = (endFraction - startFraction).coerceAtLeast(0f) * w
            drawRect(
                color = selectionColor.copy(alpha = 0.30f),
                topLeft = Offset(selX, trackY),
                size = Size(selW, trackHeightPx),
            )
            drawRect(
                color = selectionColor,
                topLeft = Offset(selX, trackY),
                size = Size(selW, trackHeightPx),
                style = Stroke(width = with(density) { 1.dp.toPx() }),
            )
            // Handles (taller + wider for an easy drag target).
            val handleY = (h - handleHeightPx) / 2f
            drawRect(
                color = handleColor,
                topLeft = Offset(startFraction * w - handleWidthPx / 2f, handleY),
                size = Size(handleWidthPx, handleHeightPx),
            )
            drawRect(
                color = handleColor,
                topLeft = Offset(endFraction * w - handleWidthPx / 2f, handleY),
                size = Size(handleWidthPx, handleHeightPx),
            )
        }
    }
}

/**
 * Pure zoom-range math. Applies a [deltaPx] drag in [mode] to the current window, returning the new
 * `(start, end)` clamped to `[0,1]` and respecting [minSpan]. Kept pure + testable.
 *
 * - mode `-1` (left handle): move start, clamped to `[0, end - minSpan]`.
 * - mode `+1` (right handle): move end, clamped to `[start + minSpan, 1]`.
 * - mode `+2` (body pan): shift both by the same delta, clamped so the window stays inside `[0,1]`.
 */
internal fun computeZoom(
    start: Float,
    end: Float,
    mode: Int,
    deltaPx: Float,
    widthPx: Float,
    minSpan: Float,
): Pair<Float, Float> {
    if (widthPx <= 0f) return start to end
    val dFrac = deltaPx / widthPx
    return when (mode) {
        -1 -> {
            val ns = (start + dFrac).coerceIn(0f, (end - minSpan).coerceAtLeast(0f))
            ns to end
        }
        1 -> {
            val ne = (end + dFrac).coerceIn((start + minSpan).coerceAtMost(1f), 1f)
            start to ne
        }
        2 -> {
            val span = end - start
            val ns = (start + dFrac).coerceIn(0f, 1f - span)
            ns to (ns + span)
        }
        else -> start to end
    }
}
