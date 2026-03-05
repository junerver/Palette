package xyz.junerver.compose.palette.components.upload

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class UploadLogicTest {
    @Test
    fun matchesAcceptedType_whenWildcard_shouldMatchPrefix() {
        assertTrue(matchesAcceptedType("image/png", listOf("image/*")))
        assertFalse(matchesAcceptedType("application/pdf", listOf("image/*")))
    }

    @Test
    fun matchesAcceptedType_whenExact_shouldMatchWholeType() {
        assertTrue(matchesAcceptedType("application/pdf", listOf("application/pdf")))
        assertFalse(matchesAcceptedType("image/png", listOf("application/pdf")))
    }

    @Test
    fun exceedsMaxSize_shouldValidateBytes() {
        assertTrue(exceedsMaxSize(sizeBytes = 1025, maxSizeBytes = 1024))
        assertFalse(exceedsMaxSize(sizeBytes = 1024, maxSizeBytes = 1024))
        assertFalse(exceedsMaxSize(sizeBytes = 10, maxSizeBytes = null))
    }

    @Test
    fun pickFileStatus_shouldReturnExpectedStatus() {
        assertEquals(
            UploadFileStatus.RejectedType,
            pickFileStatus(
                mimeType = "text/plain",
                sizeBytes = 100,
                acceptedTypes = listOf("image/*"),
                maxSizeBytes = 1024
            )
        )
        assertEquals(
            UploadFileStatus.RejectedSize,
            pickFileStatus(
                mimeType = "image/png",
                sizeBytes = 2048,
                acceptedTypes = listOf("image/*"),
                maxSizeBytes = 1024
            )
        )
        assertEquals(
            UploadFileStatus.Ready,
            pickFileStatus(
                mimeType = "image/png",
                sizeBytes = 512,
                acceptedTypes = listOf("image/*"),
                maxSizeBytes = 1024
            )
        )
    }
}
