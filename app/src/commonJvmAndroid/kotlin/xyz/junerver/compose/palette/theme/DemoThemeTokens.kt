package xyz.junerver.compose.palette.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import xyz.junerver.compose.palette.core.tokens.PaletteColors
import xyz.junerver.compose.palette.core.tokens.PaletteControlSizeTokens
import xyz.junerver.compose.palette.core.tokens.PaletteControlTokens
import xyz.junerver.compose.palette.core.tokens.PaletteElevation
import xyz.junerver.compose.palette.core.tokens.PaletteMotion
import xyz.junerver.compose.palette.core.tokens.PaletteOpacity
import xyz.junerver.compose.palette.core.tokens.PaletteShapes
import xyz.junerver.compose.palette.core.tokens.PaletteSpacing
import xyz.junerver.compose.palette.core.tokens.PaletteTypography

internal data class DemoThemeTokenConfig(
    val colors: Map<String, Color> = emptyMap(),
    val dp: Map<String, Float> = emptyMap(),
    val sp: Map<String, Float> = emptyMap(),
    val floats: Map<String, Float> = emptyMap(),
    val ints: Map<String, Int> = emptyMap(),
) {
    fun setColor(
        key: String,
        value: Color?,
    ): DemoThemeTokenConfig = copy(colors = colors.withOptionalValue(key, value))

    fun setDp(
        key: String,
        value: Float?,
    ): DemoThemeTokenConfig = copy(dp = dp.withOptionalValue(key, value))

    fun setSp(
        key: String,
        value: Float?,
    ): DemoThemeTokenConfig = copy(sp = sp.withOptionalValue(key, value))

    fun setFloat(
        key: String,
        value: Float?,
    ): DemoThemeTokenConfig = copy(floats = floats.withOptionalValue(key, value))

    fun setInt(
        key: String,
        value: Int?,
    ): DemoThemeTokenConfig = copy(ints = ints.withOptionalValue(key, value))

    fun colorOrNull(key: String): Color? = colors[key]

    fun dpOrDefault(
        key: String,
        default: Float,
    ): Float = dp[key] ?: default

    fun spOrDefault(
        key: String,
        default: Float,
    ): Float = sp[key] ?: default

    fun floatOrDefault(
        key: String,
        default: Float,
    ): Float = floats[key] ?: default

    fun intOrDefault(
        key: String,
        default: Int,
    ): Int = ints[key] ?: default

    fun hasCustomValue(key: String): Boolean =
        key in colors || key in dp || key in sp || key in floats || key in ints

    fun hasAnyCustomValue(): Boolean = customCount > 0

    val customCount: Int
        get() = colors.size + dp.size + sp.size + floats.size + ints.size
}

private fun <T> Map<String, T>.withOptionalValue(
    key: String,
    value: T?,
): Map<String, T> = if (value == null) this - key else this + (key to value)

internal data class ColorTokenSpec(
    val key: String,
    val label: String,
    val defaultValue: (PaletteColors) -> Color,
)

internal data class NumberTokenSpec(
    val key: String,
    val label: String,
    val defaultValue: Float,
    val min: Float,
    val max: Float,
    val step: Float,
    val unit: TokenNumberUnit,
)

internal enum class TokenNumberUnit(
    val suffix: String,
) {
    Dp("dp"),
    Sp("sp"),
    Float(""),
    Milliseconds("ms"),
}

internal data class IntTokenSpec(
    val key: String,
    val label: String,
    val defaultValue: Int,
    val min: Int,
    val max: Int,
    val step: Int,
    val suffix: String,
)

internal data class ComponentThemeGroupSpec(
    val key: String,
    val tokenClass: String,
    val label: String,
)

