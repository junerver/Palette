package xyz.junerver.compose.palette.components.markdown

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.isMetaPressed
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.input.TextFieldValue
import xyz.junerver.compose.hooks.useLatestState
import xyz.junerver.compose.hooks.useState
import xyz.junerver.compose.palette.markdown.MarkdownHistory
import xyz.junerver.compose.palette.markdown.continueOnEnter
import xyz.junerver.compose.palette.markdown.defaultCodeFence
import xyz.junerver.compose.palette.markdown.defaultTableSnippet
import xyz.junerver.compose.palette.markdown.indent
import xyz.junerver.compose.palette.markdown.insertText
import xyz.junerver.compose.palette.markdown.setHeadingLevel
import xyz.junerver.compose.palette.markdown.toggleLinePrefix
import xyz.junerver.compose.palette.markdown.toggleTaskItem
import xyz.junerver.compose.palette.markdown.wrapSelection

enum class MarkdownEditorMode {
    Edit,
    Preview,
    Split,
}

@Stable
class MarkdownEditorController internal constructor(
    val value: TextFieldValue,
    val mode: MarkdownEditorMode,
    val canUndo: Boolean,
    val canRedo: Boolean,
    private val onSetValue: (TextFieldValue) -> Unit,
    private val onSetText: (String) -> Unit,
    private val onSetMode: (MarkdownEditorMode) -> Unit,
    private val onSync: (TextFieldValue) -> Unit,
    private val onCommitValue: (TextFieldValue) -> Unit,
    private val onUndo: () -> Unit,
    private val onRedo: () -> Unit,
    private val onWrapSelection: (String, String) -> Unit,
    private val onToggleLinePrefix: (String, Boolean) -> Unit,
    private val onToggleTaskItem: () -> Unit,
    private val onSetHeadingLevel: (MarkdownHeadingLevel) -> Unit,
    private val onInsertText: (String, IntRange?) -> Unit,
    private val onIndent: (Boolean) -> Unit,
    private val onApplyAction: (MarkdownToolbarAction) -> Unit,
    private val onContinueOnEnter: () -> Boolean,
) {
    fun setValue(value: TextFieldValue) {
        onSetValue(value)
    }

    fun setText(text: String) {
        onSetText(text)
    }

    fun setMode(mode: MarkdownEditorMode) {
        onSetMode(mode)
    }

    fun sync(value: TextFieldValue) {
        onSync(value)
    }

    fun commit(value: TextFieldValue) {
        onCommitValue(value)
    }

    fun undo() {
        onUndo()
    }

    fun redo() {
        onRedo()
    }

    fun wrapSelection(prefix: String, suffix: String = prefix) {
        onWrapSelection(prefix, suffix)
    }

    fun toggleLinePrefix(prefix: String, ordered: Boolean = false) {
        onToggleLinePrefix(prefix, ordered)
    }

    fun toggleTaskItem() {
        onToggleTaskItem()
    }

    fun setHeadingLevel(level: MarkdownHeadingLevel) {
        onSetHeadingLevel(level)
    }

    fun insertText(snippet: String, selectInside: IntRange? = null) {
        onInsertText(snippet, selectInside)
    }

    fun indent(forward: Boolean = true) {
        onIndent(forward)
    }

    fun applyAction(action: MarkdownToolbarAction) {
        onApplyAction(action)
    }

    fun continueOnEnter(): Boolean {
        return onContinueOnEnter()
    }
}

@Stable
class MarkdownEditorScope(
    val controller: MarkdownEditorController,
    val editLabel: String,
    val previewLabel: String,
    val splitLabel: String,
    val placeholder: String,
    val enabled: Boolean,
)

