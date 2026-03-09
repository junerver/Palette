package xyz.junerver.compose.palette.components.message

import kotlin.test.Test
import kotlin.test.assertEquals

class MessageTypeTest {
    @Test
    fun messageType_shouldExposeSupportedFeedbackKinds() {
        assertEquals(
            listOf("Info", "Success", "Warning", "Error"),
            MessageType.entries.map { it.name },
        )
    }
}