internal val colorTokenSpecs =
    listOf(
        ColorTokenSpec("colors.primary", "primary") { it.primary },
        ColorTokenSpec("colors.onPrimary", "onPrimary") { it.onPrimary },
        ColorTokenSpec("colors.border", "border") { it.border },
        ColorTokenSpec("colors.surface", "surface") { it.surface },
        ColorTokenSpec("colors.onSurface", "onSurface") { it.onSurface },
        ColorTokenSpec("colors.hint", "hint") { it.hint },
        ColorTokenSpec("colors.error", "error") { it.error },
        ColorTokenSpec("colors.onError", "onError") { it.onError },
        ColorTokenSpec("colors.success", "success") { it.success },
        ColorTokenSpec("colors.warning", "warning") { it.warning },
        ColorTokenSpec("colors.info", "info") { it.info },
        ColorTokenSpec("colors.danger", "danger") { it.danger },
        ColorTokenSpec("colors.textPrimary", "textPrimary") { it.textPrimary },
        ColorTokenSpec("colors.textSecondary", "textSecondary") { it.textSecondary },
        ColorTokenSpec("colors.textTertiary", "textTertiary") { it.textTertiary },
        ColorTokenSpec("colors.textDisabled", "textDisabled") { it.textDisabled },
        ColorTokenSpec("colors.inverseSurface", "inverseSurface") { it.inverseSurface },
        ColorTokenSpec("colors.inverseOnSurface", "inverseOnSurface") { it.inverseOnSurface },
        ColorTokenSpec("colors.pageBackground", "pageBackground") { it.pageBackground },
        ColorTokenSpec("colors.appBackground", "appBackground") { it.appBackground },
        ColorTokenSpec("colors.surfaceElevated", "surfaceElevated") { it.surfaceElevated },
        ColorTokenSpec("colors.surfaceOverlay", "surfaceOverlay") { it.surfaceOverlay },
        ColorTokenSpec("colors.divider", "divider") { it.divider },
        ColorTokenSpec("colors.borderHover", "borderHover") { it.borderHover },
        ColorTokenSpec("colors.borderFocus", "borderFocus") { it.borderFocus },
        ColorTokenSpec("colors.borderDisabled", "borderDisabled") { it.borderDisabled },
        ColorTokenSpec("colors.bgDisabled", "bgDisabled") { it.bgDisabled },
        ColorTokenSpec("colors.bgHover", "bgHover") { it.bgHover },
        ColorTokenSpec("colors.bgPressed", "bgPressed") { it.bgPressed },
        ColorTokenSpec("colors.bgSelected", "bgSelected") { it.bgSelected },
        ColorTokenSpec("colors.overlay", "overlay") { it.overlay },
        ColorTokenSpec("colors.shadow", "shadow") { it.shadow },
        ColorTokenSpec("colors.shadowFocus", "shadowFocus") { it.shadowFocus },
        ColorTokenSpec("colors.shadowError", "shadowError") { it.shadowError },
    )

internal val spacingTokenSpecs =
    listOf(
        NumberTokenSpec("spacing.none", "none", 0f, 0f, 64f, 1f, TokenNumberUnit.Dp),
        NumberTokenSpec("spacing.extraSmall", "extraSmall", 4f, 0f, 64f, 1f, TokenNumberUnit.Dp),
        NumberTokenSpec("spacing.small", "small", 8f, 0f, 72f, 1f, TokenNumberUnit.Dp),
        NumberTokenSpec("spacing.medium", "medium", 16f, 0f, 96f, 1f, TokenNumberUnit.Dp),
        NumberTokenSpec("spacing.large", "large", 24f, 0f, 128f, 1f, TokenNumberUnit.Dp),
        NumberTokenSpec("spacing.extraLarge", "extraLarge", 32f, 0f, 160f, 1f, TokenNumberUnit.Dp),
    )

internal val shapeTokenSpecs =
    listOf(
        NumberTokenSpec("shapes.small.radius", "small.radius", 4f, 0f, 40f, 1f, TokenNumberUnit.Dp),
        NumberTokenSpec("shapes.medium.radius", "medium.radius", 8f, 0f, 48f, 1f, TokenNumberUnit.Dp),
        NumberTokenSpec("shapes.large.radius", "large.radius", 12f, 0f, 64f, 1f, TokenNumberUnit.Dp),
    )

