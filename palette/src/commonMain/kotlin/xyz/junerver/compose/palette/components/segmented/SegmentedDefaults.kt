package xyz.junerver.compose.palette.components.segmented

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.core.theme.PaletteTheme

object SegmentedDefaults {
    val CornerRadius: Dp = 8.dp
    val ItemPaddingHorizontal: Dp = 12.dp
    val ItemPaddingVertical: Dp = 6.dp
    val IndicatorAnimationDuration: Int = 200
    val DisabledAlpha: Float = 0.5f

    @Composable
    fun cornerRadius(): Dp = PaletteTheme.componentThemes.selectionControl.segmentedCornerRadius

    @Composable
    fun itemPaddingHorizontal(): Dp = PaletteTheme.componentThemes.selectionControl.segmentedItemPaddingHorizontal

    @Composable
    fun itemPaddingVertical(): Dp = PaletteTheme.componentThemes.selectionControl.segmentedItemPaddingVertical

    @Composable
    fun itemIconSpacing(): Dp = PaletteTheme.componentThemes.selectionControl.segmentedItemIconSpacing

    @Composable
    fun indicatorAnimationDurationMillis(): Int =
        PaletteTheme.componentThemes.selectionControl.segmentedIndicatorAnimationDurationMillis

    @Composable
    fun disabledAlpha(): Float = PaletteTheme.componentThemes.selectionControl.segmentedDisabledAlpha

    @Composable
    fun containerColor(): Color = PaletteTheme.componentThemes.selectionControl.segmentedContainerColor

    @Composable
    fun selectedItemColor(): Color = PaletteTheme.componentThemes.selectionControl.segmentedSelectedItemColor

    @Composable
    fun textColor(): Color = PaletteTheme.componentThemes.selectionControl.segmentedTextColor

    @Composable
    fun selectedTextColor(): Color = PaletteTheme.componentThemes.selectionControl.segmentedSelectedTextColor

    @Composable
    fun disabledTextColor(): Color = PaletteTheme.componentThemes.selectionControl.segmentedDisabledTextColor
}
