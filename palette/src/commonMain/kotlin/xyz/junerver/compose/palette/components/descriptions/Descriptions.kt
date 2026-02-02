package xyz.junerver.compose.palette.components.descriptions

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.core.theme.PaletteTheme

data class DescriptionItem(
    val label: String,
    val content: String
)

@Composable
fun PDescriptions(
    items: List<DescriptionItem>,
    modifier: Modifier = Modifier,
    column: Int = 1,
    bordered: Boolean = false,
    labelColor: Color = DescriptionsDefaults.labelColor(),
    contentColor: Color = DescriptionsDefaults.contentColor()
) {
    Column(modifier = modifier) {
        items.chunked(column).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                rowItems.forEach { item ->
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .height(DescriptionsDefaults.RowHeight)
                            .padding(DescriptionsDefaults.Padding)
                    ) {
                        Text(
                            text = item.label,
                            color = labelColor,
                            style = PaletteTheme.typography.body,
                            modifier = Modifier.width(DescriptionsDefaults.LabelWidth)
                        )
                        Text(
                            text = item.content,
                            color = contentColor,
                            style = PaletteTheme.typography.body,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
            
            if (bordered) {
                Divider()
            }
        }
    }
}
