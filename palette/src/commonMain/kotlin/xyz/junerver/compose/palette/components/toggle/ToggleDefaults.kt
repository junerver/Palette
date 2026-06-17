package xyz.junerver.compose.palette.components.toggle

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.core.theme.PaletteTheme

enum class ToggleVariant {
    Default, Outline
}

data class ToggleItem(
    val value: String,
    val label: String,
    val icon: (@Composable (() -> Unit))? = null,
    val disabled: Boolean = false,
)

object ToggleDefaults {
    val CornerRadius: Dp = 6.dp
    val PaddingHorizontal: Dp = 12.dp
    val PaddingVertical: Dp = 8.dp
    val GroupSpacing: Dp = 0.dp
    val DisabledAlpha: Float = 0.5f

    @Composable
    fun cornerRadius(): Dp = PaletteTheme.componentThemes.selectionControl.toggleCornerRadius

    @Composable
    fun paddingHorizontal(): Dp = PaletteTheme.componentThemes.selectionControl.togglePaddingHorizontal

    @Composable
    fun paddingVertical(): Dp = PaletteTheme.componentThemes.selectionControl.togglePaddingVertical

    @Composable
    fun groupSpacing(): Dp = PaletteTheme.componentThemes.selectionControl.toggleGroupSpacing

    @Composable
    fun borderWidth(): Dp = PaletteTheme.componentThemes.selectionControl.toggleBorderWidth

    @Composable
    fun itemIconSpacing(): Dp = PaletteTheme.componentThemes.selectionControl.toggleItemIconSpacing

    @Composable
    fun disabledAlpha(): Float = PaletteTheme.componentThemes.selectionControl.toggleDisabledAlpha

    @Composable
    fun containerColor(pressed: Boolean): Color =
        if (pressed) {
            PaletteTheme.componentThemes.selectionControl.togglePressedContainerColor
        } else {
            PaletteTheme.componentThemes.selectionControl.toggleDefaultContainerColor
        }

    @Composable
    fun borderColor(pressed: Boolean): Color =
        if (pressed) {
            PaletteTheme.componentThemes.selectionControl.togglePressedBorderColor
        } else {
            PaletteTheme.componentThemes.selectionControl.toggleDefaultBorderColor
        }

    @Composable
    fun contentColor(pressed: Boolean): Color =
        if (pressed) {
            PaletteTheme.componentThemes.selectionControl.togglePressedContentColor
        } else {
            PaletteTheme.componentThemes.selectionControl.toggleDefaultContentColor
        }
}
