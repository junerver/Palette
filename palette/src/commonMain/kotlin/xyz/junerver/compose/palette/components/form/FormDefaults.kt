package xyz.junerver.compose.palette.components.form

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import xyz.junerver.compose.palette.core.theme.PaletteTheme

object FormDefaults {
    val layout: FormLayout = FormLayout.Horizontal
    val labelPosition: FormLabelPosition = FormLabelPosition.Left
    
    val ItemSpacing: Dp = 24.dp
    val LabelWidth: Dp = 100.dp
    val LabelInputSpacing: Dp = 8.dp
    val InputHelperSpacing: Dp = 4.dp
    
    val LabelFontSize: TextUnit = 14.sp
    val HelperFontSize: TextUnit = 12.sp

    @Composable
    @ReadOnlyComposable
    fun itemSpacing(): Dp = PaletteTheme.componentThemes.form.itemSpacing

    @Composable
    @ReadOnlyComposable
    fun labelWidth(): Dp = PaletteTheme.componentThemes.form.labelWidth

    @Composable
    @ReadOnlyComposable
    fun labelInputSpacing(): Dp = PaletteTheme.componentThemes.form.labelInputSpacing

    @Composable
    @ReadOnlyComposable
    fun inputHelperSpacing(): Dp = PaletteTheme.componentThemes.form.inputHelperSpacing

    @Composable
    @ReadOnlyComposable
    fun labelTopPadding(): Dp = PaletteTheme.componentThemes.form.labelTopPadding

    @Composable
    @ReadOnlyComposable
    fun labelTextStyle(): TextStyle = PaletteTheme.componentThemes.form.labelTextStyle

    @Composable
    @ReadOnlyComposable
    fun helperTextStyle(): TextStyle = PaletteTheme.componentThemes.form.helperTextStyle
    
    val labelColor: Color
        @Composable
        @ReadOnlyComposable
        get() = PaletteTheme.componentThemes.form.labelColor

    val requiredColor: Color
        @Composable
        @ReadOnlyComposable
        get() = PaletteTheme.componentThemes.form.requiredColor
        
    val errorColor: Color
        @Composable
        @ReadOnlyComposable
        get() = PaletteTheme.componentThemes.form.errorColor
        
    val helpColor: Color
        @Composable
        @ReadOnlyComposable
        get() = PaletteTheme.componentThemes.form.helpColor
}
