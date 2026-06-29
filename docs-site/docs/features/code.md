# Code Highlighting

The `palette-code` module provides syntax highlighting aligned with Prism.js, using a declarative grammar engine.

## 16 supported languages

| | | | |
| --- | --- | --- | --- |
| C | C++ | Go | Rust |
| Java | Kotlin | TypeScript | JavaScript |
| Python | SQL | CSS | HTML/XML |
| JSON | YAML | TOML | INI |

## Usage

```kotlin
import xyz.junerver.compose.palette.components.code.PCodeBlock

PCodeBlock(
    code = """
        fun main() = println("Hello")
    """.trimIndent(),
    language = "kotlin",
)
```

## Language aliases

- C: `c`, `h`
- C++: `cpp`, `c++`, `cxx`, `cc`, `hpp`
- Go: `go`, `golang`
- Rust: `rust`, `rs`
- Kotlin: `kotlin`, `kt`, `kts`
- SQL: `sql`, `mysql`, `postgresql`, `postgres`, `sqlite`
- ...and more

## Architecture

A declarative `Grammar` engine (Prism-aligned) resolves each language to a `GrammarHighlighter`. Adding a language = a grammar definition + registry entry. See [the roadmap](https://github.com/junerver/Palette/blob/master/docs/compose/plans/palette-code-prism-alignment-roadmap.md) for the alignment plan.
