package xyz.junerver.compose.palette.core.tokens

import androidx.compose.ui.text.font.FontWeight
import kotlin.test.Test
import kotlin.test.assertEquals

class PaletteTypographyTest {
    @Test
    fun defaultTypographyValues_shouldMatchExpected() {
        val typography = PaletteTypography.default()

        assertEquals(18f, typography.title.fontSize.value)
        assertEquals(26f, typography.title.lineHeight.value)
        assertEquals(FontWeight.Medium, typography.title.fontWeight)

        assertEquals(14f, typography.body.fontSize.value)
        assertEquals(20f, typography.body.lineHeight.value)

        assertEquals(12f, typography.label.fontSize.value)
        assertEquals(16f, typography.label.lineHeight.value)
        assertEquals(FontWeight.Medium, typography.label.fontWeight)
    }
}
