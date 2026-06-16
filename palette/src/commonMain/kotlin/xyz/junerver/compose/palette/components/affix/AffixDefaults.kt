package xyz.junerver.compose.palette.components.affix

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class AffixPosition {
    Top, Bottom
}

object AffixDefaults {
    val DefaultOffset: Dp = 0.dp
    val zIndex: Float = 10f
}
