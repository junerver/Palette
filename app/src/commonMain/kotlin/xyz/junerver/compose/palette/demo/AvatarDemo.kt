package xyz.junerver.compose.palette.demo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.Language
import xyz.junerver.compose.palette.LocalLanguage
import xyz.junerver.compose.palette.components.CodeBlock
import xyz.junerver.compose.palette.components.avatar.AvatarSize
import xyz.junerver.compose.palette.components.avatar.PAvatar
import xyz.junerver.compose.palette.components.text.PText

@Composable
fun AvatarDemo() {
    val text = avatarDemoText()

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

        DemoSection(title = text.sizeSectionTitle) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                PAvatar(size = AvatarSize.Small, text = "S")
                PAvatar(size = AvatarSize.Medium, text = "M")
                PAvatar(size = AvatarSize.Large, text = "L")
                PAvatar(size = AvatarSize.XLarge, text = "XL")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = text.colorSectionTitle) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                PAvatar(text = "U", backgroundColor = Color(0xFF1890FF))
                PAvatar(text = "S", backgroundColor = Color(0xFF52C41A))
                PAvatar(text = "E", backgroundColor = Color(0xFFFAAD14))
                PAvatar(text = "R", backgroundColor = Color(0xFFF5222D))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = text.customSectionTitle) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                PAvatar(
                    content = {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            tint = Color.White,
                        )
                    },
                    backgroundColor = Color(0xFF87D068),
                )
                PAvatar(
                    content = {
                        Icon(
                            Icons.Default.Home,
                            contentDescription = null,
                            tint = Color.White,
                        )
                    },
                    backgroundColor = Color(0xFF1890FF),
                )
                PAvatar(
                    content = {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = null,
                            tint = Color.White,
                        )
                    },
                    backgroundColor = Color(0xFF722ED1),
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
private fun avatarDemoText(): AvatarDemoText =
    when (LocalLanguage.current) {
        Language.ZH_CN ->
            AvatarDemoText(
                title = "PAvatar",
                subtitle = "用来代表用户或事物，支持图片、图标或字符展示",
                sizeSectionTitle = "不同尺寸",
                colorSectionTitle = "不同背景色",
                customSectionTitle = "自定义内容",
                codeTitle = "代码示例",
                codeBlock =
                    """
                    PAvatar(
                        size = AvatarSize.Large,
                        text = "User",
                        backgroundColor = Color(0xFF1890FF)
                    )

                    PAvatar(
                        content = {
                            Icon(Icons.Default.Person, null)
                        },
                        backgroundColor = Color(0xFF87D068)
                    )
                    """.trimIndent(),
            )

        Language.EN_US ->
            AvatarDemoText(
                title = "PAvatar",
                subtitle = "Represents users or entities with image, icon, or text.",
                sizeSectionTitle = "Sizes",
                colorSectionTitle = "Background Colors",
                customSectionTitle = "Custom Content",
                codeTitle = "Code Example",
                codeBlock =
                    """
                    PAvatar(
                        size = AvatarSize.Large,
                        text = "User",
                        backgroundColor = Color(0xFF1890FF)
                    )

                    PAvatar(
                        content = {
                            Icon(Icons.Default.Person, null)
                        },
                        backgroundColor = Color(0xFF87D068)
                    )
                    """.trimIndent(),
            )
    }

private data class AvatarDemoText(
    val title: String,
    val subtitle: String,
    val sizeSectionTitle: String,
    val colorSectionTitle: String,
    val customSectionTitle: String,
    val codeTitle: String,
    val codeBlock: String,
)
