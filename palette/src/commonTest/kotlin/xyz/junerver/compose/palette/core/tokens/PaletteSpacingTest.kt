package xyz.junerver.compose.palette.core.tokens

import kotlin.test.Test
import kotlin.test.assertEquals

class PaletteSpacingTest {
    @Test
    fun defaultSpacingValues_shouldMatchExpected() {
        val spacing = PaletteSpacing.default()

        assertEquals(0f, spacing.none.value)
        assertEquals(4f, spacing.extraSmall.value)
        assertEquals(8f, spacing.small.value)
        assertEquals(16f, spacing.medium.value)
        assertEquals(24f, spacing.large.value)
        assertEquals(32f, spacing.extraLarge.value)
    }
}
