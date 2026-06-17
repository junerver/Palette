package xyz.junerver.compose.palette.components.upload

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.core.theme.PaletteTheme

object UploadDefaults {
    val BorderRadius: Dp = 8.dp
    val BorderWidth: Dp = 1.dp
    val ContentPadding: Dp = 12.dp

    @Composable
    @ReadOnlyComposable
    fun borderRadius(): Dp = PaletteTheme.componentThemes.dataEntry.uploadBorderRadius

    @Composable
    @ReadOnlyComposable
    fun borderWidth(): Dp = PaletteTheme.componentThemes.dataEntry.uploadBorderWidth

    @Composable
    @ReadOnlyComposable
    fun contentPadding(): Dp = PaletteTheme.componentThemes.dataEntry.uploadContentPadding

    @Composable
    @ReadOnlyComposable
    fun itemSpacing(): Dp = PaletteTheme.componentThemes.dataEntry.uploadItemSpacing

    @Composable
    @ReadOnlyComposable
    fun borderColor(): Color = PaletteTheme.componentThemes.dataEntry.uploadBorderColor

    @Composable
    @ReadOnlyComposable
    fun backgroundColor(): Color = PaletteTheme.componentThemes.dataEntry.uploadBackgroundColor

    @Composable
    @ReadOnlyComposable
    fun contentColor(): Color = PaletteTheme.componentThemes.dataEntry.uploadContentColor

    @Composable
    @ReadOnlyComposable
    fun successColor(): Color = PaletteTheme.componentThemes.dataEntry.uploadSuccessColor

    @Composable
    @ReadOnlyComposable
    fun errorColor(): Color = PaletteTheme.componentThemes.dataEntry.uploadErrorColor

    @Composable
    @ReadOnlyComposable
    fun textStyle(): TextStyle = PaletteTheme.componentThemes.dataEntry.uploadTextStyle
}
