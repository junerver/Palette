package xyz.junerver.compose.palette.code.grammar.languages

import xyz.junerver.compose.palette.code.grammar.Grammar
import xyz.junerver.compose.palette.code.grammar.GrammarToken
import xyz.junerver.compose.palette.code.grammar.grammarOf

/**
 * Kotlin-like grammar (Kotlin / Java / JS / TS / C-family) — declarative companion to
 * [xyz.junerver.compose.palette.code.lexer.KotlinLikeLexer].
 *
 * Currently registered only for `javascript`/`js` so HTML `<script>` blocks can be highlighted
 * via the engine's dynamic-embedding hook. The fuller Kotlin/Java/TypeScript languages stay on
 * the hand-written lexer for now (string-template state and triple-quoted strings are hard to
 * express in a single-pass regex); they migrate once this grammar covers those cases too.
 *
 * Classification mirrors the lexer for the constructs it handles: keywords (fun/val/const/…)
 * → `keyword`, types (Capitalized identifiers) → `type`, annotations (`@Foo`) → `annotation`,
 * numbers → `number`, strings → `string`, comments → `comment`, function calls (name before
 * `(`) → `function`, operators/punctuation → `operator`/`punctuation`. Unknown identifiers
 * (e.g. `console`) fall through to plain text — matching the lexer's behaviour for the HTML
 * embed test.
 */
internal val KotlinLikeGrammar: Grammar = grammarOf(
    // Line + block comments. Block comments span lines.
    "comment" to GrammarToken(
        pattern = Regex("(?s)/\\*[\\s\\S]*?\\*/|//[^\\n]*"),
    ),
    // Strings: basic ("…") and template (`…`), single-line. (Template literals `…` are common
    // in JS/TS.) Multi-line/triple-quoted are intentionally not handled here yet.
    "string" to GrammarToken(
        pattern = Regex("\"(?:\\\\.|[^\"\\\\\\n])*\"|'(?:\\\\.|[^'\\\\\\n])*'|`(?:\\\\.|[^`\\\\])*`"),
    ),
    // Annotations: @Composable, @Override, …
    "annotation" to GrammarToken(
        pattern = Regex("@[A-Za-z_]\\w*"),
    ),
    // Numbers (incl. decimals, exponents, hex/0b/0o prefixes, digit underscores).
    "number" to GrammarToken(
        pattern = Regex("\\b0[xX][0-9a-fA-F_]+|\\b0[bB][01_]+|\\b0[oO][0-7_]+|\\b\\d[\\d_]*(?:\\.\\d[\\d_]*)?(?:[eE][+-]?\\d+)?[fFlLdD]?\\b"),
    ),
    // Keywords (a representative C/JVM/JS set).
    "keyword" to GrammarToken(
        pattern = Regex(
            "\\b(?:abstract|as|break|by|catch|class|companion|const|continue|data|default|defer|do|else|enum|extends|final|finally|fn|for|fun|func|get|go|if|implements|import|in|init|inline|interface|internal|is|lateinit|let|native|new|null|object|of|open|operator|override|package|private|protected|public|return|sealed|set|static|super|suspend|switch|synchronized|this|throw|throws|transient|try|typealias|typeof|val|var|vararg|void|volatile|when|where|while|yield)\\b",
        ),
    ),
    // Boolean + null-ish literals (true/false/null) — kept separate so they map to `keyword`
    // via the same bucket as above. Merged because Grammar map keys are unique.
    "boolean" to GrammarToken(
        pattern = Regex("\\b(?:true|false|undefined)\\b"),
        alias = listOf("keyword"),
    ),
    // Built-in value types (Capitalized identifiers like String, Int, Object) → type. Also
    // covers class-name references.
    "type" to GrammarToken(
        pattern = Regex("\\b[A-Z][A-Za-z0-9_]*\\b"),
    ),
    // Function call: identifier immediately followed by `(`.
    "function" to GrammarToken(
        pattern = Regex("[A-Za-z_]\\w*(?=\\s*\\()"),
    ),
    // Operators and punctuation.
    "operator" to GrammarToken(
        pattern = Regex("[+\\-*/%=<>!&|^~?:]+|->"),
    ),
    "punctuation" to GrammarToken(
        pattern = Regex("[{}()\\[\\].,;@]"),
    ),
)
