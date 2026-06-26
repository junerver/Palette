package xyz.junerver.compose.palette.code.grammar.languages

import xyz.junerver.compose.palette.code.grammar.Grammar
import xyz.junerver.compose.palette.code.grammar.GrammarToken
import xyz.junerver.compose.palette.code.grammar.grammarOf

/**
 * SQL grammar — declarative replacement for [xyz.junerver.compose.palette.code.lexer.SqlLexer].
 *
 * SQL is largely stateless from a highlighting view. The two stateful constructs (block
 * comments `/* … */` and dollar-quoted strings `$tag$ … $tag$`) both have explicit closing
 * delimiters: the block comment is a fixed `(?s)` regex, and the dollar quote uses a
 * backreference (`\1`) so the closing `$tag$` must repeat the opening tag — handling the
 * lexer's `$body$…$body$` multi-line string state without a scanner.
 *
 * Classification mirrors the original lexer (see PaletteCodeHighlighterTest's SQL cases):
 * keywords (SELECT/FROM/…) → `keyword`, types (INTEGER/TEXT/…) → `type`, function names (an
 * identifier immediately followed by `(`) → `function`, `'…'`/`"…"` strings and dollar-quoted
 * bodies → `string`, `--`/`/* */` comments → `comment`, backtick identifiers `` `user` `` →
 * `annotation`, numbers → `number`, operators/punctuation → `operator`/`punctuation`.
 */
internal val SqlGrammar: Grammar = grammarOf(
    // Block + line comments (block comments may span lines).
    "comment" to GrammarToken(
        pattern = Regex("(?s)/\\*.*?\\*/|--[^\\n]*"),
    ),
    // Strings: dollar-quoted (`$tag$…$tag$`, backreference so the closing tag repeats the
    // opening), single-quoted (`'…'`), and double-quoted (`"…"`). Combined into one rule
    // because the Grammar map keys are unique per token name; dollar first so its multi-line
    // body wins over a stray quote inside it.
    "string" to GrammarToken(
        pattern = Regex("(?is)\\$(\\w*)\\$.*?\\$\\1\\$|'(?:''|[^'])*'|\"(?:\"\"|[^\"])*\""),
    ),
    // Backtick-quoted identifiers (MySQL/PG) → annotation.
    "annotation" to GrammarToken(
        pattern = Regex("`[^`]*`"),
    ),
    // Numbers.
    "number" to GrammarToken(
        pattern = Regex("\\b\\d+(?:\\.\\d+)?\\b"),
    ),
    // Keywords (case-insensitive) — matched before the function rule so a keyword like OVER that
    // is immediately followed by `(...)` stays a keyword, not a function.
    "keyword" to GrammarToken(
        pattern = Regex(
            "(?i)\\b(" +
                "ADD|ALL|ALTER|AND|ANY|AS|ASC|BETWEEN|BY|CASE|CHECK|CONSTRAINT|CREATE|CROSS|CURRENT|DEFAULT|DELETE|DESC|DISTINCT|DROP|ELSE|END|EXCEPT|EXISTS|FALSE|FETCH|FIRST|FOLLOWING|FOR|FOREIGN|FROM|FULL|GRANT|GROUP|HAVING|IF|IN|INDEX|INNER|INSERT|INTERSECT|INTO|IS|JOIN|KEY|LAST|LEFT|LIKE|LIMIT|NEXT|NO|NOT|NULL|OFFSET|ONLY|ON|OR|ORDER|OUTER|OVER|PARTITION|PRECEDING|PRIMARY|RANGE|REFERENCES|REPLACE|RIGHT|ROWS|SELECT|SET|TABLE|THEN|TO|TRUE|UNBOUNDED|UNION|UNIQUE|UPDATE|USING|VALUES|VIEW|WHEN|WHERE|WITH" +
                ")\\b",
        ),
    ),
    // Types.
    "type" to GrammarToken(
        pattern = Regex(
            "(?i)\\b(" +
                "BIGINT|BINARY|BLOB|BOOLEAN|CHAR|CHARACTER|CLOB|DATE|DECIMAL|DOUBLE|FLOAT|INTEGER|INT|JSON|JSONB|NCHAR|NCLOB|NUMERIC|NVARCHAR|REAL|SERIAL|SMALLINT|TEXT|TIME|TIMESTAMP|TINYINT|UUID|VARBINARY|VARCHAR" +
                ")\\b",
        ),
    ),
    // Function call: a non-keyword identifier immediately followed by `(` → function (COUNT(…)).
    "function" to GrammarToken(
        pattern = Regex("\\b[A-Za-z_]\\w*(?=\\s*\\()"),
    ),
    // Operators and punctuation.
    "operator" to GrammarToken(
        pattern = Regex("[+\\-*/%=<>!|]+"),
    ),
    "punctuation" to GrammarToken(
        pattern = Regex("[(),.;]"),
    ),
)
