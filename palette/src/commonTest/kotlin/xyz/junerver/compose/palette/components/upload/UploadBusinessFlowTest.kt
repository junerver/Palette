package xyz.junerver.compose.palette.components.upload

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class UploadBusinessFlowTest {
    @Test
    fun attachmentReviewFlow_shouldRejectUnsupportedTypeBeforeCheckingSize() {
        val status =
            pickFileStatus(
                mimeType = "application/zip",
                sizeBytes = 5_000_000,
                acceptedTypes = listOf("image/*", "application/pdf"),
                maxSizeBytes = 1_024,
            )

        assertEquals(UploadFileStatus.RejectedType, status)
    }

    @Test
    fun attachmentReviewFlow_shouldRejectOversizedSupportedFile() {
        val status =
            pickFileStatus(
                mimeType = "application/pdf",
                sizeBytes = 2_048,
                acceptedTypes = listOf("image/*", "application/pdf"),
                maxSizeBytes = 1_024,
            )

        assertEquals(UploadFileStatus.RejectedSize, status)
    }

    @Test
    fun attachmentReviewFlow_shouldMarkAcceptedFileReady() {
        val status =
            pickFileStatus(
                mimeType = "image/png",
                sizeBytes = 512,
                acceptedTypes = listOf("image/*", "application/pdf"),
                maxSizeBytes = 1_024,
            )

        assertEquals(UploadFileStatus.Ready, status)
    }

    @Test
    fun attachmentReviewFlow_shouldSupportWildcardAndExactTypesTogether() {
        assertTrue(matchesAcceptedType("image/jpeg", listOf("image/*", "application/pdf")))
        assertTrue(matchesAcceptedType("application/pdf", listOf("image/*", "application/pdf")))
    }
}
