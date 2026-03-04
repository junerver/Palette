package xyz.junerver.compose.palette.demo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import xyz.junerver.compose.palette.components.text.PText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.Language
import xyz.junerver.compose.palette.LocalLanguage
import xyz.junerver.compose.palette.components.carousel.PCarousel

@Composable
fun CarouselDemo() {
    val title = demoText("PCarousel 轮播图", "PCarousel")
    val subtitle = demoText(
        "用于展示轮播内容的组件，支持自动播放、指示器、箭头导航",
        "A carousel component with autoplay, indicators, and arrow navigation."
    )
    val basicSectionTitle = demoText("基础用法 - 带箭头导航", "Basic Usage - With Arrow Navigation")
    val autoPlaySectionTitle = demoText("自动播放 - 每3秒切换", "Autoplay - Switch Every 3 Seconds")
    val customArrowSectionTitle = demoText("自定义箭头样式", "Custom Arrow Style")
    val dragSectionTitle = demoText("无箭头导航 - 支持拖拽", "No Arrow Navigation - Draggable")
    val slidePrefix = demoText("Slide", "Slide")
    val customArrowText = demoText("自定义箭头", "Custom Arrow")
    val previousText = demoText("上一张", "Previous")
    val nextText = demoText("下一张", "Next")
    val autoItems = listOf(
        demoText("图片 1", "Image 1"),
        demoText("图片 2", "Image 2"),
        demoText("图片 3", "Image 3"),
        demoText("图片 4", "Image 4"),
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        PText(
            text = title,
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        PText(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = basicSectionTitle) {
            val colors = listOf(
                Color(0xFFE57373),
                Color(0xFF64B5F6),
                Color(0xFF81C784),
                Color(0xFFFFD54F),
                Color(0xFFBA68C8)
            )
            
            PCarousel(
                items = colors,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(12.dp))
            ) { color ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color),
                    contentAlignment = Alignment.Center
                ) {
                    PText(
                        text = "$slidePrefix ${colors.indexOf(color) + 1}",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = autoPlaySectionTitle) {
            PCarousel(
                items = autoItems,
                autoPlay = true,
                autoPlayInterval = 3000L,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(12.dp))
            ) { item ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            when (autoItems.indexOf(item)) {
                                0 -> Color(0xFF42A5F5)
                                1 -> Color(0xFF66BB6A)
                                2 -> Color(0xFFFF7043)
                                else -> Color(0xFFAB47BC)
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    PText(
                        text = item,
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = customArrowSectionTitle) {
            val colors = listOf(
                Color(0xFFFF6B6B),
                Color(0xFF4ECDC4),
                Color(0xFF45B7D1),
                Color(0xFFFECA57)
            )
            
            PCarousel(
                items = colors,
                prevArrow = { onClick ->
                    IconButton(
                        onClick = onClick,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.9f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowLeft,
                            contentDescription = previousText,
                            tint = Color.Black,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                },
                nextArrow = { onClick ->
                    IconButton(
                        onClick = onClick,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.9f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowRight,
                            contentDescription = nextText,
                            tint = Color.Black,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(12.dp))
            ) { color ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color),
                    contentAlignment = Alignment.Center
                ) {
                    PText(
                        text = customArrowText,
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = dragSectionTitle) {
            val colors = listOf(
                Color(0xFFFF6B6B),
                Color(0xFF4ECDC4),
                Color(0xFF45B7D1)
            )
            
            PCarousel(
                items = colors,
                showArrows = false,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(12.dp))
            ) { color ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color),
                    contentAlignment = Alignment.Center
                ) {
                    PText(
                        text = "$slidePrefix ${colors.indexOf(color) + 1}",
                        style = MaterialTheme.typography.headlineLarge,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
@ReadOnlyComposable
private fun demoText(zh: String, en: String): String = when (LocalLanguage.current) {
    Language.ZH_CN -> zh
    Language.EN_US -> en
}
