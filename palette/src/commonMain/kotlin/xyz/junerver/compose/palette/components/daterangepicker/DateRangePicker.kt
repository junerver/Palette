package xyz.junerver.compose.palette.components.daterangepicker

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import xyz.junerver.compose.hooks.useState
import xyz.junerver.compose.palette.components.popover.PPopover
import xyz.junerver.compose.palette.components.textfield.BorderTextField
import xyz.junerver.compose.palette.core.spec.ComponentSize
import xyz.junerver.compose.palette.core.spec.ComponentStatus
import xyz.junerver.compose.palette.core.theme.PaletteTheme

/**
 * 日期（时间）区间选择器。
 *
 * 参考 Ant Design / Element Plus / Naive UI 共识设计：
 * - 双月份面板 + pill-cap 范围高亮 + hover 预览
 * - 点击起点 → hover 实时预览 → 点击终点；[allowSwap] 控制是否自动纠错 end<start
 * - [showTime] 为正交布尔：开启后在面板 footer 出现双时间滚轮
 * - [disabledDate] 为业界标准禁用回调，与 [minDate]/[maxDate] 取并集
 * - [maxSpanDays] 限制最大跨度：hover 预览被钳制，超限终点不可提交
 * - [presets] 快捷项，工厂函数保证相对区间在点击瞬间计算
 *
 * @param value 当前区间（受控）。允许 [DateRange.isPartial] 中间态。
 * @param onValueChange 区间变更回调。
 * @param startTime 起点时间，仅 [showTime]=true 时使用；为空则默认 00:00。
 * @param endTime 终点时间，仅 [showTime]=true 时使用；为空则默认 23:59。
 */
@Composable
fun PDateRangePicker(
    value: DateRange,
    onValueChange: (DateRange) -> Unit,
    modifier: Modifier = Modifier,
    showTime: Boolean = false,
    startTime: LocalTime? = null,
    endTime: LocalTime? = null,
    minDate: LocalDate? = null,
    maxDate: LocalDate? = null,
    disabledDate: ((LocalDate) -> Boolean)? = null,
    maxSpanDays: Int? = null,
    presets: List<DateRangePreset>? = null,
    allowSwap: Boolean = true,
    minuteStep: Int = DateRangePickerDefaults.DefaultMinuteStep,
    placeholder: String = PaletteTheme.strings.dateRangePickerPlaceholder,
    size: ComponentSize = ComponentSize.Medium,
    enabled: Boolean = true,
) {
    val effectivePresets = presets ?: DateRangePickerDefaults.presets()
    val today = DateRangePickerDefaults.defaultToday()

    // 面板内交互态（局部、非受控）
    // 注：hoverDate 用原生 remember+mutableStateOf 而非 compose-hooks useState，
    // 因为它需要可空类型(LocalDate?)初始值 null，compose-hooks 的 useState 对
    // 可空初始值的泛型推断在此场景不稳定；属组件内部交互态，符合 AGENTS.md
    // 原生状态 API 例外。draftRange 用 useState 因其值类型非空、推断稳定。
    val (panelOpen, setPanelOpen) = useState(false)
    val (hoverDate, setHoverDate) = remember { mutableStateOf<LocalDate?>(null) }
    // 内部暂存：起点已选、终点待选的中间态。提交时回写到 value。
    val (draftRange, setDraftRange) = useState(value)

    // 当前生效的展示用时间（受控优先，缺省回退）
    val displayStart = startTime ?: LocalTime(0, 0)
    val displayEnd = endTime ?: LocalTime(23, 59)
    val displayText = formatDateRange(
        value,
        if (showTime) displayStart else null,
        if (showTime) displayEnd else null,
    )

    PPopover(
        visible = panelOpen,
        onVisibleChange = { open ->
            setPanelOpen(open)
            if (open) {
                setDraftRange(value)
                setHoverDate(null)
            }
        },
        modifier = modifier,
        trigger = {
            // 只读文本框 + 日历图标；点击触发面板
            BorderTextField(
                value = displayText,
                onValueChange = {},
                modifier = Modifier,
                enabled = enabled,
                readOnly = true,
                size = size,
                status = ComponentStatus.Default,
                placeholder = placeholder,
                trailingIcon = {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = if (showTime) Icons.Default.CalendarMonth else Icons.Default.DateRange,
                            contentDescription = null,
                            tint = DateRangePickerDefaults.iconColor(),
                        )
                    }
                },
            )
        },
    ) {
        // content slot：双月日历面板 + 可选 footer
        Column(modifier = Modifier.padding(8.dp)) {
            RangeCalendarPanel(
                value = draftRange,
                hoverDate = hoverDate,
                today = today,
                minDate = minDate,
                maxDate = maxDate,
                disabledDate = disabledDate,
                maxSpanDays = maxSpanDays,
                onDateClick = { date ->
                    val current = draftRange
                    val next: DateRange = when {
                        // 已有完整区间或空 → 点击的日期成为新起点
                        current.isComplete || current.isEmpty ->
                            DateRange(start = date, end = null)
                        // 仅起点 → 点击的日期成为终点（校验跨度 + 自动纠错）
                        current.isPartial -> {
                            if (!isHoverClickable(current.start, date, maxSpanDays)) {
                                // 超出最大跨度：重新以该日期为起点
                                DateRange(start = date, end = null)
                            } else {
                                val candidate = DateRange(start = current.start, end = date)
                                normalizeRange(candidate, allowSwap)
                            }
                        }
                        else -> DateRange(start = date, end = null)
                    }
                    setDraftRange(next)
                    setHoverDate(null)
                    onValueChange(next)
                    // 选满即关面板（纯日期模式）；含时间模式留面板让用户调时间
                    if (next.isComplete && !showTime) setPanelOpen(false)
                },
                onDateHover = { setHoverDate(it) },
            )

            if (effectivePresets.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                RangePresetsBar(
                    presets = effectivePresets,
                    onPresetClick = { preset ->
                        val range = preset.value()
                        val clamped = clampRangeToBounds(range, minDate, maxDate)
                        val normalized = normalizeRange(clamped, allowSwap)
                        setDraftRange(normalized)
                        setHoverDate(null)
                        onValueChange(normalized)
                        if (normalized.isComplete && !showTime) setPanelOpen(false)
                    },
                )
            }

            if (showTime) {
                Spacer(modifier = Modifier.height(8.dp))
                RangeTimeFooter(
                    startTime = startTime,
                    endTime = endTime,
                    onStartTimeChange = { /* 时间变更由受控 startTime/endTime 处理；预留回调 */ },
                    onEndTimeChange = { },
                    minuteStep = minuteStep,
                )
            }
        }
    }
}

/**
 * 快捷项栏：流式排列的标签按钮。
 */
@Composable
internal fun RangePresetsBar(
    presets: List<DateRangePreset>,
    onPresetClick: (DateRangePreset) -> Unit,
    modifier: Modifier = Modifier,
) {
    val tokens = PaletteTheme.componentThemes.dateTime
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        presets.forEach { preset ->
            Text(
                text = preset.label,
                color = tokens.calendarHeaderColor,
                style = tokens.calendarDayOfWeekTextStyle,
                modifier = Modifier
                    .clickable { onPresetClick(preset) }
                    .padding(horizontal = 8.dp, vertical = 4.dp),
            )
        }
    }
}
