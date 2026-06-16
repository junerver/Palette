package xyz.junerver.compose.palette.demo

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.hooks.useState
import xyz.junerver.compose.palette.Language
import xyz.junerver.compose.palette.LocalLanguage
import xyz.junerver.compose.palette.components.CodeBlock
import xyz.junerver.compose.palette.components.alert.AlertType
import xyz.junerver.compose.palette.components.alert.PAlert
import xyz.junerver.compose.palette.components.button.PButton
import xyz.junerver.compose.palette.components.text.PText

@Composable
fun AlertDemo() {
    val text = alertDemoText()
    val (actionClicked, setActionClicked) = useState(false)

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
            Column {
                PAlert(message = text.infoMessage, type = AlertType.Info)
                Spacer(modifier = Modifier.height(8.dp))
                PAlert(message = text.successMessage, type = AlertType.Success)
                Spacer(modifier = Modifier.height(8.dp))
                PAlert(message = text.warningMessage, type = AlertType.Warning)
                Spacer(modifier = Modifier.height(8.dp))
                PAlert(message = text.errorMessage, type = AlertType.Error)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = text.descriptionSectionTitle) {
            PAlert(
                message = text.descriptionTitle,
                description = text.descriptionContent,
                type = AlertType.Info,
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = text.closableSectionTitle) {
            PAlert(
                message = text.closableMessage,
                type = AlertType.Warning,
                closable = true,
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = text.actionSectionTitle) {
            Column {
                PAlert(
                    message = text.actionMessage,
                    type = AlertType.Success,
                    action = {
                        PButton(text = text.actionButtonText) {
                            setActionClicked(true)
                        }
                    },
                )
                if (actionClicked) {
                    Spacer(modifier = Modifier.height(8.dp))
                    PText(
                        text = text.actionClickedText,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                    )
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
}

@Composable
@ReadOnlyComposable
private fun alertDemoText(): AlertDemoText =
    when (LocalLanguage.current) {
        Language.ZH_CN ->
            AlertDemoText(
                title = "Alert",
                subtitle = "警告提示组件",
                basicSectionTitle = "基本类型",
                infoMessage = "这是一条信息提示",
                successMessage = "这是一条成功提示",
                warningMessage = "这是一条警告提示",
                errorMessage = "这是一条错误提示",
                descriptionSectionTitle = "含辅助性文字",
                descriptionTitle = "信息提示",
                descriptionContent = "这是一条带有详细描述的警告提示，用于展示更多信息。",
                closableSectionTitle = "可关闭",
                closableMessage = "这是一条可关闭的警告提示",
                actionSectionTitle = "操作按钮",
                actionMessage = "操作成功",
                actionButtonText = "查看详情",
                actionClickedText = "按钮已点击",
                codeTitle = "代码示例",
                codeBlock =
                    """
                    PAlert(
                        message = "信息提示",
                        type = AlertType.Info
                    )

                    PAlert(
                        message = "警告",
                        description = "详细描述内容",
                        type = AlertType.Warning,
                        closable = true,
                        action = {
                            PButton(text = "操作") { }
                        }
                    )
                    """.trimIndent(),
            )

        Language.EN_US ->
            AlertDemoText(
                title = "Alert",
                subtitle = "Alert component",
                basicSectionTitle = "Basic Types",
                infoMessage = "This is an info alert",
                successMessage = "This is a success alert",
                warningMessage = "This is a warning alert",
                errorMessage = "This is an error alert",
                descriptionSectionTitle = "With Description",
                descriptionTitle = "Info Alert",
                descriptionContent = "This is an alert with a detailed description for showing more information.",
                closableSectionTitle = "Closable",
                closableMessage = "This is a closable alert",
                actionSectionTitle = "Action Button",
                actionMessage = "Operation succeeded",
                actionButtonText = "View Details",
                actionClickedText = "Button clicked",
                codeTitle = "Code Example",
                codeBlock =
                    """
                    PAlert(
                        message = "Info",
                        type = AlertType.Info
                    )

                    PAlert(
                        message = "Warning",
                        description = "Detailed description",
                        type = AlertType.Warning,
                        closable = true,
                        action = {
                            PButton(text = "Action") { }
                        }
                    )
                    """.trimIndent(),
            )
    }

private data class AlertDemoText(
    val title: String,
    val subtitle: String,
    val basicSectionTitle: String,
    val infoMessage: String,
    val successMessage: String,
    val warningMessage: String,
    val errorMessage: String,
    val descriptionSectionTitle: String,
    val descriptionTitle: String,
    val descriptionContent: String,
    val closableSectionTitle: String,
    val closableMessage: String,
    val actionSectionTitle: String,
    val actionMessage: String,
    val actionButtonText: String,
    val actionClickedText: String,
    val codeTitle: String,
    val codeBlock: String,
)
