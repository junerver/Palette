package xyz.junerver.compose.palette.components.tree

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.core.theme.PaletteTheme

object TreeDefaults {
    val NodeHeight: Dp = 40.dp
    val Indent: Dp = 24.dp
    val IconSize: Dp = 16.dp

    @Composable
    fun nodeColor(): Color = PaletteTheme.colors.onSurface

    @Composable
    fun selectedColor(): Color = PaletteTheme.colors.primary

    @Composable
    fun iconColor(): Color = PaletteTheme.colors.hint
}
