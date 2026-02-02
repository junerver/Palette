package xyz.junerver.compose.palette.demo

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.components.CodeBlock
import xyz.junerver.compose.palette.components.rate.PRate

@Composable
fun RateDemo() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Text(
            text = "Rate",
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = "评分组件",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        DemoSection(title = "基础用法") {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                var rating1 by remember { mutableFloatStateOf(3f) }
                PRate(
                    value = rating1,
                    onChange = { rating1 = it }
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "评分: $rating1",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = "半星评分") {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                var rating2 by remember { mutableFloatStateOf(3.5f) }
                PRate(
                    value = rating2,
                    onChange = { rating2 = it },
                    allowHalf = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "评分: $rating2",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = "自定义数量") {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                var rating3 by remember { mutableFloatStateOf(5f) }
                PRate(
                    value = rating3,
                    onChange = { rating3 = it },
                    count = 10
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = "只读模式") {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                PRate(
                    value = 4f,
                    onChange = null
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "代码示例",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        CodeBlock(
            code = """
var rating by remember { mutableFloatStateOf(3f) }
PRate(
    value = rating,
    onChange = { rating = it },
    count = 5,
    allowHalf = true
)
            """.trimIndent()
        )
    }
}
