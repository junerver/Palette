package xyz.junerver.compose.palette.core.tokens

import kotlin.test.Test
import kotlin.test.assertEquals

class PaletteColorsExtensionsTest {
    @Test
    fun colorExtensions_shouldDeriveExpectedSemanticAliases() {
        val colors = PaletteColors.light()

        assertEquals(colors.error, colors.errorBorder)
        assertEquals(colors.warning, colors.warningBorder)
        assertEquals(colors.success, colors.successBorder)
    }

    @Test
    fun disabledColors_shouldUseTranslucentBaseColors() {
        val colors = PaletteColors.light()

        assertEquals(colors.border.copy(alpha = 0.5f), colors.disabledBorder)
        assertEquals(colors.surface.copy(alpha = 0.05f), colors.disabledBackground)
    }
}
