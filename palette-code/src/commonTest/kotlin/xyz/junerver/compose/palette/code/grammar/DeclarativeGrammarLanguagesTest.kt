package xyz.junerver.compose.palette.code.grammar

import xyz.junerver.compose.palette.code.grammar.languages.CGrammar
import xyz.junerver.compose.palette.code.grammar.languages.CppGrammar
import xyz.junerver.compose.palette.code.grammar.languages.CssGrammar
import xyz.junerver.compose.palette.code.grammar.languages.GoGrammar
import xyz.junerver.compose.palette.code.grammar.languages.HtmlGrammar
import xyz.junerver.compose.palette.code.grammar.languages.IniGrammar
import xyz.junerver.compose.palette.code.grammar.languages.JavaGrammar
import xyz.junerver.compose.palette.code.grammar.languages.KotlinGrammar
import xyz.junerver.compose.palette.code.grammar.languages.KotlinLikeGrammar
import xyz.junerver.compose.palette.code.grammar.languages.MarkdownGrammar
import xyz.junerver.compose.palette.code.grammar.languages.PythonGrammar
import xyz.junerver.compose.palette.code.grammar.languages.RustGrammar
import xyz.junerver.compose.palette.code.grammar.languages.SqlGrammar
import xyz.junerver.compose.palette.code.grammar.languages.TomlGrammar
import xyz.junerver.compose.palette.code.grammar.languages.TypeScriptGrammar
import xyz.junerver.compose.palette.code.grammar.languages.YamlGrammar
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Grammar-level contracts for the migrated declarative grammars (TOML, INI/properties). These
 * pin the token classification each grammar must produce, independent of the line-splitting
 * done by [GrammarHighlighter]; the end-to-end behaviour is also covered by
 * PaletteCodeHighlighterTest's lexer-parity cases.
 */
class DeclarativeGrammarLanguagesTest {

    // ── TOML ──────────────────────────────────────────────────────────────

    @Test
    fun tomlGrammar_classifiesTableNameAsType() {
        val tokens = GrammarTokenizer.tokenize("[project]", TomlGrammar)
        // Header recurses: '[' punctuation, 'project' type, ']' punctuation.
        assertTrue(tokens.any { it.text == "project" && it.type.name == "type" })
        assertTrue(tokens.any { it.text == "[" && it.type.name == "punctuation" })
    }

