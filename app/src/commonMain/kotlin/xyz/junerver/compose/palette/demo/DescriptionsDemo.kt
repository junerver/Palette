package xyz.junerver.compose.palette.demo

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import xyz.junerver.compose.palette.components.text.PText
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.components.descriptions.PDescriptions
import xyz.junerver.compose.palette.components.descriptions.DescriptionItem

@Composable
fun DescriptionsDemo() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        PText(
            text = "PDescriptions 描述列表",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        PText(
            text = "用于展示键值对信息的组件",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(24.dp))

        val userInfo = listOf(
            DescriptionItem("用户名", "张三"),
            DescriptionItem("手机号", "138****8888"),
            DescriptionItem("邮箱", "zhangsan@example.com"),
            DescriptionItem("地址", "北京市朝阳区某某街道123号")
        )

        DemoSection(title = "基础用法") {
            PDescriptions(
                items = userInfo,
                column = 1
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        DemoSection(title = "多列布局") {
            PDescriptions(
                items = userInfo,
                column = 2
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        DemoSection(title = "带边框") {
            PDescriptions(
                items = userInfo,
                column = 1,
                bordered = true
            )
        }
    }
}
