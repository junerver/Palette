# Getting Started

This guide walks you through adding Palette to a Compose Multiplatform project and rendering your first component.

## Prerequisites

- Kotlin **2.x**
- Compose Multiplatform **1.7+**
- JDK 17+ (Gradle JVM)

## 1. Add the dependency

Palette is on Maven Central. Add it to your module's `commonMain` dependencies:

```kotlin
// build.gradle.kts
kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("xyz.junerver.compose:palette:<version>")
            }
        }
    }
}
```

If you use the version catalog:

```toml
# gradle/libs.versions.toml
[versions]
palette = "<version>"
[libraries]
palette = { module = "xyz.junerver.compose:palette", version.ref = "palette" }
```

## 2. Wrap your UI in a Palette theme

Palette components read their colors, spacing, shapes and typography from `PaletteTheme`. Wrap your content with `PaletteMaterialTheme`:

```kotlin
import androidx.compose.runtime.Composable
import xyz.junerver.compose.palette.core.theme.PaletteMaterialTheme
import xyz.junerver.compose.palette.components.button.PButton

@Composable
fun App() {
    PaletteMaterialTheme {
        PButton(text = "Click me", onClick = {})
    }
}
```

`PaletteMaterialTheme` also wraps Material3, so Material components you mix in stay consistent.

## 3. Try a component

```kotlin
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.components.badge.PBadge
import xyz.junerver.compose.palette.components.button.PButton

Column(Modifier.padding(16.dp)) {
    PButton(text = "Primary", onClick = {})
    PBadge(content = "New")
}
```

## Next steps

- **[Theming](theming.md)** — override tokens globally, customize per component, dark mode
- **[Components](components/index.md)** — the full catalog
- **[Playground](playground.md)** — see components live in your browser
