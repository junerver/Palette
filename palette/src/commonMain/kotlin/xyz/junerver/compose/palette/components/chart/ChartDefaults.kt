package xyz.junerver.compose.palette.components.chart

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.core.theme.PaletteTheme

/**
 * Resolved colors for a chart. Every field defaults to a [PaletteChartTokens] value so the whole
 * family restyles from `PaletteTheme`; pass overrides for instance-level customization.
 */
@Immutable
data class ChartColors(
    val axisColor: Color,
    val gridColor: Color,
    val tickLabelColor: Color,
    val axisTitleColor: Color,
    val seriesLabelColor: Color,
    val legendTextColor: Color,
    val emptyStateColor: Color,
    val categoricalColors: List<Color>,
)

object ChartDefaults {
    /** Token-backed chart colors. */
    @Composable
    @ReadOnlyComposable
    fun colors(): ChartColors {
        val tokens = PaletteTheme.componentThemes.chart
        return ChartColors(
            axisColor = tokens.axisColor,
            gridColor = tokens.gridColor,
            tickLabelColor = tokens.tickLabelColor,
            axisTitleColor = tokens.axisTitleColor,
            seriesLabelColor = tokens.seriesLabelColor,
            legendTextColor = tokens.legendTextColor,
            emptyStateColor = tokens.emptyStateColor,
            categoricalColors = tokens.categoricalColors,
        )
    }

    @Composable
    @ReadOnlyComposable
    fun axisStrokeWidth(): Dp = PaletteTheme.componentThemes.chart.axisStrokeWidth

    @Composable
    @ReadOnlyComposable
    fun gridStrokeWidth(): Dp = PaletteTheme.componentThemes.chart.gridStrokeWidth

    @Composable
    @ReadOnlyComposable
    fun axisTextStyle(): TextStyle = PaletteTheme.componentThemes.chart.axisTextStyle

    @Composable
    @ReadOnlyComposable
    fun legendTextStyle(): TextStyle = PaletteTheme.componentThemes.chart.legendTextStyle

    @Composable
    @ReadOnlyComposable
    fun titleTextStyle(): TextStyle = PaletteTheme.componentThemes.chart.titleTextStyle

    @Composable
    @ReadOnlyComposable
    fun legendSymbolSize(): Dp = PaletteTheme.componentThemes.chart.legendSymbolSize

    @Composable
    @ReadOnlyComposable
    fun donutHoleRadiusFraction(): Float = PaletteTheme.componentThemes.chart.donutHoleRadiusFraction

    @Composable
    @ReadOnlyComposable
    fun barCornerRadius(): Dp = PaletteTheme.componentThemes.chart.barCornerRadius

    /** Default outer chart canvas height. */
    val defaultHeight: Dp = 240.dp
}
