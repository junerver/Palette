package xyz.junerver.compose.palette.components.form

import kotlin.test.Test
import kotlin.test.assertEquals

class FormTypesTest {
    @Test
    fun formLayout_shouldExposeSupportedArrangementModes() {
        assertEquals(
            listOf("Horizontal", "Vertical", "Inline"),
            FormLayout.entries.map { it.name },
        )
    }

    @Test
    fun formLabelPosition_shouldExposeSupportedLabelPlacements() {
        assertEquals(
            listOf("Left", "Top"),
            FormLabelPosition.entries.map { it.name },
        )
    }
}
