package xyz.junerver.compose.palette.components.markdown

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import xyz.junerver.compose.palette.markdown.MarkdownHistoryEntry
import xyz.junerver.compose.palette.markdown.MarkdownSelection

/**
 * 桥接层：在 UI 层（依赖 Compose 的 TextFieldValue/TextRange）与核心层（Compose-free 的
 * MarkdownSelection/MarkdownHistoryEntry）之间互转。核心层因此保持零 Compose 依赖。
 */
internal fun TextRange.toMarkdownSelection(): MarkdownSelection = MarkdownSelection(start, end)

internal fun MarkdownSelection.toTextRange(): TextRange = TextRange(start, end)

internal fun TextFieldValue.toCoreEntry(): MarkdownHistoryEntry =
    MarkdownHistoryEntry(text, selection.toMarkdownSelection())

internal fun MarkdownHistoryEntry.toTextFieldValue(): TextFieldValue =
    TextFieldValue(text, selection.toTextRange())
