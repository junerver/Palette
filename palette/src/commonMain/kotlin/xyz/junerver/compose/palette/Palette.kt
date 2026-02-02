@file:Suppress("unused")

package xyz.junerver.compose.palette

import xyz.junerver.compose.palette.core.tokens.PaletteColors as PaletteColorsImpl
import xyz.junerver.compose.palette.core.tokens.PaletteSemanticColors as PaletteSemanticColorsImpl
import xyz.junerver.compose.palette.core.tokens.PaletteShapes as PaletteShapesImpl
import xyz.junerver.compose.palette.core.tokens.PaletteSpacing as PaletteSpacingImpl
import xyz.junerver.compose.palette.core.tokens.PaletteTypography as PaletteTypographyImpl
import xyz.junerver.compose.palette.core.theme.LocalPaletteColors as LocalPaletteColorsImpl
import xyz.junerver.compose.palette.core.theme.LocalPaletteShapes as LocalPaletteShapesImpl
import xyz.junerver.compose.palette.core.theme.LocalPaletteSpacing as LocalPaletteSpacingImpl
import xyz.junerver.compose.palette.core.theme.LocalPaletteTypography as LocalPaletteTypographyImpl
import xyz.junerver.compose.palette.core.theme.LocalPaletteDarkTheme as LocalPaletteDarkThemeImpl
import xyz.junerver.compose.palette.core.theme.PaletteTheme as PaletteThemeImpl
import xyz.junerver.compose.palette.core.theme.PaletteMaterialTheme as PaletteMaterialThemeImpl

import xyz.junerver.compose.palette.core.spec.ComponentInteraction as ComponentInteractionImpl
import xyz.junerver.compose.palette.core.spec.ComponentSize as ComponentSizeImpl
import xyz.junerver.compose.palette.core.spec.ComponentState as ComponentStateImpl
import xyz.junerver.compose.palette.core.spec.ComponentStatus as ComponentStatusImpl
import xyz.junerver.compose.palette.core.spec.rememberComponentInteraction as rememberComponentInteractionImpl

import xyz.junerver.compose.palette.core.util.noRippleClickable as noRippleClickableImpl
import xyz.junerver.compose.palette.core.util.PaletteDefaults as PaletteDefaultsImpl

import xyz.junerver.compose.palette.foundation.border.BorderContainer as BorderContainerImpl
import xyz.junerver.compose.palette.foundation.border.BorderContainerDefaults as BorderContainerDefaultsImpl

import xyz.junerver.compose.palette.foundation.layout.CenterVerticallyRow as CenterVerticallyRowImpl

import xyz.junerver.compose.palette.components.badge.BadgeDefaults as BadgeDefaultsImpl
import xyz.junerver.compose.palette.components.badge.PBadge as PBadgeImpl

import xyz.junerver.compose.palette.components.checkbox.CheckboxDefaults as CheckboxDefaultsImpl
import xyz.junerver.compose.palette.components.checkbox.ColoredCheckBox as ColoredCheckBoxImpl

import xyz.junerver.compose.palette.components.textfield.BorderTextField as BorderTextFieldImpl
import xyz.junerver.compose.palette.components.textfield.BorderTextFieldColors as BorderTextFieldColorsImpl
import xyz.junerver.compose.palette.components.textfield.TextFieldDefaults as TextFieldDefaultsImpl

import xyz.junerver.compose.palette.components.toolbar.Toolbar as ToolbarImpl
import xyz.junerver.compose.palette.components.toolbar.ToolbarColors as ToolbarColorsImpl
import xyz.junerver.compose.palette.components.toolbar.ToolbarDefaults as ToolbarDefaultsImpl

import xyz.junerver.compose.palette.components.screen.LocalPlatformActivity as LocalPlatformActivityImpl
import xyz.junerver.compose.palette.components.screen.PlatformActivity as PlatformActivityImpl
import xyz.junerver.compose.palette.components.screen.Screen as ScreenImpl
import xyz.junerver.compose.palette.components.screen.ScreenColors as ScreenColorsImpl
import xyz.junerver.compose.palette.components.screen.ScreenDefaults as ScreenDefaultsImpl

