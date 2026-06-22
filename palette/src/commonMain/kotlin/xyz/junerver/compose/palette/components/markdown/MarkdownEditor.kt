package xyz.junerver.compose.palette.components.markdown

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import xyz.junerver.compose.palette.components.textfield.TextArea
import xyz.junerver.compose.palette.core.spec.ComponentSize
import xyz.junerver.compose.palette.core.theme.PaletteTheme

@Composable
fun PMarkdownEditor(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    showPreview: Boolean = true,
    enabled: Boolean = true,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(MarkdownDefaults.blockSpacing()),
    ) {
        TextArea(
            value = value,
            onValueChange = onValueChange,
            enabled = enabled,
            size = ComponentSize.Medium,
            placeholder = placeholder,
            minLines = 8,
            maxLines = 18,
            modifier = Modifier.defaultMinSize(minHeight = MarkdownDefaults.editorMinHeight()),
        )

        if (showPreview) {
            Text(
                text = "Preview",
                color = PaletteTheme.colors.textSecondary,
                style = PaletteTheme.typography.label,
            )
            PMarkdownViewer(markdown = value)
        }
    }
}
