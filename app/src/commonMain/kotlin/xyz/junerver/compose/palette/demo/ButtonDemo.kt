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
import xyz.junerver.compose.palette.components.button.ButtonSize
import xyz.junerver.compose.palette.components.button.ButtonType
import xyz.junerver.compose.palette.components.button.PButton

@Composable
fun ButtonDemo() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Text(
            text = "Button",
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = "按钮组件",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        DemoSection(title = "按钮类型") {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                PButton(text = "Primary Button", type = ButtonType.PRIMARY) {}
                PButton(text = "Danger Button", type = ButtonType.DANGER) {}
                PButton(text = "Plain Button", type = ButtonType.PLAIN) {}
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = "按钮尺寸") {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                PButton(text = "Large Button", size = ButtonSize.LARGE) {}
                PButton(text = "Medium Button", size = ButtonSize.MEDIUM) {}
                PButton(text = "Small Button", size = ButtonSize.SMALL) {}
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = "禁用状态") {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                PButton(text = "Disabled Button", disabled = true) {}
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = "加载状态") {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                var loading by remember { mutableStateOf(false) }
                PButton(
                    text = if (loading) "Loading..." else "Click to Load",
                    loading = loading,
                    onClick = {
                        loading = true
                    }
                )
                PButton(text = "Reset") {
                    loading = false
                }
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
PButton(
    text = "Primary Button",
    type = ButtonType.PRIMARY,
    size = ButtonSize.LARGE,
    onClick = { /* 处理点击 */ }
)
            """.trimIndent()
        )
    }
}