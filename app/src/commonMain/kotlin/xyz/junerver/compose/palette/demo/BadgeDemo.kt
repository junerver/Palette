package xyz.junerver.compose.palette.demo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import xyz.junerver.compose.palette.components.text.PText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.Language
import xyz.junerver.compose.palette.LocalLanguage
import xyz.junerver.compose.palette.components.badge.PBadge
import xyz.junerver.compose.palette.components.CodeBlock
import xyz.junerver.compose.palette.ui.theme.Error
import xyz.junerver.compose.palette.ui.theme.Primary
import xyz.junerver.compose.palette.ui.theme.Success

@Composable
fun BadgeDemo() {
    val text = badgeDemoText()

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
        PText(
            text = text.subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        DemoSection(title = text.basicSectionTitle) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(32.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PBadge(
                    holder = {
                        Icon(
                            Icons.Default.Notifications,
                            contentDescription = null,
                            modifier = Modifier.size(32.dp),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                )

                PBadge(
                    content = "5",
                    holder = {
                        Icon(
                            Icons.Default.Email,
                            contentDescription = null,
                            modifier = Modifier.size(32.dp),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                )

                PBadge(
                    content = "99+",
                    holder = {
                        Icon(
                            Icons.Default.ShoppingCart,
                            contentDescription = null,
                            modifier = Modifier.size(32.dp),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = text.colorSectionTitle) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(32.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PBadge(
                    content = "3",
                    color = Error,
                    holder = {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                        )
                    }
                )

                PBadge(
                    content = text.newBadge,
                    color = Primary,
                    holder = {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                        )
                    }
                )

                PBadge(
                    content = "OK",
                    color = Success,
                    holder = {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = text.positionSectionTitle) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(32.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PBadge(
                    content = "1",
                    alignment = Alignment.TopStart,
                    holder = {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                        )
                    }
                )

                PBadge(
                    content = "2",
                    alignment = Alignment.TopCenter,
                    holder = {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                        )
                    }
                )

                PBadge(
                    content = "3",
                    alignment = Alignment.Center,
                    holder = {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                        )
                    }
                )

                PBadge(
                    content = "4",
                    alignment = Alignment.BottomEnd,
                    holder = {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        PText(
            text = text.codeTitle,
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        CodeBlock(
            code = text.codeBlock
        )
    }
}

@Composable
@ReadOnlyComposable
private fun badgeDemoText(): BadgeDemoText = when (LocalLanguage.current) {
    Language.ZH_CN -> BadgeDemoText(
        title = "PBadge",
        subtitle = "可自定义位置和内容的徽章组件",
        basicSectionTitle = "基础用法",
        colorSectionTitle = "不同颜色",
        positionSectionTitle = "不同位置",
        newBadge = "新",
        codeTitle = "代码示例",
        codeBlock = """
PBadge(
    content = "5",
    color = Color.Red,
    alignment = Alignment.TopEnd,
    holder = {
        Icon(
            Icons.Default.Notifications,
            contentDescription = null,
            modifier = Modifier.size(32.dp)
        )
    }
)
        """.trimIndent(),
    )

    Language.EN_US -> BadgeDemoText(
        title = "PBadge",
        subtitle = "A badge component with customizable position and content.",
        basicSectionTitle = "Basic Usage",
        colorSectionTitle = "Colors",
        positionSectionTitle = "Positions",
        newBadge = "New",
        codeTitle = "Code Example",
        codeBlock = """
PBadge(
    content = "5",
    color = Color.Red,
    alignment = Alignment.TopEnd,
    holder = {
        Icon(
            Icons.Default.Notifications,
            contentDescription = null,
            modifier = Modifier.size(32.dp)
        )
    }
)
        """.trimIndent(),
    )
}

private data class BadgeDemoText(
    val title: String,
    val subtitle: String,
    val basicSectionTitle: String,
    val colorSectionTitle: String,
    val positionSectionTitle: String,
    val newBadge: String,
    val codeTitle: String,
    val codeBlock: String,
)
