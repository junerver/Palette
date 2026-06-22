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
)

object MermaidDefaults {
    @Composable
    fun colors(): MermaidColors {
        val tokens = PaletteTheme.componentThemes.utility
        return MermaidColors(
            nodeContainerColor = tokens.mermaidNodeContainerColor,
            nodeBorderColor = tokens.mermaidNodeBorderColor,
            nodeContentColor = tokens.mermaidNodeContentColor,
            edgeColor = tokens.mermaidEdgeColor,
        )
    }

    @Composable
    fun cornerRadius(): Dp = PaletteTheme.componentThemes.utility.mermaidNodeCornerRadius
}
