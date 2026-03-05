package xyz.junerver.compose.palette.components.upload

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.core.theme.PaletteTheme

object UploadDefaults {
    val BorderRadius: Dp = 8.dp
    val BorderWidth: Dp = 1.dp
    val ContentPadding: Dp = 12.dp

    @Composable
    @ReadOnlyComposable
    fun borderColor(): Color = PaletteTheme.colors.border

    @Composable
    @ReadOnlyComposable
    fun contentColor(): Color = PaletteTheme.colors.onSurface

    @Composable
    @ReadOnlyComposable
    fun successColor(): Color = PaletteTheme.colors.success

    @Composable
    @ReadOnlyComposable
    fun errorColor(): Color = PaletteTheme.colors.error
}