internal val typographyTokenSpecs =
    listOf(
        NumberTokenSpec("typography.title.fontSize", "title.fontSize", 18f, 10f, 40f, 1f, TokenNumberUnit.Sp),
        NumberTokenSpec("typography.title.lineHeight", "title.lineHeight", 26f, 10f, 56f, 1f, TokenNumberUnit.Sp),
        NumberTokenSpec("typography.body.fontSize", "body.fontSize", 14f, 8f, 32f, 1f, TokenNumberUnit.Sp),
        NumberTokenSpec("typography.body.lineHeight", "body.lineHeight", 20f, 8f, 48f, 1f, TokenNumberUnit.Sp),
        NumberTokenSpec("typography.label.fontSize", "label.fontSize", 12f, 8f, 28f, 1f, TokenNumberUnit.Sp),
        NumberTokenSpec("typography.label.lineHeight", "label.lineHeight", 16f, 8f, 40f, 1f, TokenNumberUnit.Sp),
    )

internal val opacityTokenSpecs =
    listOf(
        NumberTokenSpec("opacity.disabled", "disabled", 0.5f, 0f, 1f, 0.05f, TokenNumberUnit.Float),
        NumberTokenSpec("opacity.disabledStrong", "disabledStrong", 0.7f, 0f, 1f, 0.05f, TokenNumberUnit.Float),
        NumberTokenSpec("opacity.subtle", "subtle", 0.6f, 0f, 1f, 0.05f, TokenNumberUnit.Float),
        NumberTokenSpec("opacity.muted", "muted", 0.45f, 0f, 1f, 0.05f, TokenNumberUnit.Float),
        NumberTokenSpec("opacity.hover", "hover", 0.08f, 0f, 0.5f, 0.01f, TokenNumberUnit.Float),
        NumberTokenSpec("opacity.pressed", "pressed", 0.12f, 0f, 0.5f, 0.01f, TokenNumberUnit.Float),
        NumberTokenSpec("opacity.selected", "selected", 0.12f, 0f, 0.5f, 0.01f, TokenNumberUnit.Float),
        NumberTokenSpec("opacity.focusRing", "focusRing", 0.2f, 0f, 0.8f, 0.01f, TokenNumberUnit.Float),
        NumberTokenSpec("opacity.overlay", "overlay", 0.45f, 0f, 1f, 0.05f, TokenNumberUnit.Float),
        NumberTokenSpec("opacity.elevatedSurface", "elevatedSurface", 0.95f, 0f, 1f, 0.05f, TokenNumberUnit.Float),
        NumberTokenSpec("opacity.borderSubtle", "borderSubtle", 0.5f, 0f, 1f, 0.05f, TokenNumberUnit.Float),
    )

internal val motionTokenSpecs =
    listOf(
        IntTokenSpec("motion.durationFast", "durationFast", 150, 0, 2000, 25, "ms"),
        IntTokenSpec("motion.durationNormal", "durationNormal", 250, 0, 3000, 25, "ms"),
        IntTokenSpec("motion.durationSlow", "durationSlow", 350, 0, 4000, 25, "ms"),
        IntTokenSpec("motion.overlayEnter", "overlayEnter", 180, 0, 3000, 25, "ms"),
        IntTokenSpec("motion.overlayExit", "overlayExit", 180, 0, 3000, 25, "ms"),
    )

internal val elevationTokenSpecs =
    listOf(
        NumberTokenSpec("elevation.none", "none", 0f, 0f, 32f, 1f, TokenNumberUnit.Dp),
        NumberTokenSpec("elevation.raised", "raised", 1f, 0f, 32f, 1f, TokenNumberUnit.Dp),
        NumberTokenSpec("elevation.overlay", "overlay", 4f, 0f, 48f, 1f, TokenNumberUnit.Dp),
        NumberTokenSpec("elevation.modal", "modal", 8f, 0f, 64f, 1f, TokenNumberUnit.Dp),
        NumberTokenSpec("elevation.floating", "floating", 12f, 0f, 80f, 1f, TokenNumberUnit.Dp),
        NumberTokenSpec("elevation.focusShadowBlur", "focusShadowBlur", 4f, 0f, 48f, 1f, TokenNumberUnit.Dp),
    )

