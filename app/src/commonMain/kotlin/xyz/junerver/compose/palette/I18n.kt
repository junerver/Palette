package xyz.junerver.compose.palette

import androidx.compose.runtime.staticCompositionLocalOf
import xyz.junerver.compose.palette.core.i18n.PaletteStrings

internal enum class Language {
    ZH_CN,
    EN_US,
}

internal val LocalLanguage = staticCompositionLocalOf { Language.ZH_CN }

internal fun Language.toPaletteStrings(): PaletteStrings = when (this) {
    Language.ZH_CN -> PaletteStrings.zhCN()
    Language.EN_US -> PaletteStrings.enUS()
}
