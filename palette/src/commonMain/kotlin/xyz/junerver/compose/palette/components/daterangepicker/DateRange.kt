package xyz.junerver.compose.palette.components.daterangepicker

import androidx.compose.runtime.Immutable
import kotlinx.datetime.LocalDate

/**
 * 日期区间值模型。
 *
 * - [isEmpty] 表示未选择任何端点（仅作占位展示）。
 * - [isPartial] 表示已选起点但终点待选（交互中间态，合法）。
 * - [isComplete] 表示起点与终点都已确定（最终值）。
 *
 * 采用可空双端而非 Pair/非空类型，是为了支持前端主流区间选择器
 * "点击起点 → hover 预览 → 点击终点" 的两段式交互，期间允许仅起点有效。
 */
@Immutable
data class DateRange(
    val start: LocalDate?,
    val end: LocalDate?,
) {
    val isComplete: Boolean get() = start != null && end != null
    val isPartial: Boolean get() = start != null && end == null
    val isEmpty: Boolean get() = start == null

    companion object {
        val Empty = DateRange(start = null, end = null)
    }
}

/**
 * 区间快捷项。参考 Element Plus / Ant Design 的 presets：
 * [value] 为工厂函数，保证 "最近 7 天" 等相对区间在点击瞬间即时计算，
 * 而非绑定到组件首次渲染时刻。
 */
@Immutable
data class DateRangePreset(
    val label: String,
    val value: () -> DateRange,
)