internal val controlTokenSpecs =
    listOf(
        NumberTokenSpec("control.small.height", "small.height", 24f, 12f, 72f, 1f, TokenNumberUnit.Dp),
        NumberTokenSpec("control.small.fontSize", "small.fontSize", 14f, 8f, 30f, 1f, TokenNumberUnit.Sp),
        NumberTokenSpec("control.small.iconSize", "small.iconSize", 16f, 8f, 40f, 1f, TokenNumberUnit.Dp),
        NumberTokenSpec("control.small.horizontalPadding", "small.horizontalPadding", 12f, 0f, 48f, 1f, TokenNumberUnit.Dp),
        NumberTokenSpec("control.small.verticalPadding", "small.verticalPadding", 4f, 0f, 32f, 1f, TokenNumberUnit.Dp),
        NumberTokenSpec("control.small.cornerRadius", "small.cornerRadius", 4f, 0f, 36f, 1f, TokenNumberUnit.Dp),
        NumberTokenSpec("control.medium.height", "medium.height", 40f, 16f, 88f, 1f, TokenNumberUnit.Dp),
        NumberTokenSpec("control.medium.fontSize", "medium.fontSize", 16f, 8f, 34f, 1f, TokenNumberUnit.Sp),
        NumberTokenSpec("control.medium.iconSize", "medium.iconSize", 20f, 8f, 48f, 1f, TokenNumberUnit.Dp),
        NumberTokenSpec("control.medium.horizontalPadding", "medium.horizontalPadding", 16f, 0f, 64f, 1f, TokenNumberUnit.Dp),
        NumberTokenSpec("control.medium.verticalPadding", "medium.verticalPadding", 8f, 0f, 40f, 1f, TokenNumberUnit.Dp),
        NumberTokenSpec("control.medium.cornerRadius", "medium.cornerRadius", 6f, 0f, 44f, 1f, TokenNumberUnit.Dp),
        NumberTokenSpec("control.large.height", "large.height", 40f, 20f, 104f, 1f, TokenNumberUnit.Dp),
        NumberTokenSpec("control.large.fontSize", "large.fontSize", 18f, 8f, 40f, 1f, TokenNumberUnit.Sp),
        NumberTokenSpec("control.large.iconSize", "large.iconSize", 24f, 8f, 56f, 1f, TokenNumberUnit.Dp),
        NumberTokenSpec("control.large.horizontalPadding", "large.horizontalPadding", 20f, 0f, 80f, 1f, TokenNumberUnit.Dp),
        NumberTokenSpec("control.large.verticalPadding", "large.verticalPadding", 12f, 0f, 48f, 1f, TokenNumberUnit.Dp),
        NumberTokenSpec("control.large.cornerRadius", "large.cornerRadius", 8f, 0f, 56f, 1f, TokenNumberUnit.Dp),
        NumberTokenSpec("control.borderWidth", "borderWidth", 1f, 0f, 8f, 0.5f, TokenNumberUnit.Dp),
        NumberTokenSpec("control.focusBorderWidth", "focusBorderWidth", 2f, 0f, 10f, 0.5f, TokenNumberUnit.Dp),
        NumberTokenSpec("control.disabledBorderWidth", "disabledBorderWidth", 1f, 0f, 8f, 0.5f, TokenNumberUnit.Dp),
        NumberTokenSpec("control.focusRingOffset", "focusRingOffset", 2f, 0f, 16f, 0.5f, TokenNumberUnit.Dp),
    )