@Composable
fun useMarkdownEditorController(
    initialValue: TextFieldValue = TextFieldValue(""),
    initialMode: MarkdownEditorMode = MarkdownEditorMode.Split,
    historyLimit: Int = MarkdownHistory.DEFAULT_CAPACITY,
): MarkdownEditorController {
    // 撤销栈必须跨重组保持同一实例；这里用 remember 明确保证对象身份稳定。
    val history = remember(historyLimit) {
        MarkdownHistory(initialValue.toCoreEntry(), capacity = historyLimit)
    }
    val (value, setValueState) = useState(initialValue)
    val (mode, setModeState) = useState(initialMode)
    val latestValue = useLatestState(value)

    fun syncFromHistory() {
        setValueState(history.current.toTextFieldValue())
    }

    fun commit(result: xyz.junerver.compose.palette.markdown.MarkdownEditResult) {
        history.commit(result.toCoreEntry())
        syncFromHistory()
    }

    return MarkdownEditorController(
        value = value,
        mode = mode,
        canUndo = history.canUndo,
        canRedo = history.canRedo,
        onSetValue = { next ->
            history.pushTyping(next.toCoreEntry())
            syncFromHistory()
        },
        onSetText = { text ->
            val next = latestValue.value.copy(text = text)
            history.sync(next.toCoreEntry())
            setValueState(next)
        },
        onSetMode = setModeState,
        onSync = { next ->
            history.sync(next.toCoreEntry())
            setValueState(next)
        },
        onCommitValue = { next ->
            history.commit(next.toCoreEntry())
            syncFromHistory()
        },
        onUndo = {
            history.undo()
            syncFromHistory()
        },
        onRedo = {
            history.redo()
            syncFromHistory()
        },
        onWrapSelection = { prefix, suffix ->
            val current = latestValue.value
            commit(wrapSelection(current.text, current.selection.toMarkdownSelection(), prefix, suffix))
        },
        onToggleLinePrefix = { prefix, ordered ->
            val current = latestValue.value
            commit(toggleLinePrefix(current.text, current.selection.toMarkdownSelection(), prefix, ordered))
        },
        onToggleTaskItem = {
            val current = latestValue.value
            commit(toggleTaskItem(current.text, current.selection.toMarkdownSelection()))
        },
        onSetHeadingLevel = { level ->
            val current = latestValue.value
            commit(setHeadingLevel(current.text, current.selection.toMarkdownSelection(), level.level))
        },
        onInsertText = { snippet, selectInside ->
            val current = latestValue.value
            commit(insertText(current.text, current.selection.toMarkdownSelection(), snippet, selectInside))
        },
        onIndent = { forward ->
            val current = latestValue.value
            commit(indent(current.text, current.selection.toMarkdownSelection(), forward))
        },
        onApplyAction = { action ->
            val current = latestValue.value
            when (action) {
                MarkdownToolbarAction.Bold -> commit(wrapSelection(current.text, current.selection.toMarkdownSelection(), "**"))
                MarkdownToolbarAction.Italic -> commit(wrapSelection(current.text, current.selection.toMarkdownSelection(), "*"))
                MarkdownToolbarAction.Strikethrough -> commit(wrapSelection(current.text, current.selection.toMarkdownSelection(), "~~"))
                MarkdownToolbarAction.InlineCode -> commit(wrapSelection(current.text, current.selection.toMarkdownSelection(), "`"))
                MarkdownToolbarAction.Heading -> commit(setHeadingLevel(current.text, current.selection.toMarkdownSelection(), 1))
                MarkdownToolbarAction.UnorderedList -> commit(toggleLinePrefix(current.text, current.selection.toMarkdownSelection(), "- "))
                MarkdownToolbarAction.OrderedList -> commit(toggleLinePrefix(current.text, current.selection.toMarkdownSelection(), "1. ", ordered = true))
                MarkdownToolbarAction.TaskList -> commit(toggleTaskItem(current.text, current.selection.toMarkdownSelection()))
                MarkdownToolbarAction.Quote -> commit(toggleLinePrefix(current.text, current.selection.toMarkdownSelection(), "> "))
                MarkdownToolbarAction.Link -> commit(insertText(current.text, current.selection.toMarkdownSelection(), "[text](url)", selectInside = 7..9))
                MarkdownToolbarAction.Image -> commit(insertText(current.text, current.selection.toMarkdownSelection(), "![alt](url)", selectInside = 8..10))
                MarkdownToolbarAction.CodeBlock -> commit(insertText(current.text, current.selection.toMarkdownSelection(), defaultCodeFence()))
                MarkdownToolbarAction.Table -> commit(insertText(current.text, current.selection.toMarkdownSelection(), defaultTableSnippet))
                MarkdownToolbarAction.HorizontalRule -> commit(insertText(current.text, current.selection.toMarkdownSelection(), "---\n"))
                MarkdownToolbarAction.InlineLatex -> commit(wrapSelection(current.text, current.selection.toMarkdownSelection(), "$"))
                MarkdownToolbarAction.Subscript -> commit(wrapSelection(current.text, current.selection.toMarkdownSelection(), "~"))
                MarkdownToolbarAction.Superscript -> commit(wrapSelection(current.text, current.selection.toMarkdownSelection(), "^"))
                MarkdownToolbarAction.Highlight -> commit(wrapSelection(current.text, current.selection.toMarkdownSelection(), "=="))
            }
        },
        onContinueOnEnter = {
            val current = latestValue.value
            val result = continueOnEnter(current.text, current.selection.toMarkdownSelection())
            if (result == null) {
                false
            } else {
                commit(result)
                true
            }
        },
    )
}

fun Modifier.markdownEditorKeyBindings(controller: MarkdownEditorController): Modifier =
    onPreviewKeyEvent { event ->
        if (event.type != KeyEventType.KeyDown) return@onPreviewKeyEvent false
        val primaryMod = event.isCtrlPressed || event.isMetaPressed
        if (primaryMod) {
            return@onPreviewKeyEvent when (event.key) {
                Key.B -> {
                    controller.applyAction(MarkdownToolbarAction.Bold)
                    true
                }
                Key.I -> {
                    controller.applyAction(MarkdownToolbarAction.Italic)
                    true
                }
                Key.K -> {
                    controller.applyAction(if (event.isShiftPressed) MarkdownToolbarAction.Strikethrough else MarkdownToolbarAction.Link)
                    true
                }
                Key.E -> {
                    controller.applyAction(if (event.isShiftPressed) MarkdownToolbarAction.CodeBlock else MarkdownToolbarAction.InlineCode)
                    true
                }
                Key.U -> {
                    controller.applyAction(MarkdownToolbarAction.UnorderedList)
                    true
                }
                Key.O -> {
                    controller.applyAction(if (event.isShiftPressed) MarkdownToolbarAction.Quote else MarkdownToolbarAction.OrderedList)
                    true
                }
                Key.Z -> {
                    if (event.isShiftPressed) controller.redo() else controller.undo()
                    true
                }
                Key.Y -> {
                    controller.redo()
                    true
                }
                else -> false
            }
        }
        when (event.key) {
            Key.Tab -> {
                controller.indent(forward = !event.isShiftPressed)
                true
            }
            Key.Enter -> controller.continueOnEnter()
            else -> false
        }
    }

private fun xyz.junerver.compose.palette.markdown.MarkdownEditResult.toCoreEntry() =
    xyz.junerver.compose.palette.markdown.MarkdownHistoryEntry(text, selection)
