package xyz.junerver.compose.palette.demo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.Language
import xyz.junerver.compose.palette.LocalLanguage
import xyz.junerver.compose.palette.components.CodeBlock
import xyz.junerver.compose.palette.components.text.PText
import xyz.junerver.compose.palette.foundation.layout.CenterVerticallyRow

@Composable
fun RowLayoutDemo() {
    val text = rowLayoutDemoText()

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
            CenterVerticallyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Icon(
                    Icons.Default.Home,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.primary,
                )
                PText(
                    text = text.homeText,
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = text.multiIconSectionTitle) {
            CenterVerticallyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Icon(
                    Icons.Default.Home,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.primary,
                )
                Icon(
                    Icons.Default.Favorite,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.error,
                )
                Icon(
                    Icons.Default.Settings,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.secondary,
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = text.mixSectionTitle) {
            CenterVerticallyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Box(
                    modifier =
                        Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer),
                )
                Column {
                    PText(
                        text = text.userNameText,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    PText(
                        text = text.descriptionText,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
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
private fun rowLayoutDemoText(): RowLayoutDemoText =
    when (LocalLanguage.current) {
        Language.ZH_CN ->
            RowLayoutDemoText(
                title = "CenterVerticallyRow",
                subtitle = "垂直居中的行布局组件",
                basicSectionTitle = "基础用法",
                homeText = "首页",
                multiIconSectionTitle = "多图标组合",
                mixSectionTitle = "与其他组件配合",
                userNameText = "用户名",
                descriptionText = "描述信息",
                codeTitle = "代码示例",
                codeBlock =
                    """
                    CenterVerticallyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(Icons.Default.Home, null)
                        Text("首页")
                    }
                    """.trimIndent(),
            )

        Language.EN_US ->
            RowLayoutDemoText(
                title = "CenterVerticallyRow",
                subtitle = "A vertically centered row layout component.",
                basicSectionTitle = "Basic Usage",
                homeText = "Home",
                multiIconSectionTitle = "Multi-Icon Combination",
                mixSectionTitle = "Works with Other Components",
                userNameText = "Username",
                descriptionText = "Description",
                codeTitle = "Code Example",
                codeBlock =
                    """
                    CenterVerticallyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(Icons.Default.Home, null)
                        Text("Home")
                    }
                    """.trimIndent(),
            )
    }

private data class RowLayoutDemoText(
    val title: String,
    val subtitle: String,
    val basicSectionTitle: String,
    val homeText: String,
    val multiIconSectionTitle: String,
    val mixSectionTitle: String,
    val userNameText: String,
    val descriptionText: String,
    val codeTitle: String,
    val codeBlock: String,
)
