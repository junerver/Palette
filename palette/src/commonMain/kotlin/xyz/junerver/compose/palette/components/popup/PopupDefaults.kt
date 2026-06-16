package xyz.junerver.compose.palette.components.popup

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import xyz.junerver.compose.palette.core.theme.PaletteTheme

object PopupDefaults {
    val CornerRadius: Dp = 12.dp
    val ContentPadding: Dp = 12.dp
    val DraggableLineLength: Dp = 40.dp
    val DraggableLineThickness: Dp = 4.dp
    val DraggableLineOffset: Dp = -(12).dp
    val TitleHeight: Dp = 50.dp
    val TitleFontSize: TextUnit = 17.sp
    val AnimationDuration: Int = 150
    val DragDismissThreshold: Float = 0.5f

    @Composable
    fun containerColor(): Color = PaletteTheme.colors.surface

    @Composable
    fun titleColor(): Color = PaletteTheme.colors.onSurface

    @Composable
    fun draggableLineColor(): Color = PaletteTheme.colors.border
}
