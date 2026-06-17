package xyz.junerver.compose.palette.components.colorpicker

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.core.theme.PaletteTheme

object ColorPickerDefaults {
    val PanelWidth: Dp = 240.dp
    val PanelHeight: Dp = 160.dp
    val HueBarHeight: Dp = 16.dp
    val AlphaBarHeight: Dp = 16.dp
    val PreviewSize: Dp = 32.dp
    val CornerRadius: Dp = 8.dp
    val ThumbSize: Dp = 12.dp
    val PresetColorSize: Dp = 24.dp
    val PresetColorSpacing: Dp = 4.dp
    val BarSpacing: Dp = 12.dp
    val HexInputWidth: Dp = 80.dp

    @Composable
    fun panelWidth(): Dp = PaletteTheme.componentThemes.media.colorPickerPanelWidth

    @Composable
    fun panelHeight(): Dp = PaletteTheme.componentThemes.media.colorPickerPanelHeight

    @Composable
    fun hueBarHeight(): Dp = PaletteTheme.componentThemes.media.colorPickerHueBarHeight

    @Composable
    fun alphaBarHeight(): Dp = PaletteTheme.componentThemes.media.colorPickerAlphaBarHeight

    @Composable
    fun previewSize(): Dp = PaletteTheme.componentThemes.media.colorPickerPreviewSize

    @Composable
    fun cornerRadius(): Dp = PaletteTheme.componentThemes.media.colorPickerCornerRadius

    @Composable
    fun thumbSize(): Dp = PaletteTheme.componentThemes.media.colorPickerThumbSize

    @Composable
    fun presetColorSize(): Dp = PaletteTheme.componentThemes.media.colorPickerPresetColorSize

    @Composable
    fun presetColorSpacing(): Dp = PaletteTheme.componentThemes.media.colorPickerPresetColorSpacing

    @Composable
    fun barSpacing(): Dp = PaletteTheme.componentThemes.media.colorPickerBarSpacing

    @Composable
    fun hexInputWidth(): Dp = PaletteTheme.componentThemes.media.colorPickerHexInputWidth

    @Composable
    fun hexInputHeight(): Dp = PaletteTheme.componentThemes.media.colorPickerHexInputHeight

    @Composable
    fun inputCornerRadius(): Dp = PaletteTheme.componentThemes.media.colorPickerInputCornerRadius

    @Composable
    fun inputBorderWidth(): Dp = PaletteTheme.componentThemes.media.colorPickerInputBorderWidth

    @Composable
    fun inputPaddingHorizontal(): Dp = PaletteTheme.componentThemes.media.colorPickerInputPaddingHorizontal

    @Composable
    fun inputPaddingVertical(): Dp = PaletteTheme.componentThemes.media.colorPickerInputPaddingVertical

    @Composable
    fun inputTextStyle(): TextStyle = PaletteTheme.componentThemes.media.colorPickerInputTextStyle

    @Composable
    fun inputTextColor(): Color = PaletteTheme.componentThemes.media.colorPickerInputTextColor

    @Composable
    fun inputBorderColor(): Color = PaletteTheme.componentThemes.media.colorPickerInputBorderColor

    @Composable
    fun inputCursorColor(): Color = PaletteTheme.componentThemes.media.colorPickerInputCursorColor

    @Composable
    fun selectedBorderColor(): Color = PaletteTheme.componentThemes.media.colorPickerSelectedBorderColor

    @Composable
    fun thumbFillColor(): Color = PaletteTheme.componentThemes.media.colorPickerThumbFillColor

    @Composable
    fun thumbBorderColor(): Color = PaletteTheme.componentThemes.media.colorPickerThumbBorderColor
}
