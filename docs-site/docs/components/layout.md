# Layout Components

## Row / Col (24-column grid)

```kotlin
import xyz.junerver.compose.palette.components.grid.PRow

PRow {
    // 24-column grid; span out of 24
    PCol(span = 12) { Card1() }
    PCol(span = 12) { Card2() }
}
```

Use `gutter` for column spacing:

```kotlin
PRow(gutter = 16.dp) { /* ... */ }
```

## Space

Evenly spaced items in a row or column.

```kotlin
import xyz.junerver.compose.palette.components.space.PSpace

PSpace(direction = Direction.ROW, spacing = 8.dp) {
    PButton("A")
    PButton("B")
}
```

## Container & Scaffold

- **Container** — max-width centered wrapper
- **Scaffold** — Material-style app shell (app bar / drawer / FAB slots)
- **Screen** — full-screen container with platform-aware status handling

```kotlin
import xyz.junerver.compose.palette.components.screen.Screen

Screen {
    Text("Full screen content")
}
```

See the component source for full APIs.
