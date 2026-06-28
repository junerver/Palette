package xyz.junerver.compose.palette.code.grammar.languages

import xyz.junerver.compose.palette.code.grammar.Grammar
import xyz.junerver.compose.palette.code.grammar.GrammarToken
import xyz.junerver.compose.palette.code.grammar.grammarOf

/**
 * Rust grammar.
 *
 * Built on [cFamilyGrammar] for the shared C-family scaffolding (operators, numbers, functions,
 * punctuation, `//` and `/* */` comments), then Rust-specific constructs are layered on top:
 *   - attribute macros `#[derive(Debug)]` / `#![crate_type]` → `annotation`
 *   - raw strings `r"…"`, `r#"…"#`, byte/byte-raw strings `b"…"`, `br"…"` → `string`
 *
 * Rust has no string interpolation in the general case (format args are macros, not syntax), so
 * no `inside` grammar is needed.
 */
private val rustKeywords = listOf(
    "as", "async", "await", "break", "const", "continue", "crate", "dyn", "else",
    "enum", "extern", "false", "fn", "for", "if", "impl", "in", "let", "loop",
    "match", "mod", "move", "mut", "pub", "ref", "return", "self", "Self",
    "static", "struct", "super", "trait", "true", "type", "unsafe", "use",
    "where", "while", "abstract", "become", "box", "do", "final", "macro", "override",
    "priv", "typeof", "unsized", "virtual", "yield", "try", "union",
)

private val rustBooleans = listOf("true", "false")

private val rustPrimitiveTypes = listOf(
    "i8", "i16", "i32", "i64", "i128", "isize",
    "u8", "u16", "u32", "u64", "u128", "usize",
    "f32", "f64", "bool", "char", "str", "String", "Vec", "Option", "Result",
    "Box", "Rc", "Arc", "Cell", "RefCell", "HashMap", "HashSet", "BTreeMap",
)

private val rustKeywordAlternation = (rustKeywords + rustBooleans).joinToString("|")
private val rustTypePattern =
    "\\b(?:" + rustPrimitiveTypes.joinToString("|") + ")\\b|\\b[A-Z][A-Za-z0-9_]*\\b"

/**
 * Full Rust grammar: factory scaffolding + Rust attributes (`#[…]`) and raw strings.
 */
internal val RustGrammar: Grammar = grammarOf(
    // Attributes: `#[derive(Debug)]` and inner `#![…]`. Classified as annotation.
    "annotation" to GrammarToken(
        pattern = Regex("(?s)(#!?)(?=\\[).*?\\]"),
        greedy = true,
    ),
    // Comments: `//`, `///` (doc), `//!` (inner doc), and non-nested `/* */`.
    "comment" to GrammarToken(
        pattern = Regex("(?s)/\\*[\\s\\S]*?\\*/|//[^\n]*"),
    ),
    // Strings: raw `r"…"`, raw hash `r#"…"#`, byte `b"…"`, byte-raw `br"…"`, and regular `"…"`/`'…'`.
    // Hash-prefixed raw strings close on the matching number of `#`.
    "string" to GrammarToken(
        pattern = Regex("(?s)(?:br?|r|#)?\"(?:\\\\.|[^\"\\\\])*\"|br?\"(?:\\\\.|[^\"\\\\])*\"|r#*\"[\\s\\S]*?\"#*|'(?:\\\\.|[^'\\\\])*'"),
        greedy = true,
    ),
    "number" to GrammarToken(
        pattern = Regex("\\b0[xX][0-9a-fA-F_]+|\\b0[bB][01_]+|\\b0o[0-7_]+|\\b\\d[\\d_]*(?:\\.\\d[\\d_]*)?(?:[eE][+-]?\\d+)?(?:f32|f64|i8|i16|i32|i64|i128|isize|u8|u16|u32|u64|u128|usize)?\\b"),
    ),
    "keyword" to GrammarToken(
        pattern = Regex("\\b(?:$rustKeywordAlternation)\\b"),
    ),
    "constant" to GrammarToken(
        pattern = Regex("\\b[A-Z][A-Z0-9_]*[A-Z0-9]\\b|\\b[A-Z][A-Z_]\\b"),
        alias = listOf("builtin"),
    ),
    "property" to GrammarToken(
        pattern = Regex("(\\.)[A-Za-z_]\\w*"),
        lookbehind = true,
        alias = listOf("builtin"),
    ),
    "function" to GrammarToken(
        pattern = Regex("[A-Za-z_]\\w*(?=\\s*\\(|<)"),
    ),
    "type" to GrammarToken(
        pattern = Regex(rustTypePattern),
    ),
    "operator" to GrammarToken(
        pattern = Regex("->|=>|::|==|!=|>=|<=|&&|\\|\\||\\.\\.|\\?\\S|[+\\-*/%=<>!&|^~?:@]+"),
    ),
    "punctuation" to GrammarToken(
        pattern = Regex("[{}()\\[\\].,;]"),
    ),
)
