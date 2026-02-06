package xyz.junerver.compose.palette.core.tokens

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PaletteShapesTest {
    @Test
    fun defaultShapes_shouldUseRoundedCorners() {
        val shapes = PaletteShapes.default()

        assertTrue(shapes.small is RoundedCornerShape)
        assertTrue(shapes.medium is RoundedCornerShape)
        assertTrue(shapes.large is RoundedCornerShape)
        assertEquals(RoundedCornerShape(4.dp), shapes.small)
        assertEquals(RoundedCornerShape(8.dp), shapes.medium)
        assertEquals(RoundedCornerShape(12.dp), shapes.large)
    }
}
