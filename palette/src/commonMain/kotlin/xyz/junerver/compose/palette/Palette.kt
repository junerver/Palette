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