import xyz.junerver.compose.palette.components.loading.LoadingDefaults as LoadingDefaultsImpl
import xyz.junerver.compose.palette.components.progress.ProgressDefaults as ProgressDefaultsImpl
import xyz.junerver.compose.palette.components.button.ButtonType as ButtonTypeImpl
import xyz.junerver.compose.palette.components.button.ButtonSize as ButtonSizeImpl
import xyz.junerver.compose.palette.components.button.ButtonDefaults as ButtonDefaultsImpl
import xyz.junerver.compose.palette.components.radio.RadioDefaults as RadioDefaultsImpl
import xyz.junerver.compose.palette.components.switch.SwitchDefaults as SwitchDefaultsImpl
import xyz.junerver.compose.palette.components.slider.SliderDefaults as SliderDefaultsImpl
import xyz.junerver.compose.palette.components.rate.RateDefaults as RateDefaultsImpl
import xyz.junerver.compose.palette.components.dialog.DialogDefaults as DialogDefaultsImpl
import xyz.junerver.compose.palette.components.dialog.DialogState as DialogStateImpl
import xyz.junerver.compose.palette.components.toast.ToastDefaults as ToastDefaultsImpl
import xyz.junerver.compose.palette.components.toast.ToastIcon as ToastIconImpl
import xyz.junerver.compose.palette.components.toast.ToastState as ToastStateImpl
import xyz.junerver.compose.palette.components.skeleton.SkeletonDefaults as SkeletonDefaultsImpl

// Core - Tokens
typealias PaletteColors = PaletteColorsImpl
typealias PaletteSemanticColors = PaletteSemanticColorsImpl
typealias PaletteShapes = PaletteShapesImpl
typealias PaletteSpacing = PaletteSpacingImpl
typealias PaletteTypography = PaletteTypographyImpl
// Core - Theme
val LocalPaletteColors = LocalPaletteColorsImpl
val LocalPaletteShapes = LocalPaletteShapesImpl
val LocalPaletteSpacing = LocalPaletteSpacingImpl
val LocalPaletteTypography = LocalPaletteTypographyImpl
val LocalPaletteDarkTheme = LocalPaletteDarkThemeImpl
typealias PaletteTheme = PaletteThemeImpl
typealias PaletteMaterialTheme = PaletteMaterialThemeImpl

// Core - Spec
typealias ComponentInteraction = ComponentInteractionImpl
typealias ComponentSize = ComponentSizeImpl
typealias ComponentState = ComponentStateImpl
typealias ComponentStatus = ComponentStatusImpl
val rememberComponentInteraction = ::rememberComponentInteractionImpl

// Core - Util
val PaletteDefaults = PaletteDefaultsImpl

// Foundation - Border
typealias BorderContainerDefaults = BorderContainerDefaultsImpl

// Components - Badge
typealias BadgeDefaults = BadgeDefaultsImpl

// Components - Checkbox
typealias CheckboxDefaults = CheckboxDefaultsImpl

// Components - TextField
typealias BorderTextFieldColors = BorderTextFieldColorsImpl
typealias TextFieldDefaults = TextFieldDefaultsImpl

// Components - Toolbar
typealias ToolbarColors = ToolbarColorsImpl
typealias ToolbarDefaults = ToolbarDefaultsImpl

// Components - Screen
val LocalPlatformActivity = LocalPlatformActivityImpl
typealias PlatformActivity = PlatformActivityImpl
typealias ScreenColors = ScreenColorsImpl
typealias ScreenDefaults = ScreenDefaultsImpl

// Components - Loading
val LoadingDefaults = LoadingDefaultsImpl

// Components - Progress
val ProgressDefaults = ProgressDefaultsImpl

// Components - Button
typealias ButtonType = ButtonTypeImpl
typealias ButtonSize = ButtonSizeImpl
val ButtonDefaults = ButtonDefaultsImpl

// Components - Radio
val RadioDefaults = RadioDefaultsImpl

// Components - Switch
val SwitchDefaults = SwitchDefaultsImpl

// Components - Slider
val SliderDefaults = SliderDefaultsImpl

// Components - Rate
val RateDefaults = RateDefaultsImpl

// Components - Dialog
val DialogDefaults = DialogDefaultsImpl
typealias DialogState = DialogStateImpl

// Components - Toast
val ToastDefaults = ToastDefaultsImpl
typealias ToastIcon = ToastIconImpl
typealias ToastState = ToastStateImpl

// Components - Skeleton
val SkeletonDefaults = SkeletonDefaultsImpl

