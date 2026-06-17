package xyz.junerver.compose.palette.core.tokens

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
class PaletteSpacing(
    val none: Dp = 0.dp,
    val extraSmall: Dp = 4.dp,
    val small: Dp = 8.dp,
    val medium: Dp = 16.dp,
    val large: Dp = 24.dp,
    val extraLarge: Dp = 32.dp,
) {
    fun copy(
        none: Dp = this.none,
        extraSmall: Dp = this.extraSmall,
        small: Dp = this.small,
        medium: Dp = this.medium,
        large: Dp = this.large,
        extraLarge: Dp = this.extraLarge,
    ): PaletteSpacing = PaletteSpacing(
        none = none,
        extraSmall = extraSmall,
        small = small,
        medium = medium,
        large = large,
        extraLarge = extraLarge,
    )

    companion object {
        fun default() = PaletteSpacing()
    }
}
