package xyz.junerver.compose.palette.core.tokens

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FormTokensTest {
    @Test
    fun formTokens_shouldKeepSizesIncreasingInExpectedOrder() {
        assertTrue(FormTokens.HeightSmall < FormTokens.HeightMedium)
        assertTrue(FormTokens.HeightMedium <= FormTokens.HeightLarge)
        assertTrue(FormTokens.CornerRadiusSmall < FormTokens.CornerRadiusMedium)
        assertTrue(FormTokens.CornerRadiusMedium < FormTokens.CornerRadiusLarge)
    }

    @Test
    fun formTokens_shouldExposeExpectedDurations() {
        assertEquals(150, FormTokens.DurationFast)
        assertEquals(250, FormTokens.DurationNormal)
        assertEquals(350, FormTokens.DurationSlow)
    }
}
