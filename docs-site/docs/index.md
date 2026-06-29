# Palette

**Palette** is a Compose Multiplatform component library targeting Android, Desktop (JVM), iOS and Web (WASM) from a single codebase.

## Features

- :material-celluloid: **88+ components** — General, Form, Data Display, Feedback, Navigation, Layout
- :material-palette: **Themeable** — a full token system (`PaletteTheme` colors / spacing / shapes / typography) with root-level component theme overrides and dark mode
- :material-chart-bar: **Feature modules** — Charts (pie/bar/line), code syntax highlighting (16 languages), Markdown rendering/editing, Mermaid diagrams (19 types)
- :material-language-kotlin: **Compose-native** — no WebView, no third-party UI deps; everything renders with Compose Canvas
- :material-web: **Web/WASM** — run the same components in the browser

## Installation

```kotlin
// build.gradle.kts
dependencies {
    implementation("xyz.junerver.compose:palette:<version>")
}
```

Palette is published to Maven Central. Requires Kotlin 2.x and Compose Multiplatform 1.7+.

## Quick start

```kotlin
import xyz.junerver.compose.palette.core.theme.PaletteMaterialTheme
import xyz.junerver.compose.palette.components.button.PButton

PaletteMaterialTheme {
    PButton(text = "Hello Palette", onClick = { /* ... */ })
}
```

## Explore

- **[Getting Started](getting-started.md)** — setup, first component, theming basics
- **[Components](components/index.md)** — browse the component catalog by category
- **[Playground](playground.md)** — interactive live previews (WASM)
- **[Theming](theming.md)** — customize the entire look from one place

## Platform support

| Platform | Status |
| --- | --- |
| Android (minSdk 24) | :material-check: Stable |
| Desktop (JVM) | :material-check: Stable |
| iOS (x64 / arm64 / simulator) | :material-check: Stable |
| Web (WASM) | :material-flask: Experimental |
