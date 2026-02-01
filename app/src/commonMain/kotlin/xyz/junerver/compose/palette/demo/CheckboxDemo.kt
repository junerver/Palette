package xyz.junerver.compose.palette.demo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.components.CodeBlock
import xyz.junerver.compose.palette.components.checkbox.ColoredCheckBox
import xyz.junerver.compose.palette.foundation.layout.CenterVerticallyRow
import xyz.junerver.compose.palette.ui.theme.TextSecondary

@Composable
fun CheckboxDemo() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Text(
            text = "ColoredCheckBox",
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = "可自定义颜色的复选框",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        DemoSection(title = "基础用法") {
            var checked by remember { mutableStateOf(false) }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                ColoredCheckBox(
                    checked = checked,
                    onCheckedChange = { checked = it },
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "状态: ${if (checked) "已选中" else "未选中"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = "不同颜色") {
            CenterVerticallyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                var c1 by remember { mutableStateOf(true) }
                var c2 by remember { mutableStateOf(false) }
                var c3 by remember { mutableStateOf(true) }

                ColoredCheckBox(checked = c1, onCheckedChange = { c1 = it }, color = Color(0xFF6366F1))
                ColoredCheckBox(checked = c2, onCheckedChange = { c2 = it }, color = Color(0xFF10B981))
                ColoredCheckBox(checked = c3, onCheckedChange = { c3 = it }, color = Color(0xFFEF4444))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = "配合图标使用") {
            CenterVerticallyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                var checked by remember { mutableStateOf(false) }
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = if (checked) MaterialTheme.colorScheme.primary else TextSecondary,
                    modifier = Modifier.size(20.dp)
                )
                ColoredCheckBox(checked = checked, onCheckedChange = { checked = it })
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
var checked by remember { mutableStateOf(false) }
ColoredCheckBox(
    checked = checked,
    onCheckedChange = { checked = it },
    color = Color(0xFF6366F1)
)
            """.trimIndent()
        )
    }
}
