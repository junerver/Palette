package xyz.junerver.compose.palette.components.mermaid

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import xyz.junerver.compose.palette.core.theme.PaletteTheme

@Immutable
data class MermaidColors(
    val nodeContainerColor: Color,
    val nodeBorderColor: Color,
    val nodeContentColor: Color,
    val edgeColor: Color,
    val entityHeaderColor: Color = Color.Unspecified,
    val primaryKeyColor: Color = Color.Unspecified,
    val foreignKeyColor: Color = Color.Unspecified,
    /** Sequence-diagram note fill — a warm tint distinct from regular participant boxes. */
    val noteColor: Color = Color.Unspecified,
    /** Sequence-diagram note outline. */
    val noteBorderColor: Color = Color.Unspecified,
)

object MermaidDefaults {
    @Composable
    fun colors(): MermaidColors {
        val tokens = PaletteTheme.componentThemes.utility
        // Note 的暖色调已上提到 utility 组件 token（mermaidNoteColor / mermaidNoteBorderColor），
        // 在主题层统一从 warning 语义 token 派生，符合组件样式应由顶层 token 控制的约定。
        return MermaidColors(
            nodeContainerColor = tokens.mermaidNodeContainerColor,
            nodeBorderColor = tokens.mermaidNodeBorderColor,
            nodeContentColor = tokens.mermaidNodeContentColor,
            edgeColor = tokens.mermaidEdgeColor,
            entityHeaderColor = tokens.mermaidEntityHeaderColor,
            primaryKeyColor = tokens.mermaidPrimaryKeyColor,
            foreignKeyColor = tokens.mermaidForeignKeyColor,
            noteColor = tokens.mermaidNoteColor,
            noteBorderColor = tokens.mermaidNoteBorderColor,
        )
    }

    @Composable
    fun cornerRadius(): Dp = PaletteTheme.componentThemes.utility.mermaidNodeCornerRadius
}
