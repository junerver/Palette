package xyz.junerver.compose.palette.markdown

object MarkdownInlineParser {
    fun parse(source: String): List<MarkdownInlineNode> {
        return parseResolvedReferences(source, emptyMap())
    }

    fun parse(
        source: String,
        references: Map<String, String>,
    ): List<MarkdownInlineNode> {
        return parseResolvedReferences(
            source = source,
            references = references.mapValues { (_, destination) -> MarkdownLinkTarget(destination = destination) },
        )
    }

    internal fun parseWithTargets(
        source: String,
        references: Map<String, MarkdownLinkTarget>,
    ): List<MarkdownInlineNode> = parseResolvedReferences(source, references)

    private fun parseResolvedReferences(
        source: String,
        references: Map<String, MarkdownLinkTarget>,
    ): List<MarkdownInlineNode> {
        val nodes = mutableListOf<MarkdownInlineNode>()
        val plain = StringBuilder()
        var index = 0

        fun flushPlain() {
            if (plain.isNotEmpty()) {
                nodes += MarkdownInlineText(plain.toString().decodeMarkdownEntities())
                plain.clear()
            }
        }

        while (index < source.length) {
            when {
                source[index] == '\n' -> {
                    when {
                        plain.endsWith("\\") -> {
                            plain.setLength(plain.length - 1)
                            flushPlain()
                            nodes += MarkdownInlineHardBreak
                        }

                        plain.endsWith("  ") -> {
                            while (plain.isNotEmpty() && plain.last() == ' ') {
                                plain.setLength(plain.length - 1)
                            }
                            flushPlain()
                            nodes += MarkdownInlineHardBreak
                        }

                        else -> {
                            flushPlain()
                            nodes += MarkdownInlineSoftBreak
                        }
                    }
                    index += 1
                }

                source[index] == '\\' -> {
                    val next = source.getOrNull(index + 1)
                    if (next != null && next in EscapableMarkdownChars) {
                        plain.append(next)
                        index += 2
                    } else {
                        plain.append(source[index])
                        index += 1
                    }
                }

                source.startsWith("![", index) -> {
                    val altEnd = source.indexOf(']', startIndex = index + 2)
                    val destinationStart = altEnd + 1
                    if (
                        altEnd != -1 &&
                        destinationStart < source.length &&
                        source[destinationStart] == '('
                    ) {
                        val destinationEnd = source.findInlineLinkDestinationEnd(destinationStart)
                        val target =
                            if (destinationEnd != -1) {
                                source.substring(destinationStart + 1, destinationEnd).toLinkTarget()
                            } else {
                                null
                            }
                        if (target != null) {
                            flushPlain()
                            nodes +=
                                MarkdownInlineImage(
                                    alt = source.substring(index + 2, altEnd),
                                    destination = target.destination,
                                    title = target.title,
                                )
                            index = destinationEnd + 1
                        } else {
                            plain.append(source[index])
                            index += 1
                        }
                    } else if (
                        altEnd != -1 &&
                        destinationStart < source.length &&
                        source[destinationStart] == '['
                    ) {
                        val referenceEnd = source.indexOf(']', startIndex = destinationStart + 1)
                        val alt = source.substring(index + 2, altEnd)
                        val referenceLabel =
                            if (referenceEnd != -1) source.substring(destinationStart + 1, referenceEnd).ifEmpty { alt } else ""
                        val target = references[referenceLabel.normalizedReferenceLabel()]
                        if (target != null) {
                            flushPlain()
                            nodes +=
                                MarkdownInlineImage(
                                    alt = alt,
                                    destination = target.destination,
                                    title = target.title,
                                )
                            index = referenceEnd + 1
                        } else {
                            plain.append(source[index])
                            index += 1
                        }
                    } else {
                        plain.append(source[index])
                        index += 1
                    }
                }

                source.startsWith("~~", index) -> {
                    val end = source.indexOf("~~", startIndex = index + 2)
                    if (end != -1) {
                        val text = source.substring(index + 2, end)
                        flushPlain()
                        nodes +=
                            MarkdownInlineStrikethrough(
                                text = text,
                                children = parseResolvedReferences(text, references),
                            )
                        index = end + 2
                    } else {
                        plain.append(source[index])
                        index += 1
                    }
                }

                source.isInlineDelimiterRun(index, '*', length = 3) -> {
                    val end = source.findInlineDelimiterRun(
                        startIndex = index + 3,
                        delimiter = '*',
                        length = 3,
                    )
                    if (end != -1) {
                        val text = source.substring(index + 3, end)
                        flushPlain()
                        val inner = parseResolvedReferences(text, references)
                        nodes +=
                            MarkdownInlineStrong(
                                text = text,
                                children = listOf(
                                    MarkdownInlineEmphasis(
                                        text = text,
                                        children = inner,
                                    ),
                                ),
                            )
                        index = end + 3
                    } else if (source.isInlineDelimiterRun(index, '*', length = 2)) {
                        val end2 = source.findInlineDelimiterRun(
                            startIndex = index + 2,
                            delimiter = '*',
                            length = 2,
                        )
                        if (end2 != -1) {
                            val text = source.substring(index + 2, end2)
                            flushPlain()
                            nodes +=
                                MarkdownInlineStrong(
                                    text = text,
                                    children = parseResolvedReferences(text, references),
                                )
                            index = end2 + 2
                        } else {
                            plain.append(source[index])
                            index += 1
                        }
                    } else {
                        plain.append(source[index])
                        index += 1
                    }
                }

                source.isInlineDelimiterRun(index, '_', length = 3) -> {
                    val end = source.findInlineDelimiterRun(
                        startIndex = index + 3,
                        delimiter = '_',
                        length = 3,
                    )
                    if (end != -1) {
                        val text = source.substring(index + 3, end)
                        flushPlain()
                        val inner = parseResolvedReferences(text, references)
                        nodes +=
                            MarkdownInlineStrong(
                                text = text,
                                children = listOf(
                                    MarkdownInlineEmphasis(
                                        text = text,
                                        children = inner,
                                    ),
                                ),
                            )
                        index = end + 3
                    } else if (source.isInlineDelimiterRun(index, '_', length = 2)) {
                        val end2 = source.findInlineDelimiterRun(
                            startIndex = index + 2,
                            delimiter = '_',
                            length = 2,
                        )
                        if (end2 != -1) {
                            val text = source.substring(index + 2, end2)
                            flushPlain()
                            nodes +=
                                MarkdownInlineStrong(
                                    text = text,
                                    children = parseResolvedReferences(text, references),
                                )
                            index = end2 + 2
                        } else {
                            plain.append(source[index])
                            index += 1
                        }
                    } else {
                        plain.append(source[index])
                        index += 1
                    }
                }

                source.isInlineDelimiterRun(index, '*', length = 2) ||
                    source.isInlineDelimiterRun(index, '_', length = 2) -> {
                    val delimiter = source[index]
                    val end = source.findInlineDelimiterRun(
                        startIndex = index + 2,
                        delimiter = delimiter,
                        length = 2,
                    )
                    if (end != -1) {
                        val text = source.substring(index + 2, end)
                        flushPlain()
                        nodes +=
                            MarkdownInlineStrong(
                                text = text,
                                children = parseResolvedReferences(text, references),
                            )
                        index = end + 2
                    } else {
                        plain.append(source[index])
                        index += 1
                    }
                }

                source.isInlineDelimiterRun(index, '*', length = 1) ||
                    source.isInlineDelimiterRun(index, '_', length = 1) -> {
                    val delimiter = source[index]
                    val end = source.findInlineDelimiterRun(
                        startIndex = index + 1,
                        delimiter = delimiter,
                        length = 1,
                    )
                    if (end != -1) {
                        val text = source.substring(index + 1, end)
                        flushPlain()
                        nodes +=
                            MarkdownInlineEmphasis(
                                text = text,
                                children = parseResolvedReferences(text, references),
                            )
                        index = end + 1
                    } else {
                        plain.append(source[index])
                        index += 1
                    }
                }

                source[index] == '`' -> {
                    val delimiterLength = source.countRepeatedFrom(index, '`')
                    val end = source.findMatchingBacktickRun(index + delimiterLength, delimiterLength)
                    if (end != -1) {
                        flushPlain()
                        nodes += MarkdownInlineCode(source.substring(index + delimiterLength, end).normalizedCodeSpan())
                        index = end + delimiterLength
                    } else {
                        plain.append(source[index])
                        index += 1
                    }
                }

                source[index] == '<' -> {
                    val end = source.indexOf('>', startIndex = index + 1)
                    val autolink = if (end != -1) source.substring(index + 1, end).toAutolink() else null
                    if (autolink != null) {
                        flushPlain()
                        nodes += autolink
                        index = end + 1
                    } else {
                        val htmlEnd = source.findInlineHtmlEnd(index)
                        if (htmlEnd != -1) {
                            flushPlain()
                            nodes += MarkdownInlineHtml(source.substring(index, htmlEnd))
                            index = htmlEnd
                        } else {
                            plain.append(source[index])
                            index += 1
                        }
                    }
                }

                source.startsBareAutolink(index) -> {
                    val autolink = source.bareAutolinkAt(index)
                    if (autolink != null) {
                        flushPlain()
                        nodes += autolink
                        index += autolink.destination.length
                    } else {
                        plain.append(source[index])
                        index += 1
                    }
                }

                source[index] == '[' -> {
                    val labelEnd = source.indexOf(']', startIndex = index + 1)
                    val destinationStart = labelEnd + 1
                    if (
                        labelEnd != -1 &&
                        destinationStart < source.length &&
                        source[destinationStart] == '('
                    ) {
                        val destinationEnd = source.findInlineLinkDestinationEnd(destinationStart)
                        val target =
                            if (destinationEnd != -1) {
                                source.substring(destinationStart + 1, destinationEnd).toLinkTarget()
                            } else {
                                null
                            }
                        if (target != null) {
                            val label = source.substring(index + 1, labelEnd)
                            flushPlain()
                            nodes +=
                                MarkdownInlineLink(
                                    label = label,
                                    destination = target.destination,
                                    title = target.title,
                                    children = parseResolvedReferences(label, references),
                                )
                            index = destinationEnd + 1
                        } else {
                            plain.append(source[index])
                            index += 1
                        }
                    } else if (
                        labelEnd != -1 &&
                        destinationStart < source.length &&
                        source[destinationStart] == '['
                    ) {
                        val referenceEnd = source.indexOf(']', startIndex = destinationStart + 1)
                        val label = source.substring(index + 1, labelEnd)
                        val referenceLabel =
                            if (referenceEnd != -1) source.substring(destinationStart + 1, referenceEnd).ifEmpty { label } else ""
                        val target = references[referenceLabel.normalizedReferenceLabel()]
                        if (target != null) {
                            flushPlain()
                            nodes +=
                                MarkdownInlineLink(
                                    label = label,
                                    destination = target.destination,
                                    title = target.title,
                                    children = parseResolvedReferences(label, references),
                                )
                            index = referenceEnd + 1
                        } else {
                            plain.append(source[index])
                            index += 1
                        }
                    } else if (labelEnd != -1) {
                        val label = source.substring(index + 1, labelEnd)
                        val target = references[label.normalizedReferenceLabel()]
                        if (target != null) {
                            flushPlain()
                            nodes +=
                                MarkdownInlineLink(
                                    label = label,
                                    destination = target.destination,
                                    title = target.title,
                                    children = parseResolvedReferences(label, references),
                                )
                            index = labelEnd + 1
                        } else {
                            plain.append(source[index])
                            index += 1
                        }
                    } else {
                        plain.append(source[index])
                        index += 1
                    }
                }

                else -> {
                    plain.append(source[index])
                    index += 1
                }
            }
        }

        flushPlain()
        return nodes
    }

