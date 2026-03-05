package xyz.junerver.compose.palette.components.commandpalette

data class CommandAction(
    val id: String,
    val title: String,
    val subtitle: String? = null,
    val keywords: List<String> = emptyList(),
)

fun filterCommands(
    commands: List<CommandAction>,
    query: String,
): List<CommandAction> {
    val normalized = query.trim()
    if (normalized.isEmpty()) return commands
    return commands.filter { action ->
        action.title.contains(normalized, ignoreCase = true) ||
            action.subtitle.orEmpty().contains(normalized, ignoreCase = true) ||
            action.keywords.any { it.contains(normalized, ignoreCase = true) }
    }
}

fun moveHighlight(
    currentIndex: Int,
    offset: Int,
    size: Int,
): Int {
    if (size <= 0) return -1
    val current = currentIndex.coerceIn(0, size - 1)
    val moved = (current + offset) % size
    return if (moved < 0) moved + size else moved
}

fun pickHighlightedCommand(
    commands: List<CommandAction>,
    index: Int,
): CommandAction? = commands.getOrNull(index)
