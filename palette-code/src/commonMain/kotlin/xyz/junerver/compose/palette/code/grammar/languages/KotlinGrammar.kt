package xyz.junerver.compose.palette.code.grammar.languages

import xyz.junerver.compose.palette.code.grammar.Grammar
import xyz.junerver.compose.palette.code.grammar.GrammarToken
import xyz.junerver.compose.palette.code.grammar.grammarOf

/**
 * Kotlin grammar — declarative replacement for
 * [xyz.junerver.compose.palette.code.lexer.KotlinLikeLexer]'s kotlin path.
 *
 * Kotlin's two non-regular constructs (the lexer's reason for staying hand-written) are both
 * modelled declaratively here:
 * - **Nested block comments** (`/* /* */ */`): a custom [GrammarToken.matcher] scans `/* … */`
 *   while tracking depth, so an inner `/* */` doesn't end the outer comment. Regex can't count,
 *   so the matcher does.
 * - **String interpolation** (`$var`, `${expr}`): a `"…"` token with an `inside` grammar splits
 *   the literal parts (→ string), `$var` (→ annotation), and `${ expr }` (→ operator + annotation
 *   + operator), reproducing the lexer's [tokenizeKotlinStringInterpolation] boundaries.
 *
 * Triple-quoted `"""…"""` strings span lines via `(?s)`; classification otherwise mirrors the
 * C-family grammars (keywords, types, functions, annotations, numbers, operators).
 */
internal val KotlinGrammar: Grammar = cFamilyGrammar(
    keywords = listOf(
        "abstract", "actual", "annotation", "as", "break", "by", "catch", "class", "companion",
        "const", "constructor", "context", "continue", "contract", "crossinline", "data", "delegate",
        "do", "dynamic", "else", "enum", "expect", "external", "false", "field", "final",
        "finally", "for", "fun", "get", "if", "import", "in", "infix", "init", "inline",
        "inner", "interface", "internal", "is", "lateinit", "noinline", "null", "object", "of",
        "open", "operator", "out", "override", "package", "private", "property", "protected",
        "public", "reified", "return", "sealed", "set", "suspend", "tailrec", "this", "throw",
        "true", "try", "typealias", "typeof", "val", "value", "var", "vararg", "when", "where",
        "while",
    ),
    booleans = listOf("true", "false", "null"),
    tripleQuotedStrings = true,
    stringInterpolation = true,
    blockCommentMatcher = { text, start -> kotlinCommentMatcher(text, start) },
)

/**
 * Builds a C-family [Grammar] from a keyword list, shared by Kotlin/Java/TS/JS.
 *
 * @param primitiveTypes lowercase built-in type names → `type`.
 * @param tripleQuotedStrings when true, match `"""…"""` triple-quoted strings (Kotlin/Python-style).
 * @param templateLiterals when true, backtick template literals `` `…` `` are matched as strings.
 * @param stringInterpolation when true, `"…"` strings get an `inside` grammar splitting
 *   `$var`/`${expr}` interpolations (Kotlin-style).
 * @param blockCommentMatcher when set, replaces the regex block-comment rule with a custom
 *   matcher (e.g. Kotlin's depth-counting matcher for nested `/* /* */ */`).
 */
internal fun cFamilyGrammar(
    keywords: List<String>,
    booleans: List<String>,
    primitiveTypes: List<String> = emptyList(),
    tripleQuotedStrings: Boolean = false,
    templateLiterals: Boolean = false,
    stringInterpolation: Boolean = false,
    blockCommentMatcher: ((String, Int) -> Int?)? = null,
): Grammar {
    val keywordAlternation = (keywords + booleans).joinToString("|")
    val triplePattern = if (tripleQuotedStrings) "(?s)\"\"\"[\\s\\S]*?\"\"\"|" else ""
    val templatePattern = if (templateLiterals) "|`(?:\\\\.|[^`\\\\])*`" else ""
    val stringPattern = triplePattern + "\"(?:\\\\.|[^\"\\\\\\n])*\"|'(?:\\\\.|[^'\\\\\\n])*'" + templatePattern
    val typePattern = if (primitiveTypes.isEmpty()) {
        "\\b[A-Z][A-Za-z0-9_]*\\b"
    } else {
        "\\b(?:" + primitiveTypes.joinToString("|") + ")\\b|\\b[A-Z][A-Za-z0-9_]*\\b"
    }
    return grammarOf(
        // Line + block comments. Kotlin's nested `/* /* */ */` is non-regular, so a combined
        // matcher handles both `//…` line comments and depth-counted block comments; the regex
        // is a never-match placeholder so the tokenizer delegates to the matcher.
        "comment" to GrammarToken(
            pattern = if (blockCommentMatcher != null) Regex("\\A(?!\\A)") else Regex("(?s)/\\*[\\s\\S]*?\\*/|//[^\\n]*"),
            matcher = blockCommentMatcher,
        ),
        // Triple-quoted strings (Kotlin/Python-style) — a single un-split token spanning lines.
        // Matched before interpolated single-quoted strings and given NO `inside` so their body
        // (incl. inner quotes) isn't re-split.
        "triple" to GrammarToken(
            pattern = if (tripleQuotedStrings) Regex("(?s)\"\"\"[\\s\\S]*?\"\"\"") else Regex("\\A(?!\\A)"),
        ),
        // Interpolated single-quoted strings (only split when interpolation is enabled).
        "string" to GrammarToken(
            pattern = Regex("\"(?:\\\\.|[^\"\\\\\\n])*\"|'(?:\\\\.|[^'\\\\\\n])*'" + templatePattern),
            inside = if (stringInterpolation) kotlinStringInsideGrammar() else null,
        ),
        // Annotations: @Composable, @Override, … (incl. dotted qualified names like
        // @androidx.compose.runtime.Composable).
        "annotation" to GrammarToken(
            pattern = Regex("@[A-Za-z_][\\w.]*"),
        ),
        // Numbers (hex/bin/oct prefixes, decimals w/ exponents, digit separators, type suffixes).
        "number" to GrammarToken(
            pattern = Regex("\\b0[xX][0-9a-fA-F_]+|\\b0[bB][01_]+|\\b0[oO][0-7_]+|\\b\\d[\\d_]*(?:\\.\\d[\\d_]*)?(?:[eE][+-]?\\d+)?[fFlLdD]?\\b"),
        ),
        // Keywords + boolean-ish literals → keyword.
        "keyword" to GrammarToken(
            pattern = Regex("\\b(?:$keywordAlternation)\\b"),
        ),
        // Constant: ALL_CAPS identifier (≥2 chars) → constant.
        "constant" to GrammarToken(
            pattern = Regex("\\b[A-Z][A-Z0-9_]*[A-Z0-9]\\b|\\b[A-Z][A-Z_]\\b"),
            alias = listOf("builtin"),
        ),
        // Property: dotted member access — identifier preceded by `.`. Group 1 is the `.` (kept
        // as plain via the engine's lookbehind), the member is the token → property.
        "property" to GrammarToken(
            pattern = Regex("(\\.)[A-Za-z_]\\w*"),
            lookbehind = true,
            alias = listOf("builtin"),
        ),
        // Function call/def: identifier immediately followed by `(`. Ordered BEFORE type so a
        // Capitalized function name (`Greeting(`) classifies as function, not type — matching the
        // lexer which checks function before type.
        "function" to GrammarToken(
            pattern = Regex("[A-Za-z_]\\w*(?=\\s*\\()"),
        ),
        // Types: lowercase built-ins (int/string/…) and Capitalized names.
        "type" to GrammarToken(
            pattern = Regex(typePattern),
        ),
        // Multi-char operators (longest first), then single-char.
        "operator" to GrammarToken(
            pattern = Regex("!==|===|\\.\\.<|==|!=|>=|<=|&&|\\|\\||\\?:|\\?\\.|::|->|=>|\\?\\.|\\.\\.|[+\\-*/%=<>!&|^~?:]+"),
        ),
        "punctuation" to GrammarToken(
            pattern = Regex("[{}()\\[\\].,;@]"),
        ),
    )
}

/**
 * The `inside` grammar for a Kotlin `"…"` string, splitting interpolation. Boundaries match the
 * lexer: `"Hello, ` → string, `$name` → annotation, `${` → operator, `expr` → annotation, `}` →
 * operator, `"` (closing) → string.
 */
private fun kotlinStringInsideGrammar(): Grammar = grammarOf(
    // String parts: opening `"` + literal text up to `$` or end, AND a lone closing `"`. Both
    // under one rule because the Grammar map keys are unique.
    "string" to GrammarToken(
        pattern = Regex("\"[^\"$]*|\""),
    ),
    // `$name` (no braces) and `${expr}` body (dotted identifier) → annotation. Combined into one
    // rule so both map to `annotation` (map keys are unique).
    "annotation" to GrammarToken(
        pattern = Regex("\\$[A-Za-z_]\\w*|[A-Za-z_]\\w*(?:\\.[A-Za-z_]\\w*)*"),
    ),
    // `${` and `}` are operators.
    "operator" to GrammarToken(
        pattern = Regex("\\$\\{|}"),
    ),
)

/** Kotlin comment matcher: returns the exclusive end index of a comment starting at [start]
 *  — either a `//…` line comment or a depth-balanced `/* /* */ */` block comment — or null if
 *  [start] doesn't begin one. Line comments are regular; block comments need depth counting
 *  (regex can't), which is why Kotlin can't use a pure-regex comment rule. */
internal fun kotlinCommentMatcher(text: String, start: Int): Int? {
    // Line comment: `//` to end of line.
    if (start + 1 < text.length && text[start] == '/' && text[start + 1] == '/') {
        var i = start + 2
        while (i < text.length && text[i] != '\n') i += 1
        return i
    }
    // Block comment with nesting: `/* … */` counting `/*`/`*/` depth.
    if (start + 1 < text.length && text[start] == '/' && text[start + 1] == '*') {
        var depth = 1
        var i = start + 2
        while (i < text.length) {
            if (i + 1 < text.length && text[i] == '/' && text[i + 1] == '*') {
                depth += 1; i += 2
            } else if (i + 1 < text.length && text[i] == '*' && text[i + 1] == '/') {
                depth -= 1; i += 2
                if (depth == 0) return i
            } else {
                i += 1
            }
        }
    }
    return null
}
