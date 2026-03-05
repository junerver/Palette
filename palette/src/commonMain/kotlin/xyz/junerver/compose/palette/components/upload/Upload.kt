package xyz.junerver.compose.palette.components.upload

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.core.theme.PaletteTheme

data class UploadFile(
    val name: String,
    val mimeType: String,
    val sizeBytes: Long,
)

@Composable
fun PUpload(
    files: List<UploadFile>,
    onFilesChange: (List<UploadFile>) -> Unit,
    modifier: Modifier = Modifier,
    acceptedTypes: List<String> = emptyList(),
    maxSizeBytes: Long? = null,
    disabled: Boolean = false,
    triggerText: String = PaletteTheme.strings.uploadTriggerText,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = UploadDefaults.BorderWidth,
                color = UploadDefaults.borderColor(),
                shape = RoundedCornerShape(UploadDefaults.BorderRadius)
            )
            .background(androidx.compose.ui.graphics.Color.Transparent)
            .padding(UploadDefaults.ContentPadding),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = triggerText,
            color = UploadDefaults.contentColor(),
            modifier = Modifier.clickable(enabled = !disabled) {
                onFilesChange(files)
            }
        )

        files.forEach { file ->
            val status = pickFileStatus(
                mimeType = file.mimeType,
                sizeBytes = file.sizeBytes,
                acceptedTypes = acceptedTypes,
                maxSizeBytes = maxSizeBytes
            )
            val color = when (status) {
                UploadFileStatus.Ready -> UploadDefaults.successColor()
                UploadFileStatus.RejectedType,
                UploadFileStatus.RejectedSize -> UploadDefaults.errorColor()
            }
            val suffix = when (status) {
                UploadFileStatus.Ready -> PaletteTheme.strings.uploadReadySuffix
                UploadFileStatus.RejectedType -> PaletteTheme.strings.uploadRejectedTypeSuffix
                UploadFileStatus.RejectedSize -> PaletteTheme.strings.uploadRejectedSizeSuffix
            }
            Text(
                text = "${file.name} ($suffix)",
                color = color
            )
        }
    }
}
