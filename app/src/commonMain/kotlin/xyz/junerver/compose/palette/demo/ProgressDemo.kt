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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.components.CodeBlock
import xyz.junerver.compose.palette.components.progress.PCircleProgress
import xyz.junerver.compose.palette.components.progress.PProgress

@Composable
fun ProgressDemo() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        PText(
            text = "Progress",
            style = MaterialTheme.typography.headlineMedium
        )
        PText(
            text = "进度条组件",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        DemoSection(title = "线性进度条") {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                var progress1 by remember { mutableFloatStateOf(30f) }
                PProgress(percent = progress1)
                
                var progress2 by remember { mutableFloatStateOf(60f) }
                PProgress(percent = progress2)
                
                var progress3 by remember { mutableFloatStateOf(90f) }
                PProgress(percent = progress3)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = "圆形进度条") {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                var circleProgress by remember { mutableFloatStateOf(75f) }
                PCircleProgress(percent = circleProgress)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = "自定义格式化") {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                PProgress(
                    percent = 50f,
                    formatter = { "${it.toInt()}/100" }
                )
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
// 线性进度条
PProgress(percent = 60f)

// 圆形进度条
PCircleProgress(percent = 75f)

// 自定义格式化
PProgress(
    percent = 50f,
    formatter = { "${'$'}{it.toInt()}/100" }
)
            """.trimIndent()
        )
    }
}
