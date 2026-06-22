package xyz.junerver.compose.palette.code

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PaletteCodeHighlighterTest {
    @Test
    fun highlightsKotlinKeywordsStringsNumbersAndComments() {
        val highlighted =
            PaletteCodeHighlighter.highlight(
                code =
                    """
                    @Composable
                    fun Greeting(name: String) {
                        val count = 42
                        println("Hello, ${'$'}name")
                        // visible comment
                    }
                    """.trimIndent(),
                language = "kotlin",
            )

        val tokens = highlighted.tokens.flatten()
        assertEquals("kotlin", highlighted.language)
        assertEquals(6, highlighted.tokens.size)
        assertTrue(tokens.any { it.text == "@Composable" && it.type == CodeTokenType.Annotation })
        assertTrue(tokens.any { it.text == "fun" && it.type == CodeTokenType.Keyword })
        assertTrue(tokens.any { it.text == "Greeting" && it.type == CodeTokenType.Function })
        assertTrue(tokens.any { it.text == "String" && it.type == CodeTokenType.Type })
        assertTrue(tokens.any { it.text == "42" && it.type == CodeTokenType.NumberLiteral })
        assertTrue(tokens.any { it.text == "\"Hello, ${'$'}name\"" && it.type == CodeTokenType.StringLiteral })
        assertTrue(tokens.any { it.text == "// visible comment" && it.type == CodeTokenType.Comment })
    }

    @Test
    fun keepsUnknownLanguageAsPlainTextByLine() {
        val highlighted = PaletteCodeHighlighter.highlight("alpha\nbeta", language = "unknown")

        assertEquals("unknown", highlighted.language)
        assertEquals(2, highlighted.tokens.size)
        assertEquals(listOf(CodeToken(CodeTokenType.Plain, "alpha")), highlighted.tokens.first())
        assertEquals(listOf(CodeToken(CodeTokenType.Plain, "beta")), highlighted.tokens.last())
    }

    @Test
    fun highlightsTypeScriptKeywordsTemplateStringsAndRegexLikeJavaScript() {
        val highlighted =
            PaletteCodeHighlighter.highlight(
                code =
                    """
                    export function greet(name: string) {
                        const count = 2
                        return `Hello, ${'$'}{name}`
                    }
                    """.trimIndent(),
                language = "typescript",
            )

        val tokens = highlighted.tokens.flatten()
        assertEquals("typescript", highlighted.language)
        assertTrue(tokens.any { it.text == "export" && it.type == CodeTokenType.Keyword })
        assertTrue(tokens.any { it.text == "function" && it.type == CodeTokenType.Keyword })
        assertTrue(tokens.any { it.text == "greet" && it.type == CodeTokenType.Function })
        assertTrue(tokens.any { it.text == "string" && it.type == CodeTokenType.Type })
        assertTrue(tokens.any { it.text == "const" && it.type == CodeTokenType.Keyword })
        assertTrue(tokens.any { it.text == "2" && it.type == CodeTokenType.NumberLiteral })
        assertTrue(tokens.any { it.text == "`Hello, ${'$'}{name}`" && it.type == CodeTokenType.StringLiteral })
    }

    @Test
    fun highlightsJsonStringsNumbersBooleansNullAndPunctuation() {
        val highlighted =
            PaletteCodeHighlighter.highlight(
                code =
                    """
                    {
                      "name": "Palette",
                      "enabled": true,
                      "count": 3,
                      "next": null
                    }
                    """.trimIndent(),
                language = "json",
            )

        val tokens = highlighted.tokens.flatten()
        assertEquals("json", highlighted.language)
        assertTrue(tokens.any { it.text == "\"name\"" && it.type == CodeTokenType.StringLiteral })
        assertTrue(tokens.any { it.text == "\"Palette\"" && it.type == CodeTokenType.StringLiteral })
        assertTrue(tokens.any { it.text == "true" && it.type == CodeTokenType.Keyword })
        assertTrue(tokens.any { it.text == "3" && it.type == CodeTokenType.NumberLiteral })
        assertTrue(tokens.any { it.text == "null" && it.type == CodeTokenType.Keyword })
        assertTrue(tokens.any { it.text == "{" && it.type == CodeTokenType.Punctuation })
        assertTrue(tokens.any { it.text == ":" && it.type == CodeTokenType.Operator })
    }

    @Test
    fun highlightsCssSelectorsPropertiesValuesAndComments() {
        val highlighted =
            PaletteCodeHighlighter.highlight(
                code =
                    """
                    @media screen {
                      .card {
                        color: #fff;
                        margin: 8px;
                        content: "ready";
                        /* visible comment */
                      }
                    }
                    """.trimIndent(),
                language = "css",
            )

        val tokens = highlighted.tokens.flatten()
        assertEquals("css", highlighted.language)
        assertTrue(tokens.any { it.text == "@media" && it.type == CodeTokenType.Annotation })
        assertTrue(tokens.any { it.text == ".card" && it.type == CodeTokenType.Type })
        assertTrue(tokens.any { it.text == "color" && it.type == CodeTokenType.Keyword })
        assertTrue(tokens.any { it.text == "#fff" && it.type == CodeTokenType.NumberLiteral })
        assertTrue(tokens.any { it.text == "8px" && it.type == CodeTokenType.NumberLiteral })
        assertTrue(tokens.any { it.text == "\"ready\"" && it.type == CodeTokenType.StringLiteral })
        assertTrue(tokens.any { it.text == "/* visible comment */" && it.type == CodeTokenType.Comment })
    }

    @Test
    fun highlightsPythonDecoratorsKeywordsTypesStringsNumbersAndComments() {
        val highlighted =
            PaletteCodeHighlighter.highlight(
                code =
                    """
                    @dataclass
                    def greet(name: str) -> str:
                        count = 3
                        return f"Hello, {name}"  # visible comment
                    """.trimIndent(),
                language = "python",
            )

        val tokens = highlighted.tokens.flatten()
        assertEquals("python", highlighted.language)
        assertTrue(tokens.any { it.text == "@dataclass" && it.type == CodeTokenType.Annotation })
        assertTrue(tokens.any { it.text == "def" && it.type == CodeTokenType.Keyword })
        assertTrue(tokens.any { it.text == "greet" && it.type == CodeTokenType.Function })
        assertTrue(tokens.any { it.text == "str" && it.type == CodeTokenType.Type })
        assertTrue(tokens.any { it.text == "3" && it.type == CodeTokenType.NumberLiteral })
        assertTrue(tokens.any { it.text == "f\"Hello, {name}\"" && it.type == CodeTokenType.StringLiteral })
        assertTrue(tokens.any { it.text == "# visible comment" && it.type == CodeTokenType.Comment })
    }

    @Test
    fun highlightsHtmlTagsAttributesStringsCommentsAndPunctuation() {
        val highlighted =
            PaletteCodeHighlighter.highlight(
                code =
                    """
                    <!doctype html>
                    <!-- visible comment -->
                    <section class="card" data-count='3'>
                      <h1>Hello</h1>
                    </section>
                    """.trimIndent(),
                language = "html",
            )

        val tokens = highlighted.tokens.flatten()
        assertEquals("html", highlighted.language)
        assertTrue(tokens.any { it.text == "<!doctype" && it.type == CodeTokenType.Keyword })
        assertTrue(tokens.any { it.text == "<!-- visible comment -->" && it.type == CodeTokenType.Comment })
        assertTrue(tokens.any { it.text == "section" && it.type == CodeTokenType.Type })
        assertTrue(tokens.any { it.text == "class" && it.type == CodeTokenType.Annotation })
        assertTrue(tokens.any { it.text == "\"card\"" && it.type == CodeTokenType.StringLiteral })
        assertTrue(tokens.any { it.text == "data-count" && it.type == CodeTokenType.Annotation })
        assertTrue(tokens.any { it.text == "'3'" && it.type == CodeTokenType.StringLiteral })
        assertTrue(tokens.any { it.text == "</" && it.type == CodeTokenType.Punctuation })
    }

    @Test
    fun highlightsShellCommandsVariablesStringsAndComments() {
        val highlighted =
            PaletteCodeHighlighter.highlight(
                code =
                    """
                    export APP_NAME="Palette"
                    echo ${'$'}APP_NAME
                    ./gradlew :palette:desktopTest --info # run desktop tests
                    """.trimIndent(),
                language = "bash",
            )

        val tokens = highlighted.tokens.flatten()
        assertEquals("bash", highlighted.language)
        assertTrue(tokens.any { it.text == "export" && it.type == CodeTokenType.Keyword })
        assertTrue(tokens.any { it.text == "APP_NAME" && it.type == CodeTokenType.Plain })
        assertTrue(tokens.any { it.text == "\"Palette\"" && it.type == CodeTokenType.StringLiteral })
        assertTrue(tokens.any { it.text == "echo" && it.type == CodeTokenType.Function })
        assertTrue(tokens.any { it.text == "${'$'}APP_NAME" && it.type == CodeTokenType.Annotation })
        assertTrue(tokens.any { it.text == "./gradlew" && it.type == CodeTokenType.Function })
        assertTrue(tokens.any { it.text == "--info" && it.type == CodeTokenType.Operator })
        assertTrue(tokens.any { it.text == "# run desktop tests" && it.type == CodeTokenType.Comment })
    }

    @Test
    fun highlightsYamlKeysScalarsListsAnchorsAndComments() {
        val highlighted =
            PaletteCodeHighlighter.highlight(
                code =
                    """
                    name: Palette
                    enabled: true
                    retries: 3
                    defaults: &defaults
                      theme: "light"
                    items:
                      - *defaults # shared config
                    """.trimIndent(),
                language = "yaml",
            )

        val tokens = highlighted.tokens.flatten()
        assertEquals("yaml", highlighted.language)
        assertTrue(tokens.any { it.text == "name" && it.type == CodeTokenType.Keyword })
        assertTrue(tokens.any { it.text == "true" && it.type == CodeTokenType.Keyword })
        assertTrue(tokens.any { it.text == "3" && it.type == CodeTokenType.NumberLiteral })
        assertTrue(tokens.any { it.text == "&defaults" && it.type == CodeTokenType.Annotation })
        assertTrue(tokens.any { it.text == "\"light\"" && it.type == CodeTokenType.StringLiteral })
        assertTrue(tokens.any { it.text == "-" && it.type == CodeTokenType.Operator })
        assertTrue(tokens.any { it.text == "*defaults" && it.type == CodeTokenType.Annotation })
        assertTrue(tokens.any { it.text == "# shared config" && it.type == CodeTokenType.Comment })
    }

    @Test
    fun highlightsTomlSectionsKeysScalarsArraysAndComments() {
        val highlighted =
            PaletteCodeHighlighter.highlight(
                code =
                    """
                    # package metadata
                    [project]
                    name = "Palette"
                    enabled = true
                    retries = 3
                    targets = ["android", "desktop", "ios"]
                    [tool.palette]
                    theme = 'light'
                    """.trimIndent(),
                language = "toml",
            )

        val tokens = highlighted.tokens.flatten()
        assertEquals("toml", highlighted.language)
        assertTrue(tokens.any { it.text == "# package metadata" && it.type == CodeTokenType.Comment })
        assertTrue(tokens.any { it.text == "project" && it.type == CodeTokenType.Type })
        assertTrue(tokens.any { it.text == "tool.palette" && it.type == CodeTokenType.Type })
        assertTrue(tokens.any { it.text == "name" && it.type == CodeTokenType.Keyword })
        assertTrue(tokens.any { it.text == "=" && it.type == CodeTokenType.Operator })
        assertTrue(tokens.any { it.text == "\"Palette\"" && it.type == CodeTokenType.StringLiteral })
        assertTrue(tokens.any { it.text == "true" && it.type == CodeTokenType.Keyword })
        assertTrue(tokens.any { it.text == "3" && it.type == CodeTokenType.NumberLiteral })
        assertTrue(tokens.any { it.text == "[" && it.type == CodeTokenType.Punctuation })
        assertTrue(tokens.any { it.text == "'light'" && it.type == CodeTokenType.StringLiteral })
    }

    @Test
    fun highlightsDiffHeadersHunksInsertedAndDeletedLines() {
        val highlighted =
            PaletteCodeHighlighter.highlight(
                code =
                    """
                    diff --git a/Button.kt b/Button.kt
                    index 123..456 100644
                    --- a/Button.kt
                    +++ b/Button.kt
                    @@ -1,3 +1,4 @@
                     fun Button() {
                    -    OldButton()
                    +    NewButton()
                     }
                    """.trimIndent(),
                language = "diff",
            )

        val tokens = highlighted.tokens.flatten()
        assertEquals("diff", highlighted.language)
        assertTrue(tokens.any { it.text == "diff --git a/Button.kt b/Button.kt" && it.type == CodeTokenType.Keyword })
        assertTrue(tokens.any { it.text == "@@ -1,3 +1,4 @@" && it.type == CodeTokenType.Annotation })
        assertTrue(tokens.any { it.text == "-    OldButton()" && it.type == CodeTokenType.Deleted })
        assertTrue(tokens.any { it.text == "+    NewButton()" && it.type == CodeTokenType.Inserted })
        assertTrue(tokens.any { it.text == " fun Button() {" && it.type == CodeTokenType.Plain })
    }

    @Test
    fun highlightsMarkdownHeadingsListsLinksInlineCodeAndFences() {
        val highlighted =
            PaletteCodeHighlighter.highlight(
                code =
                    """
                    ## Palette Markdown

                    - [x] Render `inline code`
                    - [ ] Open [docs](https://example.com/docs)

                    ```kotlin
                    val component = "PMarkdownViewer"
                    ```
                    """.trimIndent(),
                language = "markdown",
            )

        val tokens = highlighted.tokens.flatten()
        assertEquals("markdown", highlighted.language)
        assertTrue(tokens.any { it.text == "##" && it.type == CodeTokenType.Keyword })
        assertTrue(tokens.any { it.text == "-" && it.type == CodeTokenType.Operator })
        assertTrue(tokens.any { it.text == "[x]" && it.type == CodeTokenType.Annotation })
        assertTrue(tokens.any { it.text == "`inline code`" && it.type == CodeTokenType.StringLiteral })
        assertTrue(tokens.any { it.text == "docs" && it.type == CodeTokenType.Type })
        assertTrue(tokens.any { it.text == "https://example.com/docs" && it.type == CodeTokenType.StringLiteral })
        assertTrue(tokens.any { it.text == "```" && it.type == CodeTokenType.Annotation })
        assertTrue(tokens.any { it.text == "kotlin" && it.type == CodeTokenType.Type })
    }

    @Test
    fun keepsMarkdownUnsupportedInlinePunctuationAsPlainText() {
        val highlighted = PaletteCodeHighlighter.highlight("Use !literal <tag> and ~approx.", language = "md")

        assertEquals("md", highlighted.language)
        assertTrue(highlighted.tokens.flatten().any { it.text.contains("!literal") && it.type == CodeTokenType.Plain })
        assertTrue(highlighted.tokens.flatten().any { it.text.contains("<tag>") && it.type == CodeTokenType.Plain })
        assertTrue(highlighted.tokens.flatten().any { it.text.contains("~approx") && it.type == CodeTokenType.Plain })
    }

    @Test
    fun highlightsSqlKeywordsTypesStringsNumbersFunctionsAndComments() {
        val highlighted =
            PaletteCodeHighlighter.highlight(
                code =
                    """
                    -- visible comment
                    SELECT id, COUNT(*) AS total
                    FROM users
                    WHERE active = TRUE AND name = 'Palette'
                    ORDER BY created_at DESC;
                    /* block comment */
                    CREATE TABLE projects (
                      id INTEGER PRIMARY KEY,
                      name TEXT NOT NULL
                    );
                    """.trimIndent(),
                language = "sql",
            )

        val tokens = highlighted.tokens.flatten()
        assertEquals("sql", highlighted.language)
        assertTrue(tokens.any { it.text == "-- visible comment" && it.type == CodeTokenType.Comment })
        assertTrue(tokens.any { it.text == "SELECT" && it.type == CodeTokenType.Keyword })
        assertTrue(tokens.any { it.text == "COUNT" && it.type == CodeTokenType.Function })
        assertTrue(tokens.any { it.text == "TRUE" && it.type == CodeTokenType.Keyword })
        assertTrue(tokens.any { it.text == "'Palette'" && it.type == CodeTokenType.StringLiteral })
        assertTrue(tokens.any { it.text == "/* block comment */" && it.type == CodeTokenType.Comment })
        assertTrue(tokens.any { it.text == "CREATE" && it.type == CodeTokenType.Keyword })
        assertTrue(tokens.any { it.text == "INTEGER" && it.type == CodeTokenType.Type })
        assertTrue(tokens.any { it.text == "TEXT" && it.type == CodeTokenType.Type })
        assertTrue(tokens.any { it.text == ";" && it.type == CodeTokenType.Punctuation })
    }
}
