package xyz.junerver.compose.palette.demo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.Language
import xyz.junerver.compose.palette.LocalLanguage
import xyz.junerver.compose.palette.components.CodeBlock
import xyz.junerver.compose.palette.components.contextmenu.ContextMenuItem
import xyz.junerver.compose.palette.components.contextmenu.PContextMenu
import xyz.junerver.compose.palette.components.contextmenu.longPressContextMenu
import xyz.junerver.compose.palette.components.contextmenu.rememberContextMenuState
import xyz.junerver.compose.palette.components.text.PText

@Composable
fun ContextMenuDemo() {
    val text = contextMenuDemoText()
    val basicState = rememberContextMenuState()
    val disabledState = rememberContextMenuState()

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
    ) {
        PText(
            text = text.title,
            style = MaterialTheme.typography.headlineMedium,
        )
        PText(
            text = text.subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(modifier = Modifier.height(32.dp))

        DemoSection(title = text.basicSectionTitle) {
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .longPressContextMenu(
                            state = basicState,
                            items = listOf(
                                ContextMenuItem(label = text.menuCopy),
                                ContextMenuItem(label = text.menuPaste),
                                ContextMenuItem(label = text.menuDelete),
                            ),
                        ),
                contentAlignment = Alignment.Center,
            ) {
                PText(
                    text = text.longPressHint,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = text.disabledSectionTitle) {
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.secondaryContainer)
                        .longPressContextMenu(
                            state = disabledState,
                            items = listOf(
                                ContextMenuItem(label = text.menuCopy),
                                ContextMenuItem(label = text.menuPaste),
                                ContextMenuItem(label = text.menuCut, disabled = true),
                            ),
                        ),
                contentAlignment = Alignment.Center,
            ) {
                PText(
                    text = text.longPressHint,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        PText(
            text = text.codeTitle,
            style = MaterialTheme.typography.titleMedium,
        )

        Spacer(modifier = Modifier.height(16.dp))

        CodeBlock(
            code = text.codeBlock,
        )
    }

    PContextMenu(state = basicState) {}
    PContextMenu(state = disabledState) {}
}

@Composable
@ReadOnlyComposable
private fun contextMenuDemoText(): ContextMenuDemoText =
    when (LocalLanguage.current) {
        Language.ZH_CN ->
            ContextMenuDemoText(
                title = "ContextMenu",
                subtitle = "上下文菜单组件",
                basicSectionTitle = "基础用法",
                disabledSectionTitle = "禁用项",
                longPressHint = "长按此区域",
                menuCopy = "复制",
                menuPaste = "粘贴",
                menuDelete = "删除",
                menuCut = "剪切",
                codeTitle = "代码示例",
                codeBlock =
                    """
                    val state = rememberContextMenuState()

                    Box(
                        modifier = Modifier
                            .longPressContextMenu(
                                state = state,
                                items = listOf(
                                    ContextMenuItem(label = "复制"),
                                    ContextMenuItem(label = "粘贴"),
                                    ContextMenuItem(label = "删除", disabled = true)
                                )
                            )
                    ) {
                        Text("长按此区域")
                    }

                    PContextMenu(state = state) { index -> }
                    """.trimIndent(),
            )

        Language.EN_US ->
            ContextMenuDemoText(
                title = "ContextMenu",
                subtitle = "Context menu component",
                basicSectionTitle = "Basic Usage",
                disabledSectionTitle = "Disabled Items",
                longPressHint = "Long press this area",
                menuCopy = "Copy",
                menuPaste = "Paste",
                menuDelete = "Delete",
                menuCut = "Cut",
                codeTitle = "Code Example",
                codeBlock =
                    """
                    val state = rememberContextMenuState()

                    Box(
                        modifier = Modifier
                            .longPressContextMenu(
                                state = state,
                                items = listOf(
                                    ContextMenuItem(label = "Copy"),
                                    ContextMenuItem(label = "Paste"),
                                    ContextMenuItem(label = "Delete", disabled = true)
                                )
                            )
                    ) {
                        Text("Long press this area")
                    }

                    PContextMenu(state = state) { index -> }
                    """.trimIndent(),
            )
    }

private data class ContextMenuDemoText(
    val title: String,
    val subtitle: String,
    val basicSectionTitle: String,
    val disabledSectionTitle: String,
    val longPressHint: String,
    val menuCopy: String,
    val menuPaste: String,
    val menuDelete: String,
    val menuCut: String,
    val codeTitle: String,
    val codeBlock: String,
)
