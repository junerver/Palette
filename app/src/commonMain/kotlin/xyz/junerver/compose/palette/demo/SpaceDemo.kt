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
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.Language
import xyz.junerver.compose.palette.LocalLanguage
import xyz.junerver.compose.palette.components.CodeBlock
import xyz.junerver.compose.palette.components.space.PSpace
import xyz.junerver.compose.palette.components.space.SpaceDirection
import xyz.junerver.compose.palette.components.text.PText

@Composable
fun SpaceDemo() {
    val text = spaceDemoText()

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

        DemoSection(title = text.horizontalSectionTitle) {
            PSpace {
                repeat(4) {
                    SpaceBox()
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = text.verticalSectionTitle) {
            PSpace(direction = SpaceDirection.Vertical) {
                repeat(3) {
                    SpaceBox()
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = text.sizeSectionTitle) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                PSpace(size = 8.dp) {
                    repeat(3) {
                        SpaceBox()
                    }
                }
                PSpace(size = 16.dp) {
                    repeat(3) {
                        SpaceBox()
                    }
                }
                PSpace(size = 24.dp) {
                    repeat(3) {
                        SpaceBox()
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = text.wrapSectionTitle) {
            PSpace(wrap = true, size = 8.dp) {
                repeat(8) {
                    SpaceBox()
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
private fun SpaceBox() {
    Box(
        modifier =
            Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.primaryContainer),
    )
}

@Composable
@ReadOnlyComposable
private fun spaceDemoText(): SpaceDemoText =
    when (LocalLanguage.current) {
        Language.ZH_CN ->
            SpaceDemoText(
                title = "PSpace",
                subtitle = "间距组件",
                horizontalSectionTitle = "水平间距",
                verticalSectionTitle = "垂直间距",
                sizeSectionTitle = "不同尺寸",
                wrapSectionTitle = "自动换行",
                codeTitle = "代码示例",
                codeBlock =
                    """
                    PSpace(size = 12.dp) {
                        Item()
                        Item()
                        Item()
                    }
                    PSpace(direction = SpaceDirection.Vertical) {
                        Item()
                        Item()
                    }
                    """.trimIndent(),
            )

        Language.EN_US ->
            SpaceDemoText(
                title = "PSpace",
                subtitle = "Spacing component.",
                horizontalSectionTitle = "Horizontal Space",
                verticalSectionTitle = "Vertical Space",
                sizeSectionTitle = "Different Sizes",
                wrapSectionTitle = "Wrap Mode",
                codeTitle = "Code Example",
                codeBlock =
                    """
                    PSpace(size = 12.dp) {
                        Item()
                        Item()
                        Item()
                    }
                    PSpace(direction = SpaceDirection.Vertical) {
                        Item()
                        Item()
                    }
                    """.trimIndent(),
            )
    }

private data class SpaceDemoText(
    val title: String,
    val subtitle: String,
    val horizontalSectionTitle: String,
    val verticalSectionTitle: String,
    val sizeSectionTitle: String,
    val wrapSectionTitle: String,
    val codeTitle: String,
    val codeBlock: String,
)
