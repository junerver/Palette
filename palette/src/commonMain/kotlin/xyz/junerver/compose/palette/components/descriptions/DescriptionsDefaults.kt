package xyz.junerver.compose.palette.components.descriptions

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.core.theme.PaletteTheme

object DescriptionsDefaults {
    val RowHeight: Dp = 40.dp
    val LabelWidth: Dp = 120.dp
    val Padding: Dp = 12.dp

    @Composable
    fun rowHeight(): Dp = PaletteTheme.componentThemes.dataDisplay.descriptionsRowHeight

    @Composable
    fun labelWidth(): Dp = PaletteTheme.componentThemes.dataDisplay.descriptionsLabelWidth

    @Composable
    fun padding(): Dp = PaletteTheme.componentThemes.dataDisplay.descriptionsPadding

    @Composable
    fun rowSpacing(): Dp = PaletteTheme.componentThemes.dataDisplay.descriptionsRowSpacing

    @Composable
    fun dividerHeight(): Dp = PaletteTheme.componentThemes.dataDisplay.descriptionsDividerHeight

    @Composable
    fun dividerColor(): Color = PaletteTheme.componentThemes.dataDisplay.descriptionsDividerColor

    @Composable
    fun textStyle(): TextStyle = PaletteTheme.componentThemes.dataDisplay.descriptionsTextStyle

    @Composable
    fun labelColor(): Color = PaletteTheme.componentThemes.dataDisplay.descriptionsLabelColor

    @Composable
    fun contentColor(): Color = PaletteTheme.componentThemes.dataDisplay.descriptionsContentColor
}
