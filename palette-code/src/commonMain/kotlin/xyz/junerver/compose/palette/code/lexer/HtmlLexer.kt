package xyz.junerver.compose.palette.code.lexer

import xyz.junerver.compose.palette.code.CodeToken
import xyz.junerver.compose.palette.code.CodeTokenType

internal class HtmlLexer(
    private val embeddedHighlighter: (String, String) -> List<CodeToken>?,
) {
    private var inComment = false
    private var embeddedLanguage: HtmlEmbeddedLanguage? = null

    fun highlight(lines: List<String>): List<List<CodeToken>> = lines.map(::highlightLine)

    private fun highlightLine(line: String): List<CodeToken> {
        val tokens = mutableListOf<CodeToken>()
        var index = 0

        while (index < line.length) {
            embeddedLanguage?.let { language ->
                val closingTagStart = line.indexOfClosingHtmlTag(language.tagName, startIndex = index)
                val contentEnd = if (closingTagStart == -1) line.length else closingTagStart
                if (contentEnd > index) {
                    tokens += highlightEmbeddedHtmlContent(
                        language = language,
                        content = line.substring(index, contentEnd),
                    )
                    index = contentEnd
                }
                if (closingTagStart == -1) {
                    return tokens
                }
                embeddedLanguage = null
                continue
            }

            if (inComment) {
                val end = line.indexOf("-->", startIndex = index)
                if (end == -1) {
                    tokens += CodeToken(CodeTokenType.Comment, line.substring(index))
                    return tokens
                }
                tokens += CodeToken(CodeTokenType.Comment, line.substring(index, end + 3))
                inComment = false
                index = end + 3
                continue
            }

            when {
                line.startsWith("<!--", index) -> {
                    val end = line.indexOf("-->", startIndex = index + 4)
                    if (end == -1) {
                        tokens += CodeToken(CodeTokenType.Comment, line.substring(index))
                        inComment = true
                        index = line.length
                    } else {
                        tokens += CodeToken(CodeTokenType.Comment, line.substring(index, end + 3))
                        index = end + 3
                    }
                }

                line.startsWith("<!", index) -> {
                    val end = line.nextHtmlIdentifierEnd(index + 2)
                    tokens += CodeToken(CodeTokenType.Keyword, line.substring(index, end))
                    index = highlightTagContent(line = line, index = end, tokens = tokens)
                }

                line.startsWith("</", index) -> {
                    tokens += CodeToken(CodeTokenType.Punctuation, "</")
                    val nameEnd = line.nextHtmlIdentifierEnd(index + 2)
                    if (nameEnd > index + 2) {
                        tokens += CodeToken(CodeTokenType.Type, line.substring(index + 2, nameEnd))
                    }
                    index = highlightTagContent(line = line, index = nameEnd, tokens = tokens)
                }

                line[index] == '<' -> {
                    tokens += CodeToken(CodeTokenType.Punctuation, "<")
                    val nameEnd = line.nextHtmlIdentifierEnd(index + 1)
                    val tagName =
                        if (nameEnd > index + 1) {
                            line.substring(index + 1, nameEnd)
                        } else {
                            ""
                        }
                    if (nameEnd > index + 1) {
                        tokens += CodeToken(CodeTokenType.Type, tagName)
                    }
                    val contentEnd = highlightTagContent(line = line, index = nameEnd, tokens = tokens)
                    if (!line.substring(nameEnd, contentEnd).trimEnd().endsWith("/>")) {
                        embeddedLanguage = tagName.toHtmlEmbeddedLanguage()
                    }
                    index = contentEnd
                }

                else -> {
                    val end = line.indexOf('<', startIndex = index).let { if (it == -1) line.length else it }
                    tokens += CodeToken(CodeTokenType.Plain, line.substring(index, end))
                    index = end
                }
            }
        }

        return tokens
    }

    private fun highlightTagContent(
        line: String,
        index: Int,
        tokens: MutableList<CodeToken>,
    ): Int {
        var currentIndex = index
        while (currentIndex < line.length) {
            val current = line[currentIndex]
            when {
                current.isWhitespace() -> {
                    val end = line.nextWhile(currentIndex, Char::isWhitespace)
                    tokens += CodeToken(CodeTokenType.Plain, line.substring(currentIndex, end))
                    currentIndex = end
                }

                line.startsWith("/>", currentIndex) -> {
                    tokens += CodeToken(CodeTokenType.Punctuation, "/>")
                    return currentIndex + 2
                }

                current == '>' -> {
                    tokens += CodeToken(CodeTokenType.Punctuation, ">")
                    return currentIndex + 1
                }

                current == '"' || current == '\'' -> {
                    val end = scanQuotedString(line, currentIndex, current)
                    tokens += CodeToken(CodeTokenType.StringLiteral, line.substring(currentIndex, end))
                    currentIndex = end
                }

                current == '=' -> {
                    tokens += CodeToken(CodeTokenType.Operator, "=")
                    currentIndex += 1
                }

                current.isHtmlIdentifierStart() -> {
                    val end = line.nextHtmlIdentifierEnd(currentIndex)
                    val text = line.substring(currentIndex, end)
                    val type =
                        if (line.nextNonWhitespace(end) == '=') {
                            CodeTokenType.Annotation
                        } else {
                            CodeTokenType.Type
                        }
                    tokens += CodeToken(type, text)
                    currentIndex = end
                }

                else -> {
                    tokens += CodeToken(CodeTokenType.Plain, current.toString())
                    currentIndex += 1
                }
            }
        }
        return currentIndex
    }

    private fun highlightEmbeddedHtmlContent(
        language: HtmlEmbeddedLanguage,
        content: String,
    ): List<CodeToken> {
        val languageName =
            when (language) {
                HtmlEmbeddedLanguage.Script -> "javascript"
                HtmlEmbeddedLanguage.Style -> "css"
            }
        return embeddedHighlighter(content, languageName)
            ?: listOf(CodeToken(CodeTokenType.Plain, content))
    }

    private enum class HtmlEmbeddedLanguage(
        val tagName: String,
    ) {
        Script("script"),
        Style("style"),
    }

    private fun String.toHtmlEmbeddedLanguage(): HtmlEmbeddedLanguage? =
        when (lowercase()) {
            HtmlEmbeddedLanguage.Script.tagName -> HtmlEmbeddedLanguage.Script
            HtmlEmbeddedLanguage.Style.tagName -> HtmlEmbeddedLanguage.Style
            else -> null
        }

    private fun String.indexOfClosingHtmlTag(
        tagName: String,
        startIndex: Int,
    ): Int =
        indexOf("</$tagName", startIndex = startIndex, ignoreCase = true)
}
