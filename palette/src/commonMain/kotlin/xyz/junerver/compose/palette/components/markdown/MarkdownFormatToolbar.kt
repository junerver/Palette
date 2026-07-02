package xyz.junerver.compose.palette.components.markdown

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FormatListBulleted
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.FormatBold
import androidx.compose.material.icons.filled.FormatItalic
import androidx.compose.material.icons.filled.FormatStrikethrough
import androidx.compose.material.icons.filled.HorizontalRule
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material.icons.filled.Title
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import xyz.junerver.compose.hooks.useState
import xyz.junerver.compose.palette.core.theme.PaletteTheme

/**
 * 工具栏可触发的动作。Toolbar 将其透传给宿主编辑器，
 * 由 [MarkdownEditActions] 中对应的纯函数完成文本变换。
 */
enum class MarkdownToolbarAction {
    Bold,
    Italic,
    Strikethrough,
    InlineCode,
    Heading, // 配合 [MarkdownHeadingLevel] 使用，默认 H1
    UnorderedList,
    OrderedList,
    TaskList,
    Quote,
    Link,
    Image,
    CodeBlock,
    Table,
    HorizontalRule,
    // 扩展行内语法
    InlineLatex,
    Subscript,
    Superscript,
    Highlight,
}

/** 标题层级。Heading 下拉项回调。 */
enum class MarkdownHeadingLevel(val level: Int) {
    H1(1), H2(2), H3(3), H4(4), H5(5), H6(6), None(0);

    companion object {
        fun of(level: Int): MarkdownHeadingLevel = entries.firstOrNull { it.level == level } ?: H1
    }
}

/**
 * Markdown 格式化工具栏。横向滚动、分组排布，样式从 [MarkdownDefaults] / [xyz.junerver.compose.palette.core.theme.PaletteTheme] 派生。
 *
 * @param onAction 普通按钮点击回调。
 * @param onHeadingLevel 标题下拉选择回调（H1..H6 / None=清除）；为 null 时使用默认 H1 行为。
 * @param enabled 是否可用（与编辑器只读态联动）。
 */
@Composable
fun MarkdownFormatToolbar(
    onAction: (MarkdownToolbarAction) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onHeadingLevel: ((MarkdownHeadingLevel) -> Unit)? = null,
) {
    val buttonSpacing = MarkdownDefaults.toolbarButtonSpacing()
    val groupSpacing = MarkdownDefaults.toolbarGroupSpacing()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .defaultMinSize(minHeight = MarkdownDefaults.toolbarMinHeight)
            .padding(vertical = MarkdownDefaults.toolbarButtonSpacing() / 2),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(buttonSpacing),
    ) {
        // 分组1：行内格式
        ToolbarGroup(spacing = groupSpacing) {
            ToolbarIconButton(MarkdownToolbarAction.Bold, Icons.Filled.FormatBold, "Bold", enabled, onAction)
            ToolbarIconButton(MarkdownToolbarAction.Italic, Icons.Filled.FormatItalic, "Italic", enabled, onAction)
            ToolbarIconButton(MarkdownToolbarAction.Strikethrough, Icons.Filled.FormatStrikethrough, "Strikethrough", enabled, onAction)
            ToolbarIconButton(MarkdownToolbarAction.InlineCode, Icons.Filled.Code, "InlineCode", enabled, onAction)
        }

        // 分组2：标题下拉
        HeadingDropdown(enabled = enabled, onHeadingLevel = onHeadingLevel) {
            onAction(MarkdownToolbarAction.Heading)
        }

        // 分组3：列表 / 引用
        ToolbarGroup(spacing = groupSpacing) {
            ToolbarIconButton(MarkdownToolbarAction.UnorderedList, Icons.AutoMirrored.Filled.FormatListBulleted, "UnorderedList", enabled, onAction)
            ToolbarIconButton(MarkdownToolbarAction.OrderedList, Icons.Filled.FormatListNumbered, "OrderedList", enabled, onAction)
            ToolbarIconButton(MarkdownToolbarAction.TaskList, Icons.Filled.Checklist, "TaskList", enabled, onAction)
            ToolbarIconButton(MarkdownToolbarAction.Quote, Icons.Filled.FormatQuote, "Quote", enabled, onAction)
        }

        // 分组4：插入
        ToolbarGroup(spacing = groupSpacing) {
            ToolbarIconButton(MarkdownToolbarAction.Link, Icons.Filled.Link, "Link", enabled, onAction)
            ToolbarIconButton(MarkdownToolbarAction.Image, Icons.Filled.Image, "Image", enabled, onAction)
            ToolbarIconButton(MarkdownToolbarAction.CodeBlock, Icons.Filled.Code, "CodeBlock", enabled, onAction)
            ToolbarIconButton(MarkdownToolbarAction.Table, Icons.Filled.TableChart, "Table", enabled, onAction)
            ToolbarIconButton(MarkdownToolbarAction.HorizontalRule, Icons.Filled.HorizontalRule, "HorizontalRule", enabled, onAction)
        }

        // 分组5：扩展行内语法（LaTeX / 下标 / 上标 / 高亮）
        ToolbarGroup(spacing = groupSpacing) {
            ToolbarTextButton(MarkdownToolbarAction.InlineLatex, "\$", "InlineLatex", enabled, onAction)
            ToolbarTextButton(MarkdownToolbarAction.Subscript, "x₂", "Subscript", enabled, onAction)
            ToolbarTextButton(MarkdownToolbarAction.Superscript, "x²", "Superscript", enabled, onAction)
            ToolbarTextButton(MarkdownToolbarAction.Highlight, "≡", "Highlight", enabled, onAction)
        }
    }
}

