package xyz.junerver.compose.palette.core.theme

import java.io.File
import kotlin.test.Test
import kotlin.test.assertTrue

class ThemeTokenizationStaticAuditTest {
    @Test
    fun defaults_shouldUseComponentTokensInsteadOfReadingPaletteColorsDirectly() {
        val violations = defaultsFiles()
            .filterNot { it.name == "TextDefaults.kt" }
            .violations { line -> "PaletteTheme.colors" in line }

        assertTrue(
            violations.isEmpty(),
            buildString {
                appendLine("Defaults files should route component styles through PaletteTheme.componentThemes.")
                appendLine("TextDefaults is the only current allowlist entry because it exposes base text roles.")
                append(violations.joinToString(separator = "\n"))
            },
        )
    }

    @Test
    fun defaults_shouldNotIntroduceRawBlackWhiteOrInlineAlphaStyleColors() {
        val rawColorPattern = Regex("""Color\.(Black|White)|copy\(\s*alpha\s*=\s*0\.""")
        val violations = defaultsFiles().violations { line -> rawColorPattern.containsMatchIn(line) }

        assertTrue(
            violations.isEmpty(),
            buildString {
                appendLine("Defaults files should derive major state colors from component/core tokens.")
                appendLine("If a raw color is an algorithmic constant, document it and add a narrow allowlist.")
                append(violations.joinToString(separator = "\n"))
            },
        )
    }

    private fun defaultsFiles(): List<File> {
        val moduleRoot = paletteModuleRoot()
        val sourceRoots = listOf(
            File(moduleRoot, "src/commonMain/kotlin/xyz/junerver/compose/palette/components"),
            File(moduleRoot, "src/commonMain/kotlin/xyz/junerver/compose/palette/foundation"),
        )

        return sourceRoots
            .filter(File::isDirectory)
            .flatMap { root ->
                root.walkTopDown()
                    .filter { it.isFile && it.name.endsWith("Defaults.kt") }
                    .toList()
            }
            .sortedBy { it.invariantSeparatorsPath }
    }

    private fun paletteModuleRoot(): File {
        val userDir = File(System.getProperty("user.dir")).absoluteFile
        return listOf(File(userDir, "palette"), userDir)
            .first { File(it, "src/commonMain/kotlin/xyz/junerver/compose/palette").isDirectory }
    }

    private fun List<File>.violations(predicate: (String) -> Boolean): List<String> =
        flatMap { file ->
            file.readLines().mapIndexedNotNull { index, line ->
                if (predicate(line)) {
                    "${file.relativeTo(paletteModuleRoot()).invariantSeparatorsPath}:${index + 1}: ${line.trim()}"
                } else {
                    null
                }
            }
        }
}
