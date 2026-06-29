package xyz.junerver.compose.palette.demo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.Language
import xyz.junerver.compose.palette.LocalLanguage
import xyz.junerver.compose.palette.components.CodeBlock
import xyz.junerver.compose.palette.components.button.PButton
import xyz.junerver.compose.palette.components.popup.PPopup
import xyz.junerver.compose.palette.components.text.PText

@Composable
fun PopupDemo() {
    val text = popupDemoText()
    var basicVisible by remember { mutableStateOf(false) }
    var noDragVisible by remember { mutableStateOf(false) }
    var customVisible by remember { mutableStateOf(false) }

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
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                PButton(text = text.basicButtonText) {
                    basicVisible = true
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = text.noDragSectionTitle) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                PButton(text = text.noDragButtonText) {
                    noDragVisible = true
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = text.customSectionTitle) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                PButton(text = text.customButtonText) {
                    customVisible = true
                }
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

    PPopup(
        visible = basicVisible,
        onClose = { basicVisible = false },
        title = text.basicPopupTitle,
    ) {
        PText(text = text.basicPopupContent)
    }

    PPopup(
        visible = noDragVisible,
        onClose = { noDragVisible = false },
        title = text.noDragPopupTitle,
        draggable = false,
    ) {
        PText(text = text.noDragPopupContent)
    }

    PPopup(
        visible = customVisible,
        onClose = { customVisible = false },
        title = text.customPopupTitle,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            repeat(5) { index ->
                PText(
                    text = "${text.itemPrefix} ${index + 1}",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                )
            }
        }
    }
}

@Composable
@ReadOnlyComposable
private fun popupDemoText(): PopupDemoText =
    when (LocalLanguage.current) {
        Language.ZH_CN ->
            PopupDemoText(
                title = "Popup",
                subtitle = "弹出层组件",
                basicSectionTitle = "基础用法",
                basicButtonText = "显示弹出层",
                basicPopupTitle = "标题",
                basicPopupContent = "这是弹出层的内容",
                noDragSectionTitle = "禁止拖拽",
                noDragButtonText = "不可拖拽弹出层",
                noDragPopupTitle = "不可拖拽",
                noDragPopupContent = "此弹出层禁止拖拽关闭",
                customSectionTitle = "自定义内容",
                customButtonText = "显示列表弹出层",
                customPopupTitle = "列表",
                itemPrefix = "列表项",
                codeTitle = "代码示例",
                codeBlock =
                    """
                    var visible by remember { mutableStateOf(false) }

                    PPopup(
                        visible = visible,
                        onClose = { visible = false },
                        title = "标题"
                    ) {
                        Text("内容")
                    }

                    PPopup(
                        visible = visible,
                        onClose = { visible = false },
                        draggable = false
                    ) {
                        Text("不可拖拽")
                    }
                    """.trimIndent(),
            )

        Language.EN_US ->
            PopupDemoText(
                title = "Popup",
                subtitle = "Popup component",
                basicSectionTitle = "Basic Usage",
                basicButtonText = "Show Popup",
                basicPopupTitle = "Title",
                basicPopupContent = "This is the popup content",
                noDragSectionTitle = "Disable Drag",
                noDragButtonText = "Non-draggable Popup",
                noDragPopupTitle = "Non-draggable",
                noDragPopupContent = "This popup cannot be dismissed by dragging",
                customSectionTitle = "Custom Content",
                customButtonText = "Show List Popup",
                customPopupTitle = "List",
                itemPrefix = "Item",
                codeTitle = "Code Example",
                codeBlock =
                    """
                    var visible by remember { mutableStateOf(false) }

                    PPopup(
                        visible = visible,
                        onClose = { visible = false },
                        title = "Title"
                    ) {
                        Text("Content")
                    }

                    PPopup(
                        visible = visible,
                        onClose = { visible = false },
                        draggable = false
                    ) {
                        Text("Non-draggable")
                    }
                    """.trimIndent(),
            )
    }

private data class PopupDemoText(
    val title: String,
    val subtitle: String,
    val basicSectionTitle: String,
    val basicButtonText: String,
    val basicPopupTitle: String,
    val basicPopupContent: String,
    val noDragSectionTitle: String,
    val noDragButtonText: String,
    val noDragPopupTitle: String,
    val noDragPopupContent: String,
    val customSectionTitle: String,
    val customButtonText: String,
    val customPopupTitle: String,
    val itemPrefix: String,
    val codeTitle: String,
    val codeBlock: String,
)
