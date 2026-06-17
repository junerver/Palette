package xyz.junerver.compose.palette.core.tokens

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class PaletteElevation(
    val none: Dp = 0.dp,
    val raised: Dp = 1.dp,
    val overlay: Dp = 4.dp,
    val modal: Dp = 8.dp,
    val floating: Dp = 12.dp,
    val focusShadowBlur: Dp = 4.dp,
) {
    companion object {
        fun default() = PaletteElevation()
    }
}
