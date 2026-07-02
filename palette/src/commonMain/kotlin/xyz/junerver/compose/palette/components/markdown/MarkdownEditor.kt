package xyz.junerver.compose.palette.components.markdown

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import xyz.junerver.compose.palette.components.segmented.PSegmented
import xyz.junerver.compose.palette.components.segmented.SegmentedOption
import xyz.junerver.compose.palette.components.textfield.TextArea
import xyz.junerver.compose.palette.core.spec.ComponentSize
import xyz.junerver.compose.palette.core.theme.LocalPaletteStrings

@Composable
fun PMarkdownEditor(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    showPreview: Boolean = true,
    enabled: Boolean = true,
    showFormatToolbar: Boolean = true,
    modeSwitch: (@Composable MarkdownEditorScope.() -> Unit)? = null,
    toolbar: (@Composable MarkdownEditorScope.() -> Unit)? = null,
    preview: (@Composable MarkdownEditorScope.() -> Unit)? = null,
) {
    PMarkdownEditorImpl(
        value = value,
        onValueChange = onValueChange,
        tfValue = null,
        onTfValueChange = null,
        modifier = modifier,
        placeholder = placeholder,
        showPreview = showPreview,
        enabled = enabled,
        showFormatToolbar = showFormatToolbar,
        modeSwitch = modeSwitch,
        toolbar = toolbar,
        preview = preview,
    )
}

@Composable
fun PMarkdownEditorValue(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    showPreview: Boolean = true,
    enabled: Boolean = true,
    showFormatToolbar: Boolean = true,
    modeSwitch: (@Composable MarkdownEditorScope.() -> Unit)? = null,
    toolbar: (@Composable MarkdownEditorScope.() -> Unit)? = null,
    preview: (@Composable MarkdownEditorScope.() -> Unit)? = null,
) {
    PMarkdownEditorImpl(
        value = value.text,
        onValueChange = null,
        tfValue = value,
        onTfValueChange = onValueChange,
        modifier = modifier,
        placeholder = placeholder,
        showPreview = showPreview,
        enabled = enabled,
        showFormatToolbar = showFormatToolbar,
        modeSwitch = modeSwitch,
        toolbar = toolbar,
        preview = preview,
    )
}

@Composable
private fun PMarkdownEditorImpl(
    value: String,
    onValueChange: ((String) -> Unit)?,
    tfValue: TextFieldValue?,
    onTfValueChange: ((TextFieldValue) -> Unit)?,
    modifier: Modifier,
    placeholder: String,
    showPreview: Boolean,
    enabled: Boolean,
    showFormatToolbar: Boolean,
    modeSwitch: (@Composable MarkdownEditorScope.() -> Unit)?,
    toolbar: (@Composable MarkdownEditorScope.() -> Unit)?,
    preview: (@Composable MarkdownEditorScope.() -> Unit)?,
) {
    val defaultMode = if (showPreview) MarkdownEditorMode.Split else MarkdownEditorMode.Edit
    val initialValue = tfValue ?: TextFieldValue(value, selection = TextRange(value.length))
    val controller = useMarkdownEditorController(
        initialValue = initialValue,
        initialMode = defaultMode,
    )
    val strings = LocalPaletteStrings.current
    val scope = MarkdownEditorScope(
        controller = controller,
        editLabel = strings.markdownEditLabel,
        previewLabel = strings.markdownPreviewLabel,
        splitLabel = strings.markdownSplitLabel,
        placeholder = placeholder,
        enabled = enabled,
    )

    val externalText = tfValue?.text ?: value
    val externalSelection = tfValue?.selection
    LaunchedEffect(externalText, externalSelection) {
        val current = controller.value
        val selection = externalSelection ?: current.selection
        val normalized = TextFieldValue(
            text = externalText,
            selection = TextRange(
                start = selection.start.coerceIn(0, externalText.length),
                end = selection.end.coerceIn(0, externalText.length),
            ),
        )
        if (normalized != current) {
            controller.sync(normalized)
        }
    }

    LaunchedEffect(controller.value) {
        onTfValueChange?.invoke(controller.value)
        onValueChange?.invoke(controller.value.text)
    }

    val taskToggleHandler: (Int) -> Unit = { taskIndex ->
        val newText = toggleTaskCheckbox(controller.value.text, taskIndex)
        controller.commit(TextFieldValue(newText, TextRange(newText.length)))
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(MarkdownDefaults.blockSpacing()),
    ) {
        if (showPreview) {
            (modeSwitch ?: { DefaultModeSwitch() }).invoke(scope)
        }

        when (controller.mode) {
            MarkdownEditorMode.Edit -> {
                if (showFormatToolbar && enabled) {
                    (toolbar ?: { DefaultToolbar() }).invoke(scope)
                }
                MarkdownEditorTextArea(
                    value = controller.value,
                    onValueChange = controller::setValue,
                    placeholder = placeholder,
                    enabled = enabled,
                    modifier = Modifier.markdownEditorKeyBindings(controller),
                )
            }

            MarkdownEditorMode.Preview -> {
                (preview ?: { DefaultPreview(taskToggleHandler) }).invoke(scope)
            }

            MarkdownEditorMode.Split -> {
                if (showFormatToolbar && enabled) {
                    (toolbar ?: { DefaultToolbar() }).invoke(scope)
                }
                MarkdownEditorTextArea(
                    value = controller.value,
                    onValueChange = controller::setValue,
                    placeholder = placeholder,
                    enabled = enabled,
                    modifier = Modifier.markdownEditorKeyBindings(controller),
                )
                (preview ?: { DefaultPreview(taskToggleHandler) }).invoke(scope)
            }
        }
    }
}

