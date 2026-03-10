package xyz.junerver.compose.palette.components.button

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ButtonDefaultsTest {
    @Test
    fun buttonSizes_shouldExposeExpectedOrderingAndTokens() {
        assertTrue(ButtonSize.SMALL.borderRadius < ButtonSize.MEDIUM.borderRadius)
        assertTrue(ButtonSize.MEDIUM.fontSize <= ButtonSize.LARGE.fontSize)
        assertEquals(ButtonSize.MEDIUM.fontSize, ButtonSize.SMALL.fontSize)
    }

    @Test
    fun buttonDefaults_shouldExposeExpectedConstants() {
        assertEquals(0.7f, ButtonDefaults.DisabledAlpha)
        assertTrue(ButtonDefaults.DefaultWidth > ButtonDefaults.LoadingSpacing)
    }
}
