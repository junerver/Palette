package xyz.junerver.compose.palette.components.bottomnavigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.material3.LocalContentColor
import xyz.junerver.compose.palette.components.text.PText

@Composable
fun PBottomNavigation(
    items: List<BottomNavigationItem>,
    selectedKey: String?,
    onItemClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    colors: BottomNavigationColors = BottomNavigationDefaults.colors(),
    height: Dp = BottomNavigationDefaults.height(),
    itemCornerRadius: Dp = BottomNavigationDefaults.itemCornerRadius(),
    itemHorizontalPadding: Dp = BottomNavigationDefaults.itemHorizontalPadding(),
    itemVerticalPadding: Dp = BottomNavigationDefaults.itemVerticalPadding(),
    itemContentVerticalPadding: Dp = BottomNavigationDefaults.itemContentVerticalPadding(),
    iconLabelSpacing: Dp = BottomNavigationDefaults.iconLabelSpacing(),
) {
    val resolvedSelected = resolveBottomNavigationSelection(items, selectedKey)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .background(colors.containerColor)
            .padding(
                horizontal = itemHorizontalPadding,
                vertical = itemVerticalPadding,
            ),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        items.forEach { item ->
            val selected = item.key == resolvedSelected
            BottomNavigationItemContent(
                item = item,
                selected = selected,
                colors = colors,
                itemCornerRadius = itemCornerRadius,
                itemContentVerticalPadding = itemContentVerticalPadding,
                iconLabelSpacing = iconLabelSpacing,
                onClick = {
                    if (!selected) {
                        onItemClick(item.key)
                    }
                },
            )
        }
    }
}

@Composable
private fun RowScope.BottomNavigationItemContent(
    item: BottomNavigationItem,
    selected: Boolean,
    colors: BottomNavigationColors,
    itemCornerRadius: Dp,
    itemContentVerticalPadding: Dp,
    iconLabelSpacing: Dp,
    onClick: () -> Unit,
) {
    val contentColor = when {
        item.disabled -> colors.disabledContentColor
        selected -> colors.selectedContentColor
        else -> colors.contentColor
    }
    val indicatorColor = if (selected) colors.selectedIndicatorColor else Color.Transparent

    Box(
        modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(itemCornerRadius))
            .background(indicatorColor)
            .clickable(enabled = !item.disabled, onClick = onClick)
            .padding(vertical = itemContentVerticalPadding),
        contentAlignment = Alignment.Center,
    ) {
        CompositionLocalProvider(LocalContentColor provides contentColor) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(iconLabelSpacing),
            ) {
                item.icon()
                PText(
                    text = item.label,
                    color = contentColor,
                    style = BottomNavigationDefaults.labelTextStyle(),
                )
            }
        }
    }
}
