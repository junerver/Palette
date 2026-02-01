package xyz.junerver.compose.palette.core.tokens

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
class PaletteColors(
    val primary: Color = Color(0xFF0F71F2),
    val onPrimary: Color = Color.White,
    val border: Color = Color(0xFFD9D9D9),
    val surface: Color = Color.White,
    val onSurface: Color = Color.Black,
    val hint: Color = Color(0xFFBFBFBF),
    val error: Color = Color(0xFFE53935),
    val success: Color = Color(0xFF10B981),
    val warning: Color = Color(0xFFF59E0B),
) {
    companion object {
        fun light() = PaletteColors()

        fun dark() = PaletteColors(
            primary = Color(0xFF4A9FF5),
            onPrimary = Color.White,
            border = Color(0xFF424242),
            surface = Color(0xFF1E1E1E),
            onSurface = Color.White,
            hint = Color(0xFF757575),
            error = Color(0xFFEF5350),
            success = Color(0xFF4CAF50),
            warning = Color(0xFFFFB74D),
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PaletteColors) return false
        return primary == other.primary &&
            onPrimary == other.onPrimary &&
            border == other.border &&
            surface == other.surface &&
            onSurface == other.onSurface &&
            hint == other.hint &&
            error == other.error &&
            success == other.success &&
            warning == other.warning
    }

    override fun hashCode(): Int {
        var result = primary.hashCode()
        result = 31 * result + onPrimary.hashCode()
        result = 31 * result + border.hashCode()
        result = 31 * result + surface.hashCode()
        result = 31 * result + onSurface.hashCode()
        result = 31 * result + hint.hashCode()
        result = 31 * result + error.hashCode()
        result = 31 * result + success.hashCode()
        result = 31 * result + warning.hashCode()
        return result
    }
}
