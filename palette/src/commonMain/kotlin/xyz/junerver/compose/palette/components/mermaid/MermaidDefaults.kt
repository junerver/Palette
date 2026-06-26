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
        val semantic = PaletteTheme.colors
        // Notes get a warm fill/border derived from the `warning` token (mermaid.live's note
        // yellow), so they're visually distinct from participant boxes without a hard-coded hue.
        val warning = if (semantic.warning != Color.Unspecified) semantic.warning else Color(0xFFF59E0B)
        return MermaidColors(
            nodeContainerColor = tokens.mermaidNodeContainerColor,
            nodeBorderColor = tokens.mermaidNodeBorderColor,
            nodeContentColor = tokens.mermaidNodeContentColor,
            edgeColor = tokens.mermaidEdgeColor,
            entityHeaderColor = tokens.mermaidEntityHeaderColor,
            primaryKeyColor = tokens.mermaidPrimaryKeyColor,
            foreignKeyColor = tokens.mermaidForeignKeyColor,
            noteColor = warning.copy(alpha = 0.20f),
            noteBorderColor = warning.copy(alpha = 0.75f),
        )
    }

    @Composable
    fun cornerRadius(): Dp = PaletteTheme.componentThemes.utility.mermaidNodeCornerRadius
}
