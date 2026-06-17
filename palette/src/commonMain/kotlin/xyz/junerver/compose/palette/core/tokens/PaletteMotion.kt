package xyz.junerver.compose.palette.core.tokens

import androidx.compose.runtime.Immutable

@Immutable
data class PaletteMotion(
    val durationFast: Int = 150,
    val durationNormal: Int = 250,
    val durationSlow: Int = 350,
    val overlayEnter: Int = 180,
    val overlayExit: Int = 180,
) {
    companion object {
        fun default() = PaletteMotion()
    }
}