internal val componentThemeGroupSpecs =
    listOf(
        ComponentThemeGroupSpec("componentThemes.borderContainer", "PaletteBorderContainerTokens", "BorderContainer"),
        ComponentThemeGroupSpec("componentThemes.button", "PaletteButtonTokens", "Button"),
        ComponentThemeGroupSpec("componentThemes.checkbox", "PaletteCheckboxTokens", "Checkbox"),
        ComponentThemeGroupSpec("componentThemes.radio", "PaletteRadioTokens", "Radio"),
        ComponentThemeGroupSpec("componentThemes.switch", "PaletteSwitchTokens", "Switch"),
        ComponentThemeGroupSpec("componentThemes.selectionControl", "PaletteSelectionControlTokens", "Slider / Rate / Toggle / Segmented"),
        ComponentThemeGroupSpec("componentThemes.form", "PaletteFormTokens", "Form"),
        ComponentThemeGroupSpec("componentThemes.textField", "PaletteTextFieldTokens", "TextField"),
        ComponentThemeGroupSpec("componentThemes.select", "PaletteSelectTokens", "Select-like"),
        ComponentThemeGroupSpec("componentThemes.dateTime", "PaletteDateTimeTokens", "Date / Time"),
        ComponentThemeGroupSpec("componentThemes.input", "PaletteInputTokens", "InputNumber / OTP / SearchBar"),
        ComponentThemeGroupSpec("componentThemes.card", "PaletteCardTokens", "Card"),
        ComponentThemeGroupSpec("componentThemes.table", "PaletteTableTokens", "Table"),
        ComponentThemeGroupSpec("componentThemes.dataGrid", "PaletteDataGridTokens", "DataGrid"),
        ComponentThemeGroupSpec("componentThemes.dataDisplay", "PaletteDataDisplayTokens", "List / Badge / Avatar / Pagination"),
        ComponentThemeGroupSpec("componentThemes.dataEntry", "PaletteDataEntryTokens", "Tree / Transfer / Upload"),
        ComponentThemeGroupSpec("componentThemes.navigationMenu", "PaletteNavigationMenuTokens", "Menu / Tabs / Steps"),
        ComponentThemeGroupSpec("componentThemes.appBar", "PaletteAppBarTokens", "Toolbar / PageHeader"),
        ComponentThemeGroupSpec("componentThemes.floatingLayer", "PaletteFloatingLayerTokens", "Tooltip / Popover / Tour"),
        ComponentThemeGroupSpec("componentThemes.dialog", "PaletteDialogTokens", "Dialog"),
        ComponentThemeGroupSpec("componentThemes.drawer", "PaletteDrawerTokens", "Drawer"),
        ComponentThemeGroupSpec("componentThemes.popup", "PalettePopupTokens", "Popup"),
        ComponentThemeGroupSpec("componentThemes.actionSheet", "PaletteActionSheetTokens", "ActionSheet"),
        ComponentThemeGroupSpec("componentThemes.message", "PaletteMessageTokens", "Message"),
        ComponentThemeGroupSpec("componentThemes.notification", "PaletteNotificationTokens", "Notification"),
        ComponentThemeGroupSpec("componentThemes.toast", "PaletteToastTokens", "Toast"),
        ComponentThemeGroupSpec("componentThemes.tag", "PaletteTagTokens", "Tag"),
        ComponentThemeGroupSpec("componentThemes.feedbackDisplay", "PaletteFeedbackDisplayTokens", "Alert / Result / Empty / Statistic"),
        ComponentThemeGroupSpec("componentThemes.progress", "PaletteProgressTokens", "Progress / Loading / Skeleton"),
        ComponentThemeGroupSpec("componentThemes.media", "PaletteMediaTokens", "Carousel / Image / ColorPicker"),
        ComponentThemeGroupSpec("componentThemes.utility", "PaletteUtilityTokens", "QRCode / Barcode / Watermark"),
        ComponentThemeGroupSpec("componentThemes.layout", "PaletteLayoutTokens", "Space / Grid / Affix"),
        ComponentThemeGroupSpec("componentThemes.floatingAction", "PaletteFloatingActionTokens", "Backtop / FloatButton"),
        ComponentThemeGroupSpec("componentThemes.screen", "PaletteScreenTokens", "Screen"),
    )

