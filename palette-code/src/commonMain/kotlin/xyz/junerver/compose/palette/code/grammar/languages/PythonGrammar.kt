package xyz.junerver.compose.palette.code.grammar.languages

import xyz.junerver.compose.palette.code.grammar.Grammar
import xyz.junerver.compose.palette.code.grammar.GrammarToken
import xyz.junerver.compose.palette.code.grammar.grammarOf

/**
 * Python grammar — declarative replacement for
 * [xyz.junerver.compose.palette.code.lexer.PythonLexer].
 *
 * Python's two stateful constructs both have explicit delimiters, so a pure-regex grammar can
 * reproduce them:
 * - **f-strings** (`f"…"` / `f'…'`): modelled as a token with an `inside` grammar that splits
 *   the opening `f"prefix text` → string, `{expr}` → operator+annotation, and the closing `"` →
 *   string, matching the lexer's [tokenizePythonFString] boundaries exactly.
 * - **triple-quoted strings** (`"""…"""` / `'''…'''`): a `(?s)` regex spans lines; the highlighter
 *   line-splits the resulting token.
 *
 * Classification mirrors the lexer (see PaletteCodeHighlighterTest's python cases):
 * `@decorator` → `annotation`, `def`/`class`/… → `keyword`, primitive/capitalized types → `type`,
 * function defs (`name(`) → `function`, numbers → `number`, strings/f-strings → `string`,
 * `# …` comments → `comment`, operators/punctuation → `operator`/`punctuation`.
 */
internal val PythonGrammar: Grammar = grammarOf(
    // Line comments: `# …`.
    "comment" to GrammarToken(
        pattern = Regex("#[^\\n]*"),
    ),
    // Decorator: @decorator_name → annotation.
    "annotation" to GrammarToken(
        pattern = Regex("@[A-Za-z_]\\w*"),
    ),
    // f-strings: must run BEFORE the plain string rule so the `f"…"` token (with its `inside`
    // interpolation grammar) wins over the bare-quoted string that would otherwise swallow the
    // body. The prefix + quotes are string, but `{expr}` interpolations split into operator +
    // annotation + operator.
    "fstring" to GrammarToken(
        pattern = Regex("f\"(?:\\\\.|\\{|}|[^\"\\\\\\n])*\"|f'(?:\\\\.|\\{|}|[^'\\\\\\n])*'"),
        inside = grammarOf(
            // String parts: the opening `f"…` (prefix + quote + body text up to `{`/end) AND the
            // bare closing quote. Both are `string`, combined into one rule because the Grammar
            // map keys are unique per token name.
            "string" to GrammarToken(
                pattern = Regex("f\"[^{}\"]*|f'[^{}']*|['\"]"),
            ),
            // `{` and `}` are operators.
            "operator" to GrammarToken(
                pattern = Regex("[{}]"),
            ),
            // Interpolated identifier → annotation.
            "annotation" to GrammarToken(
                pattern = Regex("[A-Za-z_]\\w*"),
            ),
        ),
    ),
    // Strings: triple-quoted (span lines, matched first), byte/raw-prefixed, and single-line
    // basic/literal. One alternation under a single rule name (the Grammar map keys are unique).
    "string" to GrammarToken(
        pattern = Regex(
            "(?s)\"\"\"[\\s\\S]*?\"\"\"|'''[\\s\\S]*?'''" + // triple-quoted
            "|[rbuRBU]*\"(?:\\\\.|[^\"\\\\\\n])*\"|[rbuRBU]*'(?:\\\\.|[^'\\\\\\n])*'", // basic/raw/byte
        ),
    ),
    // Numbers (incl. hex/bin/oct/complex/underscore separators).
    "number" to GrammarToken(
        pattern = Regex("\\b0[xX][0-9a-fA-F_]+|\\b0[bB][01_]+|\\b0[oO][0-7_]+|\\b\\d[\\d_]*(?:\\.\\d[\\d_]*)?(?:[eE][+-]?\\d+)?[jJ]?\\b"),
    ),
    // Keywords.
    "keyword" to GrammarToken(
        pattern = Regex(
            "\\b(?:False|None|True|and|as|assert|async|await|break|class|continue|def|del|elif|else|except|finally|for|from|global|if|import|in|is|lambda|nonlocal|not|or|pass|raise|return|try|while|with|yield|match|case)\\b",
        ),
    ),
    // Types: built-ins + capitalized names.
    "type" to GrammarToken(
        pattern = Regex("\\b(?:int|float|complex|str|bytes|bytearray|bool|list|tuple|set|frozenset|dict|range|type|object|None)\\b|\\b[A-Z][A-Za-z0-9_]*\\b"),
    ),
    // Function call / def: identifier before `(`.
    "function" to GrammarToken(
        pattern = Regex("[A-Za-z_]\\w*(?=\\s*\\()"),
    ),
    // Operators (multi-char first) and punctuation.
    "operator" to GrammarToken(
        pattern = Regex("->|==|!=|>=|<=|//|\\*\\*|:=|\\+\\+|--|[+\\-*/%=<>!&|^~@]"),
    ),
    "punctuation" to GrammarToken(
        pattern = Regex("[{}()\\[\\].,;:]"),
    ),
)
