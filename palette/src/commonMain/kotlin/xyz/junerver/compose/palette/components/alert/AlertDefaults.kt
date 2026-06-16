package xyz.junerver.compose.palette.components.alert

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import xyz.junerver.compose.palette.core.theme.PaletteTheme

enum class AlertType {
    Info, Success, Warning, Error
}

object AlertDefaults {
    val CornerRadius: Dp = 8.dp
    val ContentPadding: Dp = 12.dp
    val IconSize: Dp = 16.dp
    val IconSpacing: Dp = 8.dp
    val CloseIconSize: Dp = 16.dp
    val BorderWidth: Dp = 1.dp
    val DescriptionFontSize: TextUnit = 14.sp
    val MessageFontSize: TextUnit = 14.sp

    @Composable
    fun infoColor(): Color = PaletteTheme.colors.primary

    @Composable
    fun successColor(): Color = PaletteTheme.colors.success

    @Composable
    fun warningColor(): Color = PaletteTheme.colors.warning

    @Composable
    fun errorColor(): Color = PaletteTheme.colors.error

    @Composable
    fun containerColor(type: AlertType): Color = when (type) {
        AlertType.Info -> infoColor().copy(alpha = 0.08f)
        AlertType.Success -> successColor().copy(alpha = 0.08f)
        AlertType.Warning -> warningColor().copy(alpha = 0.08f)
        AlertType.Error -> errorColor().copy(alpha = 0.08f)
    }

    @Composable
    fun borderColor(type: AlertType): Color = when (type) {
        AlertType.Info -> infoColor().copy(alpha = 0.3f)
        AlertType.Success -> successColor().copy(alpha = 0.3f)
        AlertType.Warning -> warningColor().copy(alpha = 0.3f)
        AlertType.Error -> errorColor().copy(alpha = 0.3f)
    }

    @Composable
    fun contentColor(type: AlertType): Color = when (type) {
        AlertType.Info -> infoColor()
        AlertType.Success -> successColor()
        AlertType.Warning -> warningColor()
        AlertType.Error -> errorColor()
    }
}
