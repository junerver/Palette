package xyz.junerver.compose.palette.code.lexer

import xyz.junerver.compose.palette.code.CodeToken
import xyz.junerver.compose.palette.code.CodeTokenType
import xyz.junerver.compose.palette.code.PaletteCodeHighlighter

internal object MarkdownLexer {
    fun highlight(lines: List<String>): List<List<CodeToken>> {
        val result = mutableListOf<List<CodeToken>>()
        var index = 0
        var fenceChar: Char? = null
        var fenceLength = 0
        var fenceLanguage: String? = null

        while (index < lines.size) {
            val line = lines[index]
            if (fenceChar != null) {
                if (line.isMarkdownFenceClose(fenceChar, fenceLength)) {
                    result += highlightMarkdownLine(line)
                    fenceChar = null
                    fenceLength = 0
                    fenceLanguage = null
                } else {
                    val language = fenceLanguage
                    val nestedLines = mutableListOf<String>()
                    val nestedStartIndex = index
                    while (index < lines.size) {
                        val candidate = lines[index]
                        if (candidate.isMarkdownFenceClose(fenceChar, fenceLength)) break
                        nestedLines += candidate
                        index += 1
                    }
                    if (index == lines.size) {
                        fenceChar = null
                        fenceLength = 0
                        fenceLanguage = null
                    }
                    val nestedCode = nestedLines.joinToString("\n")
                    val nestedTokens =
                        if (language.isNullOrEmpty()) {
                            nestedLines.map { listOf(CodeToken(CodeTokenType.Plain, it)) }
                        } else {
                            PaletteCodeHighlighter.highlight(
                                code = nestedCode,
                                language = language,
                            ).tokens
                        }
                    val prefix = nestedLines.firstOrNull()?.commonPrefixLength() ?: 0
                    nestedTokens.forEachIndexed { nestedIndex, tokens ->
                        if (nestedIndex == 0) {
                            result += tokens
                        } else {
                            val targetLine = nestedLines.getOrElse(nestedIndex) { "" }
                            val linePrefix = targetLine.commonPrefixLength()
                            val effectivePrefix = minOf(prefix, linePrefix)
                            if (effectivePrefix > 0) {
                                result += listOf(CodeToken(CodeTokenType.Plain, targetLine.substring(0, effectivePrefix))) + tokens
                            } else {
                                result += tokens
                            }
                        }
                    }
                    continue
                }
            } else {
                result += highlightMarkdownLine(line)
                if (line.startsMarkdownFence()) {
                    val trimmedStart = line.indexOfFirst { !it.isWhitespace() }.let { if (it == -1) line.length else it }
                    val fenceEnd = line.nextWhile(trimmedStart) { it == line[trimmedStart] }
                    fenceChar = line[trimmedStart]
                    fenceLength = fenceEnd - trimmedStart
                    val infoStart = line.nextWhile(fenceEnd, Char::isWhitespace)
                    fenceLanguage =
                        if (infoStart < line.length) line.substring(infoStart).parseMarkdownFenceLanguage() else null
                }
            }
            index += 1
        }
        return result
    }

    private fun String.commonPrefixLength(): Int {
        var count = 0
        while (count < length && this[count] == ' ') count += 1
        return count
    }

    private fun String.isMarkdownFenceClose(fenceChar: Char, fenceLength: Int): Boolean {
        val trimmedStart = indexOfFirst { !it.isWhitespace() }.let { if (it == -1) length else it }
        if (trimmedStart >= length || this[trimmedStart] != fenceChar) return false
        val fenceEnd = nextWhile(trimmedStart) { it == fenceChar }
        if (fenceEnd - trimmedStart < fenceLength) return false
        val trailingStart = nextWhile(fenceEnd, Char::isWhitespace)
        return trailingStart == length
    }

    private fun String.startsMarkdownFence(): Boolean {
        val trimmedStart = indexOfFirst { !it.isWhitespace() }.let { if (it == -1) length else it }
        if (trimmedStart >= length) return false
        val char = this[trimmedStart]
        if (char != '`' && char != '~') return false
        val fenceEnd = nextWhile(trimmedStart) { it == char }
        if (fenceEnd - trimmedStart < 3) return false
        val infoStart = nextWhile(fenceEnd, Char::isWhitespace)
        val info = if (infoStart < length) substring(infoStart).trim() else ""
        return info.all { it != char }
    }

