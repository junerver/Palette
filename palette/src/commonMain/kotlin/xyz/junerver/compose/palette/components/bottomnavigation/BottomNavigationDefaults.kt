package xyz.junerver.compose.palette.components.bottomnavigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.core.theme.PaletteMaterialTheme
import xyz.junerver.compose.palette.core.theme.PaletteTheme

@Immutable
data class BottomNavigationColors(
    val containerColor: Color,
    val selectedContentColor: Color,
    val contentColor: Color,
    val disabledContentColor: Color,
    val selectedIndicatorColor: Color,
)

object BottomNavigationDefaults {
    val Height: Dp = 72.dp
    val ItemCornerRadius: Dp = 16.dp
    val ItemHorizontalPadding: Dp = 8.dp
    val ItemVerticalPadding: Dp = 8.dp
    val ItemContentVerticalPadding: Dp = 8.dp
    val IconLabelSpacing: Dp = 2.dp

    @Composable
    fun height(): Dp = Height

    @Composable
    fun itemCornerRadius(): Dp = ItemCornerRadius

    @Composable
    fun itemHorizontalPadding(): Dp = ItemHorizontalPadding

    @Composable
    fun itemVerticalPadding(): Dp = ItemVerticalPadding

    @Composable
    fun itemContentVerticalPadding(): Dp = ItemContentVerticalPadding

    @Composable
    fun iconLabelSpacing(): Dp = IconLabelSpacing

    @Composable
    fun labelTextStyle(): TextStyle = PaletteTheme.componentThemes.navigationMenu.tabsTextStyle

    @Composable
    fun colors(
        containerColor: Color = PaletteMaterialTheme.colorScheme.surface,
        selectedContentColor: Color = PaletteTheme.componentThemes.navigationMenu.selectedTextColor,
        contentColor: Color = PaletteTheme.componentThemes.navigationMenu.textColor,
        disabledContentColor: Color = PaletteTheme.componentThemes.navigationMenu.disabledTextColor,
        selectedIndicatorColor: Color = PaletteTheme.componentThemes.navigationMenu.selectedContainerColor,
    ): BottomNavigationColors = BottomNavigationColors(
        containerColor = containerColor,
        selectedContentColor = selectedContentColor,
        contentColor = contentColor,
        disabledContentColor = disabledContentColor,
        selectedIndicatorColor = selectedIndicatorColor,
    )
}
