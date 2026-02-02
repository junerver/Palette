# Palette

A Compose Multiplatform component library supporting Android, Desktop (JVM), and iOS platforms.

## Features

- **Multiplatform Support**: Android (API 24+), Desktop (JVM), iOS (x64, arm64, simulatorArm64)
- **Design Tokens**: Centralized colors, spacing, shapes, and typography
- **Theme System**: `PaletteTheme` with dark/light mode support
- **UI Components**: Badge, Checkbox, TextField, Toolbar, Screen, and more
- **State Management**: Integration with [compose-hooks](https://github.com/junerver/compose-hooks) for React-style state management

## Quick Start

```kotlin
// build.gradle.kts
dependencies {
    implementation("xyz.junerver.compose:palette:version")
}
```

```kotlin
import xyz.junerver.compose.palette.*

@Composable
fun MyApp() {
    PaletteTheme(darkTheme = true) {
        Screen(title = "Hello Palette") {
            // Your content
        }
    }
}
```

## Build & Run

```bash
./gradlew :palette:build
./gradlew :app:run
```

## Project Structure

```
palette/
├── core/           # Tokens, Theme, Utilities
├── foundation/     # Border, Layout primitives
└── components/     # UI Components
```

## License

See [LICENSE](LICENSE) file.

---

[中文文档](./README.zh-CN.md)
