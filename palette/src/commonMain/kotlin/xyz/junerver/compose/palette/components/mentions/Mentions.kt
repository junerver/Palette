package xyz.junerver.compose.palette.components.mentions

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.ui.window.PopupProperties
import xyz.junerver.compose.hooks.useState
import xyz.junerver.compose.palette.components.text.PText
import xyz.junerver.compose.palette.components.textfield.TextArea
import xyz.junerver.compose.palette.core.theme.PaletteTheme

@Composable
fun PMentions(
    value: String,
    onValueChange: (String) -> Unit,
    options: List<MentionsOption>,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    disabled: Boolean = false,
    prefix: String = "@",
    onSelect: ((MentionsOption) -> Unit)? = null,
    onSearch: ((String) -> Unit)? = null,
    loading: Boolean = false,
    highlight: Boolean = false,
    highlightColor: Color = MentionsDefaults.highlightColor(),
) {
    val (editorValue, setEditorValue) = useState(TextFieldValue(value, selection = TextRange(value.length)))
    val (mentionQuery, setMentionQuery) = useState("")
    val (mentionTriggerIndex, setMentionTriggerIndex) = useState(-1)
    val (showDropdown, setShowDropdown) = useState(false)
    val (anchorWidth, setAnchorWidth) = useState(0)

    val density = LocalDensity.current
    val dropdownWidth = with(density) { anchorWidth.toDp() }

    LaunchedEffect(value) {
        if (value != editorValue.text) {
            val cursor = editorValue.selection.end.coerceIn(0, value.length)
            setEditorValue(
                editorValue.copy(
                    text = value,
                    selection = TextRange(cursor),
                    composition = null,
                )
            )
        }
    }

    val filteredOptions = remember(options, mentionQuery, prefix) {
        if (mentionQuery.length < prefix.length) {
            options
        } else {
            val query = mentionQuery.removePrefix(prefix)
            options.filter {
                it.label.contains(query, ignoreCase = true) ||
                    it.value.contains(query, ignoreCase = true)
            }
        }
    }

    fun clearMentionTrigger() {
        setShowDropdown(false)
        setMentionTriggerIndex(-1)
        setMentionQuery("")
    }

    fun detectMentionTrigger(text: String, cursorPosition: Int) {
        if (prefix.isEmpty()) {
            clearMentionTrigger()
            return
        }
        val cursor = cursorPosition.coerceIn(0, text.length)
        val beforeCursor = text.substring(0, cursor)
        val triggerIndex = beforeCursor.lastIndexOf(prefix)
        if (triggerIndex == -1) {
            clearMentionTrigger()
            return
        }
        val queryWithPrefix = beforeCursor.substring(triggerIndex)
        val query = queryWithPrefix.removePrefix(prefix)
        if (query.any { it.isWhitespace() }) {
            clearMentionTrigger()
            return
        }
        setMentionTriggerIndex(triggerIndex)
        setMentionQuery(queryWithPrefix)
        setShowDropdown(true)
        onSearch?.invoke(query)
    }

    fun commitEditorValue(newValue: TextFieldValue) {
        val normalizedValue = replaceEditedMentionWithWholeDeletion(
            oldValue = editorValue,
            requestedValue = newValue,
            prefix = prefix,
        ) ?: newValue
        setEditorValue(normalizedValue)
        onValueChange(normalizedValue.text)
        if (!disabled) {
            detectMentionTrigger(normalizedValue.text, normalizedValue.selection.end)
        }
    }

    val visualTransformation = remember(prefix, highlight, highlightColor) {
        MentionsHighlightTransformation(
            prefix = prefix,
            enabled = highlight,
            highlightColor = highlightColor,
        )
    }

    val shouldShowDropdown = showDropdown && !disabled && (loading || filteredOptions.isNotEmpty())

    Box(
        modifier = modifier.onSizeChanged { setAnchorWidth(it.width) }
    ) {
        TextArea(
            value = editorValue,
            onValueChange = { newValue -> commitEditorValue(newValue) },
            modifier = Modifier.fillMaxWidth(),
            enabled = !disabled,
            placeholder = placeholder,
            minLines = MentionsDefaults.MinLines,
            visualTransformation = visualTransformation,
        )

        if (shouldShowDropdown) {
            Popup(
                onDismissRequest = { setShowDropdown(false) },
                popupPositionProvider = object : PopupPositionProvider {
                    override fun calculatePosition(
                        anchorBounds: IntRect,
                        windowSize: IntSize,
                        layoutDirection: LayoutDirection,
                        popupContentSize: IntSize,
                    ): IntOffset {
                        val x = anchorBounds.left
                        val spaceBelow = windowSize.height - anchorBounds.bottom
                        val spaceAbove = anchorBounds.top
                        val y =
                            if (spaceBelow >= popupContentSize.height || spaceBelow >= spaceAbove) {
                                anchorBounds.bottom
                            } else {
                                anchorBounds.top - popupContentSize.height
                            }
                        return IntOffset(x, y)
                    }
                },
                properties = PopupProperties(focusable = true)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .width(dropdownWidth)
                        .heightIn(max = MentionsDefaults.DropdownMaxHeight)
                        .clip(RoundedCornerShape(MentionsDefaults.CornerRadius))
                        .background(PaletteTheme.colors.surface)
                ) {
                    if (loading) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        horizontal = MentionsDefaults.OptionPaddingHorizontal,
                                        vertical = MentionsDefaults.OptionHeight / 4,
                                    )
                            ) {
                                PText(
                                    text = "Loading...",
                                    fontSize = MentionsDefaults.FontSize,
                                    color = PaletteTheme.colors.hint,
                                    style = PaletteTheme.typography.body,
                                )
                            }
                        }
                    }
                    items(
                        items = filteredOptions,
                        key = { it.value }
                    ) { option ->
                        MentionsOptionItem(
                            option = option,
                            onClick = {
                                if (!option.disabled) {
                                    onSelect?.invoke(option)
                                    val newEditorValue = replaceMentionQueryWithOption(
                                        editorValue = editorValue,
                                        mentionTriggerIndex = mentionTriggerIndex,
                                        mentionQuery = mentionQuery,
                                        prefix = prefix,
                                        option = option,
                                    )
                                    setEditorValue(newEditorValue)
                                    onValueChange(newEditorValue.text)
                                    setShowDropdown(false)
                                    setMentionTriggerIndex(-1)
                                    setMentionQuery("")
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

internal fun replaceMentionQueryWithOption(
    editorValue: TextFieldValue,
    mentionTriggerIndex: Int,
    mentionQuery: String,
    prefix: String,
    option: MentionsOption,
): TextFieldValue {
    val safeTriggerIndex = mentionTriggerIndex.coerceIn(0, editorValue.text.length)
    val replaceEnd = (safeTriggerIndex + mentionQuery.length).coerceIn(safeTriggerIndex, editorValue.text.length)
    val beforeMention = editorValue.text.substring(0, safeTriggerIndex)
    val afterMention = editorValue.text.substring(replaceEnd)
    val insertedText = "$prefix${option.label} "
    val newText = "$beforeMention$insertedText$afterMention"
    val cursor = beforeMention.length + insertedText.length
    return TextFieldValue(
        text = newText,
        selection = TextRange(cursor),
    )
}

internal data class MentionRange(
    val start: Int,
    val end: Int,
)

internal fun findMentionRanges(
    text: String,
    prefix: String,
): List<MentionRange> {
    if (prefix.isEmpty()) return emptyList()
    val ranges = mutableListOf<MentionRange>()
    var searchIndex = 0
    while (searchIndex < text.length) {
        val start = text.indexOf(prefix, startIndex = searchIndex)
        if (start == -1) break
        val labelStart = start + prefix.length
        if (labelStart >= text.length || text[labelStart].isWhitespace()) {
            searchIndex = labelStart
            continue
        }
        var end = labelStart
        while (end < text.length && !text[end].isWhitespace()) {
            end += 1
        }
        ranges += MentionRange(start, end)
        searchIndex = end
    }
    return ranges
}

internal fun replaceEditedMentionWithWholeDeletion(
    oldValue: TextFieldValue,
    requestedValue: TextFieldValue,
    prefix: String,
): TextFieldValue? {
    val oldText = oldValue.text
    val newText = requestedValue.text
    val selection = oldValue.selection
    if (!selection.collapsed || oldText.length - newText.length != 1) return null

    val cursor = selection.start
    val isBackwardDelete =
        cursor > 0 && newText == oldText.removeRange(cursor - 1, cursor)
    val isForwardDelete =
        cursor < oldText.length && newText == oldText.removeRange(cursor, cursor + 1)
    if (!isBackwardDelete && !isForwardDelete) return null

    val range = findMentionRanges(oldText, prefix).firstOrNull { mentionRange ->
        when {
            isBackwardDelete -> {
                val deletesTrailingSpace =
                    cursor == mentionRange.end + 1 &&
                        oldText.getOrNull(mentionRange.end)?.isWhitespace() == true
                cursor in (mentionRange.start + 1)..mentionRange.end || deletesTrailingSpace
            }
            else -> cursor in mentionRange.start until mentionRange.end
        }
    } ?: return null

    val removeEnd =
        if (
            isBackwardDelete &&
            cursor == range.end + 1 &&
            oldText.getOrNull(range.end)?.isWhitespace() == true
        ) {
            range.end + 1
        } else {
            range.end
        }
    val updatedText = oldText.removeRange(range.start, removeEnd)
    return TextFieldValue(
        text = updatedText,
        selection = TextRange(range.start),
    )
}

internal class MentionsHighlightTransformation(
    private val prefix: String,
    private val enabled: Boolean,
    private val highlightColor: Color,
) : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        if (!enabled) return TransformedText(text, OffsetMapping.Identity)

        val builder = AnnotatedString.Builder(text)
        findMentionRanges(text.text, prefix).forEach { range ->
            builder.addStyle(
                style = SpanStyle(background = highlightColor),
                start = range.start,
                end = range.end,
            )
        }
        return TransformedText(builder.toAnnotatedString(), OffsetMapping.Identity)
    }
}

@Composable
private fun MentionsOptionItem(
    option: MentionsOption,
    onClick: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()

    val backgroundColor = when {
        isHovered -> PaletteTheme.colors.border
        else -> PaletteTheme.colors.surface
    }
    val textColor = when {
        option.disabled -> PaletteTheme.colors.hint
        else -> MentionsDefaults.optionTextColor()
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(MentionsDefaults.CornerRadius))
            .background(backgroundColor)
            .clickable(
                enabled = !option.disabled,
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
            )
            .padding(
                horizontal = MentionsDefaults.OptionPaddingHorizontal,
                vertical = MentionsDefaults.OptionHeight / 4,
            )
    ) {
        PText(
            text = option.label,
            fontSize = MentionsDefaults.FontSize,
            color = textColor,
            style = PaletteTheme.typography.body,
        )
    }
}
