package xyz.junerver.compose.palette.demo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.components.textfield.BorderTextField
import xyz.junerver.compose.palette.components.textfield.TextFieldDefaults
import xyz.junerver.compose.palette.foundation.layout.CenterVerticallyRow
import xyz.junerver.compose.palette.core.util.noRippleClickable
import xyz.junerver.compose.palette.components.CodeBlock
import xyz.junerver.compose.palette.ui.theme.Error
import xyz.junerver.compose.palette.ui.theme.Primary

@Composable
fun TextFieldDemo() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Text(
            text = "BorderTextField",
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = "带边框和图标的输入框组件",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        DemoSection(title = "基础用法") {
            var text by remember { mutableStateOf("") }
            BorderTextField(
                value = text,
                onValueChange = { text = it },
                hint = "请输入用户名",
                leadingIcon = {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = "密码输入") {
            var password by remember { mutableStateOf("") }
            var visible by remember { mutableStateOf(false) }

            BorderTextField(
                value = password,
                onValueChange = { password = it },
                hint = "请输入密码",
                keyboardType = KeyboardType.Password,
                isPassword = true,
                passwordVisible = visible,
                leadingIcon = {
                    Icon(
                        Icons.Default.Lock,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                trailingIcon = {
                    Icon(
                        if (visible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if (visible) "隐藏" else "显示",
                        modifier = Modifier
                            .size(20.dp)
                            .noRippleClickable { visible = !visible },
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = "邮箱输入") {
            var email by remember { mutableStateOf("") }
            BorderTextField(
                value = email,
                onValueChange = { email = it },
                hint = "请输入邮箱",
                keyboardType = KeyboardType.Email,
                leadingIcon = {
                    Icon(
                        Icons.Default.Email,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = "不同颜色和尺寸") {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                var text1 by remember { mutableStateOf("") }
                var text2 by remember { mutableStateOf("") }

                BorderTextField(
                    value = text1,
                    onValueChange = { text1 = it },
                    hint = "蓝色边框",
                    colors = TextFieldDefaults.colors(borderColor = Primary),
                    height = 36.dp,
                    width = 280.dp
                )

                BorderTextField(
                    value = text2,
                    onValueChange = { text2 = it },
                    hint = "红色边框",
                    colors = TextFieldDefaults.colors(borderColor = Error),
                    height = 36.dp,
                    width = 280.dp
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
var text by remember { mutableStateOf("") }

BorderTextField(
    value = text,
    onValueChange = { text = it },
    hint = "请输入内容",
    icon = {
        Icon(Icons.Default.Person, null)
    }
)
            """.trimIndent()
        )
    }
}
