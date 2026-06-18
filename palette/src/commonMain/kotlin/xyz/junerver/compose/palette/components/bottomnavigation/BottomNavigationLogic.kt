package xyz.junerver.compose.palette.components.bottomnavigation

import androidx.compose.runtime.Composable

data class BottomNavigationItem(
    val key: String,
    val label: String,
    val icon: @Composable () -> Unit,
    val disabled: Boolean = false,
)

fun resolveBottomNavigationSelection(
    items: List<BottomNavigationItem>,
    selectedKey: String?,
): String? {
    if (!selectedKey.isNullOrBlank() && items.any { it.key == selectedKey && !it.disabled }) {
        return selectedKey
    }
    return items.firstOrNull { !it.disabled }?.key
}
