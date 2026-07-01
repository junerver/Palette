package xyz.junerver.compose.palette.components.chart

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember

/**
 * Drives the optional chart entrance animation. The progress fraction [value] animates from `0f`
 * (everything collapsed to the baseline) to `1f` (fully drawn) on first composition, using a
 * gentle spring so bars "grow", lines "draw in" and slices "sweep".
 *
 * When [enabled] is `false` the animation is skipped entirely — [value] jumps straight to `1f` and
 * the renderers draw at full geometry with no per-frame work (the `Animatable` never launches),
 * keeping the disabled path zero-cost. This resolves the previous "name-reality mismatch" where
 * [ChartOptions.animationEnabled] existed but was ignored.
 *
 * The animation re-runs only when the chart's identity key changes (a new dataset/spec), so
 * toggling legend visibility or hovering does NOT replay it — those are cheap recompositions, not
 * a fresh entrance.
 *
 * Implementation note: native `remember { Animatable(...) }` + `LaunchedEffect` is used rather than
 * `useState` because `Animatable` is itself a stable coroutine-driven state holder whose `value`
 * is observed by Compose — `useState` models a single replaceable value, not a self-animating
 * holder. This is the documented AGENTS.md exception for stable state holders.
 */
@Stable
internal class ChartEntranceAnimation(internal val animatable: Animatable<Float, AnimationVector1D>) {
    /** Current entrance progress in `[0, 1]`. Renderers scale geometry by this fraction. */
    val value: Float get() = animatable.value
    /** `true` once the entrance has settled at `1f` (so renderers can skip the multiply). */
    val isFinished: Boolean get() = animatable.value >= 0.999f
}

/**
 * Remembers a [ChartEntranceAnimation] and launches its 0→1 spring when [enabled] (and re-launches
 * on [replayKey] change). When disabled, the animation is pinned at `1f`.
 *
 * @param enabled mirrors [ChartOptions.animationEnabled]; when `false` no animation runs.
 * @param replayKey a value whose change should replay the entrance (e.g. the dataset identity).
 */
@Composable
internal fun rememberChartEntranceAnimation(
    enabled: Boolean,
    replayKey: Any?,
): ChartEntranceAnimation {
    // Start at 0 only when enabled; otherwise begin (and stay) at 1 so the disabled path is free.
    val animation = remember(enabled) {
        ChartEntranceAnimation(Animatable(if (enabled) 0f else 1f))
    }
    LaunchedEffect(enabled, replayKey) {
        if (enabled) {
            // Re-run from 0 on each replayKey change so a new dataset re-grows.
            animation.animatable.snapTo(0f)
            animation.animatable.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessMediumLow,
                ),
            )
        } else {
            animation.animatable.snapTo(1f)
        }
    }
    return animation
}
