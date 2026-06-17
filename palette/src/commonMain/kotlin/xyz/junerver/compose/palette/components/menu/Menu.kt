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
            val itemCornerRadius = MenuDefaults.itemCornerRadius()
            Text(
                text = item.label,
                style = MenuDefaults.itemTextStyle(),
                color = when {
                    item.disabled -> MenuDefaults.disabledTextColor()
                    isSelected -> MenuDefaults.selectedTextColor()
                    else -> MenuDefaults.textColor()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = MenuDefaults.itemOuterSpacing())
                    .clip(RoundedCornerShape(itemCornerRadius))
                    .background(
                        if (isSelected) MenuDefaults.selectedContainerColor()
                        else Color.Transparent
                    )
                    .clickable(enabled = !item.disabled) { onSelect(item.key) }
                    .padding(
                        horizontal = MenuDefaults.itemPaddingHorizontal(),
                        vertical = MenuDefaults.itemPaddingVertical()
                    )
            )
        }
    }
}
