package xyz.junerver.compose.palette.components.upload

enum class UploadFileStatus {
    Ready,
    RejectedType,
    RejectedSize,
}

fun matchesAcceptedType(
    mimeType: String,
    acceptedTypes: List<String>,
): Boolean {
    if (acceptedTypes.isEmpty()) return true
    return acceptedTypes.any { accepted ->
        when {
            accepted.endsWith("/*") -> {
                val prefix = accepted.removeSuffix("*")
                mimeType.startsWith(prefix)
            }

            else -> mimeType == accepted
        }
    }
}

fun exceedsMaxSize(
    sizeBytes: Long,
    maxSizeBytes: Long?,
): Boolean = maxSizeBytes != null && sizeBytes > maxSizeBytes

fun pickFileStatus(
    mimeType: String,
    sizeBytes: Long,
    acceptedTypes: List<String>,
    maxSizeBytes: Long?,
): UploadFileStatus {
    if (!matchesAcceptedType(mimeType, acceptedTypes)) return UploadFileStatus.RejectedType
    if (exceedsMaxSize(sizeBytes, maxSizeBytes)) return UploadFileStatus.RejectedSize
    return UploadFileStatus.Ready
}
