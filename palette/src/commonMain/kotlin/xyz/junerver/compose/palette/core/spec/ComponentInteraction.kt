package xyz.junerver.compose.palette.core.spec

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember

@Immutable
data class ComponentInteraction(
    val enabled: Boolean = true,
    val interactionSource: MutableInteractionSource = MutableInteractionSource(),
)

@Composable
fun rememberComponentInteraction(
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
): ComponentInteraction {
    val resolvedSource = interactionSource ?: remember { MutableInteractionSource() }
    return ComponentInteraction(enabled = enabled, interactionSource = resolvedSource)
}
