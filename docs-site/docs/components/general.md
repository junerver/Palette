# General Components

## Button

Buttons trigger actions.

```kotlin
import xyz.junerver.compose.palette.components.button.PButton
import xyz.junerver.compose.palette.components.button.ButtonType
import xyz.junerver.compose.palette.components.button.ButtonSize

PButton(text = "Primary", onClick = {})
PButton(text = "Secondary", type = ButtonType.SECONDARY, onClick = {})
PButton(text = "Disabled", disabled = true)
PButton(text = "Loading", loading = true)
```

| Parameter | Type | Default | Description |
| --- | --- | --- | --- |
| `text` | `String` | — | Button label |
| `type` | `ButtonType` | `PRIMARY` | Visual style (`PRIMARY`/`SECONDARY`/`OUTLINED`/...) |
| `size` | `ButtonSize` | `LARGE` | `SMALL`/`MEDIUM`/`LARGE` |
| `disabled` | `Boolean` | `false` | Disables interaction |
| `loading` | `Boolean` | `false` | Shows a spinner |
| `onClick` | `(() -> Unit)?` | `null` | Click handler |

## Badge

Small status markers, often overlaid on an icon.

```kotlin
import xyz.junerver.compose.palette.components.badge.PBadge

PBadge(content = "New")
PBadge(content = "99+")
PBadge() // dot badge
```

The optional `holder` slot lets you overlay the badge on another composable:

```kotlin
PBadge(content = "3") {
    Icon(Icons.Default.Notifications, contentDescription = null)
}
```

## Card

A surface container for grouping content.

```kotlin
import xyz.junerver.compose.palette.components.card.PCard

PCard {
    Text("Card content")
}
```

`CardDefaults` exposes container color, corner radius, elevation, and border — all theme-derived.

## Tag

Compact labels for categorizing or marking.

```kotlin
import xyz.junerver.compose.palette.components.tag.PTag

PTag(text = "v1.0", closable = true, onClose = {})
```

## Other general components

- **Avatar** — user/image avatars
- **Skeleton** — loading placeholders
- **Empty** — empty-state illustration
- **Result** — success/error result pages
- **Tour** — guided product tours
- **FloatButton** / **Affix** / **Backtop** — floating & sticky actions

See the component source for full APIs.
