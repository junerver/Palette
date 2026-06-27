package xyz.junerver.compose.palette.components.markdown

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import xyz.junerver.compose.hooks.useLatestState
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

/**
 * 字符串版编辑器（向后兼容）。内部仍以 [TextFieldValue] 管理光标，
 * 对外只暴露纯文本。
 */
@Composable
fun PMarkdownEditor(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    showPreview: Boolean = true,
    enabled: Boolean = true,
    showFormatToolbar: Boolean = true,
    mode: MarkdownEditorMode? = null,
    onModeChange: ((MarkdownEditorMode) -> Unit)? = null,
    editLabel: String = "Edit",
    previewLabel: String = "Preview",
    splitLabel: String = "Split",
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
        mode = mode,
        onModeChange = onModeChange,
        editLabel = editLabel,
        previewLabel = previewLabel,
        splitLabel = splitLabel,
    )
}

/**
 * [TextFieldValue] 版编辑器：暴露完整光标 / 选区，
 * 供需要精确光标控制（如自动补全、外部工具栏）的调用方使用。
 */
@Composable
fun PMarkdownEditor(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    showPreview: Boolean = true,
    enabled: Boolean = true,
    showFormatToolbar: Boolean = true,
    mode: MarkdownEditorMode? = null,
    onModeChange: ((MarkdownEditorMode) -> Unit)? = null,
    editLabel: String = "Edit",
    previewLabel: String = "Preview",
    splitLabel: String = "Split",
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
        mode = mode,
        onModeChange = onModeChange,
        editLabel = editLabel,
        previewLabel = previewLabel,
        splitLabel = splitLabel,
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
    mode: MarkdownEditorMode?,
    onModeChange: ((MarkdownEditorMode) -> Unit)?,
    editLabel: String,
    previewLabel: String,
    splitLabel: String,
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

    // 以 TextFieldValue 作为单一编辑状态来源，保证工具栏 / 键盘交互能读写光标。
    val initial = tfValue ?: TextFieldValue(value, selection = TextRange(value.length))
    val (editorValue, setEditorValue) = useState(initial)

    // 外部受控值变化时同步文本（保留光标），避免工具栏回写造成光标跳动。
    val externalText = tfValue?.text ?: value
    val externalSelection = tfValue?.selection
    LaunchedEffect(externalText, externalSelection) {
        val current = editorValue
        val textChanged = externalText != current.text
        val selChanged = externalSelection != null && externalSelection != current.selection
        if (textChanged || selChanged) {
            val cursor = (externalSelection ?: current.selection)
                .let { TextRange(it.start.coerceIn(0, externalText.length), it.end.coerceIn(0, externalText.length)) }
            setEditorValue(TextFieldValue(externalText, cursor, composition = null))
        }
    }

    val onEdit: (TextFieldValue) -> Unit = { newTf ->
        setEditorValue(newTf)
        onTfValueChange?.invoke(newTf)
        onValueChange?.invoke(newTf.text)
    }

    // 用 latest 持有最新编辑值，便于在键盘事件等稳定回调里读取。
    val latestEditorValue = useLatestState(editorValue)
    val latestOnEdit = useLatestState(onEdit)

    fun applyEdit(result: MarkdownEditResult) {
        latestOnEdit.value()(TextFieldValue(result.text, result.selection))
    }

    val toolbarOnAction: (MarkdownToolbarAction) -> Unit = { action ->
        val tf = latestEditorValue.value()
        val result = when (action) {
            MarkdownToolbarAction.Bold -> wrapSelection(tf.text, tf.selection, "**")
            MarkdownToolbarAction.Italic -> wrapSelection(tf.text, tf.selection, "*")
            MarkdownToolbarAction.Strikethrough -> wrapSelection(tf.text, tf.selection, "~~")
            MarkdownToolbarAction.InlineCode -> wrapSelection(tf.text, tf.selection, "`")
            MarkdownToolbarAction.Heading -> setHeadingLevel(tf.text, tf.selection, 1)
            MarkdownToolbarAction.UnorderedList -> toggleLinePrefix(tf.text, tf.selection, "- ")
            MarkdownToolbarAction.OrderedList -> toggleLinePrefix(tf.text, tf.selection, "1. ", ordered = true)
            MarkdownToolbarAction.TaskList -> toggleTaskItem(tf.text, tf.selection)
            MarkdownToolbarAction.Quote -> toggleLinePrefix(tf.text, tf.selection, "> ")
            MarkdownToolbarAction.Link -> insertText(tf.text, tf.selection, "[text](url)", selectInside = 7..9)
            MarkdownToolbarAction.Image -> insertText(tf.text, tf.selection, "![alt](url)", selectInside = 8..10)
            MarkdownToolbarAction.CodeBlock -> insertText(tf.text, tf.selection, defaultCodeFence())
            MarkdownToolbarAction.Table -> insertText(tf.text, tf.selection, defaultTableSnippet)
            MarkdownToolbarAction.HorizontalRule -> insertText(tf.text, tf.selection, "---\n")
        }
        applyEdit(result)
    }
    val toolbarOnHeading: (MarkdownHeadingLevel) -> Unit = { level ->
        val tf = latestEditorValue.value()
        applyEdit(setHeadingLevel(tf.text, tf.selection, level.level))
    }

    // 键盘拦截：Tab / Shift+Tab 缩进；Enter 列表/引用续行。
    val keyModifier = Modifier.onPreviewKeyEvent { event ->
        if (event.type != KeyEventType.KeyDown) return@onPreviewKeyEvent false
        val tf = latestEditorValue.value()
        when (event.key) {
            Key.Tab -> {
                applyEdit(indent(tf.text, tf.selection, forward = !event.isShiftPressed))
                true
            }
            Key.Enter -> {
                val continued = continueOnEnter(tf.text, tf.selection)
                if (continued != null) {
                    applyEdit(continued)
                    true
                } else {
                    false
                }
            }
            else -> false
        }
    }

    val taskToggleHandler: (Int) -> Unit = { taskIndex ->
        // 预览态下勾选复选框：整体替换并重置光标到末尾。
        val newText = toggleTaskCheckbox(externalText, taskIndex)
        onEdit(TextFieldValue(newText, TextRange(newText.length)))
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
            MarkdownEditorMode.Edit -> {
                if (showFormatToolbar && enabled) {
                    MarkdownFormatToolbar(
                        onAction = toolbarOnAction,
                        onHeadingLevel = toolbarOnHeading,
                        enabled = enabled,
                    )
                }
                MarkdownEditorTextArea(
                    value = editorValue,
                    onValueChange = onEdit,
                    placeholder = placeholder,
                    enabled = enabled,
                    modifier = keyModifier,
                )
            }

            MarkdownEditorMode.Preview ->
                PMarkdownViewer(
                    markdown = externalText,
                    onTaskCheckedChange = taskToggleHandler,
                )

            MarkdownEditorMode.Split -> {
                if (showFormatToolbar && enabled) {
                    MarkdownFormatToolbar(
                        onAction = toolbarOnAction,
                        onHeadingLevel = toolbarOnHeading,
                        enabled = enabled,
                    )
                }
                MarkdownEditorTextArea(
                    value = editorValue,
                    onValueChange = onEdit,
                    placeholder = placeholder,
                    enabled = enabled,
                    modifier = keyModifier,
                )
                PMarkdownViewer(
                    markdown = externalText,
                    onTaskCheckedChange = taskToggleHandler,
                )
            }
        }
    }
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
            val result = "$marker [$newMark] $text"
            result
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
