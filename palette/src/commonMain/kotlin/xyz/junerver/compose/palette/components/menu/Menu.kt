package xyz.junerver.compose.palette.components.menu

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun PMenu(
    items: List<MenuItem>,
    selectedKey: String? = null,
    modifier: Modifier = Modifier,
    onSelect: (String) -> Unit = {},
) {
    val resolvedSelected = resolveMenuSelection(items, selectedKey)

    Column(modifier = modifier) {
        items.forEach { item ->
            val isSelected = item.key == resolvedSelected
            Text(
                text = item.label,
                color = when {
                    item.disabled -> MenuDefaults.disabledTextColor()
                    isSelected -> MenuDefaults.selectedTextColor()
                    else -> MenuDefaults.textColor()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp)
                    .clip(RoundedCornerShape(MenuDefaults.ItemCornerRadius))
                    .background(
                        if (isSelected) MenuDefaults.selectedContainerColor()
                        else Color.Transparent
                    )
                    .clickable(enabled = !item.disabled) { onSelect(item.key) }
                    .padding(
                        horizontal = MenuDefaults.ItemPaddingHorizontal,
                        vertical = MenuDefaults.ItemPaddingVertical
                    )
            )
        }
    }
}