internal fun DemoThemeTokenConfig.resolveColors(base: PaletteColors): PaletteColors {
    val primary = colorOrNull("colors.primary") ?: base.primary
    val onPrimary = colorOrNull("colors.onPrimary") ?: base.onPrimary
    val border = colorOrNull("colors.border") ?: base.border
    val surface = colorOrNull("colors.surface") ?: base.surface
    val onSurface = colorOrNull("colors.onSurface") ?: base.onSurface
    val hint = colorOrNull("colors.hint") ?: base.hint
    val error = colorOrNull("colors.error") ?: base.error
    val onError = colorOrNull("colors.onError") ?: base.onError
    val success = colorOrNull("colors.success") ?: base.success
    val warning = colorOrNull("colors.warning") ?: base.warning
    val derived = base.derive(
        primary = primary,
        onPrimary = onPrimary,
        border = border,
        surface = surface,
        onSurface = onSurface,
        hint = hint,
        error = error,
        onError = onError,
        success = success,
        warning = warning,
        overlay = base.overlay,
        shadow = base.shadow,
    )

    return derived.copy(
        info = colorOrNull("colors.info") ?: derived.info,
        danger = colorOrNull("colors.danger") ?: derived.danger,
        textPrimary = colorOrNull("colors.textPrimary") ?: derived.textPrimary,
        textSecondary = colorOrNull("colors.textSecondary") ?: derived.textSecondary,
        textTertiary = colorOrNull("colors.textTertiary") ?: derived.textTertiary,
        textDisabled = colorOrNull("colors.textDisabled") ?: derived.textDisabled,
        inverseSurface = colorOrNull("colors.inverseSurface") ?: derived.inverseSurface,
        inverseOnSurface = colorOrNull("colors.inverseOnSurface") ?: derived.inverseOnSurface,
        pageBackground = colorOrNull("colors.pageBackground") ?: derived.pageBackground,
        appBackground = colorOrNull("colors.appBackground") ?: derived.appBackground,
        surfaceElevated = colorOrNull("colors.surfaceElevated") ?: derived.surfaceElevated,
        surfaceOverlay = colorOrNull("colors.surfaceOverlay") ?: derived.surfaceOverlay,
        divider = colorOrNull("colors.divider") ?: derived.divider,
        borderHover = colorOrNull("colors.borderHover") ?: derived.borderHover,
        borderFocus = colorOrNull("colors.borderFocus") ?: derived.borderFocus,
        borderDisabled = colorOrNull("colors.borderDisabled") ?: derived.borderDisabled,
        bgDisabled = colorOrNull("colors.bgDisabled") ?: derived.bgDisabled,
        bgHover = colorOrNull("colors.bgHover") ?: derived.bgHover,
        bgPressed = colorOrNull("colors.bgPressed") ?: derived.bgPressed,
        bgSelected = colorOrNull("colors.bgSelected") ?: derived.bgSelected,
        overlay = colorOrNull("colors.overlay") ?: derived.overlay,
        shadow = colorOrNull("colors.shadow") ?: derived.shadow,
        shadowFocus = colorOrNull("colors.shadowFocus") ?: derived.shadowFocus,
        shadowError = colorOrNull("colors.shadowError") ?: derived.shadowError,
    )
}

internal fun DemoThemeTokenConfig.resolveSpacing(): PaletteSpacing =
    PaletteSpacing(
        none = dpOrDefault("spacing.none", 0f).dp,
        extraSmall = dpOrDefault("spacing.extraSmall", 4f).dp,
        small = dpOrDefault("spacing.small", 8f).dp,
        medium = dpOrDefault("spacing.medium", 16f).dp,
        large = dpOrDefault("spacing.large", 24f).dp,
        extraLarge = dpOrDefault("spacing.extraLarge", 32f).dp,
    )

internal fun DemoThemeTokenConfig.resolveShapes(): PaletteShapes =
    PaletteShapes(
        small = RoundedCornerShape(dpOrDefault("shapes.small.radius", 4f).dp),
        medium = RoundedCornerShape(dpOrDefault("shapes.medium.radius", 8f).dp),
        large = RoundedCornerShape(dpOrDefault("shapes.large.radius", 12f).dp),
    )

internal fun DemoThemeTokenConfig.resolveTypography(): PaletteTypography =
    PaletteTypography(
        title = PaletteTypography.default().title.copy(
            fontSize = spOrDefault("typography.title.fontSize", 18f).sp,
            lineHeight = spOrDefault("typography.title.lineHeight", 26f).sp,
            fontWeight = FontWeight.Medium,
        ),
        body = PaletteTypography.default().body.copy(
            fontSize = spOrDefault("typography.body.fontSize", 14f).sp,
            lineHeight = spOrDefault("typography.body.lineHeight", 20f).sp,
        ),
        label = PaletteTypography.default().label.copy(
            fontSize = spOrDefault("typography.label.fontSize", 12f).sp,
            lineHeight = spOrDefault("typography.label.lineHeight", 16f).sp,
            fontWeight = FontWeight.Medium,
        ),
    )

