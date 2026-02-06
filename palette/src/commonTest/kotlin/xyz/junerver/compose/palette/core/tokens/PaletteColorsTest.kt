package xyz.junerver.compose.palette.core.tokens

import androidx.compose.ui.graphics.Color
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class PaletteColorsTest {
    @Test
    fun lightAndDarkFactories_shouldProduceDifferentSurfaces() {
        val light = PaletteColors.light()
        val dark = PaletteColors.dark()

        assertNotEquals(light.surface, dark.surface)
        assertNotEquals(light.onSurface, dark.onSurface)
        assertNotEquals(light.border, dark.border)
    }

    @Test
    fun equalsAndHashCode_shouldRespectAllProperties() {
        val one = PaletteColors.light()
        val sameAsOne = PaletteColors.light()
        val different =
            PaletteColors(
                primary = Color(0xFF123456),
                onPrimary = one.onPrimary,
                border = one.border,
                surface = one.surface,
                onSurface = one.onSurface,
                hint = one.hint,
                error = one.error,
                onError = one.onError,
                success = one.success,
                warning = one.warning,
            )

        assertEquals(one, sameAsOne)
        assertEquals(one.hashCode(), sameAsOne.hashCode())
        assertNotEquals(one, different)
    }

    @Test
    fun extensionColors_shouldApplyExpectedAlpha() {
        val colors = PaletteColors.light()

        assertEquals(colors.primary.copy(alpha = 0.6f), colors.focusBorder)
        assertEquals(colors.primary.copy(alpha = 0.3f), colors.hoverBorder)
        assertEquals(colors.border.copy(alpha = 0.5f), colors.disabledBorder)
        assertEquals(colors.surface.copy(alpha = 0.05f), colors.disabledBackground)
        assertEquals(colors.primary.copy(alpha = 0.2f), colors.focusShadow)
        assertEquals(colors.error.copy(alpha = 0.2f), colors.errorShadow)
        assertEquals(colors.error, colors.errorBorder)
        assertEquals(colors.warning, colors.warningBorder)
        assertEquals(colors.success, colors.successBorder)
    }
}
