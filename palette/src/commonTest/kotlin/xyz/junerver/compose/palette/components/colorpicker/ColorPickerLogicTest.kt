package xyz.junerver.compose.palette.components.colorpicker

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ColorPickerLogicTest {
    @Test
    fun hueFromPosition_clampsDraggedPositionToValidHueRange() {
        assertEquals(0f, hueFromPosition(positionX = -25f, width = 100f))
        assertEquals(180f, hueFromPosition(positionX = 50f, width = 100f))
        assertEquals(360f, hueFromPosition(positionX = 125f, width = 100f))
    }

    @Test
    fun hueFromPosition_handlesEmptyWidth() {
        assertEquals(0f, hueFromPosition(positionX = 50f, width = 0f))
    }

    @Test
    fun hsvToColor_clampsInvalidHsvInput() {
        val color = hsvToColor(
            hue = -30f,
            saturation = 2f,
            value = 1.5f,
            alpha = 2f,
        )

        assertTrue(color.red in 0f..1f)
        assertTrue(color.green in 0f..1f)
        assertTrue(color.blue in 0f..1f)
        assertTrue(color.alpha in 0f..1f)
    }
}
