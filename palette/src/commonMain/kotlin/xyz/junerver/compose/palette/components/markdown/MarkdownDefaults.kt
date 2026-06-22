package xyz.junerver.compose.palette.components.markdown

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import xyz.junerver.compose.palette.core.theme.PaletteTheme

object MarkdownDefaults {
    @Composable
    fun blockSpacing(): Dp = PaletteTheme.componentThemes.utility.markdownBlockSpacing

    @Composable
    fun editorMinHeight(): Dp = PaletteTheme.componentThemes.utility.markdownEditorMinHeight
}
