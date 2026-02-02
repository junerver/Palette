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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.components.CodeBlock
import xyz.junerver.compose.palette.components.skeleton.PSkeletonCircle
import xyz.junerver.compose.palette.components.skeleton.PSkeletonSquare
import xyz.junerver.compose.palette.components.skeleton.PSkeletonRectangle
import xyz.junerver.compose.palette.components.skeleton.PSkeletonLine

@Composable
fun SkeletonDemo() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Text(
            text = "Skeleton",
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = "骨架屏组件",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        DemoSection(title = "圆形骨架") {
            PSkeletonCircle(size = 60.dp)
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = "方形骨架") {
            PSkeletonSquare(size = 60.dp)
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = "矩形骨架") {
            PSkeletonRectangle(height = 100.dp)
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = "线条骨架") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                PSkeletonLine(width = 300.dp)
                PSkeletonLine(width = 240.dp)
                PSkeletonLine(width = 180.dp)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = "组合示例 - 用户卡片") {
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

        DemoSection(title = "组合示例 - 文章列表") {
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

        Text(
            text = "代码示例",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        CodeBlock(
            code = """
// 圆形
PSkeletonCircle(size = 60.dp)

// 方形
PSkeletonSquare(size = 60.dp)

// 矩形
PSkeletonRectangle(height = 100.dp)

// 线条
PSkeletonLine(width = 200.dp)
            """.trimIndent()
        )
    }
}
