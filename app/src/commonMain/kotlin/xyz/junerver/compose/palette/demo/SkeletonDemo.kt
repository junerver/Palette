package xyz.junerver.compose.palette.demo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import xyz.junerver.compose.palette.Language
import xyz.junerver.compose.palette.LocalLanguage
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.components.CodeBlock
import xyz.junerver.compose.palette.components.text.PText
import xyz.junerver.compose.palette.components.skeleton.PSkeletonCircle
import xyz.junerver.compose.palette.components.skeleton.PSkeletonSquare
import xyz.junerver.compose.palette.components.skeleton.PSkeletonRectangle
import xyz.junerver.compose.palette.components.skeleton.PSkeletonLine

@Composable
fun SkeletonDemo() {
    val text = skeletonDemoText()

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

        DemoSection(title = text.circleSectionTitle) {
            PSkeletonCircle(size = 60.dp)
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = text.squareSectionTitle) {
            PSkeletonSquare(size = 60.dp)
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = text.rectangleSectionTitle) {
            PSkeletonRectangle(height = 100.dp)
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = text.lineSectionTitle) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                PSkeletonLine(width = 300.dp)
                PSkeletonLine(width = 240.dp)
                PSkeletonLine(width = 180.dp)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = text.userCardSectionTitle) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PSkeletonCircle(size = 48.dp)
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PSkeletonLine(width = 180.dp)
                    PSkeletonLine(width = 120.dp)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = text.articleListSectionTitle) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                repeat(3) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        PSkeletonRectangle(height = 120.dp)
                        PSkeletonLine(width = 300.dp)
                        PSkeletonLine(width = 210.dp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        PText(
            text = text.codeTitle,
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        CodeBlock(
            code = text.codeBlock,
        )
    }
}

@Composable
@ReadOnlyComposable
private fun skeletonDemoText(): SkeletonDemoText = when (LocalLanguage.current) {
    Language.ZH_CN -> SkeletonDemoText(
        title = "Skeleton",
        subtitle = "骨架屏组件",
        circleSectionTitle = "圆形骨架",
        squareSectionTitle = "方形骨架",
        rectangleSectionTitle = "矩形骨架",
        lineSectionTitle = "线条骨架",
        userCardSectionTitle = "组合示例 - 用户卡片",
        articleListSectionTitle = "组合示例 - 文章列表",
        codeTitle = "代码示例",
        codeBlock = """
// 圆形
PSkeletonCircle(size = 60.dp)

// 方形
PSkeletonSquare(size = 60.dp)

// 矩形
PSkeletonRectangle(height = 100.dp)

// 线条
PSkeletonLine(width = 200.dp)
        """.trimIndent(),
    )

    Language.EN_US -> SkeletonDemoText(
        title = "Skeleton",
        subtitle = "Skeleton component",
        circleSectionTitle = "Circle Skeleton",
        squareSectionTitle = "Square Skeleton",
        rectangleSectionTitle = "Rectangle Skeleton",
        lineSectionTitle = "Line Skeleton",
        userCardSectionTitle = "Combined Example - User Card",
        articleListSectionTitle = "Combined Example - Article List",
        codeTitle = "Code Example",
        codeBlock = """
// Circle
PSkeletonCircle(size = 60.dp)

// Square
PSkeletonSquare(size = 60.dp)

// Rectangle
PSkeletonRectangle(height = 100.dp)

// Line
PSkeletonLine(width = 200.dp)
        """.trimIndent(),
    )
}

private data class SkeletonDemoText(
    val title: String,
    val subtitle: String,
    val circleSectionTitle: String,
    val squareSectionTitle: String,
    val rectangleSectionTitle: String,
    val lineSectionTitle: String,
    val userCardSectionTitle: String,
    val articleListSectionTitle: String,
    val codeTitle: String,
    val codeBlock: String,
)
