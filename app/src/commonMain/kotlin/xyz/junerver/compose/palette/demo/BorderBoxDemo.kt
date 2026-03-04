package xyz.junerver.compose.palette.demo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import xyz.junerver.compose.palette.components.text.PText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.Language
import xyz.junerver.compose.palette.LocalLanguage
import xyz.junerver.compose.palette.foundation.border.BorderContainer
import xyz.junerver.compose.palette.components.CodeBlock
import xyz.junerver.compose.palette.ui.theme.Primary
import xyz.junerver.compose.palette.ui.theme.Success

@Composable
fun BorderBoxDemo() {
    val text = borderBoxDemoText()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        PText(
            text = text.title,
            style = MaterialTheme.typography.headlineMedium
        )
        PText(
            text = text.subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        DemoSection(title = text.basicSectionTitle) {
            BorderContainer {
                PText(
                    text = text.basicContent,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = text.colorSectionTitle) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                BorderContainer(
                    borderColor = Primary,
                    borderWidth = 1.dp
                ) {
                    Text(text.blueBorderText, color = Primary)
                }

                BorderContainer(
                    borderColor = Success,
                    borderWidth = 1.dp
                ) {
                    Text(text.greenBorderText, color = Success)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = text.sizeSectionTitle) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                BorderContainer(
                    height = 40.dp,
                    width = 200.dp,
                    cornerSize = 8.dp
                ) {
                    Text(text.largeSizeText)
                }

                BorderContainer(
                    height = 24.dp,
                    width = 150.dp,
                    cornerSize = 4.dp
                ) {
                    Text(text.smallSizeText, style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        PText(
            text = text.codeTitle,
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        CodeBlock(
            code = text.codeBlock
        )
    }
}

@Composable
@ReadOnlyComposable
private fun borderBoxDemoText(): BorderBoxDemoText = when (LocalLanguage.current) {
    Language.ZH_CN -> BorderBoxDemoText(
        title = "BorderContainer",
        subtitle = "带边框和圆角的内容容器",
        basicSectionTitle = "基础用法",
        basicContent = "这是容器内容",
        colorSectionTitle = "自定义颜色",
        blueBorderText = "蓝色边框",
        greenBorderText = "绿色边框",
        sizeSectionTitle = "不同尺寸",
        largeSizeText = "较大尺寸",
        smallSizeText = "较小尺寸",
        codeTitle = "代码示例",
        codeBlock = """
BorderContainer(
    height = 28.dp,
    width = 300.dp,
    borderWidth = 0.5.dp,
    cornerSize = 5.dp,
    borderColor = Color(0xFFD9D9D9),
    backgroundColor = Color.White
) {
    Text("内容")
}
        """.trimIndent(),
    )

    Language.EN_US -> BorderBoxDemoText(
        title = "BorderContainer",
        subtitle = "A content container with border and rounded corners.",
        basicSectionTitle = "Basic Usage",
        basicContent = "This is container content",
        colorSectionTitle = "Custom Colors",
        blueBorderText = "Blue Border",
        greenBorderText = "Green Border",
        sizeSectionTitle = "Different Sizes",
        largeSizeText = "Larger Size",
        smallSizeText = "Smaller Size",
        codeTitle = "Code Example",
        codeBlock = """
BorderContainer(
    height = 28.dp,
    width = 300.dp,
    borderWidth = 0.5.dp,
    cornerSize = 5.dp,
    borderColor = Color(0xFFD9D9D9),
    backgroundColor = Color.White
) {
    Text("Content")
}
        """.trimIndent(),
    )
}

private data class BorderBoxDemoText(
    val title: String,
    val subtitle: String,
    val basicSectionTitle: String,
    val basicContent: String,
    val colorSectionTitle: String,
    val blueBorderText: String,
    val greenBorderText: String,
    val sizeSectionTitle: String,
    val largeSizeText: String,
    val smallSizeText: String,
    val codeTitle: String,
    val codeBlock: String,
)
