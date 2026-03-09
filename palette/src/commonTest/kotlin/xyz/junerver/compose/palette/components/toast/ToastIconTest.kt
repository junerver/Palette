package xyz.junerver.compose.palette.components.toast

import kotlin.test.Test
import kotlin.test.assertEquals

class ToastIconTest {
    @Test
    fun toastIcon_shouldExposeSupportedVisualModes() {
        assertEquals(
            listOf("SUCCESS", "FAIL", "LOADING", "NONE"),
            ToastIcon.entries.map { it.name },
        )
    }
}
