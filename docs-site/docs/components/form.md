# Form Components

## TextField

The primary text input, themed via `PaletteTheme.componentThemes.textField`.

```kotlin
import xyz.junerver.compose.palette.components.textfield.BorderTextField
import xyz.junerver.compose.palette.core.spec.ComponentStatus

var text by remember { mutableStateOf("") }
BorderTextField(
    value = text,
    onValueChange = { text = it },
    placeholder = "Enter your name",
    status = ComponentStatus.Default,
)
```

`ComponentStatus` drives border/label color: `Default` / `Success` / `Warning` / `Error`.

## Select (dropdown)

`PSelect` is a controlled, generic single-select dropdown — the standard way to pick one option.

```kotlin
import xyz.junerver.compose.palette.components.select.PSelect
import xyz.junerver.compose.palette.components.select.SelectOption

val options = listOf(
    SelectOption("Apple", "apple"),
    SelectOption("Banana", "banana"),
)
var value by remember { mutableStateOf<String?>(null) }
PSelect(
    options = options,
    value = value,
    onValueChange = { value = it },
    placeholder = "Pick a fruit",
    searchable = true,
)
```

`PSelect` covers the "dropdown" use case (Material3 `DropdownMenu`, three sizes, four status colors, optional search, custom option rendering).

## Checkbox & Radio

```kotlin
import xyz.junerver.compose.palette.components.checkbox.ColoredCheckBox
import xyz.junerver.compose.palette.components.radio.PRadioGroup

ColoredCheckBox(checked = checked, onCheckedChange = { checked = it })

PRadioGroup(options = listOf("A", "B"), selected = "A", onSelect = {})
```

## Switch

```kotlin
import xyz.junerver.compose.palette.components.switch.PSwitch
PSwitch(checked = on, onCheckedChange = { on = it })
```

## Slider & Rate

```kotlin
PSlider(value = 0.5f, onValueChange = {})
PRate(value = 3, count = 5, onValueChange = {})
```

## Other form components

- **Autocomplete** — free-text + suggestions
- **Cascader** — multi-level selection
- **TreeSelect** — tree-shaped selection
- **InputNumber** — numeric stepper
- **InputOTP** — one-time-password input
- **DatePicker** / **TimePicker** / **DateRangePicker** / **Calendar** — date/time
- **Upload** — file upload
- **Mentions** — @-mention input
- **Form** — `PForm` / `PFormItem` with validation rules

See the component source for full APIs.
