package xyz.junerver.compose.palette.demo

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.badge.PBadge
import xyz.junerver.compose.palette.components.CodeBlock
import xyz.junerver.compose.palette.ui.theme.Error
import xyz.junerver.compose.palette.ui.theme.Primary
import xyz.junerver.compose.palette.ui.theme.Success
import xyz.junerver.compose.palette.ui.theme.Warning

@Composable
fun BadgeDemo() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Text(
            text = "PBadge",
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = "可自定义位置和内容的徽章组件",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        DemoSection(title = "基础用法") {
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

        DemoSection(title = "不同颜色") {
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
                    content = "新",
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

                PBadge(
                    content = "!",
                    color = Warning,
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

        DemoSection(title = "不同位置") {
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
                    alignment = Alignment.TopEnd,
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
                    alignment = Alignment.BottomStart,
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

        Text(
            text = "代码示例",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        CodeBlock(
            code = """
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
            """.trimIndent()
        )
    }
}
