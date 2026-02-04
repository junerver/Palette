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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.foundation.border.BorderContainer
import xyz.junerver.compose.palette.components.CodeBlock
import xyz.junerver.compose.palette.ui.theme.Primary
import xyz.junerver.compose.palette.ui.theme.Success

@Composable
fun BorderBoxDemo() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        PText(
            text = "BorderContainer",
            style = MaterialTheme.typography.headlineMedium
        )
        PText(
            text = "带边框和圆角的内容容器",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        DemoSection(title = "基础用法") {
            BorderContainer {
                PText(
                    text = "这是容器内容",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = "自定义颜色") {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                BorderContainer(
                    borderColor = Primary,
                    borderWidth = 1.dp
                ) {
                    Text("蓝色边框", color = Primary)
                }

                BorderContainer(
                    borderColor = Success,
                    borderWidth = 1.dp
                ) {
                    Text("绿色边框", color = Success)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = "不同尺寸") {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                BorderContainer(
                    height = 40.dp,
                    width = 200.dp,
                    cornerSize = 8.dp
                ) {
                    Text("较大尺寸")
                }

                BorderContainer(
                    height = 24.dp,
                    width = 150.dp,
                    cornerSize = 4.dp
                ) {
                    Text("较小尺寸", style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        PText(
            text = "代码示例",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        CodeBlock(
            code = """
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
            """.trimIndent()
        )
    }
}
