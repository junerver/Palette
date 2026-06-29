# Navigation Components

## Menu

```kotlin
import xyz.junerver.compose.palette.components.menu.PMenu
import xyz.junerver.compose.palette.components.menu.MenuItem

PMenu(
    items = listOf(
        MenuItem("1", "New"),
        MenuItem("2", "Open"),
        MenuItem("3", "Save", disabled = true),
    ),
    selectedKey = "1",
    onSelect = {},
)
```

## Tabs

```kotlin
PTabs(tabs = listOf("Overview", "Details"), selected = 0, onSelect = {})
```

## Breadcrumb & Steps

```kotlin
PBreadcrumb(items = listOf("Home", "Settings", "Profile"))
PSteps(steps = listOf("Cart", "Pay", "Done"), current = 1)
```

## Pagination

```kotlin
PPagination(current = 2, total = 100, pageSize = 10, onChange = {})
```

## PageHeader & CommandPalette

- **PageHeader** — page title bar with back/breadcrumb/actions
- **CommandPalette** — Ctrl/Cmd+K command launcher
- **BottomNavigation** — mobile bottom nav

## Carousel

```kotlin
PCarousel(items = images) { item -> PImage(item) }
```

See the component source for full APIs.