    private fun highlightMarkdownLine(
        line: String,
    ): List<CodeToken> {
        val tokens = mutableListOf<CodeToken>()
        var index = line.indexOfFirst { !it.isWhitespace() }.let { if (it == -1) line.length else it }
        if (index > 0) {
            tokens += CodeToken(CodeTokenType.Plain, line.substring(0, index))
        }

        when {
            index == line.length -> return tokens
            line.startsMarkdownFence(index) -> {
                val fenceEnd = line.nextWhile(index) { it == line[index] }
                tokens += CodeToken(CodeTokenType.Annotation, line.substring(index, fenceEnd))
                val infoStart = line.nextWhile(fenceEnd, Char::isWhitespace)
                if (infoStart > fenceEnd) {
                    tokens += CodeToken(CodeTokenType.Plain, line.substring(fenceEnd, infoStart))
                }
                if (infoStart < line.length) {
                    tokens += CodeToken(CodeTokenType.Type, line.substring(infoStart))
                }
            }

            line.isMarkdownHeadingAt(index) -> {
                val markerEnd = line.nextWhile(index) { it == '#' }
                tokens += CodeToken(CodeTokenType.Keyword, line.substring(index, markerEnd))
                index = markerEnd
                tokens.addMarkdownInlineTokens(line, index)
            }

            line.startsWith(">", index) -> {
                tokens += CodeToken(CodeTokenType.Operator, ">")
                tokens.addMarkdownInlineTokens(line, index + 1)
            }

            else -> {
                val listMarker = line.markdownListMarker(index)
                if (listMarker != null) {
                    tokens += CodeToken(CodeTokenType.Operator, listMarker.marker)
                    index += listMarker.marker.length
                    val afterMarker = line.nextWhile(index, Char::isWhitespace)
                    if (afterMarker > index) {
                        tokens += CodeToken(CodeTokenType.Plain, line.substring(index, afterMarker))
                    }
                    index = afterMarker
                    val taskMarker = line.markdownTaskMarker(index)
                    if (taskMarker != null) {
                        tokens += CodeToken(CodeTokenType.Annotation, taskMarker)
                        index += taskMarker.length
                    }
                }
                tokens.addMarkdownInlineTokens(line, index)
            }
        }

        return tokens
    }

    private fun String.parseMarkdownFenceLanguage(): String? {
        val info = trim()
        if (info.isEmpty()) return null
        val attributeMatch = Regex("""\{\.([A-Za-z0-9_-]+)""").find(info)
        if (attributeMatch != null) return attributeMatch.groupValues[1].ifEmpty { null }
        val firstToken = info.substringBefore(' ').trim()
        return firstToken.ifEmpty { null }
    }

    private fun MutableList<CodeToken>.addMarkdownInlineTokens(
        line: String,
        start: Int,
    ) {
        var index = start
        while (index < line.length) {
            when {
                line[index].isWhitespace() -> {
                    val end = line.nextWhile(index, Char::isWhitespace)
                    this += CodeToken(CodeTokenType.Plain, line.substring(index, end))
                    index = end
                }

                line[index] == '`' -> {
                    val markerEnd = line.nextWhile(index) { it == '`' }
                    val marker = line.substring(index, markerEnd)
                    val close = line.indexOf(marker, startIndex = markerEnd)
                    val end = if (close == -1) line.length else close + marker.length
                    this += CodeToken(CodeTokenType.StringLiteral, line.substring(index, end))
                    index = end
                }

                line.startsWith("![", index) -> {
                    val read = addMarkdownLinkTokens(line, index, image = true)
                    if (read == index) {
                        this += CodeToken(CodeTokenType.Plain, line[index].toString())
                        index += 1
                    } else {
                        index = read
                    }
                }

                line[index] == '[' -> {
                    val read = addMarkdownLinkTokens(line, index, image = false)
                    if (read == index) {
                        this += CodeToken(CodeTokenType.Plain, line[index].toString())
                        index += 1
                    } else {
                        index = read
                    }
                }

                line.startsWith("<http://", index) || line.startsWith("<https://", index) -> {
                    val end = line.indexOf('>', startIndex = index + 1)
                    if (end == -1) {
                        this += CodeToken(CodeTokenType.Plain, line[index].toString())
                        index += 1
                    } else {
                        this += CodeToken(CodeTokenType.Punctuation, "<")
                        this += CodeToken(CodeTokenType.StringLiteral, line.substring(index + 1, end))
                        this += CodeToken(CodeTokenType.Punctuation, ">")
                        index = end + 1
                    }
                }

                line.startsWith("**", index) || line.startsWith("__", index) || line.startsWith("~~", index) -> {
                    this += CodeToken(CodeTokenType.Operator, line.substring(index, index + 2))
                    index += 2
                }

                line[index] == '*' || line[index] == '_' -> {
                    this += CodeToken(CodeTokenType.Operator, line[index].toString())
                    index += 1
                }

                else -> {
                    var end = line.nextMarkdownPlainEnd(index)
                    if (end == index + 1 && line[index] in setOf('!', '<', '~')) {
                        end = line.nextMarkdownPlainEnd(end)
                    }
                    this += CodeToken(CodeTokenType.Plain, line.substring(index, end))
                    index = end
                }
            }
        }
    }

    private fun MutableList<CodeToken>.addMarkdownLinkTokens(
        line: String,
        start: Int,
        image: Boolean,
    ): Int {
        val labelStart = if (image) start + 2 else start + 1
        val labelEnd = line.indexOf(']', startIndex = labelStart)
        val destinationOpen = labelEnd + 1
        if (
            labelEnd == -1 ||
            destinationOpen >= line.length ||
            line[destinationOpen] != '('
        ) {
            return start
        }

        val destinationEnd = line.findMarkdownLinkDestinationEnd(destinationOpen)
        if (destinationEnd == -1) return start

        if (image) {
            this += CodeToken(CodeTokenType.Punctuation, "!")
        }
        this += CodeToken(CodeTokenType.Punctuation, "[")
        this += CodeToken(CodeTokenType.Type, line.substring(labelStart, labelEnd))
        this += CodeToken(CodeTokenType.Punctuation, "]")
        this += CodeToken(CodeTokenType.Punctuation, "(")
        this += CodeToken(CodeTokenType.StringLiteral, line.substring(destinationOpen + 1, destinationEnd))
        this += CodeToken(CodeTokenType.Punctuation, ")")
        return destinationEnd + 1
    }
}
