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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.components.CodeBlock
import xyz.junerver.compose.palette.components.switch.PSwitch

@Composable
fun SwitchDemo() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Text(
            text = "Switch",
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = "开关组件",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        DemoSection(title = "基础用法") {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                var checked1 by remember { mutableStateOf(false) }
                PSwitch(
                    checked = checked1,
                    onChange = { checked1 = it }
                )
                Text(
                    text = "状态: ${if (checked1) "开启" else "关闭"}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = "默认开启") {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                var checked2 by remember { mutableStateOf(true) }
                PSwitch(
                    checked = checked2,
                    onChange = { checked2 = it }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = "禁用状态") {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                PSwitch(checked = false, onChange = null, disabled = true)
                PSwitch(checked = true, onChange = null, disabled = true)
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
PSwitch(
    checked = checked,
    onChange = { checked = it }
)
            """.trimIndent()
        )
    }
}
