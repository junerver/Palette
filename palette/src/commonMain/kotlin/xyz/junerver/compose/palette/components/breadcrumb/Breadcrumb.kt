package xyz.junerver.compose.palette.components.breadcrumb

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

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
        horizontalArrangement = Arrangement.spacedBy(BreadcrumbDefaults.itemSpacing()),
        verticalAlignment = Alignment.CenterVertically
    ) {
        items.forEachIndexed { index, item ->
            val isLast = index == items.lastIndex
            Text(
                text = item.label,
                style = BreadcrumbDefaults.textStyle(),
                color = if (isLast) BreadcrumbDefaults.currentColor() else BreadcrumbDefaults.textColor(),
                modifier = if (isLast) Modifier else Modifier.clickable { onClick(item.key) }
            )
            if (!isLast) {
                Text(
                    text = separator,
                    style = BreadcrumbDefaults.textStyle(),
                    color = BreadcrumbDefaults.separatorColor()
                )
            }
        }
    }
}
