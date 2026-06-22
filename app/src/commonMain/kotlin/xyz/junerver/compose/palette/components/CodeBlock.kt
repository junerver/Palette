package xyz.junerver.compose.palette.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import xyz.junerver.compose.palette.components.markdown.PMarkdownViewer

@Composable
fun CodeBlock(
    code: String,
    modifier: Modifier = Modifier,
) {
    PMarkdownViewer(
        markdown =
            """
            ```kotlin
            ${code.trimIndent()}
            ```
            """.trimIndent(),
        modifier = modifier,
    )
}
