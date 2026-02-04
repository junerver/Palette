package xyz.junerver.compose.palette.core.tokens

import androidx.compose.ui.graphics.Color

val PaletteColors.focusBorder: Color
    get() = primary.copy(alpha = 0.6f)

val PaletteColors.hoverBorder: Color
    get() = primary.copy(alpha = 0.3f)

val PaletteColors.disabledBorder: Color
    get() = border.copy(alpha = 0.5f)

val PaletteColors.disabledBackground: Color
    get() = surface.copy(alpha = 0.05f)

val PaletteColors.errorBorder: Color
    get() = error

val PaletteColors.warningBorder: Color
    get() = warning

val PaletteColors.successBorder: Color
    get() = success

val PaletteColors.focusShadow: Color
    get() = primary.copy(alpha = 0.2f)

val PaletteColors.errorShadow: Color
    get() = error.copy(alpha = 0.2f)
