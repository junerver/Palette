package xyz.junerver.compose.palette.components.toggle

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.components.text.PText
import xyz.junerver.compose.palette.core.spec.ComponentSize
import xyz.junerver.compose.palette.core.util.clickableWithoutRipple

@Composable
fun PToggle(
    pressed: Boolean,
    onPressedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    variant: ToggleVariant = ToggleVariant.Default,
    disabled: Boolean = false,
    size: ComponentSize = ComponentSize.Medium,
    content: @Composable () -> Unit,
) {
    val shape = RoundedCornerShape(ToggleDefaults.CornerRadius)
    val containerColor = ToggleDefaults.containerColor(pressed)
    val borderColor = ToggleDefaults.borderColor(pressed)

    Box(
        modifier = modifier
            .defaultMinSize(minHeight = size.height)
            .alpha(if (disabled) ToggleDefaults.DisabledAlpha else 1f)
            .then(
                if (variant == ToggleVariant.Outline) {
                    Modifier.border(1.dp, borderColor, shape)
                } else {
                    Modifier
                }
            )
            .clip(shape)
            .background(containerColor, shape)
            .clickableWithoutRipple(enabled = !disabled) { onPressedChange(!pressed) }
            .padding(
                horizontal = ToggleDefaults.PaddingHorizontal,
                vertical = ToggleDefaults.PaddingVertical
            ),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

@Immutable
interface PToggleGroupScope {
    @Composable
    fun PToggleItem(
        value: String,
        label: String,
        icon: (@Composable (() -> Unit))? = null,
        disabled: Boolean = false,
    )
}

@Immutable
private class PToggleGroupScopeImpl(
    private val selectedValues: List<String>,
    private val onValueChange: (List<String>) -> Unit,
    private val variant: ToggleVariant,
    private val disabled: Boolean,
    private val size: ComponentSize,
) : PToggleGroupScope {

    @Composable
    override fun PToggleItem(
        value: String,
        label: String,
        icon: (@Composable (() -> Unit))?,
        disabled: Boolean,
    ) {
        val isPressed = value in selectedValues
        val itemDisabled = this.disabled || disabled

        PToggle(
            pressed = isPressed,
            onPressedChange = {
                if (!itemDisabled) {
                    onValueChange(
                        if (isPressed) selectedValues - value else selectedValues + value
                    )
                }
            },
            variant = variant,
            disabled = itemDisabled,
            size = size,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                icon?.invoke()
                PText(
                    text = label,
                    color = ToggleDefaults.contentColor(isPressed),
                    fontSize = size.fontSize,
                )
            }
        }
    }
}

@Composable
fun PToggleGroup(
    value: List<String>,
    onValueChange: (List<String>) -> Unit,
    modifier: Modifier = Modifier,
    variant: ToggleVariant = ToggleVariant.Default,
    disabled: Boolean = false,
    size: ComponentSize = ComponentSize.Medium,
    multiple: Boolean = false,
    content: @Composable PToggleGroupScope.() -> Unit,
) {
    val scope = remember(value, variant, disabled, size, multiple) {
        PToggleGroupScopeImpl(
            selectedValues = value,
            onValueChange = { newValues ->
                if (multiple) {
                    onValueChange(newValues)
                } else {
                    onValueChange(if (newValues.size > 1) listOf(newValues.last()) else newValues)
                }
            },
            variant = variant,
            disabled = disabled,
            size = size,
        )
    }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(ToggleDefaults.GroupSpacing),
    ) {
        scope.content()
    }
}
