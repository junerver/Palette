package xyz.junerver.compose.palette.latex

/**
 * LaTeX 源码词法分析。把原始字符串切成一个 [LatexToken] 序列，
 * 供 [LatexParser] 做递归下降解析。设计为 internal。
 */
internal sealed interface LatexToken {
    data class Word(val text: String) : LatexToken       // 普通字符序列（含数字、ASCII 运算符）
    data class Command(val name: String) : LatexToken    // \name
    data class CommandSymbol(val symbol: Char) : LatexToken // \% \$ \# \& \_ \{ \} \\
    data object LBrace : LatexToken                      // {
    data object RBrace : LatexToken                      // }
    data object LBracket : LatexToken                    // [
    data object RBracket : LatexToken                    // ]
    data object Sup : LatexToken                         // ^
    data object Sub : LatexToken                         // _
    data object Ampersand : LatexToken                   // & (矩阵分隔，预留)
}

private class Tokenizer(private val src: String) {
    private val tokens = ArrayList<LatexToken>()
    private var i = 0

    fun tokenize(): List<LatexToken> {
        while (i < src.length) {
            val c = src[i]
            when {
                c.isWhitespace() -> { i++ }                       // 空白在数学模式中无意义
                c == '\\' -> readCommand()
                c == '{' -> { tokens.add(LatexToken.LBrace); i++ }
                c == '}' -> { tokens.add(LatexToken.RBrace); i++ }
                c == '[' -> { tokens.add(LatexToken.LBracket); i++ }
                c == ']' -> { tokens.add(LatexToken.RBracket); i++ }
                c == '^' -> { tokens.add(LatexToken.Sup); i++ }
                c == '_' -> { tokens.add(LatexToken.Sub); i++ }
                c == '&' -> { tokens.add(LatexToken.Ampersand); i++ }
                else -> readWord(c)
            }
        }
        return tokens
    }

    /**
     * 读取普通字符序列。把连续同类字符聚成一个 [LatexToken.Word]：
     * 连续字母成词（多字母变量名）、连续数字（含 `.` `,`）成词（多位数字）、
     * 运算符 / 标点各自成词。这样 `x`、`123`、`+`、`=` 都各自成词，
     * 便于解析器按字符决定斜体 / 正体，也避免 `\frac12` 中 `1` 被吞并。
     */
    private fun readWord(first: Char) {
        i++
        val word: String = when {
            first.isLetter() -> {
                val sb = StringBuilder().append(first)
                while (i < src.length && src[i].isLetter()) {
                    sb.append(src[i]); i++
                }
                sb.toString()
            }
            first.isDigit() -> {
                val sb = StringBuilder().append(first)
                while (i < src.length && (src[i].isDigit() || src[i] == '.' || src[i] == ',')) {
                    sb.append(src[i]); i++
                }
                sb.toString()
            }
            else -> first.toString() // 单符号运算符 / 标点
        }
        tokens.add(LatexToken.Word(word))
    }

    /**
     * 读取反斜杠命令：`\name`（字母序列）或 `\%` 等单符号命令。
     */
    private fun readCommand() {
        i++ // 跳过 \
        if (i >= src.length) {
            tokens.add(LatexToken.CommandSymbol('\\'))
            return
        }
        val c = src[i]
        if (c.isLetter()) {
            val sb = StringBuilder()
            while (i < src.length && src[i].isLetter()) {
                sb.append(src[i])
                i++
            }
            tokens.add(LatexToken.Command(sb.toString()))
        } else {
            tokens.add(LatexToken.CommandSymbol(c))
            i++
        }
    }

}

/**
 * 词法分析入口。
 */
internal fun tokenizeLatex(source: String): List<LatexToken> = Tokenizer(source).tokenize()
