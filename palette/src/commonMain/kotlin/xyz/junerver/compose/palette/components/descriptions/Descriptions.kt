package xyz.junerver.compose.palette.components.descriptions

import androidx.compose.foundation.layout.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

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
                horizontalArrangement = Arrangement.spacedBy(DescriptionsDefaults.rowSpacing())
            ) {
                rowItems.forEach { item ->
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .height(DescriptionsDefaults.rowHeight())
                            .padding(DescriptionsDefaults.padding())
                    ) {
                        Text(
                            text = item.label,
                            color = labelColor,
                            style = DescriptionsDefaults.textStyle(),
                            modifier = Modifier.width(DescriptionsDefaults.labelWidth())
                        )
                        Text(
                            text = item.content,
                            color = contentColor,
                            style = DescriptionsDefaults.textStyle(),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
            
            if (bordered) {
                HorizontalDivider(
                    thickness = DescriptionsDefaults.dividerHeight(),
                    color = DescriptionsDefaults.dividerColor()
                )
            }
        }
    }
}
