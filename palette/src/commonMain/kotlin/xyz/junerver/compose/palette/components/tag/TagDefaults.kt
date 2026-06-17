package xyz.junerver.compose.palette.components.tag

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import xyz.junerver.compose.palette.core.theme.PaletteTheme
import kotlin.math.abs

enum class TagSize {
    Small, Medium, Large
}

data class TagSizeTokens(
    val height: Dp,
    val horizontalPadding: Dp,
    val cornerRadius: Dp,
    val closeButtonSize: Dp,
    val fontSize: TextUnit
)

object TagDefaults {
    // 向后兼容的旧常量
    val Height: Dp = 32.dp
    val HorizontalPadding: Dp = 12.dp
    val CornerRadius: Dp = 16.dp
    val CloseButtonSize: Dp = 18.dp
    val BorderWidth: Dp = 1.dp

    // Pastel 调色板 (16 色)
    private val pastelColors = listOf(
        Color(0xFFFFCDD2), // Light Red
        Color(0xFFF8BBD0), // Light Pink
        Color(0xFFE1BEE7), // Light Purple
        Color(0xFFD1C4E9), // Light Deep Purple
        Color(0xFFC5CAE9), // Light Indigo
        Color(0xFFBBDEFB), // Light Blue
        Color(0xFFB3E5FC), // Light Cyan
        Color(0xFFB2DFDB), // Light Teal
        Color(0xFFC8E6C9), // Light Green
        Color(0xFFDCEDC8), // Light Light Green
        Color(0xFFF0F4C3), // Light Lime
        Color(0xFFFFF9C4), // Light Yellow
        Color(0xFFFFECB3), // Light Amber
        Color(0xFFFFE0B2), // Light Orange
        Color(0xFFFFCCBC), // Light Deep Orange
        Color(0xFFD7CCC8)  // Light Brown
    )

    // 尺寸系统
    @Composable
    fun sizeTokens(size: TagSize): TagSizeTokens {
        val tokens = when (size) {
            TagSize.Small -> PaletteTheme.componentThemes.tag.small
            TagSize.Medium -> PaletteTheme.componentThemes.tag.medium
            TagSize.Large -> PaletteTheme.componentThemes.tag.large
        }
        return TagSizeTokens(
            height = tokens.height,
            horizontalPadding = tokens.horizontalPadding,
            cornerRadius = tokens.cornerRadius,
            closeButtonSize = tokens.closeButtonSize,
            fontSize = tokens.fontSize,
        )
    }

    fun legacySizeTokens(size: TagSize): TagSizeTokens = when (size) {
        TagSize.Small -> TagSizeTokens(
            height = 24.dp,
            horizontalPadding = 8.dp,
            cornerRadius = 12.dp,
            closeButtonSize = 14.dp,
            fontSize = 12.sp
        )
        TagSize.Medium -> TagSizeTokens(
            height = 32.dp,
            horizontalPadding = 12.dp,
            cornerRadius = 16.dp,
            closeButtonSize = 18.dp,
            fontSize = 14.sp
        )
        TagSize.Large -> TagSizeTokens(
            height = 40.dp,
            horizontalPadding = 16.dp,
            cornerRadius = 20.dp,
            closeButtonSize = 22.dp,
            fontSize = 16.sp
        )
    }

    @Composable
    fun borderWidth(): Dp = PaletteTheme.componentThemes.tag.borderWidth

    @Composable
    fun itemSpacing(): Dp = PaletteTheme.componentThemes.tag.itemSpacing

    @Composable
    fun closeIconScale(): Float = PaletteTheme.componentThemes.tag.closeIconScale

    // 向后兼容的颜色方案
    @Composable
    fun defaultColors(): TagColors = TagColors(
        containerColor = PaletteTheme.componentThemes.tag.defaultContainerColor,
        contentColor = PaletteTheme.componentThemes.tag.defaultContentColor,
        borderColor = PaletteTheme.componentThemes.tag.defaultBorderColor
    )

    @Composable
    fun outlinedColors(): TagColors = TagColors(
        containerColor = PaletteTheme.componentThemes.tag.outlinedContainerColor,
        contentColor = PaletteTheme.componentThemes.tag.outlinedContentColor,
        borderColor = PaletteTheme.componentThemes.tag.outlinedBorderColor
    )

    // 新增：语义化颜色方案
    @Composable
    fun successColors(): TagColors = TagColors(
        containerColor = PaletteTheme.componentThemes.tag.successContainerColor,
        contentColor = PaletteTheme.componentThemes.tag.successContentColor,
        borderColor = Color.Transparent
    )

    @Composable
    fun warningColors(): TagColors = TagColors(
        containerColor = PaletteTheme.componentThemes.tag.warningContainerColor,
        contentColor = PaletteTheme.componentThemes.tag.warningContentColor,
        borderColor = Color.Transparent
    )

    @Composable
    fun errorColors(): TagColors = TagColors(
        containerColor = PaletteTheme.componentThemes.tag.errorContainerColor,
        contentColor = PaletteTheme.componentThemes.tag.errorContentColor,
        borderColor = Color.Transparent
    )

    @Composable
    fun infoColors(): TagColors = TagColors(
        containerColor = PaletteTheme.componentThemes.tag.infoContainerColor,
        contentColor = PaletteTheme.componentThemes.tag.infoContentColor,
        borderColor = Color.Transparent
    )

    // 新增：Pastel 颜色方案（基于字符串哈希）
    @Composable
    fun pastelColors(seed: String): TagColors {
        val color = pastelColors[abs(seed.hashCode()) % pastelColors.size]
        return TagColors(
            containerColor = color,
            contentColor = PaletteTheme.componentThemes.tag.pastelContentColor,
            borderColor = Color.Transparent
        )
    }

    // 新增：自定义颜色快捷方式
    @Composable
    fun colors(color: Color): TagColors = TagColors(
        containerColor = color.copy(alpha = PaletteTheme.opacity.selected),
        contentColor = color,
        borderColor = Color.Transparent
    )
}

data class TagColors(
    val containerColor: Color,
    val contentColor: Color,
    val borderColor: Color
)
