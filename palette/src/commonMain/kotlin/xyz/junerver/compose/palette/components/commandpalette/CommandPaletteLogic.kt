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

    val filtered = ArrayList<CommandAction>(commands.size)
    for (index in commands.indices) {
        val action = commands[index]
        if (action.title.contains(normalized, ignoreCase = true)) {
            filtered.add(action)
            continue
        }

        val subtitle = action.subtitle
        if (subtitle != null && subtitle.contains(normalized, ignoreCase = true)) {
            filtered.add(action)
            continue
        }

        val keywords = action.keywords
        for (keywordIndex in keywords.indices) {
            if (keywords[keywordIndex].contains(normalized, ignoreCase = true)) {
                filtered.add(action)
                break
            }
        }
    }
    return filtered
}

fun moveHighlight(
    currentIndex: Int,
    offset: Int,
    size: Int,
): Int {
    if (size <= 0) return -1
    val maxIndex = size - 1
    var current = currentIndex
    if (current < 0) {
        current = 0
    } else if (current > maxIndex) {
        current = maxIndex
    }
    var moved = current + offset
    moved %= size
    return if (moved < 0) moved + size else moved
}

fun pickHighlightedCommand(
    commands: List<CommandAction>,
    index: Int,
): CommandAction? = commands.getOrNull(index)
