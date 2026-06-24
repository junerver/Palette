package xyz.junerver.compose.palette.code

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
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
        assertTrue(tokens.any { it.text == "\"Hello, " && it.type == CodeTokenType.StringLiteral })
        assertTrue(tokens.any { it.text == "${'$'}name" && it.type == CodeTokenType.Annotation })
        assertTrue(tokens.any { it.text == "\"" && it.type == CodeTokenType.StringLiteral })
        assertTrue(tokens.any { it.text == "// visible comment" && it.type == CodeTokenType.Comment })
    }

    @Test
    fun highlightsKotlinStringInterpolationTokens() {
        val highlighted =
            PaletteCodeHighlighter.highlight(
                code = "val text = \"Hello, ${'$'}name ${'$'}{user.displayName}\"",
                language = "kotlin",
            )

        val tokens = highlighted.tokens.flatten()
        assertTrue(tokens.any { it.text == "\"Hello, " && it.type == CodeTokenType.StringLiteral })
        assertTrue(tokens.any { it.text == "${'$'}name" && it.type == CodeTokenType.Annotation })
        assertTrue(tokens.any { it.text == "${'$'}{" && it.type == CodeTokenType.Operator })
        assertTrue(tokens.any { it.text == "user.displayName" && it.type == CodeTokenType.Annotation })
        assertTrue(tokens.any { it.text == "}" && it.type == CodeTokenType.Operator })
        assertTrue(tokens.any { it.text == "\"" && it.type == CodeTokenType.StringLiteral })
    }

    @Test
    fun highlightsKotlinTripleQuotedStringsAcrossLines() {
        val highlighted =
            PaletteCodeHighlighter.highlight(
                code =
                    """
                    val json = ""${'"'}
                      "name": "Palette"
                    }""${'"'}
                    val done = true
                    """.trimIndent(),
                language = "kotlin",
            )

        assertEquals(CodeTokenType.StringLiteral, highlighted.tokens[0].last().type)
        assertEquals(CodeTokenType.StringLiteral, highlighted.tokens[1].single().type)
        assertEquals(CodeTokenType.StringLiteral, highlighted.tokens[2].single().type)
        assertTrue(highlighted.tokens[3].any { it.text == "val" && it.type == CodeTokenType.Keyword })
        assertTrue(highlighted.tokens[3].any { it.text == "true" && it.type == CodeTokenType.Keyword })
    }

    @Test
    fun highlightsModernKotlinKeywordsUsedInComposeProjects() {
        val highlighted =
            PaletteCodeHighlighter.highlight(
                code =
                    """
                    suspend inline fun <reified T> load(vararg ids: String) = try {
                        companion object
                    } catch (error: Throwable) {
                        throw error
                    } finally {
                        expect actual value class Token(val value: String)
                    }
                    """.trimIndent(),
                language = "kotlin",
            )

        val tokens = highlighted.tokens.flatten()
        listOf(
            "suspend",
            "inline",
            "reified",
            "vararg",
            "companion",
            "object",
            "try",
            "catch",
            "throw",
            "finally",
            "expect",
            "actual",
            "value",
            "class",
        ).forEach { keyword ->
            assertTrue(tokens.any { it.text == keyword && it.type == CodeTokenType.Keyword }, "Missing keyword $keyword")
        }
    }

    @Test
    fun keepsKotlinNestedBlockCommentsCommentedUntilOuterClose() {
        val highlighted =
            PaletteCodeHighlighter.highlight(
                code =
                    """
                    val before = 1
                    /* outer
                     * /* inner */
                     * still outer comment
                     */
                    val after = 2
                    """.trimIndent(),
                language = "kotlin",
            )

        assertTrue(highlighted.tokens[0].any { it.text == "val" && it.type == CodeTokenType.Keyword })
        assertEquals(CodeTokenType.Comment, highlighted.tokens[1].first().type)
        assertEquals(CodeTokenType.Comment, highlighted.tokens[2].first().type)
        assertEquals(CodeTokenType.Comment, highlighted.tokens[3].first().type)
        assertEquals(CodeTokenType.Comment, highlighted.tokens[4].first().type)
        assertTrue(highlighted.tokens[5].any { it.text == "val" && it.type == CodeTokenType.Keyword })
    }

    @Test
    fun highlightsKotlinMultiCharacterOperatorsAsSingleTokens() {
        val highlighted =
            PaletteCodeHighlighter.highlight(
                code =
                    """
                    val same = a === b || a !== c
                    val next = value ?. transform() ?: fallback
                    val range = start ..< end
                    val ref = Type :: member
                    val lambda = input -> output
                    """.trimIndent(),
                language = "kotlin",
            )

        val operatorTexts =
            highlighted.tokens
                .flatten()
                .filter { it.type == CodeTokenType.Operator }
                .map { it.text }

        listOf("===", "||", "!==", "?.", "?:", "..<", "::", "->").forEach { operator ->
            assertTrue(operator in operatorTexts, "Missing operator $operator in $operatorTexts")
        }
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
    fun reportsDiagnosticsForUnsupportedLanguages() {
        val highlighted = PaletteCodeHighlighter.highlightWithDiagnostics("alpha\nbeta", language = "unknown")

        assertEquals("unknown", highlighted.language)
        assertEquals(1, highlighted.diagnostics.size)
        assertEquals(PaletteCodeDiagnosticCode.UnsupportedLanguage, highlighted.diagnostics.single().code)
        assertEquals(PaletteCodeDiagnosticSeverity.Warning, highlighted.diagnostics.single().severity)
        assertEquals(listOf(CodeToken(CodeTokenType.Plain, "alpha")), highlighted.tokens.first())
        assertEquals(listOf(CodeToken(CodeTokenType.Plain, "beta")), highlighted.tokens.last())
    }

    @Test
    fun reportsDiagnosticsForBlankLanguage() {
        val highlighted = PaletteCodeHighlighter.highlightWithDiagnostics("plain", language = "   ")

        assertEquals("plain", highlighted.language)
        assertEquals(1, highlighted.diagnostics.size)
        assertEquals(PaletteCodeDiagnosticCode.BlankLanguage, highlighted.diagnostics.single().code)
        assertEquals(PaletteCodeDiagnosticSeverity.Warning, highlighted.diagnostics.single().severity)
        assertEquals(listOf(CodeToken(CodeTokenType.Plain, "plain")), highlighted.tokens.single())
    }

    @Test
    fun reportsDiagnosticsWhenCustomHighlighterFails() {
        PaletteCodeHighlighter.registerLanguage("broken") {
            throw IllegalStateException("boom")
        }

        try {
            val highlighted = PaletteCodeHighlighter.highlightWithDiagnostics("alpha", language = "broken")

            assertEquals("broken", highlighted.language)
            assertEquals(1, highlighted.diagnostics.size)
            assertEquals(PaletteCodeDiagnosticCode.HighlighterFailure, highlighted.diagnostics.single().code)
            assertEquals(PaletteCodeDiagnosticSeverity.Error, highlighted.diagnostics.single().severity)
            assertEquals(listOf(CodeToken(CodeTokenType.Plain, "alpha")), highlighted.tokens.single())
        } finally {
            PaletteCodeHighlighter.unregisterLanguage("broken")
        }
    }

    @Test
    fun supportsRegisteredCustomLanguagesAndAliases() {
        PaletteCodeHighlighter.registerLanguage(
            language = "todo",
            aliases = setOf("tasks"),
        ) { lines ->
            lines.map { line ->
                if (line.startsWith("!")) {
                    listOf(CodeToken(CodeTokenType.Annotation, line))
                } else {
                    listOf(CodeToken(CodeTokenType.Plain, line))
                }
            }
        }

        try {
            val highlighted = PaletteCodeHighlighter.highlight("!ship\nreview", language = "tasks")

            assertEquals("tasks", highlighted.language)
            assertEquals(listOf(CodeToken(CodeTokenType.Annotation, "!ship")), highlighted.tokens.first())
            assertEquals(listOf(CodeToken(CodeTokenType.Plain, "review")), highlighted.tokens.last())
        } finally {
            PaletteCodeHighlighter.unregisterLanguage("todo")
            PaletteCodeHighlighter.unregisterLanguage("tasks")
        }
    }

    @Test
    fun unregistersCustomLanguagesBackToPlainFallback() {
        PaletteCodeHighlighter.registerLanguage("notes") { lines ->
            lines.map { line -> listOf(CodeToken(CodeTokenType.Comment, line)) }
        }

        PaletteCodeHighlighter.unregisterLanguage("notes")

        val highlighted = PaletteCodeHighlighter.highlight("plain", language = "notes")
        assertEquals(listOf(CodeToken(CodeTokenType.Plain, "plain")), highlighted.tokens.single())
    }

    @Test
    fun keepsUnsupportedWebFrontendAliasesAsPlainText() {
        listOf("jsx", "tsx", "scss", "sass", "less").forEach { language ->
            val highlighted = PaletteCodeHighlighter.highlight("<Component color={theme.primary} />", language)

            assertEquals(language, highlighted.language)
            assertEquals(listOf(CodeToken(CodeTokenType.Plain, "<Component color={theme.primary} />")), highlighted.tokens.single())
            assertFalse(highlighted.tokens.flatten().any { it.type != CodeTokenType.Plain })
        }
    }

    @Test
    fun highlightsDockerfileInstructionsFlagsVariablesAndComments() {
        val highlighted =
            PaletteCodeHighlighter.highlight(
                code =
                    """
                    # build image
                    FROM gradle:8-jdk17 AS builder
                    ARG APP_HOME=/workspace
                    COPY --from=builder ${'$'}{APP_HOME}/build/libs/app.jar /app/app.jar
                    EXPOSE 8080
                    CMD ["java", "-jar", "/app/app.jar"]
                    """.trimIndent(),
                language = "dockerfile",
            )

        val tokens = highlighted.tokens.flatten()
        assertEquals("dockerfile", highlighted.language)
        assertEquals(emptyList(), highlighted.diagnostics)
        listOf("FROM", "ARG", "COPY", "EXPOSE", "CMD").forEach { instruction ->
            assertTrue(tokens.any { it.text == instruction && it.type == CodeTokenType.Keyword }, "Missing instruction $instruction")
        }
        assertTrue(tokens.any { it.text == "AS" && it.type == CodeTokenType.Keyword })
        assertTrue(tokens.any { it.text == "--from=builder" && it.type == CodeTokenType.Annotation })
        assertTrue(tokens.any { it.text == "${'$'}{APP_HOME}" && it.type == CodeTokenType.Annotation })
        assertTrue(tokens.any { it.text == "8080" && it.type == CodeTokenType.NumberLiteral })
        assertTrue(tokens.any { it.text == "\"java\"" && it.type == CodeTokenType.StringLiteral })
        assertTrue(tokens.any { it.text == "# build image" && it.type == CodeTokenType.Comment })
    }

    @Test
    fun highlightsContainerfileAlias() {
        val highlighted = PaletteCodeHighlighter.highlight("RUN echo \"ok\"", language = "containerfile")

        assertEquals("containerfile", highlighted.language)
        assertEquals(emptyList(), highlighted.diagnostics)
        assertTrue(highlighted.tokens.flatten().any { it.text == "RUN" && it.type == CodeTokenType.Keyword })
        assertTrue(highlighted.tokens.flatten().any { it.text == "\"ok\"" && it.type == CodeTokenType.StringLiteral })
    }

    @Test
    fun highlightsIniSectionsKeysValuesAndComments() {
        val highlighted =
            PaletteCodeHighlighter.highlight(
                code =
                    """
                    ; app config
                    [server.main]
                    host = "localhost"
                    port = 8080
                    enabled = true
                    path = ${'$'}{APP_HOME}/data # comment
                    """.trimIndent(),
                language = "ini",
            )

        val tokens = highlighted.tokens.flatten()
        assertEquals("ini", highlighted.language)
        assertEquals(emptyList(), highlighted.diagnostics)
        assertTrue(tokens.any { it.text == "; app config" && it.type == CodeTokenType.Comment })
        assertTrue(tokens.any { it.text == "server.main" && it.type == CodeTokenType.Type })
        listOf("host", "port", "enabled", "path").forEach { key ->
            assertTrue(tokens.any { it.text == key && it.type == CodeTokenType.Keyword }, "Missing key $key")
        }
        assertTrue(tokens.any { it.text == "\"localhost\"" && it.type == CodeTokenType.StringLiteral })
        assertTrue(tokens.any { it.text == "8080" && it.type == CodeTokenType.NumberLiteral })
        assertTrue(tokens.any { it.text == "true" && it.type == CodeTokenType.Keyword })
        assertTrue(tokens.any { it.text == "${'$'}{APP_HOME}" && it.type == CodeTokenType.Annotation })
        assertTrue(tokens.any { it.text == "# comment" && it.type == CodeTokenType.Comment })
    }

    @Test
    fun highlightsPropertiesAliasAndWhitespaceSeparators() {
        val highlighted =
            PaletteCodeHighlighter.highlight(
                code =
                    """
                    app.name Palette
                    app.enabled:false
                    app.path=C:\\Palette
                    """.trimIndent(),
                language = "properties",
            )

        val tokens = highlighted.tokens.flatten()
        assertEquals("properties", highlighted.language)
        assertEquals(emptyList(), highlighted.diagnostics)
        assertTrue(tokens.any { it.text == "app.name" && it.type == CodeTokenType.Keyword })
        assertTrue(tokens.any { it.text == " " && it.type == CodeTokenType.Operator })
        assertTrue(tokens.any { it.text == "app.enabled" && it.type == CodeTokenType.Keyword })
        assertTrue(tokens.any { it.text == ":" && it.type == CodeTokenType.Operator })
        assertTrue(tokens.any { it.text == "false" && it.type == CodeTokenType.Keyword })
        assertTrue(tokens.any { it.text == "app.path" && it.type == CodeTokenType.Keyword })
        assertTrue(tokens.any { it.text == "=" && it.type == CodeTokenType.Operator })
    }

    @Test
    fun highlightsGraphQlOperationsVariablesDirectivesAndFragments() {
        val highlighted =
            PaletteCodeHighlighter.highlight(
                code =
                    """
                    # load user
                    query GetUser(${'$'}id: ID!, ${'$'}withPosts: Boolean = true) {
                      user(id: ${'$'}id) @include(if: ${'$'}withPosts) {
                        ...UserFields
                        posts(limit: 10) { title }
                      }
                    }
                    fragment UserFields on User {
                      id
                      name
                    }
                    """.trimIndent(),
                language = "graphql",
            )

        val tokens = highlighted.tokens.flatten()
        assertEquals("graphql", highlighted.language)
        assertEquals(emptyList(), highlighted.diagnostics)
        assertTrue(tokens.any { it.text == "# load user" && it.type == CodeTokenType.Comment })
        assertTrue(tokens.any { it.text == "query" && it.type == CodeTokenType.Keyword })
        assertTrue(tokens.any { it.text == "fragment" && it.type == CodeTokenType.Keyword })
        assertTrue(tokens.any { it.text == "on" && it.type == CodeTokenType.Keyword })
        assertTrue(tokens.any { it.text == "${'$'}id" && it.type == CodeTokenType.Annotation })
        assertTrue(tokens.any { it.text == "@include" && it.type == CodeTokenType.Annotation })
        assertTrue(tokens.any { it.text == "ID" && it.type == CodeTokenType.Type })
        assertTrue(tokens.any { it.text == "Boolean" && it.type == CodeTokenType.Type })
        assertTrue(tokens.any { it.text == "user" && it.type == CodeTokenType.Function })
        assertTrue(tokens.any { it.text == "posts" && it.type == CodeTokenType.Function })
        assertTrue(tokens.any { it.text == "..." && it.type == CodeTokenType.Operator })
        assertTrue(tokens.any { it.text == "10" && it.type == CodeTokenType.NumberLiteral })
        assertTrue(tokens.any { it.text == "true" && it.type == CodeTokenType.Keyword })
    }

    @Test
    fun highlightsGraphQlSchemaAlias() {
        val highlighted =
            PaletteCodeHighlighter.highlight(
                code =
                    """
                    type User implements Node {
                      id: ID!
                      name: String
                    }
                    """.trimIndent(),
                language = "gql",
            )

        val tokens = highlighted.tokens.flatten()
        assertEquals("gql", highlighted.language)
        assertEquals(emptyList(), highlighted.diagnostics)
        assertTrue(tokens.any { it.text == "type" && it.type == CodeTokenType.Keyword })
        assertTrue(tokens.any { it.text == "implements" && it.type == CodeTokenType.Keyword })
        assertTrue(tokens.any { it.text == "User" && it.type == CodeTokenType.Type })
        assertTrue(tokens.any { it.text == "Node" && it.type == CodeTokenType.Type })
        assertTrue(tokens.any { it.text == "String" && it.type == CodeTokenType.Type })
    }

    @Test
    fun highlightsModernJavaKeywordsUsedInInteropSources() {
        val highlighted =
            PaletteCodeHighlighter.highlight(
                code =
                    """
                    module palette.demo {
                        requires java.desktop;
                    }
                    public sealed interface Result permits Success {}
                    public record Success(String value) implements Result {}
                    var value = switch (input) {
                        case null -> "empty";
                        default -> "ok";
                    };
                    """.trimIndent(),
                language = "java",
            )

        val tokens = highlighted.tokens.flatten()
        listOf(
            "module",
            "requires",
            "sealed",
            "interface",
            "permits",
            "record",
            "implements",
            "var",
            "switch",
            "case",
            "default",
        ).forEach { keyword ->
            assertTrue(tokens.any { it.text == keyword && it.type == CodeTokenType.Keyword }, "Missing keyword $keyword")
        }
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
        assertTrue(tokens.any { it.text == "f\"Hello, " && it.type == CodeTokenType.StringLiteral })
        assertTrue(tokens.any { it.text == "{" && it.type == CodeTokenType.Operator })
        assertTrue(tokens.any { it.text == "name" && it.type == CodeTokenType.Annotation })
        assertTrue(tokens.any { it.text == "}" && it.type == CodeTokenType.Operator })
        assertTrue(tokens.any { it.text == "\"" && it.type == CodeTokenType.StringLiteral })
        assertTrue(tokens.any { it.text == "# visible comment" && it.type == CodeTokenType.Comment })
    }

    @Test
    fun highlightsPythonTripleQuotedStringsAcrossLines() {
        val highlighted =
            PaletteCodeHighlighter.highlight(
                code =
                    """
                    def greet() -> str:
                        ""${'"'}Build greeting.
                        Keeps this line as a string.
                        ""${'"'}
                        return "done"
                    """.trimIndent(),
                language = "python",
            )

        assertTrue(highlighted.tokens[0].any { it.text == "def" && it.type == CodeTokenType.Keyword })
        assertEquals(CodeTokenType.StringLiteral, highlighted.tokens[1].dropWhile { it.text.isBlank() }.single().type)
        assertEquals(CodeTokenType.StringLiteral, highlighted.tokens[2].single().type)
        assertEquals(CodeTokenType.StringLiteral, highlighted.tokens[3].single().type)
        assertTrue(highlighted.tokens[4].any { it.text == "return" && it.type == CodeTokenType.Keyword })
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
    fun highlightsEmbeddedCssAndJavaScriptInHtml() {
        val highlighted =
            PaletteCodeHighlighter.highlight(
                code =
                    """
                    <style>
                    .card { display: flex; }
                    </style>
                    <script>
                    const answer = 42
                    console.log(answer)
                    </script>
                    """.trimIndent(),
                language = "html",
            )

        val tokens = highlighted.tokens.flatten()
        assertEquals("html", highlighted.language)
        assertTrue(tokens.any { it.text == "style" && it.type == CodeTokenType.Type })
        assertTrue(tokens.any { it.text == ".card" && it.type == CodeTokenType.Type })
        assertTrue(tokens.any { it.text == "display" && it.type == CodeTokenType.Keyword })
        assertTrue(tokens.any { it.text == "flex" && it.type == CodeTokenType.Keyword })
        assertTrue(tokens.any { it.text == "script" && it.type == CodeTokenType.Type })
        assertTrue(tokens.any { it.text == "const" && it.type == CodeTokenType.Keyword })
        assertTrue(tokens.any { it.text == "42" && it.type == CodeTokenType.NumberLiteral })
        assertTrue(tokens.any { it.text == "console" && it.type == CodeTokenType.Plain })
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
        assertTrue(tokens.any { it.text == "echo" && it.type == CodeTokenType.Builtin })
        assertTrue(tokens.any { it.text == "${'$'}APP_NAME" && it.type == CodeTokenType.Annotation })
        assertTrue(tokens.any { it.text == "./gradlew" && it.type == CodeTokenType.Function })
        assertTrue(tokens.any { it.text == "--info" && it.type == CodeTokenType.Operator })
        assertTrue(tokens.any { it.text == "# run desktop tests" && it.type == CodeTokenType.Comment })
    }

    @Test
    fun highlightsShellHeredocAndCommandSubstitution() {
        val highlighted =
            PaletteCodeHighlighter.highlight(
                code =
                    """
                    cat <<EOF
                    hello ${'$'}USER
                    EOF
                    version=${'$'}(git rev-parse --short HEAD)
                    legacy=`pwd`
                    """.trimIndent(),
                language = "bash",
            )

        val tokens = highlighted.tokens.flatten()
        assertEquals("bash", highlighted.language)
        assertEquals(CodeTokenType.StringLiteral, highlighted.tokens[1].single().type)
        assertEquals("hello ${'$'}USER", highlighted.tokens[1].single().text)
        assertEquals(CodeTokenType.Operator, highlighted.tokens[2].single().type)
        assertEquals("EOF", highlighted.tokens[2].single().text)
        assertTrue(tokens.any { it.text == "${'$'}(" && it.type == CodeTokenType.Operator })
        assertTrue(tokens.any { it.text == "git rev-parse --short HEAD" && it.type == CodeTokenType.Annotation })
        assertTrue(tokens.any { it.text == ")" && it.type == CodeTokenType.Operator })
        assertEquals(2, tokens.count { it.text == "`" && it.type == CodeTokenType.Operator })
        assertTrue(tokens.any { it.text == "pwd" && it.type == CodeTokenType.Annotation })
    }

    @Test
    fun highlightsShellProcessSubstitutionAndTestExpressions() {
        val highlighted =
            PaletteCodeHighlighter.highlight(
                code =
                    """
                    diff <(sort left.txt) >(sort right.txt)
                    if [[ -n ${'$'}APP_NAME && "${'$'}APP_NAME" == "Palette" ]]; then
                      echo ready
                    fi
                    """.trimIndent(),
                language = "bash",
            )

        val tokens = highlighted.tokens.flatten()
        assertEquals("bash", highlighted.language)
        assertTrue(tokens.any { it.text == "<(" && it.type == CodeTokenType.Operator })
        assertTrue(tokens.any { it.text == "sort left.txt" && it.type == CodeTokenType.Annotation })
        assertTrue(tokens.any { it.text == ">(" && it.type == CodeTokenType.Operator })
        assertTrue(tokens.any { it.text == "sort right.txt" && it.type == CodeTokenType.Annotation })
        assertTrue(tokens.any { it.text == "[[" && it.type == CodeTokenType.Operator })
        assertTrue(tokens.any { it.text == "-n" && it.type == CodeTokenType.Operator })
        assertTrue(tokens.any { it.text == "${'$'}APP_NAME" && it.type == CodeTokenType.Annotation })
        assertTrue(tokens.any { it.text == "&&" && it.type == CodeTokenType.Operator })
        assertTrue(tokens.any { it.text == "==" && it.type == CodeTokenType.Operator })
        assertTrue(tokens.any { it.text == "\"Palette\"" && it.type == CodeTokenType.StringLiteral })
        assertTrue(tokens.any { it.text == "]]" && it.type == CodeTokenType.Operator })
        assertTrue(tokens.any { it.text == "then" && it.type == CodeTokenType.Keyword })
        assertTrue(tokens.any { it.text == "fi" && it.type == CodeTokenType.Keyword })
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
    fun highlightsYamlBlockScalarsDirectivesTagsAndDocumentMarkers() {
        val highlighted =
            PaletteCodeHighlighter.highlight(
                code =
                    """
                    %YAML 1.2
                    ---
                    description: |
                      first line
                      second line
                    folded: >-
                      folded line
                    resource: !Ref AppName
                    ...
                    """.trimIndent(),
                language = "yaml",
            )

        val tokens = highlighted.tokens.flatten()
        assertEquals("yaml", highlighted.language)
        assertTrue(tokens.any { it.text == "%YAML 1.2" && it.type == CodeTokenType.Annotation })
        assertEquals(CodeTokenType.Operator, highlighted.tokens[1].single().type)
        assertEquals("---", highlighted.tokens[1].single().text)
        assertTrue(tokens.any { it.text == "description" && it.type == CodeTokenType.Keyword })
        assertTrue(tokens.any { it.text == "|" && it.type == CodeTokenType.Operator })
        assertEquals(CodeTokenType.StringLiteral, highlighted.tokens[3].single().type)
        assertEquals("  first line", highlighted.tokens[3].single().text)
        assertEquals(CodeTokenType.StringLiteral, highlighted.tokens[4].single().type)
        assertTrue(tokens.any { it.text == "folded" && it.type == CodeTokenType.Keyword })
        assertTrue(tokens.any { it.text == ">-" && it.type == CodeTokenType.Operator })
        assertEquals(CodeTokenType.StringLiteral, highlighted.tokens[6].single().type)
        assertTrue(tokens.any { it.text == "!Ref" && it.type == CodeTokenType.Annotation })
        assertEquals(CodeTokenType.Operator, highlighted.tokens[8].single().type)
        assertEquals("...", highlighted.tokens[8].single().text)
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
    fun highlightsTomlArrayOfTablesAndMultilineStrings() {
        val highlighted =
            PaletteCodeHighlighter.highlight(
                code =
                    """
                    [[products]]
                    name = "Hammer"
                    description = ${"\"\"\""}
                    Heavy duty
                    hammer
                    ${"\"\"\""}
                    literal = '''
                    no escapes
                    '''
                    in_stock = false
                    """.trimIndent(),
                language = "toml",
            )

        val tokens = highlighted.tokens.flatten()
        assertEquals("toml", highlighted.language)
        assertTrue(tokens.any { it.text == "[[" && it.type == CodeTokenType.Punctuation })
        assertTrue(tokens.any { it.text == "products" && it.type == CodeTokenType.Type })
        assertTrue(tokens.any { it.text == "]]" && it.type == CodeTokenType.Punctuation })
        assertTrue(tokens.any { it.text == "description" && it.type == CodeTokenType.Keyword })
        assertTrue(tokens.any { it.text == "\"\"\"" && it.type == CodeTokenType.StringLiteral })
        assertEquals(CodeTokenType.StringLiteral, highlighted.tokens[3].single().type)
        assertEquals("Heavy duty", highlighted.tokens[3].single().text)
        assertEquals(CodeTokenType.StringLiteral, highlighted.tokens[4].single().type)
        assertEquals("hammer", highlighted.tokens[4].single().text)
        assertEquals(CodeTokenType.StringLiteral, highlighted.tokens[5].single().type)
        assertTrue(tokens.any { it.text == "'''" && it.type == CodeTokenType.StringLiteral })
        assertEquals(CodeTokenType.StringLiteral, highlighted.tokens[7].single().type)
        assertEquals("no escapes", highlighted.tokens[7].single().text)
        assertTrue(tokens.any { it.text == "in_stock" && it.type == CodeTokenType.Keyword })
        assertTrue(tokens.any { it.text == "false" && it.type == CodeTokenType.Keyword })
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
        assertTrue(tokens.any { it.text == "diff --git" && it.type == CodeTokenType.Keyword })
        assertTrue(tokens.any { it.text == "a/Button.kt" && it.type == CodeTokenType.Type })
        assertTrue(tokens.any { it.text == "b/Button.kt" && it.type == CodeTokenType.Type })
        assertTrue(tokens.any { it.text == "index" && it.type == CodeTokenType.Keyword })
        assertTrue(tokens.any { it.text == "123..456" && it.type == CodeTokenType.Annotation })
        assertTrue(tokens.any { it.text == "100644" && it.type == CodeTokenType.NumberLiteral })
        assertTrue(tokens.any { it.text == "---" && it.type == CodeTokenType.Deleted })
        assertTrue(tokens.any { it.text == "+++" && it.type == CodeTokenType.Inserted })
        assertEquals(2, tokens.count { it.text == "@@" && it.type == CodeTokenType.Annotation })
        assertTrue(tokens.any { it.text == "-1,3" && it.type == CodeTokenType.Deleted })
        assertTrue(tokens.any { it.text == "+1,4" && it.type == CodeTokenType.Inserted })
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
        assertTrue(tokens.any { it.text == "val" && it.type == CodeTokenType.Keyword })
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

    @Test
    fun highlightsSqlCteWindowBacktickIdentifiersAndDollarQuotedStrings() {
        val highlighted =
            PaletteCodeHighlighter.highlight(
                code =
                    """
                    WITH ranked AS (
                      SELECT `user`, ROW_NUMBER() OVER (PARTITION BY org_id ORDER BY created_at) AS rn
                      FROM `events`
                    )
                    SELECT ${'$'}body${'$'}
                    line one
                    line two
                    ${'$'}body${'$'} AS payload
                    FROM ranked
                    WHERE rn = 1;
                    """.trimIndent(),
                language = "postgresql",
            )

        val tokens = highlighted.tokens.flatten()
        assertEquals("postgresql", highlighted.language)
        assertTrue(tokens.any { it.text == "WITH" && it.type == CodeTokenType.Keyword })
        assertTrue(tokens.any { it.text == "ROW_NUMBER" && it.type == CodeTokenType.Function })
        assertTrue(tokens.any { it.text == "OVER" && it.type == CodeTokenType.Keyword })
        assertTrue(tokens.any { it.text == "PARTITION" && it.type == CodeTokenType.Keyword })
        assertTrue(tokens.any { it.text == "`user`" && it.type == CodeTokenType.Annotation })
        assertTrue(tokens.any { it.text == "`events`" && it.type == CodeTokenType.Annotation })
        assertTrue(highlighted.tokens[4].any { it.text == "SELECT" && it.type == CodeTokenType.Keyword })
        assertTrue(highlighted.tokens[4].any { it.text == "${'$'}body${'$'}" && it.type == CodeTokenType.StringLiteral })
        assertEquals(CodeTokenType.StringLiteral, highlighted.tokens[5].single().type)
        assertEquals("line one", highlighted.tokens[5].single().text)
        assertEquals(CodeTokenType.StringLiteral, highlighted.tokens[6].single().type)
        assertEquals("line two", highlighted.tokens[6].single().text)
        assertTrue(highlighted.tokens[7].any { it.text == "${'$'}body${'$'}" && it.type == CodeTokenType.StringLiteral })
        assertTrue(highlighted.tokens[7].any { it.text == "AS" && it.type == CodeTokenType.Keyword })
    }

    @Test
    fun highlightsNestedFencedCodeWithTildeAndAttributeFences() {
        val highlighted =
            PaletteCodeHighlighter.highlight(
                code =
                    """
                    ~~~kotlin
                    fun main() = println("ok")
                    ~~~

                    ```{.kotlin title="Greeting.kt"}
                    val answer = 42
                    ```
                    """.trimIndent(),
                language = "markdown",
            )

        val tokens = highlighted.tokens.flatten()
        assertTrue(tokens.any { it.text == "fun" && it.type == CodeTokenType.Keyword })
        assertTrue(tokens.any { it.text == "val" && it.type == CodeTokenType.Keyword })
    }

    @Test
    fun highlightsQualifiedKotlinAnnotations() {
        val result = PaletteCodeHighlighter.highlight(
            """@androidx.compose.runtime.Composable
fun Greeting() {}""",
            "kotlin",
        )
        val annotationToken = result.tokens[0].first { it.type == CodeTokenType.Annotation }
        assertEquals("@androidx.compose.runtime.Composable", annotationToken.text)
    }

    @Test
    fun highlightsNewKotlinContextKeywords() {
        val result = PaletteCodeHighlighter.highlight("context(Resolver) suspend fun resolve()", "kotlin")
        val contextToken = result.tokens[0].first { it.text == "context" }
        assertEquals(CodeTokenType.Keyword, contextToken.type)
        val suspendToken = result.tokens[0].first { it.text == "suspend" }
        assertEquals(CodeTokenType.Keyword, suspendToken.type)
    }
}