    private fun String.toAutolink(): MarkdownInlineLink? =
        when {
            matches(AutolinkUrlRegex) -> MarkdownInlineLink(label = this, destination = this)
            matches(AutolinkEmailRegex) -> MarkdownInlineLink(label = this, destination = "mailto:$this")
            else -> null
        }

    private fun String.toLinkTarget(): MarkdownLinkTarget? {
        val source = trim()
        if (source.isEmpty()) return null
        val (destinationSource, titleSource) =
            if (source.startsWith("<")) {
                val destinationEnd = source.indexOf('>')
                if (destinationEnd == -1) return null
                source.substring(1, destinationEnd) to source.substring(destinationEnd + 1).trim()
            } else {
                // Find destination end respecting balanced parentheses
                var depth = 0
                var index = 0
                var lastSpace = -1
                while (index < source.length) {
                    val char = source[index]
                    when {
                        char == '(' -> depth++
                        char == ')' -> depth--
                        char.isWhitespace() && depth == 0 && lastSpace == -1 -> lastSpace = index
                    }
                    index++
                }
                val destinationEnd = if (lastSpace != -1) lastSpace else source.length
                if (destinationEnd == source.length) {
                    source to ""
                } else {
                    source.substring(0, destinationEnd) to source.substring(destinationEnd + 1).trim()
                }
            }
        val destination = destinationSource.trimReferenceDestination().takeIf { it.isNotEmpty() } ?: return null
        return MarkdownLinkTarget(destination = destination, title = titleSource.toLinkTitle())
    }

