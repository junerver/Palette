package xyz.junerver.compose.palette.components.markdown

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.core.theme.PaletteTheme

object MarkdownDefaults {
    @Composable
    fun blockSpacing(): Dp = PaletteTheme.componentThemes.utility.markdownBlockSpacing

    @Composable
    fun editorMinHeight(): Dp = PaletteTheme.componentThemes.utility.markdownEditorMinHeight

    /** 格式化工具栏的图标尺寸，跟随主题字号令牌缩放。 */
    val toolbarIconSize: Dp = 20.dp

    /** 工具栏按钮之间的间距，从主题间距令牌派生。 */
    @Composable
    fun toolbarButtonSpacing(): Dp = PaletteTheme.spacing.extraSmall

    /** 工具栏分组之间的额外间距。 */
    @Composable
    fun toolbarGroupSpacing(): Dp = PaletteTheme.spacing.small

    /** 工具栏整体高度下限，确保触摸/点击区域。 */
    val toolbarMinHeight: Dp = 40.dp
}
