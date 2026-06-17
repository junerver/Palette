package xyz.junerver.compose.palette.components.tag

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.hooks.useState
import xyz.junerver.compose.palette.core.theme.PaletteTheme

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PEditableTagGroup(
    tags: List<String>,
    onTagsChange: (List<String>) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Add tag...",
    variant: TagVariant = TagVariant.Default,
    size: TagSize = TagSize.Medium,
    maxTags: Int = Int.MAX_VALUE,
    validator: (String) -> Boolean = { it.isNotBlank() },
    tagColors: @Composable (String) -> TagColors = { TagDefaults.pastelColors(it) }
) {
    val (isEditing, setIsEditing) = useState(false)
    val (inputText, setInputText) = useState("")
    val sizeTokens = TagDefaults.sizeTokens(size)
    val fieldTokens = PaletteTheme.componentThemes.textField

    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(TagDefaults.itemSpacing()),
        verticalArrangement = Arrangement.spacedBy(TagDefaults.itemSpacing())
    ) {
        // 显示现有标签
        tags.forEach { tag ->
            PTag(
                text = tag,
                variant = variant,
                size = size,
                closable = true,
                onClose = {
                    onTagsChange(tags.filter { it != tag })
                },
                colors = tagColors(tag)
            )
        }

        // 添加按钮或输入框
        if (isEditing) {
            // 输入框模式
            Surface(
                modifier = Modifier.padding(0.dp),
                shape = RoundedCornerShape(sizeTokens.cornerRadius),
                color = fieldTokens.backgroundColor,
                border = androidx.compose.foundation.BorderStroke(
                    TagDefaults.borderWidth(),
                    fieldTokens.borderColor
                )
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = sizeTokens.horizontalPadding)
                        .size(width = 120.dp, height = sizeTokens.height),
                    verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    BasicTextField(
                        value = inputText,
                        onValueChange = setInputText,
                        modifier = Modifier.weight(1f),
                        textStyle = PaletteTheme.typography.body.copy(
                            fontSize = sizeTokens.fontSize,
                            color = fieldTokens.textColor
                        ),
                        cursorBrush = SolidColor(fieldTokens.cursorColor),
                        singleLine = true,
                        decorationBox = { innerTextField ->
                            if (inputText.isEmpty()) {
                                Text(
                                    text = placeholder,
                                    style = PaletteTheme.typography.body.copy(
                                        fontSize = sizeTokens.fontSize,
                                        color = fieldTokens.placeholderColor
                                    )
                                )
                            }
                            innerTextField()
                        }
                    )

                    // 确认按钮
                    IconButton(
                        onClick = {
                            if (validator(inputText) && !tags.contains(inputText)) {
                                onTagsChange(tags + inputText)
                                setInputText("")
                                setIsEditing(false)
                            }
                        },
                        modifier = Modifier.size(sizeTokens.closeButtonSize)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Confirm",
                            tint = PaletteTheme.colors.success,
                            modifier = Modifier.size(sizeTokens.closeButtonSize * TagDefaults.closeIconScale())
                        )
                    }

                    // 取消按钮
                    IconButton(
                        onClick = {
                            setInputText("")
                            setIsEditing(false)
                        },
                        modifier = Modifier.size(sizeTokens.closeButtonSize)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cancel",
                            tint = PaletteTheme.colors.error,
                            modifier = Modifier.size(sizeTokens.closeButtonSize * TagDefaults.closeIconScale())
                        )
                    }
                }
            }
        } else if (tags.size < maxTags) {
            // 添加按钮模式
            Surface(
                onClick = { setIsEditing(true) },
                modifier = Modifier.size(height = sizeTokens.height, width = sizeTokens.height),
                shape = RoundedCornerShape(sizeTokens.cornerRadius),
                color = TagDefaults.defaultColors().containerColor,
                contentColor = TagDefaults.defaultColors().contentColor
            ) {
                Row(
                    modifier = Modifier.padding(4.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add tag",
                        modifier = Modifier.size(sizeTokens.closeButtonSize)
                    )
                }
            }
        }
    }
}
