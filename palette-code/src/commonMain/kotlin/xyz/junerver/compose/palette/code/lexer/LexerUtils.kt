package xyz.junerver.compose.palette.code.lexer

internal fun String.nextWhile(
    start: Int,
    predicate: (Char) -> Boolean,
): Int {
    var index = start
    while (index < length && predicate(this[index])) index += 1
    return index
}

internal fun String.nextIdentifierEnd(start: Int): Int = nextWhile(start) { it.isLetterOrDigit() || it == '_' }

internal fun String.nextDottedIdentifierEnd(start: Int): Int = nextWhile(start) { it.isLetterOrDigit() || it == '_' || it == '.' }

internal fun String.nextJsonNumberEnd(start: Int): Int =
    nextWhile(start) { it.isDigit() || it == '-' || it == '+' || it == '.' || it == 'e' || it == 'E' }

internal fun String.nextPythonNumberEnd(start: Int): Int =
    nextWhile(start) { it.isLetterOrDigit() || it == '_' || it == '.' }

internal fun String.nextCssIdentifierEnd(start: Int): Int =
    nextWhile(start) { it.isLetterOrDigit() || it == '_' || it == '-' }

internal fun String.nextCssNumberEnd(start: Int): Int =
    nextWhile(start) { it.isLetterOrDigit() || it == '.' || it == '%' }

internal fun String.nextHtmlIdentifierEnd(start: Int): Int =
    nextWhile(start) { it.isLetterOrDigit() || it == '_' || it == '-' || it == ':' }

internal fun String.nextShellWordEnd(start: Int): Int =
    nextWhile(start) { it.isShellWordPart() }

internal fun String.nextShellVariableEnd(start: Int): Int =
    when (getOrNull(start + 1)) {
        '{' -> {
            val end = indexOf('}', startIndex = start + 2)
            if (end == -1) length else end + 1
        }

        else -> nextWhile(start + 1) { it.isLetterOrDigit() || it == '_' }.coerceAtLeast(start + 1)
    }

internal fun String.nextYamlWordEnd(start: Int): Int =
    nextWhile(start) { it.isLetterOrDigit() || it in setOf('_', '-', '.', '/') }

internal fun String.nextYamlNumberEnd(start: Int): Int =
    nextWhile(start) { it.isDigit() || it == '-' || it == '+' || it == '.' || it == '_' }

internal fun String.nextTomlBareKeyEnd(start: Int): Int =
    nextWhile(start) { it.isLetterOrDigit() || it in setOf('_', '-', '.') }

internal fun String.nextTomlNumberEnd(start: Int): Int =
    nextWhile(start) { it.isLetterOrDigit() || it in setOf('-', '+', '.', '_', ':') }

internal fun String.nextIniWordEnd(start: Int): Int =
    nextWhile(start) { it.isLetterOrDigit() || it in setOf('_', '-', '.', '/', ':') }

internal fun String.nextGraphQlNameEnd(start: Int): Int =
    nextWhile(start) { it.isLetterOrDigit() || it == '_' }

internal fun String.nextGraphQlVariableEnd(start: Int): Int =
    (start + 1).let { nameStart ->
        nextWhile(nameStart) { it.isLetterOrDigit() || it == '_' }.coerceAtLeast(nameStart)
    }

internal fun String.nextGraphQlNumberEnd(start: Int): Int =
    nextWhile(start) { it.isDigit() || it == '-' || it == '+' || it == '.' || it == 'e' || it == 'E' }

internal fun String.nextSqlIdentifierEnd(start: Int): Int =
    nextWhile(start) { it.isLetterOrDigit() || it == '_' || it == '$' }

internal fun String.nextSqlNumberEnd(start: Int): Int =
    nextWhile(start) { it.isDigit() || it == '.' }

internal fun String.nextDockerfileWordEnd(start: Int): Int =
    nextWhile(start) { it.isLetterOrDigit() || it in setOf('_', '-', '.', '/', ':', '@') }

internal fun String.nextDockerfileFlagEnd(start: Int): Int =
    nextWhile(start) { !it.isWhitespace() && it !in setOf('[', ']', '{', '}', '(', ')', ',', '"', '\'') }

internal fun String.nextDockerfileVariableEnd(start: Int): Int =
    when (getOrNull(start + 1)) {
        '{' -> {
            val end = indexOf('}', startIndex = start + 2)
            if (end == -1) length else end + 1
        }

        else -> nextWhile(start + 1) { it.isLetterOrDigit() || it == '_' }.coerceAtLeast(start + 1)
    }

internal fun String.nextMarkdownPlainEnd(start: Int): Int {
    val end = nextWhile(start) { it !in setOf('`', '[', '!', '<', '*', '_', '~') }
    return if (end == start) (start + 1).coerceAtMost(length) else end
}

internal fun String.findIniPropertySeparator(start: Int): Int {
    var index = start
    var escaped = false
    while (index < length) {
        val current = this[index]
        when {
            escaped -> escaped = false
            current == '\\' -> escaped = true
            current == '=' || current == ':' -> return index
            current.isWhitespace() -> {
                val next = nextWhile(index, Char::isWhitespace)
                if (next < length && this[next] !in setOf('#', ';')) return index
                index = next
                continue
            }
        }
        index += 1
    }
    return -1
}

