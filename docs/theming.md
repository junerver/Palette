# Palette theming

Palette exposes theme tokens in two layers:

1. Core semantic tokens: colors, spacing, shapes, typography, opacity, motion, elevation, and shared control density.
2. Component tokens: `PaletteComponentThemes`, available from `PaletteTheme.componentThemes`.

Component defaults should follow this precedence:

1. Explicit component parameter.
2. Explicit parameter passed to an `XxxDefaults.colors()` or similar builder.
3. `PaletteTheme.componentThemes.xxx`.
4. Core semantic tokens such as `PaletteTheme.colors`, `PaletteTheme.spacing`, and `PaletteTheme.typography`.
5. A controlled fallback value for algorithmic or compatibility constants.

## Core tokens

`PaletteColors` keeps the existing fields (`primary`, `surface`, `onSurface`, `border`, `error`, `success`, `warning`) and adds semantic state fields:

- Text: `textPrimary`, `textSecondary`, `textTertiary`, `textDisabled`, `inverseOnSurface`.
- Surface: `pageBackground`, `appBackground`, `surfaceElevated`, `surfaceOverlay`, `inverseSurface`.
- State: `borderHover`, `borderFocus`, `borderDisabled`, `bgDisabled`, `bgHover`, `bgPressed`, `bgSelected`.
- Structure: `divider`, `overlay`, `shadow`, `shadowFocus`, `shadowError`, `info`, `danger`.

Use `copy()` when you want to preserve all existing derived values. Use `derive()` when you want derived semantic values to be recomputed from a changed core value:

```kotlin
val brandColors = PaletteColors.light().derive(
    primary = Color(0xFF0057D9),
    surface = Color(0xFFFFFFFF),
)
```

`PaletteOpacity`, `PaletteMotion`, `PaletteElevation`, and `PaletteControlTokens` centralize repeated disabled, selected, animation, elevation, border, and control-size decisions. Form-like components should read `PaletteControlTokens` through their `XxxDefaults` compatibility functions instead of reading `ComponentSize` constants directly.

## Component themes

`PaletteComponentThemes` is the root-level component override entry:

```kotlin
val colors = PaletteColors.light().derive(primary = Color(0xFF0057D9))
val baseComponents = PaletteComponentThemes.default(colors = colors)

PaletteMaterialTheme(
    colors = colors,
    componentThemes = baseComponents.copy(
        button = baseComponents.button.copy(
            primaryContainerColor = Color(0xFF0057D9),
            disabledAlpha = 0.56f,
        ),
        card = baseComponents.card.copy(
            cornerRadius = 0.dp,
            elevation = 0.dp,
        ),
    ),
) {
    App()
}
```

The current component theme registry includes:

| Token | Components using it |
| --- | --- |
| `borderContainer` | `BorderContainer`, `BorderContainerDefaults` |
| `button` | `PButton`, `ButtonDefaults` |
| `checkbox` | `ColoredCheckBox`, `CheckboxDefaults` |
| `radio` | `PRadio`, `PRadioGroup`, `RadioDefaults` |
| `switch` | `PSwitch`, `SwitchDefaults` |
| `selectionControl` | `PSlider`, `PRate`, `PToggle`, `PToggleGroup`, `PSegmented`, related Defaults |
| `form` | `PForm`, `PFormItem`, `FormDefaults` |
| `textField` | `BorderTextField`, `TextArea`, `PasswordField`, `TextFieldDefaults` |
| `select` | `PSelect`, `PAutocomplete`, `PCascader`, `PCascaderPanel`, `PTreeSelect`, `PMentions`, related Defaults |
| `dateTime` | `PDatePicker`, `PTimePicker`, `PDateTimeRange`, `PCalendar`, related Defaults |
| `input` | `PInputNumber`, `PInputOTP`, `PSearchBar`, related Defaults |
| `card` | `PCard`, `CardDefaults` |
| `table` | `PTable`, `TableDefaults` |
| `dataGrid` | `PDataGrid`, `DataGridDefaults` |
| `dataDisplay` | `PList`, `PDescriptions`, `PBadge`, `PAvatar`, `PPagination`, related Defaults |
| `dataEntry` | `PTimeline`, `PTransfer`, `PTree`, `PSortable`, `PVirtualList`, `PInfiniteScroll`, `PUpload`, related Defaults |
| `navigationMenu` | `PMenu`, `PContextMenu`, `PCommandPalette`, `PTabs`, `PBreadcrumb`, `PSteps`, `PCollapse`, related Defaults |
| `appBar` | `Toolbar`, `PPageHeader`, related Defaults |
| `floatingLayer` | `PTooltip`, `PPopover`, `PPopconfirm`, `PTour`, related Defaults |
| `dialog` | `PDialog`, `DialogDefaults` |
| `drawer` | `PDrawer`, `DrawerDefaults` |
| `popup` | `PPopup`, `PopupDefaults` |
| `actionSheet` | `PActionSheet`, `ActionSheetDefaults` |
| `message` | `PMessage`, `MessageDefaults` |
| `notification` | `PNotification`, `NotificationDefaults` |
| `toast` | `PToast`, `ToastDefaults` |
| `tag` | `PTag`, `PEditableTagGroup`, `TagDefaults` |
| `feedbackDisplay` | `PAlert`, `PResult`, `PEmpty`, `PStatistic`, related Defaults |
| `progress` | `PProgress`, `PCircleProgress`, `PDashboardProgress`, `PLoading`, `PSkeleton*`, related Defaults |
| `media` | `PCarousel`, `PImage`, `PColorPicker`, related Defaults |
| `utility` | `PQRCode`, `PBarcode`, `PWatermark`, related Defaults |
| `layout` | `PSpace`, `PRow`, `PAffix`, related Defaults |
| `floatingAction` | `PBacktop`, `PFloatButton`, related Defaults |
| `screen` | `Screen`, `ScreenDefaults` |

