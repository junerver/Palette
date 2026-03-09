package xyz.junerver.compose.palette.core.tokens

import kotlin.test.Test
import kotlin.test.assertEquals

class PaletteSemanticColorsTest {
    @Test
    fun toSemanticColors_shouldCarryCoreMeaningfulColors() {
        val colors = PaletteColors.light()

        val semantic = colors.toSemanticColors()

        assertEquals(colors.primary, semantic.primary)
        assertEquals(colors.border, semantic.border)
        assertEquals(colors.surface, semantic.surface)
        assertEquals(colors.success, semantic.success)
        assertEquals(colors.warning, semantic.warning)
    }

    @Test
    fun toMaterialScheme_shouldMapPrimaryAndSurfaceConsistently() {
        val semantic = PaletteColors.dark().toSemanticColors()

        val scheme = semantic.toMaterialScheme()

        assertEquals(semantic.primary, scheme.primary)
        assertEquals(semantic.onPrimary, scheme.onPrimary)
        assertEquals(semantic.surface, scheme.surface)
        assertEquals(semantic.onSurface, scheme.onSurface)
        assertEquals(semantic.error, scheme.error)
    }
}
