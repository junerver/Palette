package xyz.junerver.compose.palette.components.form

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
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
    
    val labelColor: Color
        @Composable
        @ReadOnlyComposable
        get() = PaletteTheme.colors.onSurface.copy(alpha = 0.85f)
        
    val errorColor: Color
        @Composable
        @ReadOnlyComposable
        get() = PaletteTheme.colors.error
        
    val helpColor: Color
        @Composable
        @ReadOnlyComposable
        get() = PaletteTheme.colors.onSurface.copy(alpha = 0.45f)
}