    private fun String.toLinkTitle(): String? {
        val source = trim()
        if (source.length < 2) return null
        val quote = source.first()
        val endQuote =
            when (quote) {
                '"', '\'' -> quote
                '(' -> ')'
                else -> return null
            }
        if (source.last() != endQuote) return null
        return source.drop(1).dropLast(1).trim()
    }

    private fun String.findInlineHtmlEnd(start: Int): Int {
        val openingEnd = indexOf('>', startIndex = start + 1)
        if (openingEnd == -1) return -1
        val opening = substring(start, openingEnd + 1)
        val tagMatch = InlineHtmlOpeningTagRegex.matchEntire(opening) ?: return -1
        if (opening.startsWith("</") || opening.endsWith("/>")) return openingEnd + 1
        val tagName = tagMatch.groupValues[1]
        if (tagName.lowercase() in VoidHtmlTags) return openingEnd + 1
        val closing = "</$tagName>"
        val closingStart = indexOf(closing, startIndex = openingEnd + 1, ignoreCase = true)
        return if (closingStart == -1) openingEnd + 1 else closingStart + closing.length
    }

    private fun String.findInlineLinkDestinationEnd(openingParenIndex: Int): Int {
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

    private fun String.trimReferenceDestination(): String =
        trim().trim('<', '>')

    private fun String.startsBareAutolink(index: Int): Boolean {
        if (getOrNull(index - 1)?.isBareAutolinkBoundary() == false) return false
        if (startsWith("http://", index) || startsWith("https://", index) || startsWith("www.", index)) return true
        return bareAutolinkCandidateAt(index).trimBareAutolinkEnd().matches(AutolinkEmailRegex)
    }

    private fun String.bareAutolinkAt(index: Int): MarkdownInlineLink? {
        val candidate = bareAutolinkCandidateAt(index)
        val url = candidate.trimBareAutolinkEnd()
        return url
            .takeIf { it.isNotEmpty() }
            ?.let { 
                val destination = if (it.startsWith("www.")) "http://$it" else it
                MarkdownInlineLink(label = it, destination = destination)
            }
    }

    private fun String.bareAutolinkCandidateAt(index: Int): String {
        var end = index
        while (end < length && !this[end].isWhitespace() && this[end] != '<') {
            end += 1
        }
        return substring(index, end)
    }

    private fun String.trimBareAutolinkEnd(): String {
        var value = trimEnd { it in BareAutolinkTrailingPunctuation }
        while (value.endsWith(")") && value.count { it == ')' } > value.count { it == '(' }) {
            value = value.dropLast(1)
        }
        return value
    }

    private val AutolinkUrlRegex = Regex("""https?://[^\s<>]+""")
    private val AutolinkEmailRegex = Regex("""[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}""")
    private val BareWwwAutolinkRegex = Regex("""www\.[A-Za-z0-9.-]+\.[A-Za-z]{2,}[^\s<>]*""")
    private val InlineHtmlOpeningTagRegex = Regex("""</?\s*([A-Za-z][A-Za-z0-9-]*)(?:\s+[^<>]*)?/?>""")
    private val VoidHtmlTags = setOf("area", "base", "br", "col", "embed", "hr", "img", "input", "link", "meta", "param", "source", "track", "wbr")
    private val BareAutolinkTrailingPunctuation = setOf('.', ',', ';', ':', '!', '?')
    private val EscapableMarkdownChars = setOf('\\', '`', '*', '_', '{', '}', '[', ']', '(', ')', '#', '+', '-', '.', '!', '|', '~')

    private fun Char.isBareAutolinkBoundary(): Boolean =
        isWhitespace() || this in setOf('(', '[', '{', '<')

    private fun String.decodeMarkdownEntities(): String =
        replace(MarkdownEntityRegex) { match ->
            val body = match.groupValues[1].ifEmpty { match.groupValues[2] }
            when {
                body.startsWith("#x", ignoreCase = true) ->
                    body.drop(2).toIntOrNull(radix = 16)?.toChar()?.toString() ?: match.value
                body.startsWith("#") ->
                    body.drop(1).toIntOrNull()?.toChar()?.toString() ?: match.value
                else -> MarkdownNamedEntities[body] ?: match.value
            }
        }

    private val MarkdownEntityRegex = Regex("""&([A-Za-z][A-Za-z0-9]+|#[0-9]+|#x[0-9A-Fa-f]+);""")
    private val MarkdownNamedEntities = mapOf(
        "amp" to "&", "lt" to "<", "gt" to ">", "quot" to "\"",
        "apos" to "'", "nbsp" to "\u00A0",
        "copy" to "\u00A9", "reg" to "\u00AE", "trade" to "\u2122",
        "mdash" to "\u2014", "ndash" to "\u2013", "hellip" to "\u2026",
        "laquo" to "\u00AB", "raquo" to "\u00BB",
        "ldquo" to "\u201C", "rdquo" to "\u201D",
        "lsquo" to "\u2018", "rsquo" to "\u2019",
        "bull" to "\u2022", "middot" to "\u00B7",
        "ensp" to "\u2002", "emsp" to "\u2003", "thinsp" to "\u2009",
        "cent" to "\u00A2", "pound" to "\u00A3", "yen" to "\u00A5",
        "euro" to "\u20AC", "sect" to "\u00A7", "para" to "\u00B6",
        "larr" to "\u2190", "rarr" to "\u2192", "uarr" to "\u2191", "darr" to "\u2193",
        "harr" to "\u2194", "crarr" to "\u21B5",
        "times" to "\u00D7", "divide" to "\u00F7",
        "plusmn" to "\u00B1", "micro" to "\u00B5", "deg" to "\u00B0",
    )

    private fun String.normalizedReferenceLabel(): String =
        trim()
            .replace(WhitespaceRegex, " ")
            .lowercase()

    private fun String.isInlineDelimiterRun(
        index: Int,
        delimiter: Char,
        length: Int,
    ): Boolean {
        // Must have enough characters
        if (index + length > this.length) return false
        // Must all be the delimiter character
        if (!substring(index, index + length).all { it == delimiter }) return false
        // Must not be part of a longer delimiter run
        if (getOrNull(index - 1) == delimiter) return false
        if (getOrNull(index + length) == delimiter) return false
        
        val before = getOrNull(index - 1)
        val after = getOrNull(index + length)
        val beforeIsWhitespace = before?.isWhitespace() ?: true
        val afterIsWhitespace = after?.isWhitespace() ?: true
        val beforeIsPunctuation = before != null && before.isPunctuation()
        val afterIsPunctuation = after != null && after.isPunctuation()
        
        // Left-flanking: not followed by whitespace, and (not followed by punctuation OR preceded by whitespace or punctuation)
        val isLeftFlanking = !afterIsWhitespace && (!afterIsPunctuation || beforeIsWhitespace || beforeIsPunctuation)
        // Right-flanking: not preceded by whitespace, and (not preceded by punctuation OR followed by whitespace or punctuation)
        val isRightFlanking = !beforeIsWhitespace && (!beforeIsPunctuation || afterIsWhitespace || afterIsPunctuation)
        
        return when (delimiter) {
            '*' -> isLeftFlanking || isRightFlanking
            '_' -> (isLeftFlanking && (!isRightFlanking || beforeIsPunctuation)) ||
                   (isRightFlanking && (!isLeftFlanking || afterIsPunctuation))
            else -> true
        }
    }
    
    private fun Char.isPunctuation(): Boolean =
        !isLetterOrDigit() && !isWhitespace()

    private fun String.findInlineDelimiterRun(
        startIndex: Int,
        delimiter: Char,
        length: Int,
    ): Int {
        var candidate = startIndex
        while (candidate < this.length) {
            val index = indexOf(delimiter, startIndex = candidate)
            if (index == -1) return -1
            if (isInlineDelimiterRun(index, delimiter, length)) return index
            candidate = index + 1
        }
        return -1
    }

    private fun String.normalizedCodeSpan(): String {
        val normalized = replace(Regex("""\s+"""), " ")
        return if (
            normalized.length >= 2 &&
            normalized.first() == ' ' &&
            normalized.last() == ' ' &&
            normalized.any { !it.isWhitespace() }
        ) {
            normalized.drop(1).dropLast(1)
        } else {
            normalized
        }
    }

    private fun String.findMatchingBacktickRun(
        startIndex: Int,
        length: Int,
    ): Int {
        var candidate = startIndex
        while (candidate < this.length) {
            val index = indexOf('`', startIndex = candidate)
            if (index == -1) return -1
            if (countRepeatedFrom(index, '`') == length) return index
            candidate = index + 1
        }
        return -1
    }

    private fun String.countRepeatedFrom(
        start: Int,
        char: Char,
    ): Int {
        var index = start
        while (index < length && this[index] == char) index += 1
        return index - start
    }

    private val WhitespaceRegex = Regex("""\s+""")
}
