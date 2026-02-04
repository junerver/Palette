package xyz.junerver.compose.palette.demo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.components.CodeBlock
import xyz.junerver.compose.palette.components.loading.PLoading
import xyz.junerver.compose.palette.components.loading.PLoadingBars
import xyz.junerver.compose.palette.components.loading.PLoadingBounce
import xyz.junerver.compose.palette.components.loading.PLoadingCircle
import xyz.junerver.compose.palette.components.loading.PLoadingDots

@Composable
fun LoadingDemo() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        PText(
            text = "Loading",
            style = MaterialTheme.typography.headlineMedium
        )
        PText(
            text = "加载指示器",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        DemoSection(title = "默认加载") {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                PLoading()
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = "点状加载") {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                PLoadingDots()
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = "条状加载") {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                PLoadingBars()
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = "圆形加载") {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                PLoadingCircle()
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = "跳动加载") {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                PLoadingBounce()
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = "所有样式") {
            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PLoadingDots()
                PLoadingBars()
                PLoadingCircle()
                PLoadingBounce()
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
// 默认加载
PLoading()

// 点状加载
PLoadingDots()

// 条状加载
PLoadingBars()

// 圆形加载
PLoadingCircle()

// 跳动加载
PLoadingBounce()
            """.trimIndent()
        )
    }
}
