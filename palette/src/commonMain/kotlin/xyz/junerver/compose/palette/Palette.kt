@file:Suppress("unused")

package xyz.junerver.compose.palette

// Core - Tokens
import xyz.junerver.compose.palette.core.tokens.PaletteColors as PaletteColorsImpl
import xyz.junerver.compose.palette.core.tokens.PaletteShapes as PaletteShapesImpl
import xyz.junerver.compose.palette.core.tokens.PaletteSpacing as PaletteSpacingImpl

// Core - Theme
import xyz.junerver.compose.palette.core.theme.LocalPaletteColors as LocalPaletteColorsImpl
import xyz.junerver.compose.palette.core.theme.LocalPaletteShapes as LocalPaletteShapesImpl
import xyz.junerver.compose.palette.core.theme.LocalPaletteSpacing as LocalPaletteSpacingImpl
import xyz.junerver.compose.palette.core.theme.PaletteTheme as PaletteThemeImpl

// Core - Util
import xyz.junerver.compose.palette.core.util.noRippleClickable as noRippleClickableImpl

// Foundation - Border
import xyz.junerver.compose.palette.foundation.border.BorderContainer as BorderContainerImpl
import xyz.junerver.compose.palette.foundation.border.BorderContainerDefaults as BorderContainerDefaultsImpl

// Foundation - Layout
import xyz.junerver.compose.palette.foundation.layout.CenterVerticallyRow as CenterVerticallyRowImpl

// Components - Badge
import xyz.junerver.compose.palette.components.badge.BadgeDefaults as BadgeDefaultsImpl
import xyz.junerver.compose.palette.components.badge.PBadge as PBadgeImpl

// Components - Checkbox
import xyz.junerver.compose.palette.components.checkbox.CheckboxDefaults as CheckboxDefaultsImpl
import xyz.junerver.compose.palette.components.checkbox.ColoredCheckBox as ColoredCheckBoxImpl

// Components - TextField
import xyz.junerver.compose.palette.components.textfield.BorderTextField as BorderTextFieldImpl
import xyz.junerver.compose.palette.components.textfield.BorderTextFieldColors as BorderTextFieldColorsImpl
import xyz.junerver.compose.palette.components.textfield.TextFieldDefaults as TextFieldDefaultsImpl

// Components - Toolbar
import xyz.junerver.compose.palette.components.toolbar.Toolbar as ToolbarImpl
import xyz.junerver.compose.palette.components.toolbar.ToolbarColors as ToolbarColorsImpl
import xyz.junerver.compose.palette.components.toolbar.ToolbarDefaults as ToolbarDefaultsImpl

// Components - Screen
import xyz.junerver.compose.palette.components.screen.LocalPlatformActivity as LocalPlatformActivityImpl
import xyz.junerver.compose.palette.components.screen.PlatformActivity as PlatformActivityImpl
import xyz.junerver.compose.palette.components.screen.Screen as ScreenImpl
import xyz.junerver.compose.palette.components.screen.ScreenColors as ScreenColorsImpl
import xyz.junerver.compose.palette.components.screen.ScreenDefaults as ScreenDefaultsImpl

// Re-exports for backward compatibility and convenience

// Core - Tokens
typealias PaletteColors = PaletteColorsImpl
typealias PaletteShapes = PaletteShapesImpl
typealias PaletteSpacing = PaletteSpacingImpl

// Core - Theme
val LocalPaletteColors = LocalPaletteColorsImpl
val LocalPaletteShapes = LocalPaletteShapesImpl
val LocalPaletteSpacing = LocalPaletteSpacingImpl
typealias PaletteTheme = PaletteThemeImpl

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
