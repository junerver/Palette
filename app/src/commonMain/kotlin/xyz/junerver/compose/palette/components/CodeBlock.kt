package xyz.junerver.compose.palette.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import xyz.junerver.compose.palette.components.code.PCodeBlock

@Composable
fun CodeBlock(
    code: String,
    modifier: Modifier = Modifier,
    language: String = "kotlin",
) {
    PCodeBlock(
        code = code,
        modifier = modifier,
        language = language,
    )
}
