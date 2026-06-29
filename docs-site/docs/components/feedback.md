# Feedback Components

## Dialog

```kotlin
import xyz.junerver.compose.palette.components.dialog.PDialog

PDialog(
    visible = show,
    onDismissRequest = { show = false },
    title = "Confirm",
) {
    Text("Are you sure?")
}
```

## Drawer

A side panel sliding in from an edge.

```kotlin
import xyz.junerver.compose.palette.components.drawer.PDrawer

PDrawer(visible = show, onDismissRequest = { show = false }) {
    Text("Drawer content")
}
```

## Toast / Message / Notification

Transient feedback at different levels of prominence:

```kotlin
import xyz.junerver.compose.palette.components.toast.PToast
import xyz.junerver.compose.palette.components.message.PMessage
import xyz.junerver.compose.palette.components.notification.PNotification

PToast.show("Saved")
PMessage.success("Done")
PNotification.info("Update available", "v2 is out")
```

## Popover / Popconfirm / Tooltip

- **Popover** — rich content in a floating panel
- **Popconfirm** — confirmation popover for actions
- **Tooltip** — lightweight text hints on hover

```kotlin
import xyz.junerver.compose.palette.components.popover.PPopover
import xyz.junerver.compose.palette.components.tooltip.PTooltip

PPopover(trigger = { Text("Open") }) { Text("Popover content") }
PTooltip(text = "Helpful hint") { Icon(Icons.Default.Info, null) }
```

## Progress & Loading

```kotlin
import xyz.junerver.compose.palette.components.progress.PProgress
import xyz.junerver.compose.palette.components.progress.PCircleProgress

PProgress(progress = 0.6f)
PCircleProgress(progress = 0.6f)
```

- **Loading** — overlay spinner
- **Skeleton** — content-shaped placeholders during load
- **DashboardProgress** — gauge-style progress

See the component source for full APIs.
