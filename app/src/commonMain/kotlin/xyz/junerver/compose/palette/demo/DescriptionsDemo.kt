package xyz.junerver.compose.palette.demo

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import xyz.junerver.compose.palette.Language
import xyz.junerver.compose.palette.LocalLanguage
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.components.descriptions.DescriptionItem
import xyz.junerver.compose.palette.components.descriptions.PDescriptions
import xyz.junerver.compose.palette.components.text.PText

@Composable
fun DescriptionsDemo() {
    val text = descriptionsDemoText()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        PText(
            text = text.title,
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        PText(
            text = text.subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(24.dp))

        val userInfo = listOf(
            DescriptionItem(text.userNameLabel, text.userNameValue),
            DescriptionItem(text.phoneLabel, "138****8888"),
            DescriptionItem(text.emailLabel, "zhangsan@example.com"),
            DescriptionItem(text.addressLabel, text.addressValue),
        )

        DemoSection(title = text.basicSectionTitle) {
            PDescriptions(
                items = userInfo,
                column = 1
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        DemoSection(title = text.multiColumnSectionTitle) {
            PDescriptions(
                items = userInfo,
                column = 2
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        DemoSection(title = text.borderedSectionTitle) {
            PDescriptions(
                items = userInfo,
                column = 1,
                bordered = true
            )
        }
    }
}

@Composable
@ReadOnlyComposable
private fun descriptionsDemoText(): DescriptionsDemoText = when (LocalLanguage.current) {
    Language.ZH_CN -> DescriptionsDemoText(
        title = "PDescriptions 描述列表",
        subtitle = "用于展示键值对信息的组件",
        basicSectionTitle = "基础用法",
        multiColumnSectionTitle = "多列布局",
        borderedSectionTitle = "带边框",
        userNameLabel = "用户名",
        userNameValue = "张三",
        phoneLabel = "手机号",
        emailLabel = "邮箱",
        addressLabel = "地址",
        addressValue = "北京市朝阳区某某街道123号",
    )

    Language.EN_US -> DescriptionsDemoText(
        title = "PDescriptions",
        subtitle = "A component for displaying key-value information",
        basicSectionTitle = "Basic Usage",
        multiColumnSectionTitle = "Multi-Column Layout",
        borderedSectionTitle = "Bordered",
        userNameLabel = "Username",
        userNameValue = "Zhang San",
        phoneLabel = "Phone",
        emailLabel = "Email",
        addressLabel = "Address",
        addressValue = "No.123, Chaoyang District, Beijing",
    )
}

private data class DescriptionsDemoText(
    val title: String,
    val subtitle: String,
    val basicSectionTitle: String,
    val multiColumnSectionTitle: String,
    val borderedSectionTitle: String,
    val userNameLabel: String,
    val userNameValue: String,
    val phoneLabel: String,
    val emailLabel: String,
    val addressLabel: String,
    val addressValue: String,
)
