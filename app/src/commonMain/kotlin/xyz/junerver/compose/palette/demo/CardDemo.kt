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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.components.CodeBlock
import xyz.junerver.compose.palette.components.card.CardVariant
import xyz.junerver.compose.palette.components.card.PCard

@Composable
fun CardDemo() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Text(
            text = "Card",
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = "卡片组件",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        DemoSection(title = "Elevated Card (Default)") {
            PCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Elevated Card",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "这是默认的卡片样式，具有阴影效果，适用于需要与背景分离的内容。",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = "Filled Card") {
            PCard(
                modifier = Modifier.fillMaxWidth(),
                variant = CardVariant.Filled
            ) {
                Text(
                    text = "Filled Card",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "填充样式的卡片，使用表面颜色填充，视觉层级较低。",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = "Outlined Card") {
            PCard(
                modifier = Modifier.fillMaxWidth(),
                variant = CardVariant.Outlined
            ) {
                Text(
                    text = "Outlined Card",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "描边样式的卡片，带有边框但没有阴影，适用于边界清晰的内容区域。",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = "Clickable Card") {
            PCard(
                modifier = Modifier.fillMaxWidth(),
                onClick = { /* Handle click */ }
            ) {
                Text(
                    text = "Click Me",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "这是一个可点击的卡片，具有点击涟漪效果。",
                    style = MaterialTheme.typography.bodyMedium
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
PCard(
    modifier = Modifier.fillMaxWidth(),
    variant = CardVariant.Elevated,
    onClick = { /* optional */ }
) {
    Text(text = "Card Title")
    Text(text = "Card content goes here...")
}
            """.trimIndent()
        )
    }
}
