package xyz.junerver.compose.palette.components.markdown

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import xyz.junerver.compose.hooks.useState
import xyz.junerver.compose.palette.components.textfield.TextArea
import xyz.junerver.compose.palette.components.toggle.PToggleGroup
import xyz.junerver.compose.palette.components.toggle.ToggleVariant
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

    val latestValue by rememberUpdatedState(value)
    val taskToggleHandler = remember<(Int, Boolean) -> Unit> {
        { taskIndex: Int, checked: Boolean ->
            onValueChange(toggleTaskCheckbox(latestValue, taskIndex, checked))
        }
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(MarkdownDefaults.blockSpacing()),
    ) {
        if (showPreview) {
            PToggleGroup(
                value = listOf(currentMode.name),
                onValueChange = { selected ->
                    val next = selected.firstOrNull() ?: return@PToggleGroup
                    val nextMode = MarkdownEditorMode.valueOf(next)
                    if (mode == null) setInternalMode(nextMode)
                    onModeChange?.invoke(nextMode)
                },
                variant = ToggleVariant.Surface,
                size = ComponentSize.Small,
            ) {
                PToggleItem(value = MarkdownEditorMode.Edit.name, label = editLabel)
                PToggleItem(value = MarkdownEditorMode.Preview.name, label = previewLabel)
                PToggleItem(value = MarkdownEditorMode.Split.name, label = splitLabel)
            }
        }

        when (currentMode) {
            MarkdownEditorMode.Edit ->
                MarkdownEditorTextArea(
                    value = value,
                    onValueChange = onValueChange,
                    placeholder = placeholder,
                    enabled = enabled,
                )

            MarkdownEditorMode.Preview ->
                PMarkdownViewer(
                    markdown = value,
                    onTaskCheckedChange = taskToggleHandler,
                )

            MarkdownEditorMode.Split -> {
                MarkdownEditorTextArea(
                    value = value,
                    onValueChange = onValueChange,
                    placeholder = placeholder,
                    enabled = enabled,
                )
                PMarkdownViewer(
                    markdown = value,
                    onTaskCheckedChange = taskToggleHandler,
                )
            }
        }
    }
}

private val TaskLineRegex = Regex("""^(\s*[-*+])\s+\[([ xX])]\s+(.*)$""")

internal fun toggleTaskCheckbox(markdown: String, taskIndex: Int, checked: Boolean): String {
    val lines = markdown.lines()
    var currentTaskIndex = 0
    val newLines = lines.map { line ->
        val match = TaskLineRegex.matchEntire(line)
        if (match != null && currentTaskIndex++ == taskIndex) {
            val marker = match.groupValues[1]
            val text = match.groupValues[3]
            val mark = if (checked) "x" else " "
            "$marker [$mark] $text"
        } else {
            line
        }
    }
    return newLines.joinToString("\n")
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
