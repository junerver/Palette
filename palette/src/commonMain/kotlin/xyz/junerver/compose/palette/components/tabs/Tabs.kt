package xyz.junerver.compose.palette.components.tabs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

data class TabItem(
    val key: String,
    val label: String,
    val disabled: Boolean = false,
)

@Composable
fun PTabs(
    items: List<TabItem>,
    selectedKey: String,
    modifier: Modifier = Modifier,
    onTabChange: (String) -> Unit = {},
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(TabsDefaults.ItemSpacing)
    ) {
        items.forEach { item ->
            val selected = item.key == selectedKey
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable(enabled = !item.disabled) { onTabChange(item.key) },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = item.label,
                    color = when {
                        item.disabled -> TabsDefaults.inactiveColor().copy(alpha = 0.45f)
                        selected -> TabsDefaults.activeColor()
                        else -> TabsDefaults.inactiveColor()
                    },
                    modifier = Modifier.padding(
                        horizontal = TabsDefaults.HorizontalPadding,
                        vertical = TabsDefaults.VerticalPadding
                    )
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(TabsDefaults.IndicatorHeight)
                        .background(if (selected) TabsDefaults.activeColor() else Color.Transparent)
                )
            }
        }
    }
}
