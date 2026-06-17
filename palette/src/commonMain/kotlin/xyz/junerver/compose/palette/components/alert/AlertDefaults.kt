package xyz.junerver.compose.palette.components.alert

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
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
    fun cornerRadius(): Dp = PaletteTheme.componentThemes.feedbackDisplay.alertCornerRadius

    @Composable
    fun contentPadding(): Dp = PaletteTheme.componentThemes.feedbackDisplay.alertContentPadding

    @Composable
    fun iconSize(): Dp = PaletteTheme.componentThemes.feedbackDisplay.alertIconSize

    @Composable
    fun iconSpacing(): Dp = PaletteTheme.componentThemes.feedbackDisplay.alertIconSpacing

    @Composable
    fun closeIconSize(): Dp = PaletteTheme.componentThemes.feedbackDisplay.alertCloseIconSize

    @Composable
    fun borderWidth(): Dp = PaletteTheme.componentThemes.feedbackDisplay.alertBorderWidth

    @Composable
    fun messageDescriptionSpacing(): Dp = PaletteTheme.componentThemes.feedbackDisplay.alertMessageDescriptionSpacing

    @Composable
    fun descriptionAlpha(): Float = PaletteTheme.componentThemes.feedbackDisplay.alertDescriptionAlpha

    @Composable
    fun closeIconAlpha(): Float = PaletteTheme.componentThemes.feedbackDisplay.alertCloseIconAlpha

    @Composable
    fun messageTextStyle(): TextStyle = PaletteTheme.componentThemes.feedbackDisplay.alertMessageTextStyle

    @Composable
    fun messageFontSize(): TextUnit = PaletteTheme.componentThemes.feedbackDisplay.alertMessageTextStyle.fontSize

    @Composable
    fun descriptionTextStyle(): TextStyle = PaletteTheme.componentThemes.feedbackDisplay.alertDescriptionTextStyle

    @Composable
    fun descriptionFontSize(): TextUnit = PaletteTheme.componentThemes.feedbackDisplay.alertDescriptionTextStyle.fontSize

    @Composable
    fun infoColor(): Color = PaletteTheme.componentThemes.feedbackDisplay.infoColor

    @Composable
    fun successColor(): Color = PaletteTheme.componentThemes.feedbackDisplay.successColor

    @Composable
    fun warningColor(): Color = PaletteTheme.componentThemes.feedbackDisplay.warningColor

    @Composable
    fun errorColor(): Color = PaletteTheme.componentThemes.feedbackDisplay.errorColor

    @Composable
    fun containerColor(type: AlertType): Color = when (type) {
        AlertType.Info -> PaletteTheme.componentThemes.feedbackDisplay.alertInfoContainerColor
        AlertType.Success -> PaletteTheme.componentThemes.feedbackDisplay.alertSuccessContainerColor
        AlertType.Warning -> PaletteTheme.componentThemes.feedbackDisplay.alertWarningContainerColor
        AlertType.Error -> PaletteTheme.componentThemes.feedbackDisplay.alertErrorContainerColor
    }

    @Composable
    fun borderColor(type: AlertType): Color = when (type) {
        AlertType.Info -> PaletteTheme.componentThemes.feedbackDisplay.alertInfoBorderColor
        AlertType.Success -> PaletteTheme.componentThemes.feedbackDisplay.alertSuccessBorderColor
        AlertType.Warning -> PaletteTheme.componentThemes.feedbackDisplay.alertWarningBorderColor
        AlertType.Error -> PaletteTheme.componentThemes.feedbackDisplay.alertErrorBorderColor
    }

    @Composable
    fun contentColor(type: AlertType): Color = when (type) {
        AlertType.Info -> infoColor()
        AlertType.Success -> successColor()
        AlertType.Warning -> warningColor()
        AlertType.Error -> errorColor()
    }
}
