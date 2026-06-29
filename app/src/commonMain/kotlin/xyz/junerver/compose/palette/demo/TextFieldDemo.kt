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
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.Language
import xyz.junerver.compose.palette.LocalLanguage
import xyz.junerver.compose.palette.components.CodeBlock
import xyz.junerver.compose.palette.components.text.PText
import xyz.junerver.compose.palette.components.textfield.BorderTextField

@Composable
fun TextFieldDemo() {
    val text = textFieldDemoText()

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
    ) {
        PText(
            text = text.title,
            style = MaterialTheme.typography.headlineMedium,
        )
        PText(
            text = text.subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(modifier = Modifier.height(32.dp))

        DemoSection(title = text.basicSectionTitle) {
            var inputValue by remember { mutableStateOf("") }
            BorderTextField(
                value = inputValue,
                onValueChange = { inputValue = it },
                placeholder = text.placeholderUsername,
                leadingIcon = {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                },
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = text.validationSectionTitle) {
            var email by remember { mutableStateOf("") }
            var password by remember { mutableStateOf("") }

            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                BorderTextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = text.placeholderEmail,
                    keyboardOptions =
                        androidx.compose.foundation.text.KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                        ),
                    leadingIcon = {
                        Icon(
                            Icons.Default.Email,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    },
                )

                BorderTextField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = text.placeholderPassword,
                    keyboardOptions =
                        androidx.compose.foundation.text.KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                        ),
                    leadingIcon = {
                        Icon(
                            Icons.Default.Lock,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    },
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = text.colorSectionTitle) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                var text1 by remember { mutableStateOf("") }
                var text2 by remember { mutableStateOf("") }

                BorderTextField(
                    value = text1,
                    onValueChange = { text1 = it },
                    placeholder = text.blueBorderPlaceholder,
                    status = xyz.junerver.compose.palette.core.spec.ComponentStatus.Default,
                )

                BorderTextField(
                    value = text2,
                    onValueChange = { text2 = it },
                    placeholder = text.redBorderPlaceholder,
                    status = xyz.junerver.compose.palette.core.spec.ComponentStatus.Error,
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        PText(
            text = text.codeTitle,
            style = MaterialTheme.typography.titleMedium,
        )

        Spacer(modifier = Modifier.height(16.dp))

        CodeBlock(
            code = text.codeBlock,
        )
    }
}

@Composable
@ReadOnlyComposable
private fun textFieldDemoText(): TextFieldDemoText =
    when (LocalLanguage.current) {
        Language.ZH_CN ->
            TextFieldDemoText(
                title = "BorderTextField",
                subtitle = "带边框和图标的输入框组件",
                basicSectionTitle = "基础用法",
                placeholderUsername = "请输入用户名",
                validationSectionTitle = "带图标和验证",
                placeholderEmail = "请输入邮箱",
                placeholderPassword = "请输入密码",
                colorSectionTitle = "不同颜色和尺寸",
                blueBorderPlaceholder = "蓝色边框",
                redBorderPlaceholder = "红色边框",
                codeTitle = "代码示例",
                codeBlock =
                    """
                    BorderTextField(
                        value = text,
                        onValueChange = { text = it },
                        placeholder = "请输入用户名",
                        leadingIcon = {
                            Icon(Icons.Default.Person, contentDescription = null)
                        }
                    )
                    """.trimIndent(),
            )

        Language.EN_US ->
            TextFieldDemoText(
                title = "BorderTextField",
                subtitle = "A text field component with border and icons.",
                basicSectionTitle = "Basic Usage",
                placeholderUsername = "Enter username",
                validationSectionTitle = "With Icon and Validation",
                placeholderEmail = "Enter email",
                placeholderPassword = "Enter password",
                colorSectionTitle = "Different Colors and Sizes",
                blueBorderPlaceholder = "Blue border",
                redBorderPlaceholder = "Red border",
                codeTitle = "Code Example",
                codeBlock =
                    """
                    BorderTextField(
                        value = text,
                        onValueChange = { text = it },
                        placeholder = "Enter username",
                        leadingIcon = {
                            Icon(Icons.Default.Person, contentDescription = null)
                        }
                    )
                    """.trimIndent(),
            )
    }

private data class TextFieldDemoText(
    val title: String,
    val subtitle: String,
    val basicSectionTitle: String,
    val placeholderUsername: String,
    val validationSectionTitle: String,
    val placeholderEmail: String,
    val placeholderPassword: String,
    val colorSectionTitle: String,
    val blueBorderPlaceholder: String,
    val redBorderPlaceholder: String,
    val codeTitle: String,
    val codeBlock: String,
)
