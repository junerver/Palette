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
    fun nodeHeight(): Dp = PaletteTheme.componentThemes.dataEntry.treeNodeHeight

    @Composable
    fun indent(): Dp = PaletteTheme.componentThemes.dataEntry.treeIndent

    @Composable
    fun iconSize(): Dp = PaletteTheme.componentThemes.dataEntry.treeIconSize

    @Composable
    fun iconTextSpacing(): Dp = PaletteTheme.componentThemes.dataEntry.treeIconTextSpacing

    @Composable
    fun nodeColor(): Color = PaletteTheme.componentThemes.dataEntry.treeNodeColor

    @Composable
    fun selectedColor(): Color = PaletteTheme.componentThemes.dataEntry.treeSelectedColor

    @Composable
    fun selectedContainerColor(): Color = PaletteTheme.componentThemes.dataEntry.treeSelectedContainerColor

    @Composable
    fun iconColor(): Color = PaletteTheme.componentThemes.dataEntry.treeIconColor
}
