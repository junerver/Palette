package xyz.junerver.compose.palette.demo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.Language
import xyz.junerver.compose.palette.LocalLanguage
import xyz.junerver.compose.palette.components.CodeBlock
import xyz.junerver.compose.palette.components.grid.PRow
import xyz.junerver.compose.palette.components.text.PText

@Composable
fun GridDemo() {
    val text = gridDemoText()

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
            PRow {
                PCol(span = 12) {
                    GridCell(text = "span=12", color = MaterialTheme.colorScheme.primaryContainer)
                }
                PCol(span = 12) {
                    GridCell(text = "span=12", color = MaterialTheme.colorScheme.secondaryContainer)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = text.spanSectionTitle) {
            Column {
                PRow {
                    PCol(span = 8) {
                        GridCell(text = "span=8", color = MaterialTheme.colorScheme.primaryContainer)
                    }
                    PCol(span = 8) {
                        GridCell(text = "span=8", color = MaterialTheme.colorScheme.secondaryContainer)
                    }
                    PCol(span = 8) {
                        GridCell(text = "span=8", color = MaterialTheme.colorScheme.tertiaryContainer)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                PRow {
                    PCol(span = 6) {
                        GridCell(text = "span=6", color = MaterialTheme.colorScheme.primaryContainer)
                    }
                    PCol(span = 6) {
                        GridCell(text = "span=6", color = MaterialTheme.colorScheme.secondaryContainer)
                    }
                    PCol(span = 12) {
                        GridCell(text = "span=12", color = MaterialTheme.colorScheme.tertiaryContainer)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = text.offsetSectionTitle) {
            PRow {
                PCol(span = 8) {
                    GridCell(text = "span=8", color = MaterialTheme.colorScheme.primaryContainer)
                }
                PCol(span = 8, offset = 8) {
                    GridCell(text = "span=8, offset=8", color = MaterialTheme.colorScheme.secondaryContainer)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = text.gutterSectionTitle) {
            PRow(gutter = 16.dp) {
                PCol(span = 8) {
                    GridCell(text = "gutter=16", color = MaterialTheme.colorScheme.primaryContainer)
                }
                PCol(span = 8) {
                    GridCell(text = "gutter=16", color = MaterialTheme.colorScheme.secondaryContainer)
                }
                PCol(span = 8) {
                    GridCell(text = "gutter=16", color = MaterialTheme.colorScheme.tertiaryContainer)
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
private fun GridCell(
    text: String,
    color: androidx.compose.ui.graphics.Color,
) {
    Box(
        modifier =
            Modifier
                .height(48.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(color)
                .padding(horizontal = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        PText(
            text = text,
            style = MaterialTheme.typography.bodySmall,
        )
    }
}

@Composable
@ReadOnlyComposable
private fun gridDemoText(): GridDemoText =
    when (LocalLanguage.current) {
        Language.ZH_CN ->
            GridDemoText(
                title = "PRow / PCol",
                subtitle = "栅格布局组件",
                basicSectionTitle = "基础用法",
                spanSectionTitle = "不同列宽",
                offsetSectionTitle = "列偏移",
                gutterSectionTitle = "列间距",
                codeTitle = "代码示例",
                codeBlock =
                    """
                    PRow(gutter = 16.dp) {
                        PCol(span = 8) {
                            Text("span=8")
                        }
                        PCol(span = 8, offset = 8) {
                            Text("span=8, offset=8")
                        }
                    }
                    """.trimIndent(),
            )

        Language.EN_US ->
            GridDemoText(
                title = "PRow / PCol",
                subtitle = "Grid layout component.",
                basicSectionTitle = "Basic Usage",
                spanSectionTitle = "Different Spans",
                offsetSectionTitle = "Column Offset",
                gutterSectionTitle = "Column Gutter",
                codeTitle = "Code Example",
                codeBlock =
                    """
                    PRow(gutter = 16.dp) {
                        PCol(span = 8) {
                            Text("span=8")
                        }
                        PCol(span = 8, offset = 8) {
                            Text("span=8, offset=8")
                        }
                    }
                    """.trimIndent(),
            )
    }

private data class GridDemoText(
    val title: String,
    val subtitle: String,
    val basicSectionTitle: String,
    val spanSectionTitle: String,
    val offsetSectionTitle: String,
    val gutterSectionTitle: String,
    val codeTitle: String,
    val codeBlock: String,
)
