package xyz.junerver.compose.palette.code.lexer

import xyz.junerver.compose.palette.code.CodeToken
import xyz.junerver.compose.palette.code.CodeTokenType

internal object DiffLexer {
    fun highlight(lines: List<String>): List<List<CodeToken>> =
        lines.map { line -> highlightLine(line) }

    private fun highlightLine(line: String): List<CodeToken> =
        when {
            line.startsWith("diff --git ") -> highlightGitDiffHeader(line)
            line.startsWith("index ") -> highlightIndexHeader(line)
            line.startsWith("--- ") -> highlightPathHeader(line, marker = "---", markerType = CodeTokenType.Deleted)
            line.startsWith("+++ ") -> highlightPathHeader(line, marker = "+++", markerType = CodeTokenType.Inserted)
            line.startsWith("@@") -> highlightHunkHeader(line)
            line.startsWith("+") -> listOf(CodeToken(CodeTokenType.Inserted, line))
            line.startsWith("-") -> listOf(CodeToken(CodeTokenType.Deleted, line))
            else -> listOf(CodeToken(CodeTokenType.Plain, line))
        }

    private fun highlightGitDiffHeader(line: String): List<CodeToken> {
        val tokens = mutableListOf<CodeToken>()
        val parts = line.split(' ')
        if (parts.size >= 4) {
            tokens += CodeToken(CodeTokenType.Keyword, "diff --git")
            tokens += CodeToken(CodeTokenType.Plain, " ")
            tokens += CodeToken(CodeTokenType.Type, parts[2])
            tokens += CodeToken(CodeTokenType.Plain, " ")
            tokens += CodeToken(CodeTokenType.Type, parts[3])
            val rest = parts.drop(4).joinToString(" ")
            if (rest.isNotEmpty()) {
                tokens += CodeToken(CodeTokenType.Plain, " $rest")
            }
            return tokens
        }
        return listOf(CodeToken(CodeTokenType.Keyword, line))
    }

    private fun highlightIndexHeader(line: String): List<CodeToken> {
        val tokens = mutableListOf<CodeToken>()
        val parts = line.split(' ')
        tokens += CodeToken(CodeTokenType.Keyword, "index")
        if (parts.size >= 2) {
            tokens += CodeToken(CodeTokenType.Plain, " ")
            tokens += CodeToken(CodeTokenType.Annotation, parts[1])
        }
        if (parts.size >= 3) {
            tokens += CodeToken(CodeTokenType.Plain, " ")
            tokens += CodeToken(CodeTokenType.NumberLiteral, parts[2])
        }
        if (parts.size > 3) {
            tokens += CodeToken(CodeTokenType.Plain, " ${parts.drop(3).joinToString(" ")}")
        }
        return tokens
    }

    private fun highlightPathHeader(
        line: String,
        marker: String,
        markerType: CodeTokenType,
    ): List<CodeToken> {
        val path = line.removePrefix(marker).trimStart()
        return if (path.isEmpty()) {
            listOf(CodeToken(markerType, line))
        } else {
            listOf(
                CodeToken(markerType, marker),
                CodeToken(CodeTokenType.Plain, " "),
                CodeToken(CodeTokenType.Type, path),
            )
        }
    }

    private fun highlightHunkHeader(line: String): List<CodeToken> {
        val tokens = mutableListOf<CodeToken>()
        var index = 0
        while (index < line.length) {
            when {
                line.startsWith("@@", index) -> {
                    tokens += CodeToken(CodeTokenType.Annotation, "@@")
                    index += 2
                }

                line[index].isWhitespace() -> {
                    val end = line.nextWhile(index, Char::isWhitespace)
                    tokens += CodeToken(CodeTokenType.Plain, line.substring(index, end))
                    index = end
                }

                line[index] == '-' || line[index] == '+' -> {
                    val end = line.nextWhile(index + 1) { it.isDigit() || it == ',' }
                    val type = if (line[index] == '-') CodeTokenType.Deleted else CodeTokenType.Inserted
                    tokens += CodeToken(type, line.substring(index, end))
                    index = end
                }

                else -> {
                    tokens += CodeToken(CodeTokenType.Plain, line.substring(index))
                    index = line.length
                }
            }
        }
        return tokens
    }
}
