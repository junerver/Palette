package xyz.junerver.compose.palette.core.tokens

import androidx.compose.runtime.Immutable

@Immutable
data class PaletteOpacity(
    val disabled: Float = 0.5f,
    val disabledStrong: Float = 0.7f,
    val subtle: Float = 0.6f,
    val muted: Float = 0.45f,
    val hover: Float = 0.08f,
    val pressed: Float = 0.12f,
    val selected: Float = 0.12f,
    val focusRing: Float = 0.2f,
    val overlay: Float = 0.45f,
    val elevatedSurface: Float = 0.95f,
    val borderSubtle: Float = 0.5f,
) {
    companion object {
        fun default() = PaletteOpacity()
    }
}
