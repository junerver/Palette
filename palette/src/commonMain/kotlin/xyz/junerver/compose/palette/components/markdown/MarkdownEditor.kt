package xyz.junerver.compose.palette.components.markdown

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import xyz.junerver.compose.hooks.useState
import xyz.junerver.compose.palette.components.segmented.PSegmented
import xyz.junerver.compose.palette.components.segmented.SegmentedOption
import xyz.junerver.compose.palette.components.textfield.TextArea
import xyz.junerver.compose.palette.core.spec.ComponentSize

enum class MarkdownEditorMode {
    Edit,
    Preview,
    Split,
}

@Composable
fun PMarkdownEditor(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    showPreview: Boolean = true,
    enabled: Boolean = true,
    mode: MarkdownEditorMode? = null,
    onModeChange: ((MarkdownEditorMode) -> Unit)? = null,
    editLabel: String = "Edit",
    previewLabel: String = "Preview",
    splitLabel: String = "Split",
) {
    val defaultMode = if (showPreview) MarkdownEditorMode.Split else MarkdownEditorMode.Edit
    val (internalMode, setInternalMode) = useState(mode ?: defaultMode)
    val requestedMode = mode ?: internalMode
    val currentMode = if (showPreview || mode != null) requestedMode else MarkdownEditorMode.Edit

    LaunchedEffect(mode, showPreview) {
        when {
            mode != null -> setInternalMode(mode)
            !showPreview -> setInternalMode(MarkdownEditorMode.Edit)
        }
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(MarkdownDefaults.blockSpacing()),
    ) {
        if (showPreview) {
            PSegmented(
                options =
                    listOf(
                        SegmentedOption(value = MarkdownEditorMode.Edit.name, label = editLabel),
                        SegmentedOption(value = MarkdownEditorMode.Preview.name, label = previewLabel),
                        SegmentedOption(value = MarkdownEditorMode.Split.name, label = splitLabel),
                    ),
                value = currentMode.name,
                onValueChange = { next ->
                    val nextMode = MarkdownEditorMode.valueOf(next)
                    if (mode == null) setInternalMode(nextMode)
                    onModeChange?.invoke(nextMode)
                },
                size = ComponentSize.Small,
            )
        }

        when (currentMode) {
            MarkdownEditorMode.Edit ->
                MarkdownEditorTextArea(
                    value = value,
                    onValueChange = onValueChange,
                    placeholder = placeholder,
                    enabled = enabled,
                )

            MarkdownEditorMode.Preview -> PMarkdownViewer(markdown = value)

            MarkdownEditorMode.Split -> {
                MarkdownEditorTextArea(
                    value = value,
                    onValueChange = onValueChange,
                    placeholder = placeholder,
                    enabled = enabled,
                )
                PMarkdownViewer(markdown = value)
            }
        }
    }
}

@Composable
private fun MarkdownEditorTextArea(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    enabled: Boolean,
) {
    TextArea(
        value = value,
        onValueChange = onValueChange,
        enabled = enabled,
        size = ComponentSize.Medium,
        placeholder = placeholder,
        minLines = 8,
        maxLines = 18,
        modifier = Modifier.defaultMinSize(minHeight = MarkdownDefaults.editorMinHeight()),
    )
}
