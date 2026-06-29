package xyz.junerver.compose.palette.demo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.Language
import xyz.junerver.compose.palette.LocalLanguage
import xyz.junerver.compose.palette.components.text.PText

@Composable
fun ImageDemo() {
    val text = imageDemoText()

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
        Spacer(modifier = Modifier.height(8.dp))
        PText(
            text = text.subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = text.basicSectionTitle) {
            Box(
                modifier =
                    Modifier
                        .size(200.dp)
                        .background(Color.LightGray),
            ) {
                PText(
                    text.placeholderText,
                    modifier = Modifier.padding(16.dp),
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        PText(
            text = text.noticeText,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.error,
        )
    }
}

@Composable
@ReadOnlyComposable
private fun imageDemoText(): ImageDemoText =
    when (LocalLanguage.current) {
        Language.ZH_CN ->
            ImageDemoText(
                title = "PImage 图片",
                subtitle = "用于展示图片的组件",
                basicSectionTitle = "基础用法",
                placeholderText = "图片占位符",
                noticeText = "注意：PImage 组件需要 Painter 参数，请使用 painterResource 加载图片资源",
            )

        Language.EN_US ->
            ImageDemoText(
                title = "PImage",
                subtitle = "A component for displaying images.",
                basicSectionTitle = "Basic Usage",
                placeholderText = "Image Placeholder",
                noticeText = "Note: PImage requires a Painter parameter. Use painterResource to load image resources.",
            )
    }

private data class ImageDemoText(
    val title: String,
    val subtitle: String,
    val basicSectionTitle: String,
    val placeholderText: String,
    val noticeText: String,
)
