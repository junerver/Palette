package xyz.junerver.compose.palette.components.statistic

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.core.theme.PaletteTheme

object StatisticDefaults {
    val TrendIconSize: Dp = 16.dp
    val Spacing: Dp = 8.dp

    @Composable
    fun valueColor(): Color = PaletteTheme.colors.onSurface

    @Composable
    fun titleColor(): Color = PaletteTheme.colors.onSurface.copy(alpha = 0.6f)

    @Composable
    fun trendUpColor(): Color = Color(0xFF52C41A)

    @Composable
    fun trendDownColor(): Color = Color(0xFFFF4D4F)
}
