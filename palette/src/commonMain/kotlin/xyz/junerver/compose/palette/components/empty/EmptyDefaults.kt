package xyz.junerver.compose.palette.components.empty

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.core.theme.PaletteTheme

object EmptyDefaults {
    val IconSize: Dp = 64.dp
    val IconToTitle: Dp = 16.dp
    val TitleToDescription: Dp = 8.dp
    val DescriptionToAction: Dp = 24.dp

    @Composable
    fun iconSize(): Dp = PaletteTheme.componentThemes.feedbackDisplay.emptyIconSize

    @Composable
    fun iconToTitleSpacing(): Dp = PaletteTheme.componentThemes.feedbackDisplay.emptyIconToTitleSpacing

    @Composable
    fun titleToDescriptionSpacing(): Dp = PaletteTheme.componentThemes.feedbackDisplay.emptyTitleToDescriptionSpacing

    @Composable
    fun descriptionToActionSpacing(): Dp = PaletteTheme.componentThemes.feedbackDisplay.emptyDescriptionToActionSpacing

    @Composable
    fun titleTextStyle(): TextStyle = PaletteTheme.componentThemes.feedbackDisplay.emptyTitleTextStyle

    @Composable
    fun descriptionTextStyle(): TextStyle = PaletteTheme.componentThemes.feedbackDisplay.emptyDescriptionTextStyle
    
    @Composable
    fun iconColor(): Color = PaletteTheme.componentThemes.feedbackDisplay.neutralColor
    
    @Composable
    fun titleColor(): Color = PaletteTheme.componentThemes.feedbackDisplay.titleColor
    
    @Composable
    fun descriptionColor(): Color = PaletteTheme.componentThemes.feedbackDisplay.subtleColor

    @Composable
    fun defaultTitle(): String = PaletteTheme.strings.emptyDefaultTitle

    @Composable
    fun defaultDescription(): String = PaletteTheme.strings.emptyDefaultDescription
}