@Composable
private fun MarkdownEditorScope.DefaultModeSwitch() {
    PSegmented(
        options = listOf(
            SegmentedOption(value = MarkdownEditorMode.Edit.name, label = editLabel),
            SegmentedOption(value = MarkdownEditorMode.Preview.name, label = previewLabel),
            SegmentedOption(value = MarkdownEditorMode.Split.name, label = splitLabel),
        ),
        value = controller.mode.name,
        onValueChange = { controller.setMode(MarkdownEditorMode.valueOf(it)) },
        modifier = Modifier.testTag("markdown-mode-switch"),
        size = ComponentSize.Medium,
    )
}

@Composable
private fun MarkdownEditorScope.DefaultToolbar() {
    MarkdownFormatToolbar(
        onAction = controller::applyAction,
        enabled = enabled,
        onHeadingLevel = controller::setHeadingLevel,
    )
}

@Composable
private fun MarkdownEditorScope.DefaultPreview(onTaskCheckedChange: (Int) -> Unit) {
    PMarkdownViewer(
        markdown = controller.value.text,
        onTaskCheckedChange = onTaskCheckedChange,
        inlineImageContent = { image -> DefaultInlineImage(image) },
    )
}

private val TaskLineRegex = Regex("""^(\s*[-*+])\s+\[([ xX])]\s+(.*)$""")

internal fun toggleTaskCheckbox(markdown: String, taskIndex: Int): String {
    val lines = markdown.lines()
    var currentTaskIndex = 0
    val newLines = lines.map { line ->
        val match = TaskLineRegex.matchEntire(line)
        if (match != null && currentTaskIndex++ == taskIndex) {
            val marker = match.groupValues[1]
            val currentMark = match.groupValues[2]
            val text = match.groupValues[3]
            val newMark = if (currentMark.equals("x", ignoreCase = true)) " " else "x"
            "$marker [$newMark] $text"
        } else {
            line
        }
    }
    return newLines.joinToString("\n")
}

@Composable
private fun MarkdownEditorTextArea(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    placeholder: String,
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    TextArea(
        value = value,
        onValueChange = onValueChange,
        enabled = enabled,
        size = ComponentSize.Medium,
        placeholder = placeholder,
        minLines = 8,
        maxLines = 18,
        modifier = modifier.defaultMinSize(minHeight = MarkdownDefaults.editorMinHeight()),
    )
}
