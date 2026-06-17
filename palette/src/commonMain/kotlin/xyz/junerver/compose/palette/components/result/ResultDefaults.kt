package xyz.junerver.compose.palette.components.result

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import xyz.junerver.compose.palette.core.theme.PaletteTheme

enum class ResultStatus {
    Success, Error, Info, Warning, NotFound, NetworkError
}

object ResultDefaults {
    val IconSize: Dp = 64.dp
    val TitleFontSize: TextUnit = 20.sp
    val SubtitleFontSize: TextUnit = 14.sp
    val IconToTitleSpacing: Dp = 16.dp
    val TitleToSubtitleSpacing: Dp = 8.dp
    val SubtitleToExtraSpacing: Dp = 24.dp

    @Composable
    fun iconSize(): Dp = PaletteTheme.componentThemes.feedbackDisplay.resultIconSize

    @Composable
    fun titleTextStyle(): TextStyle = PaletteTheme.componentThemes.feedbackDisplay.resultTitleTextStyle

    @Composable
    fun titleFontSize(): TextUnit = PaletteTheme.componentThemes.feedbackDisplay.resultTitleTextStyle.fontSize

    @Composable
    fun subtitleTextStyle(): TextStyle = PaletteTheme.componentThemes.feedbackDisplay.resultSubtitleTextStyle

    @Composable
    fun subtitleFontSize(): TextUnit = PaletteTheme.componentThemes.feedbackDisplay.resultSubtitleTextStyle.fontSize

    @Composable
    fun iconToTitleSpacing(): Dp = PaletteTheme.componentThemes.feedbackDisplay.resultIconToTitleSpacing

    @Composable
    fun titleToSubtitleSpacing(): Dp = PaletteTheme.componentThemes.feedbackDisplay.resultTitleToSubtitleSpacing

    @Composable
    fun subtitleToExtraSpacing(): Dp = PaletteTheme.componentThemes.feedbackDisplay.resultSubtitleToExtraSpacing

    fun defaultTitle(status: ResultStatus): String = when (status) {
        ResultStatus.Success -> "操作成功"
        ResultStatus.Error -> "操作失败"
        ResultStatus.Info -> "信息提示"
        ResultStatus.Warning -> "警告提示"
        ResultStatus.NotFound -> "404"
        ResultStatus.NetworkError -> "网络异常"
    }

    fun defaultSubtitle(status: ResultStatus): String = when (status) {
        ResultStatus.Success -> "您的操作已成功完成"
        ResultStatus.Error -> "请稍后重试或联系管理员"
        ResultStatus.Info -> "请留意相关提示信息"
        ResultStatus.Warning -> "请注意核实相关信息"
        ResultStatus.NotFound -> "抱歉，您访问的页面不存在"
        ResultStatus.NetworkError -> "网络连接异常，请检查网络设置"
    }

    @Composable
    fun iconColor(status: ResultStatus): Color = when (status) {
        ResultStatus.Success -> PaletteTheme.componentThemes.feedbackDisplay.successColor
        ResultStatus.Error -> PaletteTheme.componentThemes.feedbackDisplay.errorColor
        ResultStatus.Info -> PaletteTheme.componentThemes.feedbackDisplay.infoColor
        ResultStatus.Warning -> PaletteTheme.componentThemes.feedbackDisplay.warningColor
        ResultStatus.NotFound -> PaletteTheme.componentThemes.feedbackDisplay.neutralColor
        ResultStatus.NetworkError -> PaletteTheme.componentThemes.feedbackDisplay.neutralColor
    }

    @Composable
    fun titleColor(): Color = PaletteTheme.componentThemes.feedbackDisplay.titleColor

    @Composable
    fun subtitleColor(): Color = PaletteTheme.componentThemes.feedbackDisplay.subtleColor
}
