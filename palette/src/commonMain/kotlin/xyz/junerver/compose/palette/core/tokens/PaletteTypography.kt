package xyz.junerver.compose.palette.core.tokens

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Immutable
class PaletteTypography(
    val title: TextStyle = TextStyle(
        fontSize = 18.sp,
        lineHeight = 26.sp,
        fontWeight = FontWeight.Medium,
    ),
    val body: TextStyle = TextStyle(
        fontSize = 14.sp,
        lineHeight = 20.sp,
    ),
    val label: TextStyle = TextStyle(
        fontSize = 12.sp,
        lineHeight = 16.sp,
        fontWeight = FontWeight.Medium,
    ),
) {
    fun copy(
        title: TextStyle = this.title,
        body: TextStyle = this.body,
        label: TextStyle = this.label,
    ): PaletteTypography = PaletteTypography(
        title = title,
        body = body,
        label = label,
    )

    companion object {
        fun default() = PaletteTypography()
    }
}
