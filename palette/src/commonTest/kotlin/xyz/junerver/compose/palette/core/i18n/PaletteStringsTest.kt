package xyz.junerver.compose.palette.core.i18n

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class PaletteStringsTest {
    @Test
    fun zhAndEnFactories_shouldExposeDifferentUserFacingCopy() {
        val zh = PaletteStrings.zhCN()
        val en = PaletteStrings.enUS()

        assertEquals("展开", zh.commonExpand)
        assertEquals("Expand", en.commonExpand)
        assertNotEquals(zh.emptyDefaultTitle, en.emptyDefaultTitle)
    }

    @Test
    fun paginationSummary_shouldMatchLocalePattern() {
        val zh = PaletteStrings.zhCN()
        val en = PaletteStrings.enUS()

        assertEquals("第 2 / 9 页", zh.tablePaginationSummary(2, 9))
        assertEquals("Page 2 of 9", en.tablePaginationSummary(2, 9))
    }
}
