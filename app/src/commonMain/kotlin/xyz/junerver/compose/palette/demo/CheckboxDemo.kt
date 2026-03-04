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
import xyz.junerver.compose.palette.components.text.PText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.Language
import xyz.junerver.compose.palette.LocalLanguage
import xyz.junerver.compose.palette.components.CodeBlock
import xyz.junerver.compose.palette.components.checkbox.ColoredCheckBox
import xyz.junerver.compose.palette.foundation.layout.CenterVerticallyRow
import xyz.junerver.compose.palette.ui.theme.TextSecondary

@Composable
fun CheckboxDemo() {
    val title = demoText("ColoredCheckBox", "ColoredCheckBox")
    val subtitle = demoText("可自定义颜色的复选框", "Customizable color checkbox.")
    val basicSectionTitle = demoText("基础用法", "Basic Usage")
    val statusPrefix = demoText("状态: ", "Status: ")
    val checkedText = demoText("已选中", "Checked")
    val uncheckedText = demoText("未选中", "Unchecked")
    val colorSectionTitle = demoText("不同颜色", "Different Colors")
    val iconSectionTitle = demoText("配合图标使用", "With Icon")
    val codeTitle = demoText("代码示例", "Code Example")
    val codeBlock = demoText(
        """
var checked by remember { mutableStateOf(false) }
ColoredCheckBox(
    checked = checked,
    onCheckedChange = { checked = it }
)
        """.trimIndent(),
        """
var checked by remember { mutableStateOf(false) }
ColoredCheckBox(
    checked = checked,
    onCheckedChange = { checked = it }
)
        """.trimIndent(),
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        PText(
            text = title,
            style = MaterialTheme.typography.headlineMedium
        )
        PText(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        DemoSection(title = basicSectionTitle) {
            var checked by remember { mutableStateOf(false) }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                ColoredCheckBox(
                    checked = checked,
                    onCheckedChange = { checked = it }
                )
                Spacer(modifier = Modifier.height(8.dp))
                PText(
                    text = "$statusPrefix${if (checked) checkedText else uncheckedText}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = colorSectionTitle) {
            CenterVerticallyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                var c1 by remember { mutableStateOf(true) }
                var c2 by remember { mutableStateOf(false) }
                var c3 by remember { mutableStateOf(true) }

                ColoredCheckBox(checked = c1, onCheckedChange = { c1 = it })
                ColoredCheckBox(checked = c2, onCheckedChange = { c2 = it })
                ColoredCheckBox(checked = c3, onCheckedChange = { c3 = it })
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = iconSectionTitle) {
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

        PText(
            text = codeTitle,
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        CodeBlock(
            code = codeBlock
        )
    }
}

@Composable
@ReadOnlyComposable
private fun demoText(zh: String, en: String): String = when (LocalLanguage.current) {
    Language.ZH_CN -> zh
    Language.EN_US -> en
}
