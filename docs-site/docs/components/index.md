# Components

Palette ships **88+ components** organized into six categories. This section documents the most commonly used components per category. See the component source for the full parameter list, and the [Playground](../playground.md) for live previews.

## Categories

- **[General](general.md)** — Button, Badge, Card, Tag, Avatar, Skeleton, Empty, Result, Tour, FloatButton, Affix, Backtop, Divider
- **[Form](form.md)** — TextField, Select, Autocomplete, Cascader, Checkbox, Radio, Switch, Slider, Rate, Upload, InputNumber, InputOTP, DatePicker, TimePicker, Mentions
- **[Data Display](data-display.md)** — Table, DataGrid, Tree, TreeSelect, Descriptions, Statistic, Timeline, List, Collapse, Transfer, Tag, Chart, Code, Markdown, Mermaid
- **[Feedback](feedback.md)** — Dialog, Drawer, Toast, Message, Notification, Popover, Popconfirm, Tooltip, Progress, Loading
- **[Navigation](navigation.md)** — Menu, Tabs, Breadcrumb, Pagination, Steps, BottomNavigation, PageHeader, CommandPalette, Carousel, Collapse
- **[Layout](layout.md)** — Row/Col (24-col grid), Space, Container, Scaffold, Screen

## Conventions

Every component follows the same structure:

- `PXxx` — the Composable entry point
- `XxxDefaults` — an object exposing theme-derived defaults
- `XxxColors` (where relevant) — an immutable color bundle you can override
- Parameters: explicit arg > `XxxDefaults` > component token > semantic token

All defaults derive from `PaletteTheme`, so you can retheme the entire library from one place — see [Theming](../theming.md).

## Component sizes & status

Many components accept `ComponentSize` (`Small` / `Medium` / `Large`) and `ComponentStatus` (`Default` / `Success` / `Warning` / `Error`):

```kotlin
PButton(text = "Save", size = ButtonSize.LARGE)
BorderTextField(
    value = text,
    onValueChange = {},
    status = ComponentStatus.Error,
    placeholder = "Required",
)
```
