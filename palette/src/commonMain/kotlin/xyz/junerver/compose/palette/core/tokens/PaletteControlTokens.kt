package xyz.junerver.compose.palette.core.tokens

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Immutable
data class PaletteControlSizeTokens(
    val height: Dp,
    val fontSize: TextUnit,
    val iconSize: Dp,
    val horizontalPadding: Dp,
    val verticalPadding: Dp,
    val cornerRadius: Dp,
)

@Immutable
data class PaletteControlTokens(
    val small: PaletteControlSizeTokens = PaletteControlSizeTokens(
        height = 24.dp,
        fontSize = 14.sp,
        iconSize = 16.dp,
        horizontalPadding = 12.dp,
        verticalPadding = 4.dp,
        cornerRadius = 4.dp,
    ),
    val medium: PaletteControlSizeTokens = PaletteControlSizeTokens(
        height = 40.dp,
        fontSize = 16.sp,
        iconSize = 20.dp,
        horizontalPadding = 16.dp,
        verticalPadding = 8.dp,
        cornerRadius = 6.dp,
    ),
    val large: PaletteControlSizeTokens = PaletteControlSizeTokens(
        height = 40.dp,
        fontSize = 18.sp,
        iconSize = 24.dp,
        horizontalPadding = 20.dp,
        verticalPadding = 12.dp,
        cornerRadius = 8.dp,
    ),
    val borderWidth: Dp = 1.dp,
    val focusBorderWidth: Dp = 2.dp,
    val disabledBorderWidth: Dp = 1.dp,
    val focusRingOffset: Dp = 2.dp,
) {
    companion object {
        fun default() = PaletteControlTokens()
    }
}
