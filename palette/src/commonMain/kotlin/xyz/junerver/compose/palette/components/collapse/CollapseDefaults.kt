package xyz.junerver.compose.palette.components.collapse

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.core.theme.PaletteTheme

object CollapseDefaults {
    val TitleHeight: Dp = 48.dp
    val ContentPadding: Dp = 16.dp
    val AnimationDuration: Int = 300

    @Composable
    fun titleColor(): Color = PaletteTheme.colors.onSurface

    @Composable
    fun contentColor(): Color = PaletteTheme.colors.onSurface

    @Composable
    fun iconColor(): Color = PaletteTheme.colors.hint
}
