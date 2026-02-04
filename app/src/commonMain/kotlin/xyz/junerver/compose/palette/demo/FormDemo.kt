package xyz.junerver.compose.palette.demo

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import xyz.junerver.compose.palette.components.text.PText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.hooks.useform.Required
import xyz.junerver.compose.hooks.useform.Email
import xyz.junerver.compose.palette.components.form.FormLayout
import xyz.junerver.compose.palette.components.form.PFormTextField

@Composable
fun FormDemo() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        PText("Form", style = MaterialTheme.typography.headlineMedium)
        PText("表单组件，基于 compose-hooks 的无头 Form 基础设施", style = MaterialTheme.typography.bodyMedium)
        
        Spacer(modifier = Modifier.height(32.dp))
        
        DemoSection(title = "基础用法") {
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
                        label = "Email",
                        placeholder = "Enter your email",
                        help = "We'll never share your email",
                        validators = arrayOf(Required(), Email())
                    )
                    
                    PFormTextField(
                        name = "password",
                        label = "Password",
                        placeholder = "Enter your password",
                        validators = arrayOf(Required())
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Button(
                        onClick = {
                            // Form will handle submission via onSubmit callback
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Submit")
                    }
                }
            }
        }
    }
}
