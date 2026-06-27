package xyz.junerver.compose.palette.code.grammar.languages

import xyz.junerver.compose.palette.code.grammar.Grammar
import xyz.junerver.compose.palette.code.grammar.GrammarToken
import xyz.junerver.compose.palette.code.grammar.grammarOf

/**
 * C-family declarative grammars (Kotlin-like: Kotlin / Java / JS / TS / Go), the declarative
 * companions to [xyz.junerver.compose.palette.code.lexer.KotlinLikeLexer].
 *
 * Migration status:
 * - **JavaScript/TypeScript/Java** are fully migrated to grammars here (no stateful constructs
 *   the regex engine can't express — block comments are non-nested, template literals are one
 *   unsplit token).
 * - **Kotlin stays on the hand-written lexer**: it has *nested* block comments (`/* /* */ */`)
 *   and `${}` string interpolation split into multiple tokens, both of which a single-pass
 *   regex grammar cannot reproduce. Its grammar exists for HTML `<script>` embedding only.
 *
 * Classification mirrors the lexer: keywords → `keyword`, primitive/Capitalized types → `type`,
 * annotations (`@Foo`) → `annotation`, numbers → `number`, strings → `string`, comments →
 * `comment`, function calls (name before `(`) → `function`, multi-char operators → `operator`,
 * brackets/`.`,/`;` → `punctuation`. Property (`prevNonWhitespace == '.'`) and Constant
 * (ALL_CAPS) distinctions from the lexer are approximated: dotted names → `property`, all-caps
 * identifiers → `constant`.
 */
internal val KotlinLikeGrammar: Grammar = cFamilyGrammar(
    keywords = listOf(
        "abstract", "as", "break", "by", "catch", "class", "companion", "const", "continue", "data",
        "default", "defer", "do", "else", "enum", "extends", "final", "finally", "fn", "for", "fun",
        "func", "get", "go", "if", "implements", "import", "in", "init", "inline", "interface",
        "internal", "is", "lateinit", "let", "native", "new", "null", "object", "of", "open",
        "operator", "override", "package", "private", "protected", "public", "return", "sealed",
        "set", "static", "super", "suspend", "switch", "synchronized", "this", "throw", "throws",
        "transient", "try", "typealias", "typeof", "val", "var", "vararg", "void", "volatile",
        "when", "where", "while", "yield",
    ),
    booleans = listOf("true", "false", "undefined"),
)

/** Java grammar — replaces the Java KotlinLikeLexer path. Non-nested `/* */` comments.
 *  Primitive types (int/boolean/…) stay in the keyword set to match the lexer, which checks
 *  keywords before primitiveTypes. */
internal val JavaGrammar: Grammar = cFamilyGrammar(
    keywords = listOf(
        "abstract", "assert", "boolean", "break", "byte", "case", "catch", "char", "class",
        "const", "continue", "default", "do", "double", "else", "enum", "extends", "false",
        "final", "finally", "float", "for", "if", "implements", "import", "instanceof", "int",
        "interface", "long", "module", "native", "new", "null", "open", "opens", "package",
        "permits", "private", "protected", "public", "record", "requires", "return", "sealed",
        "short", "static", "strictfp", "super", "switch", "synchronized", "this", "throw",
        "throws", "to", "transient", "true", "try", "uses", "var", "void", "volatile", "with",
        "while", "yield",
    ),
    booleans = listOf("true", "false"),
)

/** TypeScript grammar (incl. JavaScript keywords) — replaces the TS/JS KotlinLikeLexer path. */
internal val TypeScriptGrammar: Grammar = cFamilyGrammar(
    keywords = listOf(
        // JS core
        "async", "await", "break", "case", "catch", "class", "const", "continue", "default",
        "else", "export", "false", "for", "from", "function", "if", "import", "let", "new", "null",
        "return", "switch", "this", "throw", "true", "try", "undefined", "var", "while",
        // TS additions
        "as", "declare", "enum", "implements", "interface", "namespace", "private", "protected",
        "public", "readonly", "type", "abstract",
    ),
    booleans = listOf("true", "false", "undefined", "null"),
    primitiveTypes = listOf("boolean", "never", "number", "string", "unknown", "void", "any"),
    templateLiterals = true,
)

/**
 * Builds a C-family [Grammar] from a keyword list. Shared structure keeps Java/TS/JS consistent;
 * only the keyword set and a few flags (primitive types, template literals) differ.
 *
 * @param primitiveTypes lowercase built-in type names (`int`, `string`, …) classified as `type`.
 * @param templateLiterals when true, backtick template literals `` `…` `` are matched as strings
 *   (TypeScript/JS). Off for Java.
 */
private fun cFamilyGrammar(
    keywords: List<String>,
    booleans: List<String>,
    primitiveTypes: List<String> = emptyList(),
    templateLiterals: Boolean = false,
): Grammar {
    val keywordAlternation = (keywords + booleans).joinToString("|")
    val stringPattern = if (templateLiterals) {
        "\"(?:\\\\.|[^\"\\\\\\n])*\"|'(?:\\\\.|[^'\\\\\\n])*'|`(?:\\\\.|[^`\\\\])*`"
    } else {
        "\"(?:\\\\.|[^\"\\\\\\n])*\"|'(?:\\\\.|[^'\\\\\\n])*'"
    }
    val typePattern = if (primitiveTypes.isEmpty()) {
        "\\b[A-Z][A-Za-z0-9_]*\\b"
    } else {
        "\\b(?:" + primitiveTypes.joinToString("|") + ")\\b|\\b[A-Z][A-Za-z0-9_]*\\b"
    }
    return grammarOf(
        // Line + (non-nested) block comments. Block comments span lines.
        "comment" to GrammarToken(
            pattern = Regex("(?s)/\\*[\\s\\S]*?\\*/|//[^\\n]*"),
        ),
        // Strings: basic, char, and (optionally) backtick template literals.
        "string" to GrammarToken(
            pattern = Regex(stringPattern),
        ),
        // Annotations: @Composable, @Override, …
        "annotation" to GrammarToken(
            pattern = Regex("@[A-Za-z_]\\w*"),
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
        // Types: lowercase built-ins (int/string/…) and Capitalized names. Matched after
        // property/constant so those win where applicable.
        "type" to GrammarToken(
            pattern = Regex(typePattern),
        ),
        // Function call: identifier immediately followed by `(`.
        "function" to GrammarToken(
            pattern = Regex("[A-Za-z_]\\w*(?=\\s*\\()"),
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
