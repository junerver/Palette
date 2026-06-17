package xyz.junerver.compose.palette.components.searchbar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import kotlin.time.Duration
import xyz.junerver.compose.hooks.useDebounce
import xyz.junerver.compose.hooks.useLatestState
import xyz.junerver.compose.hooks.useState
import xyz.junerver.compose.palette.core.theme.PaletteTheme
import xyz.junerver.compose.palette.core.tokens.FormTokens
import xyz.junerver.compose.palette.core.util.clickableWithoutRipple

@Composable
fun PSearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "搜索",
    enabled: Boolean = true,
    onSearch: ((String) -> Unit)? = null,
    debounce: Boolean = false,
    debounceWait: Duration = SearchBarDefaults.DebounceWait,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val isHovered by interactionSource.collectIsHoveredAsState()
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val latestOnSearch = useLatestState(onSearch)
    val (lastSubmittedQuery, setLastSubmittedQuery) = useState(value)
    val debouncedValue by useDebounce(value = value) {
        wait = debounceWait
    }

    val shape = RoundedCornerShape(SearchBarDefaults.CornerRadius)
    val backgroundColor by animateColorAsState(
        targetValue = SearchBarDefaults.backgroundColor(enabled),
        animationSpec = tween(FormTokens.DurationFast),
    )
    val borderColor by animateColorAsState(
        targetValue = SearchBarDefaults.borderColor(
            isFocused = isFocused,
            isHovered = isHovered,
            enabled = enabled,
        ),
        animationSpec = tween(FormTokens.DurationFast),
    )
    val textColor = SearchBarDefaults.textColor(enabled)
    val iconColor = SearchBarDefaults.iconColor(enabled)
    val placeholderColor = SearchBarDefaults.placeholderColor(enabled)
    val clearIconColor = SearchBarDefaults.clearIconColor(enabled)

    fun submitSearch(query: String) {
        val search = latestOnSearch.value ?: return
        search(query)
        setLastSubmittedQuery(query)
    }

    LaunchedEffect(debounce, debouncedValue) {
        if (enabled && debounce && debouncedValue != lastSubmittedQuery) {
            submitSearch(debouncedValue)
        }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(SearchBarDefaults.Height)
            .border(SearchBarDefaults.BorderWidth, borderColor, shape)
            .clip(shape)
            .background(backgroundColor)
            .hoverable(interactionSource = interactionSource, enabled = enabled)
            .clickableWithoutRipple(enabled) {
                focusRequester.requestFocus()
            }
            .padding(horizontal = SearchBarDefaults.ContentPadding),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(SearchBarDefaults.IconSize),
        )

        Spacer(modifier = Modifier.width(SearchBarDefaults.IconTextSpacing))

        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.CenterStart,
        ) {
            if (value.isEmpty()) {
                Text(
                    text = placeholder,
                    style = PaletteTheme.typography.body.copy(
                        fontSize = SearchBarDefaults.FontSize,
                        color = placeholderColor,
                    ),
                )
            }

            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                enabled = enabled,
                singleLine = true,
                textStyle = TextStyle(
                    fontSize = SearchBarDefaults.FontSize,
                    color = textColor,
                ),
                cursorBrush = SolidColor(SearchBarDefaults.cursorColor()),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        submitSearch(value)
                        focusManager.clearFocus()
                    },
                ),
                interactionSource = interactionSource,
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
            )
        }

        AnimatedVisibility(
            visible = value.isNotEmpty() && enabled,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            IconButton(
                onClick = {
                    onValueChange("")
                    focusRequester.requestFocus()
                },
                modifier = Modifier.size(SearchBarDefaults.ClearButtonSize),
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Clear",
                    tint = clearIconColor,
                    modifier = Modifier.size(SearchBarDefaults.ClearIconSize),
                )
            }
        }
    }
}
