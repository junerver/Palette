package xyz.junerver.compose.palette.core.i18n

import androidx.compose.runtime.Immutable

@Immutable
data class PaletteStrings(
    val commonExpand: String,
    val commonCollapse: String,
    val paginationEllipsis: String,
    val tablePaginationSummary: (Int, Int) -> String,
    val selectNoResult: String,
    val datePickerPlaceholder: String,
    val timePickerPlaceholder: String,
    val uploadTriggerText: String,
    val uploadReadySuffix: String,
    val uploadRejectedTypeSuffix: String,
    val uploadRejectedSizeSuffix: String,
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
            selectNoResult = "无匹配项",
            datePickerPlaceholder = "YYYY-MM-DD",
            timePickerPlaceholder = "HH:mm",
            uploadTriggerText = "选择文件",
            uploadReadySuffix = "可用",
            uploadRejectedTypeSuffix = "类型不支持",
            uploadRejectedSizeSuffix = "大小超限",
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
            selectNoResult = "No matching options",
            datePickerPlaceholder = "YYYY-MM-DD",
            timePickerPlaceholder = "HH:mm",
            uploadTriggerText = "Select files",
            uploadReadySuffix = "ready",
            uploadRejectedTypeSuffix = "type rejected",
            uploadRejectedSizeSuffix = "size rejected",
            emptyDefaultTitle = "No Data",
            emptyDefaultDescription = "The current list is empty",
            toastSuccessContentDescription = "Success",
            toastFailContentDescription = "Failed",
            toastLoadingContentDescription = "Loading",
        )
    }
}
