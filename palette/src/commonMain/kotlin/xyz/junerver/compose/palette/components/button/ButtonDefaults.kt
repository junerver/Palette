package xyz.junerver.compose.palette.components.button

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import xyz.junerver.compose.palette.core.theme.PaletteTheme
import xyz.junerver.compose.palette.core.tokens.PaletteButtonSizeTokens

enum class ButtonType {
    PRIMARY,
    DANGER,
    PLAIN
}

enum class ButtonSize(
    val padding: PaddingValues,
    val fontSize: TextUnit,
    val borderRadius: Dp = 8.dp
) {
    LARGE(PaddingValues(vertical = 12.dp, horizontal = 24.dp), 17.sp),
    MEDIUM(PaddingValues(vertical = 10.dp, horizontal = 24.dp), 14.sp),
    SMALL(PaddingValues(vertical = 6.dp, horizontal = 12.dp), 14.sp, 6.dp)
}

object ButtonDefaults {
    val DefaultWidth: Dp = 184.dp
    val LoadingSpacing: Dp = 8.dp
    val DisabledAlpha: Float = 0.7f

    @Composable
    fun defaultWidth(): Dp = PaletteTheme.componentThemes.button.defaultWidth

    @Composable
    fun loadingSpacing(): Dp = PaletteTheme.componentThemes.button.loadingSpacing

    @Composable
    fun disabledAlpha(): Float = PaletteTheme.componentThemes.button.disabledAlpha

    @Composable
    fun sizeTokens(size: ButtonSize): PaletteButtonSizeTokens = when (size) {
        ButtonSize.LARGE -> PaletteTheme.componentThemes.button.large
        ButtonSize.MEDIUM -> PaletteTheme.componentThemes.button.medium
        ButtonSize.SMALL -> PaletteTheme.componentThemes.button.small
    }

    @Composable
    fun padding(size: ButtonSize): PaddingValues {
        val tokens = sizeTokens(size)
        return PaddingValues(
            vertical = tokens.verticalPadding,
            horizontal = tokens.horizontalPadding,
        )
    }

    @Composable
    fun fontSize(size: ButtonSize): TextUnit = sizeTokens(size).fontSize

    @Composable
    fun borderRadius(size: ButtonSize): Dp = sizeTokens(size).borderRadius

    @Composable
    fun primaryContainerColor(): Color = PaletteTheme.componentThemes.button.primaryContainerColor

    @Composable
    fun primaryContentColor(): Color = PaletteTheme.componentThemes.button.primaryContentColor

    @Composable
    fun dangerContainerColor(): Color = PaletteTheme.componentThemes.button.dangerContainerColor

    @Composable
    fun dangerContentColor(): Color = PaletteTheme.componentThemes.button.dangerContentColor

    @Composable
    fun plainContainerColor(): Color = PaletteTheme.componentThemes.button.plainContainerColor

    @Composable
    fun plainContentColor(): Color = PaletteTheme.componentThemes.button.plainContentColor
}
