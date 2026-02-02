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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.components.CodeBlock
import xyz.junerver.compose.palette.components.slider.PSlider

@Composable
fun SliderDemo() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Text(
            text = "Slider",
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = "滑块组件",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        DemoSection(title = "基础用法") {
            var value1 by remember { mutableFloatStateOf(50f) }
            PSlider(
                value = value1,
                onChange = { value1 = it }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = "自定义范围") {
            var value2 by remember { mutableFloatStateOf(25f) }
            PSlider(
                value = value2,
                onChange = { value2 = it },
                range = 0f..50f
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = "步进值") {
            var value3 by remember { mutableFloatStateOf(10f) }
            PSlider(
                value = value3,
                onChange = { value3 = it },
                step = 10
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = "自定义格式化") {
            var value4 by remember { mutableFloatStateOf(50f) }
            PSlider(
                value = value4,
                onChange = { value4 = it },
                formatter = { "${it.toInt()}%" }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = "禁用状态") {
            PSlider(
                value = 30f,
                onChange = {},
                disabled = true
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "代码示例",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        CodeBlock(
            code = """
var value by remember { mutableFloatStateOf(50f) }
PSlider(
    value = value,
    onChange = { value = it },
    range = 0f..100f,
    step = 1
)
            """.trimIndent()
        )
    }
}
