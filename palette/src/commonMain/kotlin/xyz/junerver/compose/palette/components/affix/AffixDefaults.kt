package xyz.junerver.compose.palette.components.affix

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.core.theme.PaletteTheme

enum class AffixPosition {
    Top, Bottom
}

object AffixDefaults {
    val DefaultOffset: Dp = 0.dp
    val zIndex: Float = 10f

    @Composable
    fun defaultOffset(): Dp = PaletteTheme.componentThemes.layout.affixDefaultOffset
}
