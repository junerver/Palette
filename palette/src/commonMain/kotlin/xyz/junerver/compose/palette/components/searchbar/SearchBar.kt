package xyz.junerver.compose.palette.components.searchbar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.core.theme.PaletteTheme
import xyz.junerver.compose.palette.core.util.clickableWithoutRipple

@Composable
fun PSearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "搜索",
    enabled: Boolean = true,
    onSearch: ((String) -> Unit)? = null,
) {
    var isFocused by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    val shape = RoundedCornerShape(SearchBarDefaults.CornerRadius)
    val backgroundColor = SearchBarDefaults.backgroundColor()
    val textColor = SearchBarDefaults.textColor()
    val iconColor = SearchBarDefaults.iconColor()
    val placeholderColor = SearchBarDefaults.placeholderColor()
    val cancelColor = SearchBarDefaults.cancelColor()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(SearchBarDefaults.Height)
            .padding(horizontal = SearchBarDefaults.ContentPadding),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .clip(shape)
                .background(backgroundColor)
                .clickableWithoutRipple(!isFocused) {
                    isFocused = true
                }
                .padding(horizontal = SearchBarDefaults.IconPadding),
            contentAlignment = Alignment.CenterStart,
        ) {
            Icon(
                imageVector = Icons.Outlined.Search,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(SearchBarDefaults.IconSize),
            )

            if (isFocused) {
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    enabled = enabled,
                    singleLine = true,
                    textStyle = TextStyle(
                        fontSize = SearchBarDefaults.FontSize,
                        color = textColor,
                    ),
                    cursorBrush = SolidColor(PaletteTheme.colors.primary),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            onSearch?.invoke(value)
                            focusManager.clearFocus()
                        }
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = SearchBarDefaults.IconSize + 4.dp)
                        .focusRequester(focusRequester)
                        .onFocusChanged { focusState ->
                            isFocused = focusState.isFocused
                        },
                    decorationBox = { innerTextField ->
                        Box {
                            innerTextField()
                            if (value.isEmpty()) {
                                Text(
                                    text = placeholder,
                                    fontSize = SearchBarDefaults.FontSize,
                                    color = placeholderColor,
                                )
                            }
                        }
                    },
                )

                LaunchedEffect(Unit) {
                    focusRequester.requestFocus()
                }
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Spacer(modifier = Modifier.width(SearchBarDefaults.IconSize + 4.dp))
                    Text(
                        text = placeholder,
                        fontSize = SearchBarDefaults.FontSize,
                        color = placeholderColor,
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = isFocused,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            Text(
                text = "取消",
                fontSize = SearchBarDefaults.CancelFontSize,
                color = cancelColor,
                modifier = Modifier
                    .clickableWithoutRipple {
                        onValueChange("")
                        focusManager.clearFocus()
                    }
                    .padding(start = SearchBarDefaults.ContentPadding),
            )
        }
    }
}