@Composable
private fun ToolbarGroup(
    spacing: Dp,
    content: @Composable androidx.compose.foundation.layout.RowScope.() -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(MarkdownDefaults.toolbarButtonSpacing()),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(end = spacing),
        content = content,
    )
}

@Composable
private fun ToolbarIconButton(
    action: MarkdownToolbarAction,
    icon: ImageVector,
    tagSuffix: String,
    enabled: Boolean,
    onAction: (MarkdownToolbarAction) -> Unit,
) {
    IconButton(
        onClick = { onAction(action) },
        enabled = enabled,
        modifier = Modifier
            .focusProperties { canFocus = false }
            .testTag("md-toolbar-$tagSuffix"),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = action.name,
            modifier = Modifier.size(MarkdownDefaults.toolbarIconSize),
        )
    }
}

/**
 * 文本图标按钮：用于无合适 Material 矢量图标的行内语法（LaTeX `$`、下标 `x₂`、上标 `x²`、高亮 `≡`）。
 */
@Composable
private fun ToolbarTextButton(
    action: MarkdownToolbarAction,
    label: String,
    tagSuffix: String,
    enabled: Boolean,
    onAction: (MarkdownToolbarAction) -> Unit,
) {
    IconButton(
        onClick = { onAction(action) },
        enabled = enabled,
        modifier = Modifier
            .focusProperties { canFocus = false }
            .testTag("md-toolbar-$tagSuffix"),
    ) {
        Text(
            text = label,
            style = PaletteTheme.typography.body,
            color = PaletteTheme.colors.textPrimary,
        )
    }
}

/**
 * 标题按钮：点击主体应用默认 H1；点击下拉箭头展开 H1..H6 / 清除。
 */
@Composable
private fun HeadingDropdown(
    enabled: Boolean,
    onHeadingLevel: ((MarkdownHeadingLevel) -> Unit)?,
    onDefaultHeading: () -> Unit,
) {
    val (expanded, setExpanded) = useState(false)
    Box {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = { onDefaultHeading() },
                enabled = enabled,
                modifier = Modifier
                    .focusProperties { canFocus = false }
                    .testTag("md-toolbar-Heading"),
            ) {
                Icon(
                    imageVector = Icons.Filled.Title,
                    contentDescription = "Heading",
                    modifier = Modifier.size(MarkdownDefaults.toolbarIconSize),
                )
            }
            IconButton(
                onClick = { setExpanded(true) },
                enabled = enabled,
                modifier = Modifier
                    .focusProperties { canFocus = false }
                    .testTag("md-toolbar-HeadingArrow"),
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowDropDown,
                    contentDescription = "更多标题级别",
                    modifier = Modifier.size(MarkdownDefaults.toolbarIconSize),
                )
            }
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { setExpanded(false) },
        ) {
            MarkdownHeadingLevel.entries.forEach { level ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = if (level == MarkdownHeadingLevel.None) "清除标题" else "Heading ${level.name}",
                            style = PaletteTheme.typography.body,
                        )
                    },
                    onClick = {
                        setExpanded(false)
                        onHeadingLevel?.invoke(level)
                    },
                )
            }
        }
    }
}
