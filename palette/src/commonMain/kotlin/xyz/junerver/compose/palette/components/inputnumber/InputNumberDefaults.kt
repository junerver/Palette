package xyz.junerver.compose.palette.components.inputnumber

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.core.theme.PaletteTheme

object InputNumberDefaults {
    val Width: Dp = 120.dp
    val ButtonWidth: Dp = 32.dp
    val MinButtonWidth: Dp = 32.dp
    val AnimationDuration: Int = 100
    val DisabledAlpha: Float = 0.5f

    @Composable
    fun buttonColor(): Color = PaletteTheme.colors.border

    @Composable
    fun buttonIconColor(): Color = PaletteTheme.colors.onSurface

    @Composable
    fun disabledButtonColor(): Color = PaletteTheme.colors.border.copy(alpha = DisabledAlpha)

    @Composable
    fun disabledButtonIconColor(): Color = PaletteTheme.colors.onSurface.copy(alpha = DisabledAlpha)
}
