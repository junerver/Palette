package xyz.junerver.compose.palette.components.menu

data class MenuItem(
    val key: String,
    val label: String,
    val disabled: Boolean = false,
)

fun resolveMenuSelection(
    items: List<MenuItem>,
    selectedKey: String?,
): String? {
    if (!selectedKey.isNullOrBlank() && items.any { it.key == selectedKey && !it.disabled }) {
        return selectedKey
    }
    return items.firstOrNull { !it.disabled }?.key
}
