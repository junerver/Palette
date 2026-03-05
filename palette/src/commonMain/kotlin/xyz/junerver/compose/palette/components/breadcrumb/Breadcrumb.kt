package xyz.junerver.compose.palette.components.breadcrumb

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

data class BreadcrumbItem(
    val key: String,
    val label: String,
)

@Composable
fun PBreadcrumb(
    items: List<BreadcrumbItem>,
    modifier: Modifier = Modifier,
    separator: String = "/",
    onClick: (String) -> Unit = {},
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        items.forEachIndexed { index, item ->
            val isLast = index == items.lastIndex
            Text(
                text = item.label,
                color = if (isLast) BreadcrumbDefaults.currentColor() else BreadcrumbDefaults.textColor(),
                modifier = if (isLast) Modifier else Modifier.clickable { onClick(item.key) }
            )
            if (!isLast) {
                Text(
                    text = separator,
                    color = BreadcrumbDefaults.textColor()
                )
            }
        }
    }
}