internal fun String.nextNonWhitespace(start: Int): Char? {
    var index = start
    while (index < length) {
        val current = this[index]
        if (!current.isWhitespace()) return current
        index += 1
    }
    return null
}

internal fun Char.isIdentifierStart(): Boolean = isLetter() || this == '_'

internal fun Char.isPythonStringPrefixStart(): Boolean = this in setOf('f', 'F', 'r', 'R', 'b', 'B', 'u', 'U')

internal fun Char.isCssIdentifierStart(): Boolean = isLetter() || this == '_' || this == '-'

internal fun Char.isHtmlIdentifierStart(): Boolean = isLetter() || this == '_' || this == ':'

internal fun Char.isShellWordStart(): Boolean = isLetterOrDigit() || this in setOf('_', '.', '/', '~')

internal fun Char.isShellWordPart(): Boolean = isShellWordStart() || this in setOf('-', '+')

internal fun Char.isYamlWordStart(): Boolean = isLetter() || this == '_' || this == '.'

internal fun Char.isTomlBareKeyStart(): Boolean = isLetter() || this == '_' || this == '-'

internal fun Char.isIniWordStart(): Boolean = isLetter() || isDigit() || this in setOf('_', '-', '.', '/')

internal fun Char.isGraphQlNameStart(): Boolean = isLetter() || this == '_'

internal fun Char.isSqlIdentifierStart(): Boolean = isLetter() || this == '_'

internal fun Char.isDockerfileWordStart(): Boolean = isLetterOrDigit() || this in setOf('_', '.', '/', '$')

internal fun String.startsMarkdownFence(index: Int): Boolean {
    val marker = getOrNull(index)?.takeIf { it == '`' || it == '~' } ?: return false
    return nextWhile(index) { it == marker } - index >= 3
}

internal fun String.isMarkdownHeadingAt(index: Int): Boolean {
    if (getOrNull(index) != '#') return false
    val markerEnd = nextWhile(index) { it == '#' }
    return markerEnd - index in 1..6 && getOrNull(markerEnd)?.isWhitespace() == true
}

internal data class MarkdownListMarker(
    val marker: String,
)

internal fun String.markdownListMarker(index: Int): MarkdownListMarker? {
    val marker =
        when {
            getOrNull(index) in setOf('-', '*', '+') && getOrNull(index + 1)?.isWhitespace() == true ->
                get(index).toString()
            getOrNull(index)?.isDigit() == true -> {
                val numberEnd = nextWhile(index, Char::isDigit)
                if (
                    getOrNull(numberEnd) in setOf('.', ')') &&
                    getOrNull(numberEnd + 1)?.isWhitespace() == true
                ) {
                    substring(index, numberEnd + 1)
                } else {
                    null
                }
            }
            else -> null
        }
    return marker?.let { MarkdownListMarker(it) }
}

internal fun String.markdownTaskMarker(index: Int): String? {
    val marker = substring(index, (index + 3).coerceAtMost(length))
    return marker.takeIf {
        length >= index + 3 &&
            marker.first() == '[' &&
            marker.last() == ']' &&
            marker[1] in setOf(' ', 'x', 'X')
    }
}

internal fun String.findMarkdownLinkDestinationEnd(openingParenIndex: Int): Int {
    var index = openingParenIndex + 1
    var nestedParentheses = 0
    var quote: Char? = null
    var escaped = false
    while (index < length) {
        val char = this[index]
        when {
            escaped -> escaped = false
            char == '\\' -> escaped = true
            quote != null -> if (char == quote) quote = null
            char == '"' || char == '\'' -> quote = char
            char == '(' -> nestedParentheses += 1
            char == ')' && nestedParentheses > 0 -> nestedParentheses -= 1
            char == ')' -> return index
        }
        index += 1
    }
    return -1
}

internal fun String.isHexColorToken(): Boolean {
    val value = drop(1)
    return value.length in setOf(3, 4, 6, 8) && value.all { it.isDigit() || it.lowercaseChar() in 'a'..'f' }
}

internal fun String.tripleQuoteDelimiterAt(start: Int): String? {
    val quote = getOrNull(start) ?: return null
    if (quote != '"' && quote != '\'') return null
    val delimiter = "$quote$quote$quote"
    return delimiter.takeIf { startsWith(it, start) }
}

internal fun scanMultilineStringSegment(
    line: String,
    start: Int,
    delimiter: String,
    skipOpeningDelimiter: Boolean,
): Int {
    val end = findMultilineStringClose(line, start, delimiter, skipOpeningDelimiter)
    return if (end == -1) line.length else end + delimiter.length
}

internal fun hasMultilineStringClose(
    line: String,
    start: Int,
    delimiter: String,
    skipOpeningDelimiter: Boolean,
): Boolean = findMultilineStringClose(line, start, delimiter, skipOpeningDelimiter) != -1

internal fun findMultilineStringClose(
    line: String,
    start: Int,
    delimiter: String,
    skipOpeningDelimiter: Boolean,
): Int {
    val searchStart = if (skipOpeningDelimiter) start + delimiter.length else start
    return line.indexOf(delimiter, startIndex = searchStart)
}

internal fun scanQuotedString(
    line: String,
    start: Int,
    quote: Char,
): Int {
    var index = start + 1
    var escaped = false
    while (index < line.length) {
        val current = line[index]
        if (!escaped && current == quote) return index + 1
        escaped = !escaped && current == '\\'
        index += 1
    }
    return line.length
}