Additional components should be migrated by adding a small immutable `PaletteXxxTokens` class or extending the closest domain token group, then routing `XxxDefaults` through `PaletteTheme.componentThemes.xxx`. Keep existing public constants as compatibility aliases unless a major version intentionally removes them.

## Common overrides

Brand theme:

```kotlin
val colors = PaletteColors.light().derive(
    primary = Color(0xFF006ADC),
    bgSelected = Color(0x1A006ADC),
    borderFocus = Color(0x99006ADC),
)

PaletteTheme(colors = colors) {
    App()
}
```

Compact density:

```kotlin
val control = PaletteControlTokens.default().copy(
    medium = PaletteControlSizeTokens(
        height = 32.dp,
        fontSize = 14.sp,
        iconSize = 18.dp,
        horizontalPadding = 12.dp,
        verticalPadding = 4.dp,
        cornerRadius = 4.dp,
    ),
)

PaletteTheme(control = control) {
    App()
}
```

Sharp surfaces:

```kotlin
val components = PaletteComponentThemes.default().let { base ->
    base.copy(
        card = base.card.copy(cornerRadius = 0.dp),
        dialog = base.dialog.copy(borderRadius = 0.dp),
        tag = base.tag.copy(
            small = base.tag.small.copy(cornerRadius = 2.dp),
            medium = base.tag.medium.copy(cornerRadius = 2.dp),
            large = base.tag.large.copy(cornerRadius = 2.dp),
        ),
    )
}

PaletteTheme(componentThemes = components) {
    App()
}
```

Dark mode:

```kotlin
val colors = PaletteColors.dark().derive(
    primary = Color(0xFF7AB7FF),
    surfaceElevated = Color(0xFF252525),
)

PaletteMaterialTheme(colors = colors, darkTheme = true) {
    App()
}
```

## Component author checklist

When adding or migrating a component:

1. Define the major user-facing style surface: colors, size, typography, radius, border, elevation, motion, and opacity.
2. Add immutable token data only for those major surfaces.
3. Derive default tokens from core tokens in `PaletteComponentThemes.default()`.
4. Route `XxxDefaults` through `PaletteTheme.componentThemes.xxx`.
5. Keep public constants as compatibility aliases and add composable token-backed functions beside them.
6. Add tests for root theme override and explicit parameter precedence.
7. Leave algorithmic constants explicit when they are not a style decision, and document the reason in code if it is not obvious.

## Static audit

`ThemeTokenizationStaticAuditTest` guards the current migration boundary for Defaults files:

- Component Defaults should not read `PaletteTheme.colors` directly; route major visual style through `PaletteTheme.componentThemes`.
- `TextDefaults` is allowlisted because it intentionally exposes base text semantic roles.
- Defaults should not introduce raw `Color.Black` / `Color.White` or inline alpha state colors without a narrow documented allowlist.
