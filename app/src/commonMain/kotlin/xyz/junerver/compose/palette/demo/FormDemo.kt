package xyz.junerver.compose.palette.demo

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import xyz.junerver.compose.palette.components.text.PText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.hooks.useform.Required
import xyz.junerver.compose.hooks.useform.Email
import xyz.junerver.compose.palette.Language
import xyz.junerver.compose.palette.LocalLanguage
import xyz.junerver.compose.palette.components.form.PFormTextField

@Composable
fun FormDemo() {
    val text = formDemoText()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        PText(text.title, style = MaterialTheme.typography.headlineMedium)
        PText(text.subtitle, style = MaterialTheme.typography.bodyMedium)
        
        Spacer(modifier = Modifier.height(32.dp))
        
        DemoSection(title = text.basicSectionTitle) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 直接使用 Form composable
                xyz.junerver.compose.hooks.useform.Form(
                    onSubmit = { values ->
                        println("Form submitted: $values")
                    }
                ) {
                    PFormTextField(
                        name = "email",
                        label = text.emailLabel,
                        placeholder = text.emailPlaceholder,
                        help = text.emailHelp,
                        validators = arrayOf(Required(), Email())
                    )
                    
                    PFormTextField(
                        name = "password",
                        label = text.passwordLabel,
                        placeholder = text.passwordPlaceholder,
                        validators = arrayOf(Required())
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Button(
                        onClick = {
                            // Form will handle submission via onSubmit callback
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text.submitText)
                    }
                }
            }
        }
    }
}

@Composable
@ReadOnlyComposable
private fun formDemoText(): FormDemoText = when (LocalLanguage.current) {
    Language.ZH_CN -> FormDemoText(
        title = "Form",
        subtitle = "表单组件，基于 compose-hooks 的无头 Form 基础设施",
        basicSectionTitle = "基础用法",
        emailLabel = "Email",
        emailPlaceholder = "Enter your email",
        emailHelp = "We'll never share your email",
        passwordLabel = "Password",
        passwordPlaceholder = "Enter your password",
        submitText = "Submit",
    )

    Language.EN_US -> FormDemoText(
        title = "Form",
        subtitle = "Form component built on compose-hooks headless form infrastructure.",
        basicSectionTitle = "Basic Usage",
        emailLabel = "Email",
        emailPlaceholder = "Enter your email",
        emailHelp = "We'll never share your email",
        passwordLabel = "Password",
        passwordPlaceholder = "Enter your password",
        submitText = "Submit",
    )
}

private data class FormDemoText(
    val title: String,
    val subtitle: String,
    val basicSectionTitle: String,
    val emailLabel: String,
    val emailPlaceholder: String,
    val emailHelp: String,
    val passwordLabel: String,
    val passwordPlaceholder: String,
    val submitText: String,
)