internal fun DemoThemeTokenConfig.resolveOpacity(): PaletteOpacity =
    PaletteOpacity(
        disabled = floatOrDefault("opacity.disabled", 0.5f),
        disabledStrong = floatOrDefault("opacity.disabledStrong", 0.7f),
        subtle = floatOrDefault("opacity.subtle", 0.6f),
        muted = floatOrDefault("opacity.muted", 0.45f),
        hover = floatOrDefault("opacity.hover", 0.08f),
        pressed = floatOrDefault("opacity.pressed", 0.12f),
        selected = floatOrDefault("opacity.selected", 0.12f),
        focusRing = floatOrDefault("opacity.focusRing", 0.2f),
        overlay = floatOrDefault("opacity.overlay", 0.45f),
        elevatedSurface = floatOrDefault("opacity.elevatedSurface", 0.95f),
        borderSubtle = floatOrDefault("opacity.borderSubtle", 0.5f),
    )

internal fun DemoThemeTokenConfig.resolveMotion(): PaletteMotion =
    PaletteMotion(
        durationFast = intOrDefault("motion.durationFast", 150),
        durationNormal = intOrDefault("motion.durationNormal", 250),
        durationSlow = intOrDefault("motion.durationSlow", 350),
        overlayEnter = intOrDefault("motion.overlayEnter", 180),
        overlayExit = intOrDefault("motion.overlayExit", 180),
    )

internal fun DemoThemeTokenConfig.resolveElevation(): PaletteElevation =
    PaletteElevation(
        none = dpOrDefault("elevation.none", 0f).dp,
        raised = dpOrDefault("elevation.raised", 1f).dp,
        overlay = dpOrDefault("elevation.overlay", 4f).dp,
        modal = dpOrDefault("elevation.modal", 8f).dp,
        floating = dpOrDefault("elevation.floating", 12f).dp,
        focusShadowBlur = dpOrDefault("elevation.focusShadowBlur", 4f).dp,
    )

internal fun DemoThemeTokenConfig.resolveControl(): PaletteControlTokens =
    PaletteControlTokens(
        small = controlSize("control.small", 24f, 14f, 16f, 12f, 4f, 4f),
        medium = controlSize("control.medium", 40f, 16f, 20f, 16f, 8f, 6f),
        large = controlSize("control.large", 40f, 18f, 24f, 20f, 12f, 8f),
        borderWidth = dpOrDefault("control.borderWidth", 1f).dp,
        focusBorderWidth = dpOrDefault("control.focusBorderWidth", 2f).dp,
        disabledBorderWidth = dpOrDefault("control.disabledBorderWidth", 1f).dp,
        focusRingOffset = dpOrDefault("control.focusRingOffset", 2f).dp,
    )

private fun DemoThemeTokenConfig.controlSize(
    prefix: String,
    height: Float,
    fontSize: Float,
    iconSize: Float,
    horizontalPadding: Float,
    verticalPadding: Float,
    cornerRadius: Float,
): PaletteControlSizeTokens =
    PaletteControlSizeTokens(
        height = dpOrDefault("$prefix.height", height).dp,
        fontSize = spOrDefault("$prefix.fontSize", fontSize).sp,
        iconSize = dpOrDefault("$prefix.iconSize", iconSize).dp,
        horizontalPadding = dpOrDefault("$prefix.horizontalPadding", horizontalPadding).dp,
        verticalPadding = dpOrDefault("$prefix.verticalPadding", verticalPadding).dp,
        cornerRadius = dpOrDefault("$prefix.cornerRadius", cornerRadius).dp,
    )

internal fun Color.toTokenHex(): String {
    val alpha = alpha.toByteInt()
    val red = red.toByteInt()
    val green = green.toByteInt()
    val blue = blue.toByteInt()
    return if (alpha == 255) {
        "#${red.hexByte()}${green.hexByte()}${blue.hexByte()}"
    } else {
        "#${alpha.hexByte()}${red.hexByte()}${green.hexByte()}${blue.hexByte()}"
    }
}

internal fun parseTokenColor(value: String): Color? {
    val cleaned = value.trim().removePrefix("#")
    if (cleaned.length != 6 && cleaned.length != 8) return null
    val argb = cleaned.toLongOrNull(radix = 16) ?: return null
    return if (cleaned.length == 6) {
        Color(0xFF000000 or argb)
    } else {
        Color(argb)
    }
}

private fun Float.toByteInt(): Int = (this * 255f).toInt().coerceIn(0, 255)

private fun Int.hexByte(): String = toString(16).padStart(2, '0').uppercase()

internal fun Dp.toTokenFloat(): Float = value
