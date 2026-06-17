package xyz.junerver.compose.palette.components.card

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.core.theme.PaletteTheme

object CardDefaults {
    val CornerRadius: Dp = 12.dp
    val ContentPadding: Dp = 16.dp
    val Elevation: Dp = 1.dp
    val BorderWidth: Dp = 1.dp

    @Composable
    fun cornerRadius(): Dp = PaletteTheme.componentThemes.card.cornerRadius

    @Composable
    fun contentPadding(): Dp = PaletteTheme.componentThemes.card.contentPadding

    @Composable
    fun elevation(): Dp = PaletteTheme.componentThemes.card.elevation

    @Composable
    fun borderWidth(): Dp = PaletteTheme.componentThemes.card.borderWidth

    @Composable
    fun outlinedBorderColor(): Color = PaletteTheme.componentThemes.card.outlinedBorderColor

    @Composable
    fun elevatedColors(): CardColors = CardColors(
        containerColor = PaletteTheme.componentThemes.card.elevatedContainerColor,
        contentColor = PaletteTheme.componentThemes.card.elevatedContentColor
    )

    @Composable
    fun filledColors(): CardColors = CardColors(
        containerColor = PaletteTheme.componentThemes.card.filledContainerColor,
        contentColor = PaletteTheme.componentThemes.card.filledContentColor
    )

    @Composable
    fun outlinedColors(): CardColors = CardColors(
        containerColor = PaletteTheme.componentThemes.card.outlinedContainerColor,
        contentColor = PaletteTheme.componentThemes.card.outlinedContentColor
    )
}

data class CardColors(
    val containerColor: Color,
    val contentColor: Color
)
