package xyz.junerver.compose.palette.demo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.Language
import xyz.junerver.compose.palette.LocalLanguage
import xyz.junerver.compose.palette.components.CodeBlock
import xyz.junerver.compose.palette.components.floatbutton.FloatButtonShape
import xyz.junerver.compose.palette.components.floatbutton.PFloatButton
import xyz.junerver.compose.palette.components.text.PText

@Composable
fun FloatButtonDemo() {
    val text = floatButtonDemoText()

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

        DemoSection(title = text.iconSectionTitle) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                PFloatButton(
                    onClick = {},
                    icon = {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                        )
                    },
                )
                PFloatButton(
                    onClick = {},
                    icon = {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                        )
                    },
                )
                PFloatButton(
                    onClick = {},
                    icon = {
                        Icon(
                            Icons.Default.Favorite,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                        )
                    },
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = text.textSectionTitle) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                PFloatButton(
                    onClick = {},
                    text = text.createText,
                )
                PFloatButton(
                    onClick = {},
                    text = text.editText,
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = text.shapeSectionTitle) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    PFloatButton(
                        onClick = {},
                        shape = FloatButtonShape.Circle,
                        icon = {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                            )
                        },
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    PText(
                        text = text.circleText,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    PFloatButton(
                        onClick = {},
                        shape = FloatButtonShape.Square,
                        icon = {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                            )
                        },
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    PText(
                        text = text.squareText,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
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
private fun floatButtonDemoText(): FloatButtonDemoText =
    when (LocalLanguage.current) {
        Language.ZH_CN ->
            FloatButtonDemoText(
                title = "PFloatButton",
                subtitle = "悬浮操作按钮组件",
                iconSectionTitle = "图标按钮",
                textSectionTitle = "文字按钮",
                shapeSectionTitle = "不同形状",
                createText = "新建",
                editText = "编辑",
                circleText = "圆形",
                squareText = "方形",
                codeTitle = "代码示例",
                codeBlock =
                    """
                    PFloatButton(
                        onClick = { },
                        shape = FloatButtonShape.Circle,
                        icon = {
                            Icon(Icons.Default.Add, contentDescription = null)
                        },
                    )

                    PFloatButton(
                        onClick = { },
                        text = "新建",
                    )
                    """.trimIndent(),
            )

        Language.EN_US ->
            FloatButtonDemoText(
                title = "PFloatButton",
                subtitle = "A floating action button component.",
                iconSectionTitle = "Icon Button",
                textSectionTitle = "Text Button",
                shapeSectionTitle = "Different Shapes",
                createText = "Create",
                editText = "Edit",
                circleText = "Circle",
                squareText = "Square",
                codeTitle = "Code Example",
                codeBlock =
                    """
                    PFloatButton(
                        onClick = { },
                        shape = FloatButtonShape.Circle,
                        icon = {
                            Icon(Icons.Default.Add, contentDescription = null)
                        },
                    )

                    PFloatButton(
                        onClick = { },
                        text = "Create",
                    )
                    """.trimIndent(),
            )
    }

private data class FloatButtonDemoText(
    val title: String,
    val subtitle: String,
    val iconSectionTitle: String,
    val textSectionTitle: String,
    val shapeSectionTitle: String,
    val createText: String,
    val editText: String,
    val circleText: String,
    val squareText: String,
    val codeTitle: String,
    val codeBlock: String,
)
