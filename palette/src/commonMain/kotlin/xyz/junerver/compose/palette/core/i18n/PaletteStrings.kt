package xyz.junerver.compose.palette.core.i18n

import androidx.compose.runtime.Immutable

@Immutable
data class PaletteStrings(
    val commonExpand: String,
    val commonCollapse: String,
    val paginationEllipsis: String,
    val tablePaginationSummary: (Int, Int) -> String,
    val emptyDefaultTitle: String,
    val emptyDefaultDescription: String,
    val toastSuccessContentDescription: String,
    val toastFailContentDescription: String,
    val toastLoadingContentDescription: String,
) {
    companion object {
        fun zhCN(): PaletteStrings = PaletteStrings(
            commonExpand = "展开",
            commonCollapse = "收起",
            paginationEllipsis = "...",
            tablePaginationSummary = { current, total -> "第 $current / $total 页" },
            emptyDefaultTitle = "暂无数据",
            emptyDefaultDescription = "当前列表为空",
            toastSuccessContentDescription = "成功",
            toastFailContentDescription = "失败",
            toastLoadingContentDescription = "加载中",
        )

        fun enUS(): PaletteStrings = PaletteStrings(
            commonExpand = "Expand",
            commonCollapse = "Collapse",
            paginationEllipsis = "...",
            tablePaginationSummary = { current, total -> "Page $current of $total" },
            emptyDefaultTitle = "No Data",
            emptyDefaultDescription = "The current list is empty",
            toastSuccessContentDescription = "Success",
            toastFailContentDescription = "Failed",
            toastLoadingContentDescription = "Loading",
        )
    }
}
