package xyz.junerver.compose.palette.components.markdown

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import xyz.junerver.compose.palette.components.code.PCodeBlock
import xyz.junerver.compose.palette.components.mermaid.PMermaidDiagram
import xyz.junerver.compose.palette.core.theme.PaletteTheme
import xyz.junerver.compose.palette.markdown.MarkdownParser
import xyz.junerver.compose.palette.markdown.MarkdownRenderBlock
import xyz.junerver.compose.palette.markdown.MarkdownRenderModel
import xyz.junerver.compose.palette.markdown.MarkdownRenderer

@Composable
fun PMarkdownViewer(
    markdown: String,
    modifier: Modifier = Modifier,
    renderModel: MarkdownRenderModel = MarkdownRenderer.toRenderModel(MarkdownParser.parse(markdown)),
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(MarkdownDefaults.blockSpacing()),
    ) {
        renderModel.blocks.forEach { block ->
            when (block) {
                is MarkdownRenderBlock.Heading ->
                    Text(
                        text = block.text,
                        color = PaletteTheme.colors.textPrimary,
                        style =
                            when (block.level) {
                                1 -> PaletteTheme.typography.title.copy(fontSize = 24.sp, fontWeight = FontWeight.SemiBold)
                                2 -> PaletteTheme.typography.title.copy(fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
                                else -> PaletteTheme.typography.body.copy(fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                            },
                    )

                is MarkdownRenderBlock.Paragraph ->
                    Text(
                        text = block.text,
                        color = PaletteTheme.colors.textPrimary,
                        style = PaletteTheme.typography.body,
                    )

                is MarkdownRenderBlock.ListBlock ->
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        block.items.forEachIndexed { index, item ->
                            Row(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = if (block.ordered) "${index + 1}." else "-",
                                    color = PaletteTheme.colors.textSecondary,
                                    modifier = Modifier.padding(end = 8.dp),
                                )
                                Text(
                                    text = item,
                                    color = PaletteTheme.colors.textPrimary,
                                    style = PaletteTheme.typography.body,
                                )
                            }
                        }
                    }

                is MarkdownRenderBlock.Code ->
                    PCodeBlock(
                        code = block.highlighted.tokens.joinToString("\n") { line -> line.joinToString("") { it.text } },
                        language = block.language,
                        highlightedCode = block.highlighted,
                    )

                is MarkdownRenderBlock.Mermaid -> PMermaidDiagram(source = block.source)
                MarkdownRenderBlock.ThematicBreak -> Divider(color = PaletteTheme.colors.border)
            }
        }
    }
}