    @Test
    fun tomlGrammar_classifiesKeyAsKeywordAndValueAsTypes() {
        val tokens = GrammarTokenizer.tokenize("""name = "Palette"""", TomlGrammar)
        assertTrue(tokens.any { it.text == "name" && it.type.name == "keyword" })
        assertTrue(tokens.any { it.text == "=" && it.type.name == "operator" })
        assertTrue(tokens.any { it.text == "\"Palette\"" && it.type.name == "string" })
    }

    @Test
    fun tomlGrammar_classifiesBooleansAsKeyword() {
        val tokens = GrammarTokenizer.tokenize("enabled = true", TomlGrammar)
        assertTrue(tokens.any { it.text == "true" && it.type.name == "keyword" })
    }

    @Test
    fun tomlGrammar_spansMultilineStringAsOneToken() {
        val src = "description = \"\"\"\nHeavy duty\nhammer\n\"\"\""
        val tokens = GrammarTokenizer.tokenize(src, TomlGrammar)
        // The multi-line body is a single string token (the highlighter splits it per line).
        assertTrue(tokens.any { it.type.name == "string" && it.text.contains("Heavy duty") })
    }

    // ── CSS ───────────────────────────────────────────────────────────────

    @Test
    fun cssGrammar_classifiesAtRuleAsAnnotation() {
        val tokens = GrammarTokenizer.tokenize("@media screen { }", CssGrammar)
        assertTrue(tokens.any { it.text == "@media" && it.type.name == "annotation" })
    }

    @Test
    fun cssGrammar_classifiesClassSelectorAsType() {
        val tokens = GrammarTokenizer.tokenize(".card { }", CssGrammar)
        assertTrue(tokens.any { it.text == ".card" && it.type.name == "type" })
    }

    @Test
    fun cssGrammar_classifiesHexColourAsNumberButIdSelectorAsType() {
        assertEquals("number", GrammarTokenizer.tokenize("#fff", CssGrammar).first().type.name)
        assertEquals("type", GrammarTokenizer.tokenize("#main", CssGrammar).first().type.name)
    }

    @Test
    fun cssGrammar_classifiesPropertyAndValueKeywordAsKeyword() {
        val src = ".card { color: #fff; display: flex; }"
        val tokens = GrammarTokenizer.tokenize(src, CssGrammar)
        assertTrue(tokens.any { it.text == "color" && it.type.name == "keyword" })
        assertTrue(tokens.any { it.text == "flex" && it.type.name == "keyword" })
    }

    @Test
    fun cssGrammar_spansBlockCommentAsOneToken() {
        val tokens = GrammarTokenizer.tokenize("/* visible\ncomment */", CssGrammar)
        assertTrue(tokens.any { it.type.name == "comment" && it.text.contains("visible") })
    }

    // ── HTML / XML ────────────────────────────────────────────────────────

    @Test
    fun htmlGrammar_classifiesTagNameAsTypeAndAttributesAsAnnotation() {
        val tokens = GrammarTokenizer.tokenize("""<section class="card">""", HtmlGrammar)
        assertTrue(tokens.any { it.text == "section" && it.type.name == "type" })
        assertTrue(tokens.any { it.text == "class" && it.type.name == "annotation" })
        assertTrue(tokens.any { it.text == "\"card\"" && it.type.name == "string" })
    }

    @Test
    fun htmlGrammar_classifiesClosingTagPunctuation() {
        val tokens = GrammarTokenizer.tokenize("</section>", HtmlGrammar)
        assertTrue(tokens.any { it.text == "</" && it.type.name == "punctuation" })
        assertTrue(tokens.any { it.text == "section" && it.type.name == "type" })
    }

    @Test
    fun htmlGrammar_embedsCssInStyleBlock() {
        val src = "<style>\n.card { display: flex; }\n</style>"
        val tokens = GrammarTokenizer.tokenize(src, HtmlGrammar)
        // Wrapper tag name + embedded CSS body both classify.
        assertTrue(tokens.any { it.text == "style" && it.type.name == "type" })
        assertTrue(tokens.any { it.text == ".card" && it.type.name == "type" })
        assertTrue(tokens.any { it.text == "display" && it.type.name == "keyword" })
    }

    @Test
    fun htmlGrammar_embedsJsInScriptBlock() {
        val src = "<script>\nconst answer = 42\n</script>"
        val tokens = GrammarTokenizer.tokenize(src, HtmlGrammar)
        assertTrue(tokens.any { it.text == "script" && it.type.name == "type" })
        assertTrue(tokens.any { it.text == "const" && it.type.name == "keyword" })
        assertTrue(tokens.any { it.text == "42" && it.type.name == "number" })
    }

    // ── JavaScript (Kotlin-like, for HTML embedding) ──────────────────────

    @Test
    fun jsGrammar_classifiesKeywordNumberAndLeavesUnknownIdentifierPlain() {
        val tokens = GrammarTokenizer.tokenize("const x = 42", KotlinLikeGrammar)
        assertTrue(tokens.any { it.text == "const" && it.type.name == "keyword" })
        assertTrue(tokens.any { it.text == "42" && it.type.name == "number" })
        // `x` is not a keyword/builtin/capitalised-type in this grammar → plain text.
        assertTrue(tokens.any { it.text.contains("x") && it.type.name == "plain" })
    }

    // ── Markdown ──────────────────────────────────────────────────────────

    @Test
    fun markdownGrammar_classifiesHeadingAndListMarkers() {
        val tokens = GrammarTokenizer.tokenize("## Title\n- item", MarkdownGrammar)
        assertTrue(tokens.any { it.text == "##" && it.type.name == "keyword" })
        assertTrue(tokens.any { it.text == "-" && it.type.name == "operator" })
    }

    @Test
    fun markdownGrammar_classifiesTaskCheckboxAndInlineCode() {
        val tokens = GrammarTokenizer.tokenize("- [x] done `code`", MarkdownGrammar)
        assertTrue(tokens.any { it.text == "[x]" && it.type.name == "annotation" })
        assertTrue(tokens.any { it.text == "`code`" && it.type.name == "string" })
    }

    @Test
    fun markdownGrammar_classifiesLinkTextAndUrl() {
        val tokens = GrammarTokenizer.tokenize("[docs](https://example.com/docs)", MarkdownGrammar)
        assertTrue(tokens.any { it.text == "docs" && it.type.name == "type" })
        assertTrue(tokens.any { it.text == "https://example.com/docs" && it.type.name == "string" })
    }

    @Test
    fun markdownGrammar_embedsFencedCodeBodyAndClassifiesInfoString() {
        // fenced body `kotlin` runs through the full highlighter (lexer fallback), so `val`
        // becomes a keyword; the fence delimiter and info string classify as annotation/type.
        val src = "```kotlin\nval component = \"PMarkdownViewer\"\n```"
        val tokens = GrammarTokenizer.tokenize(src, MarkdownGrammar)
        assertTrue(tokens.any { it.text == "```" && it.type.name == "annotation" })
        assertTrue(tokens.any { it.text == "kotlin" && it.type.name == "type" })
        assertTrue(tokens.any { it.text == "val" && it.type.name == "keyword" })
    }

    // ── SQL ───────────────────────────────────────────────────────────────

    @Test
    fun sqlGrammar_classifiesKeywordsTypesAndFunctionsCaseInsensitively() {
        val tokens = GrammarTokenizer.tokenize("select count(*) AS total from Integer", SqlGrammar)
        assertTrue(tokens.any { it.text == "select" && it.type.name == "keyword" })
        assertTrue(tokens.any { it.text == "count" && it.type.name == "function" })
        assertTrue(tokens.any { it.text == "AS" && it.type.name == "keyword" })
        assertTrue(tokens.any { it.text == "Integer" && it.type.name == "type" })
    }

    @Test
    fun sqlGrammar_keywordBeforeFunctionSoOverStaysKeyword() {
        // OVER is a keyword but also followed by `(`; the keyword rule wins → keyword, not fn.
        val tokens = GrammarTokenizer.tokenize("OVER (", SqlGrammar)
        assertTrue(tokens.any { it.text == "OVER" && it.type.name == "keyword" })
    }

    @Test
    fun sqlGrammar_backtickIdentifierIsAnnotation() {
        assertEquals("annotation", GrammarTokenizer.tokenize("`user`", SqlGrammar).first().type.name)
    }

    @Test
    fun sqlGrammar_dollarQuotedStringMatchesRepeatedTagAcrossLines() {
        // The backreference (\1) forces the closing $body$ to repeat the opening tag.
        val body = "SELECT " + "$" + "body" + "$\nline one\nline two\n" + "$" + "body" + "$ AS x"
        val tokens = GrammarTokenizer.tokenize(body, SqlGrammar)
        assertTrue(tokens.any { it.type.name == "string" && it.text.contains("line one") })
    }

    // ── INI / properties ──────────────────────────────────────────────────

    @Test
    fun iniGrammar_classifiesSectionNameAsType() {
        val tokens = GrammarTokenizer.tokenize("[server.main]", IniGrammar)
        assertTrue(tokens.any { it.text == "server.main" && it.type.name == "type" })
    }

    @Test
    fun iniGrammar_classifiesEqualsKeyAsKeyword() {
        val tokens = GrammarTokenizer.tokenize("host = localhost", IniGrammar)
        assertTrue(tokens.any { it.text == "host" && it.type.name == "keyword" })
        assertTrue(tokens.any { it.text == "=" && it.type.name == "operator" })
    }

    @Test
    fun iniGrammar_classifiesColonKeyAsKeyword() {
        val tokens = GrammarTokenizer.tokenize("app.enabled:false", IniGrammar)
        assertTrue(tokens.any { it.text == "app.enabled" && it.type.name == "keyword" })
        assertTrue(tokens.any { it.text == ":" && it.type.name == "operator" })
    }

    @Test
    fun iniGrammar_classifiesVariableInterpolationAsAnnotation() {
        val tokens = GrammarTokenizer.tokenize("path = \${APP_HOME}", IniGrammar)
        assertTrue(tokens.any { it.text == "\${APP_HOME}" && it.type.name == "annotation" })
    }

    @Test
    fun iniGrammar_classifiesCommentFromHashOrSemicolon() {
        assertEquals("comment", GrammarTokenizer.tokenize("# note", IniGrammar).first().type.name)
        assertEquals("comment", GrammarTokenizer.tokenize("; note", IniGrammar).first().type.name)
    }

    // ── Python ───────────────────────────────────────────────────────────

    @Test
    fun pythonGrammar_classifiesDecoratorKeywordTypeFunction() {
        val tokens = GrammarTokenizer.tokenize("@dataclass\ndef greet(name: str) -> str:", PythonGrammar)
        assertTrue(tokens.any { it.text == "@dataclass" && it.type.name == "annotation" })
        assertTrue(tokens.any { it.text == "def" && it.type.name == "keyword" })
        assertTrue(tokens.any { it.text == "greet" && it.type.name == "function" })
        assertTrue(tokens.any { it.text == "str" && it.type.name == "type" })
    }

    @Test
    fun pythonGrammar_fStringSplitsInterpolation() {
        // Build `f"Hello, {name}"` without a `$` (real Python interpolation).
        val src = "f\"Hello, " + "{" + "name" + "}\""
        val tokens = GrammarTokenizer.tokenize(src, PythonGrammar)
        // `f"Hello, ` is one string token (prefix + body up to `{`).
        assertTrue(tokens.any { it.text == "f\"Hello, " && it.type.name == "string" })
        assertTrue(tokens.any { it.text == "{" && it.type.name == "operator" })
        assertTrue(tokens.any { it.text == "name" && it.type.name == "annotation" })
        assertTrue(tokens.any { it.text == "}" && it.type.name == "operator" })
        // Closing quote is its own string token.
        assertTrue(tokens.any { it.text == "\"" && it.type.name == "string" })
    }

    @Test
    fun pythonGrammar_classifiesComment() {
        assertEquals("comment", GrammarTokenizer.tokenize("# visible comment", PythonGrammar).first().type.name)
    }

    // ── Java ──────────────────────────────────────────────────────────────

    @Test
    fun javaGrammar_classifiesModernKeywords() {
        val tokens = GrammarTokenizer.tokenize("public sealed interface Result permits Success {}", JavaGrammar)
        assertTrue(tokens.any { it.text == "sealed" && it.type.name == "keyword" })
        assertTrue(tokens.any { it.text == "permits" && it.type.name == "keyword" })
    }

    @Test
    fun javaGrammar_classifiesPrimitiveKeywordAndCapitalizedType() {
        val tokens = GrammarTokenizer.tokenize("int count = 0; String name;", JavaGrammar)
        // Primitive `int` is a keyword (matches lexer: keywords checked before primitiveTypes).
        assertTrue(tokens.any { it.text == "int" && it.type.name == "keyword" })
        // Capitalized `String` is a type.
        assertTrue(tokens.any { it.text == "String" && it.type.name == "type" })
    }

    // ── TypeScript ────────────────────────────────────────────────────────

    @Test
    fun typescriptGrammar_classifiesKeywordsAndTypesAndFunctions() {
        val tokens = GrammarTokenizer.tokenize("export function greet(name: string) {}", TypeScriptGrammar)
        assertTrue(tokens.any { it.text == "export" && it.type.name == "keyword" })
        assertTrue(tokens.any { it.text == "greet" && it.type.name == "function" })
        assertTrue(tokens.any { it.text == "string" && it.type.name == "type" })
    }

    @Test
    fun typescriptGrammar_treatsTemplateLiteralAsOneString() {
        val src = "`Hello, " + "\${name}" + "`"
        val tokens = GrammarTokenizer.tokenize(src, TypeScriptGrammar)
        assertTrue(tokens.any { it.type.name == "string" && it.text.contains("Hello") })
    }

    // ── Kotlin ────────────────────────────────────────────────────────────

    @Test
    fun kotlinGrammar_classifiesNestedBlockCommentAsOneToken() {
        // Nested `/* /* */ */` — non-regular, modelled by a depth-counting matcher. The whole
        // span (incl. inner `/* */`) is one comment token.
        val src = "val x = 1 /* outer /* inner */ still outer */ + 2"
        val tokens = GrammarTokenizer.tokenize(src, KotlinGrammar)
        assertTrue(tokens.any { it.type.name == "comment" && it.text.contains("still outer") })
    }

    @Test
    fun kotlinGrammar_interpolatedStringSplitsDollarVarAndBraceExpr() {
        // `\$name` → annotation, `\${ expr }` → operator/annotation/operator; string parts stay string.
        val src = "\"" + "Hello, " + "\$" + "name " + "\$" + "{user.displayName}\""
        val tokens = GrammarTokenizer.tokenize(src, KotlinGrammar)
        assertTrue(tokens.any { it.text == "\"Hello, " && it.type.name == "string" })
        assertTrue(tokens.any { it.text == "\$name" && it.type.name == "annotation" })
        assertTrue(tokens.any { it.text == "\${" && it.type.name == "operator" })
        assertTrue(tokens.any { it.text == "}" && it.type.name == "operator" })
    }

    @Test
    fun kotlinGrammar_classifiesCapitalizedFunctionNameBeforeParenAsFunction() {
        val tokens = GrammarTokenizer.tokenize("fun Greeting() {}", KotlinGrammar)
        assertTrue(tokens.any { it.text == "fun" && it.type.name == "keyword" })
        assertTrue(tokens.any { it.text == "Greeting" && it.type.name == "function" })
    }

    // ── YAML ──────────────────────────────────────────────────────────────

    @Test
    fun yamlGrammar_classifiesKeysAndScalarsAndTags() {
        val tokens = GrammarTokenizer.tokenize("name: Palette\nresource: !Ref App", YamlGrammar)
        assertTrue(tokens.any { it.text == "name" && it.type.name == "keyword" })
        assertTrue(tokens.any { it.text == "!Ref" && it.type.name == "annotation" })
    }

    @Test
    fun yamlGrammar_blockScalarConsumesIndentedLines() {
        val src = "description: |\n  first line\n  second line\n..."
        val tokens = GrammarTokenizer.tokenize(src, YamlGrammar)
        // The block-scalar matcher consumes the indicator line + indented body.
        assertTrue(tokens.any { it.text == "|" && it.type.name == "operator" })
        assertTrue(tokens.any { it.text.contains("first line") && it.type.name == "string" })
        assertTrue(tokens.any { it.text.contains("second line") && it.type.name == "string" })
    }

    @Test
    fun yamlGrammar_classifiesListMarkerAndDocumentMarkers() {
        val tokens = GrammarTokenizer.tokenize("---\nitems:\n  - first\n  - second\n...", YamlGrammar)
        assertTrue(tokens.any { it.text == "-" && it.type.name == "list-marker" })
        assertTrue(tokens.any { it.text == "---" && it.type.name == "operator" })
        assertTrue(tokens.any { it.text == "..." && it.type.name == "operator" })
    }

    // ── C ────────────────────────────────────────────────────────────────

    @Test
    fun cGrammar_classifiesKeywordsPrimitivesAndBlockComment() {
        val src = "/* header */\nint main(void) { return 0; }"
        val tokens = GrammarTokenizer.tokenize(src, CGrammar)
        assertTrue(tokens.any { it.text == "int" && it.type.name == "keyword" })
        assertTrue(tokens.any { it.text == "return" && it.type.name == "keyword" })
        assertTrue(tokens.any { it.text == "main" && it.type.name == "function" })
        assertTrue(tokens.any { it.text.contains("header") && it.type.name == "comment" })
    }

    @Test
    fun cGrammar_classifiesStdintTypesAndHexNumber() {
        val tokens = GrammarTokenizer.tokenize("uint32_t count = 0xFF;", CGrammar)
        assertTrue(tokens.any { it.text == "uint32_t" && it.type.name == "type" })
        assertTrue(tokens.any { it.text == "0xFF" && it.type.name == "number" })
    }

    // ── C++ ──────────────────────────────────────────────────────────────

    @Test
    fun cppGrammar_classifiesClassTemplateAndNamespace() {
        val src = "namespace app { template<typename T> class Vec { T x; }; }"
        val tokens = GrammarTokenizer.tokenize(src, CppGrammar)
        assertTrue(tokens.any { it.text == "namespace" && it.type.name == "keyword" })
        assertTrue(tokens.any { it.text == "template" && it.type.name == "keyword" })
        assertTrue(tokens.any { it.text == "class" && it.type.name == "keyword" })
        // Capitalized Vec and T → type.
        assertTrue(tokens.any { it.text == "Vec" && it.type.name == "type" })
    }

    @Test
    fun cppGrammar_classifiesLineCommentAndString() {
        val tokens = GrammarTokenizer.tokenize("// note\nauto s = \"hello\";", CppGrammar)
        assertTrue(tokens.any { it.text.contains("note") && it.type.name == "comment" })
        assertTrue(tokens.any { it.text == "\"hello\"" && it.type.name == "string" })
        assertTrue(tokens.any { it.text == "auto" && it.type.name == "keyword" })
    }

    // ── Go ───────────────────────────────────────────────────────────────

    @Test
    fun goGrammar_classifiesKeywordsAndFunc() {
        val src = "package main\n\nfunc add(a int, b int) int { return a + b }"
        val tokens = GrammarTokenizer.tokenize(src, GoGrammar)
        assertTrue(tokens.any { it.text == "package" && it.type.name == "keyword" })
        assertTrue(tokens.any { it.text == "func" && it.type.name == "keyword" })
        assertTrue(tokens.any { it.text == "add" && it.type.name == "function" })
        assertTrue(tokens.any { it.text == "return" && it.type.name == "keyword" })
    }

    @Test
    fun goGrammar_classifiesBacktickRawString() {
        val tokens = GrammarTokenizer.tokenize("var s = `raw\nstring`", GoGrammar)
        assertTrue(tokens.any { it.text.startsWith("`") && it.type.name == "string" })
    }

    @Test
    fun goGrammar_classifiesBuiltinTypesAndBooleans() {
        val tokens = GrammarTokenizer.tokenize("var b bool = true", GoGrammar)
        assertTrue(tokens.any { it.text == "bool" && it.type.name == "type" })
        assertTrue(tokens.any { it.text == "true" && it.type.name == "keyword" })
    }

    // ── Rust ─────────────────────────────────────────────────────────────

    @Test
    fun rustGrammar_classifiesKeywordsAndAttribute() {
        val src = "#[derive(Debug)]\npub struct Point { x: i32 }"
        val tokens = GrammarTokenizer.tokenize(src, RustGrammar)
        assertTrue(tokens.any { it.text == "pub" && it.type.name == "keyword" })
        assertTrue(tokens.any { it.text == "struct" && it.type.name == "keyword" })
        assertTrue(tokens.any { it.text.startsWith("#[derive") && it.type.name == "annotation" })
    }

    @Test
    fun rustGrammar_classifiesRawStringAndPrimitiveType() {
        val tokens = GrammarTokenizer.tokenize("let s: &str = r#\"raw\"#;", RustGrammar)
        assertTrue(tokens.any { it.text.contains("raw") && it.type.name == "string" })
        assertTrue(tokens.any { it.text == "str" && it.type.name == "type" })
        assertTrue(tokens.any { it.text == "let" && it.type.name == "keyword" })
    }

    @Test
    fun rustGrammar_classifiesFunctionWithGenerics() {
        val tokens = GrammarTokenizer.tokenize("fn id<T>(x: T) -> T { x }", RustGrammar)
        assertTrue(tokens.any { it.text == "fn" && it.type.name == "keyword" })
        assertTrue(tokens.any { it.text == "id" && it.type.name == "function" })
    }
}
