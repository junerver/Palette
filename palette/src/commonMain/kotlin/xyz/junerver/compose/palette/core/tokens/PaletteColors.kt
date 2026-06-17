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
    val onError: Color = Color.White,
    val success: Color = Color(0xFF10B981),
    val warning: Color = Color(0xFFF59E0B),
    val info: Color = primary,
    val danger: Color = error,
    val textPrimary: Color = onSurface,
    val textSecondary: Color = onSurface.copy(alpha = 0.64f),
    val textTertiary: Color = onSurface.copy(alpha = 0.45f),
    val textDisabled: Color = onSurface.copy(alpha = 0.38f),
    val inverseSurface: Color = onSurface,
    val inverseOnSurface: Color = surface,
    val pageBackground: Color = surface,
    val appBackground: Color = surface,
    val surfaceElevated: Color = surface,
    val surfaceOverlay: Color = surface.copy(alpha = 0.95f),
    val divider: Color = border.copy(alpha = 0.72f),
    val borderHover: Color = primary.copy(alpha = 0.3f),
    val borderFocus: Color = primary.copy(alpha = 0.6f),
    val borderDisabled: Color = border.copy(alpha = 0.5f),
    val bgDisabled: Color = surface.copy(alpha = 0.05f),
    val bgHover: Color = primary.copy(alpha = 0.08f),
    val bgPressed: Color = primary.copy(alpha = 0.12f),
    val bgSelected: Color = primary.copy(alpha = 0.12f),
    val overlay: Color = Color.Black.copy(alpha = 0.45f),
    val shadow: Color = Color.Black.copy(alpha = 0.16f),
    val shadowFocus: Color = primary.copy(alpha = 0.2f),
    val shadowError: Color = error.copy(alpha = 0.2f),
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
            onError = Color.White,
            success = Color(0xFF4CAF50),
            warning = Color(0xFFFFB74D),
            pageBackground = Color(0xFF121212),
            appBackground = Color(0xFF101010),
            surfaceElevated = Color(0xFF2A2A2A),
            surfaceOverlay = Color(0xFF1E1E1E).copy(alpha = 0.95f),
            overlay = Color.Black.copy(alpha = 0.64f),
            shadow = Color.Black.copy(alpha = 0.32f),
        )
    }

    fun copy(
        primary: Color = this.primary,
        onPrimary: Color = this.onPrimary,
        border: Color = this.border,
        surface: Color = this.surface,
        onSurface: Color = this.onSurface,
        hint: Color = this.hint,
        error: Color = this.error,
        onError: Color = this.onError,
        success: Color = this.success,
        warning: Color = this.warning,
        info: Color = this.info,
        danger: Color = this.danger,
        textPrimary: Color = this.textPrimary,
        textSecondary: Color = this.textSecondary,
        textTertiary: Color = this.textTertiary,
        textDisabled: Color = this.textDisabled,
        inverseSurface: Color = this.inverseSurface,
        inverseOnSurface: Color = this.inverseOnSurface,
        pageBackground: Color = this.pageBackground,
        appBackground: Color = this.appBackground,
        surfaceElevated: Color = this.surfaceElevated,
        surfaceOverlay: Color = this.surfaceOverlay,
        divider: Color = this.divider,
        borderHover: Color = this.borderHover,
        borderFocus: Color = this.borderFocus,
        borderDisabled: Color = this.borderDisabled,
        bgDisabled: Color = this.bgDisabled,
        bgHover: Color = this.bgHover,
        bgPressed: Color = this.bgPressed,
        bgSelected: Color = this.bgSelected,
        overlay: Color = this.overlay,
        shadow: Color = this.shadow,
        shadowFocus: Color = this.shadowFocus,
        shadowError: Color = this.shadowError,
    ): PaletteColors = PaletteColors(
        primary = primary,
        onPrimary = onPrimary,
        border = border,
        surface = surface,
        onSurface = onSurface,
        hint = hint,
        error = error,
        onError = onError,
        success = success,
        warning = warning,
        info = info,
        danger = danger,
        textPrimary = textPrimary,
        textSecondary = textSecondary,
        textTertiary = textTertiary,
        textDisabled = textDisabled,
        inverseSurface = inverseSurface,
        inverseOnSurface = inverseOnSurface,
        pageBackground = pageBackground,
        appBackground = appBackground,
        surfaceElevated = surfaceElevated,
        surfaceOverlay = surfaceOverlay,
        divider = divider,
        borderHover = borderHover,
        borderFocus = borderFocus,
        borderDisabled = borderDisabled,
        bgDisabled = bgDisabled,
        bgHover = bgHover,
        bgPressed = bgPressed,
        bgSelected = bgSelected,
        overlay = overlay,
        shadow = shadow,
        shadowFocus = shadowFocus,
        shadowError = shadowError,
    )

    fun derive(
        primary: Color = this.primary,
        onPrimary: Color = this.onPrimary,
        border: Color = this.border,
        surface: Color = this.surface,
        onSurface: Color = this.onSurface,
        hint: Color = this.hint,
        error: Color = this.error,
        onError: Color = this.onError,
        success: Color = this.success,
        warning: Color = this.warning,
        info: Color = primary,
        danger: Color = error,
        textPrimary: Color = onSurface,
        textSecondary: Color = onSurface.copy(alpha = 0.64f),
        textTertiary: Color = onSurface.copy(alpha = 0.45f),
        textDisabled: Color = onSurface.copy(alpha = 0.38f),
        inverseSurface: Color = onSurface,
        inverseOnSurface: Color = surface,
        pageBackground: Color = surface,
        appBackground: Color = surface,
        surfaceElevated: Color = surface,
        surfaceOverlay: Color = surface.copy(alpha = 0.95f),
        divider: Color = border.copy(alpha = 0.72f),
        borderHover: Color = primary.copy(alpha = 0.3f),
        borderFocus: Color = primary.copy(alpha = 0.6f),
        borderDisabled: Color = border.copy(alpha = 0.5f),
        bgDisabled: Color = surface.copy(alpha = 0.05f),
        bgHover: Color = primary.copy(alpha = 0.08f),
        bgPressed: Color = primary.copy(alpha = 0.12f),
        bgSelected: Color = primary.copy(alpha = 0.12f),
        overlay: Color = Color.Black.copy(alpha = 0.45f),
        shadow: Color = Color.Black.copy(alpha = 0.16f),
        shadowFocus: Color = primary.copy(alpha = 0.2f),
        shadowError: Color = error.copy(alpha = 0.2f),
    ): PaletteColors = PaletteColors(
        primary = primary,
        onPrimary = onPrimary,
        border = border,
        surface = surface,
        onSurface = onSurface,
        hint = hint,
        error = error,
        onError = onError,
        success = success,
        warning = warning,
        info = info,
        danger = danger,
        textPrimary = textPrimary,
        textSecondary = textSecondary,
        textTertiary = textTertiary,
        textDisabled = textDisabled,
        inverseSurface = inverseSurface,
        inverseOnSurface = inverseOnSurface,
        pageBackground = pageBackground,
        appBackground = appBackground,
        surfaceElevated = surfaceElevated,
        surfaceOverlay = surfaceOverlay,
        divider = divider,
        borderHover = borderHover,
        borderFocus = borderFocus,
        borderDisabled = borderDisabled,
        bgDisabled = bgDisabled,
        bgHover = bgHover,
        bgPressed = bgPressed,
        bgSelected = bgSelected,
        overlay = overlay,
        shadow = shadow,
        shadowFocus = shadowFocus,
        shadowError = shadowError,
    )

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
            onError == other.onError &&
            success == other.success &&
            warning == other.warning &&
            info == other.info &&
            danger == other.danger &&
            textPrimary == other.textPrimary &&
            textSecondary == other.textSecondary &&
            textTertiary == other.textTertiary &&
            textDisabled == other.textDisabled &&
            inverseSurface == other.inverseSurface &&
            inverseOnSurface == other.inverseOnSurface &&
            pageBackground == other.pageBackground &&
            appBackground == other.appBackground &&
            surfaceElevated == other.surfaceElevated &&
            surfaceOverlay == other.surfaceOverlay &&
            divider == other.divider &&
            borderHover == other.borderHover &&
            borderFocus == other.borderFocus &&
            borderDisabled == other.borderDisabled &&
            bgDisabled == other.bgDisabled &&
            bgHover == other.bgHover &&
            bgPressed == other.bgPressed &&
            bgSelected == other.bgSelected &&
            overlay == other.overlay &&
            shadow == other.shadow &&
            shadowFocus == other.shadowFocus &&
            shadowError == other.shadowError
    }

    override fun hashCode(): Int {
        var result = primary.hashCode()
        result = 31 * result + onPrimary.hashCode()
        result = 31 * result + border.hashCode()
        result = 31 * result + surface.hashCode()
        result = 31 * result + onSurface.hashCode()
        result = 31 * result + hint.hashCode()
        result = 31 * result + error.hashCode()
        result = 31 * result + onError.hashCode()
        result = 31 * result + success.hashCode()
        result = 31 * result + warning.hashCode()
        result = 31 * result + info.hashCode()
        result = 31 * result + danger.hashCode()
        result = 31 * result + textPrimary.hashCode()
        result = 31 * result + textSecondary.hashCode()
        result = 31 * result + textTertiary.hashCode()
        result = 31 * result + textDisabled.hashCode()
        result = 31 * result + inverseSurface.hashCode()
        result = 31 * result + inverseOnSurface.hashCode()
        result = 31 * result + pageBackground.hashCode()
        result = 31 * result + appBackground.hashCode()
        result = 31 * result + surfaceElevated.hashCode()
        result = 31 * result + surfaceOverlay.hashCode()
        result = 31 * result + divider.hashCode()
        result = 31 * result + borderHover.hashCode()
        result = 31 * result + borderFocus.hashCode()
        result = 31 * result + borderDisabled.hashCode()
        result = 31 * result + bgDisabled.hashCode()
        result = 31 * result + bgHover.hashCode()
        result = 31 * result + bgPressed.hashCode()
        result = 31 * result + bgSelected.hashCode()
        result = 31 * result + overlay.hashCode()
        result = 31 * result + shadow.hashCode()
        result = 31 * result + shadowFocus.hashCode()
        result = 31 * result + shadowError.hashCode()
        return result
    }
}
