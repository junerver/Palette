package xyz.junerver.compose.palette.components.statistic

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.core.theme.PaletteTheme

object StatisticDefaults {
    val TrendIconSize: Dp = 16.dp
    val Spacing: Dp = 8.dp

    @Composable
    fun trendIconSize(): Dp = PaletteTheme.componentThemes.feedbackDisplay.statisticTrendIconSize

    @Composable
    fun spacing(): Dp = PaletteTheme.componentThemes.feedbackDisplay.statisticSpacing

    @Composable
    fun rowItemSpacing(): Dp = PaletteTheme.componentThemes.feedbackDisplay.statisticRowItemSpacing

    @Composable
    fun valueTextStyle(): TextStyle = PaletteTheme.componentThemes.feedbackDisplay.statisticValueTextStyle

    @Composable
    fun titleTextStyle(): TextStyle = PaletteTheme.componentThemes.feedbackDisplay.statisticTitleTextStyle

    @Composable
    fun trendTextStyle(): TextStyle = PaletteTheme.componentThemes.feedbackDisplay.statisticTrendTextStyle

    @Composable
    fun valueColor(): Color = PaletteTheme.componentThemes.feedbackDisplay.statisticValueColor

    @Composable
    fun titleColor(): Color = PaletteTheme.componentThemes.feedbackDisplay.statisticTitleColor

    @Composable
    fun trendUpColor(): Color = PaletteTheme.componentThemes.feedbackDisplay.statisticTrendUpColor

    @Composable
    fun trendDownColor(): Color = PaletteTheme.componentThemes.feedbackDisplay.statisticTrendDownColor
}
