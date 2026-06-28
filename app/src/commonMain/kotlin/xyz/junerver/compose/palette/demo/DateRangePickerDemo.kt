package xyz.junerver.compose.palette.demo

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import xyz.junerver.compose.palette.DateRange
import xyz.junerver.compose.palette.DateRangePickerDefaults
import xyz.junerver.compose.palette.Language
import xyz.junerver.compose.palette.LocalLanguage
import xyz.junerver.compose.palette.PDateRangePicker
import xyz.junerver.compose.palette.components.CodeBlock
import xyz.junerver.compose.palette.components.text.PText

@Composable
fun DateRangePickerDemo() {
    val text = dateRangePickerDemoText()
    val today = remember { LocalDate(2026, 6, 16) }

    // 各示例独立状态，避免互相干扰
    var basicValue by remember { mutableStateOf(DateRange.Empty) }
    var timeValue by remember { mutableStateOf(DateRange.Empty) }
    var presetValue by remember { mutableStateOf(DateRange.Empty) }
    var limitedValue by remember { mutableStateOf(DateRange.Empty) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
    ) {
        PText(text = text.title, style = MaterialTheme.typography.headlineMedium)
        PText(
            text = text.subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(modifier = Modifier.height(32.dp))

        // 基础用法：纯日期区间
        DemoSection(title = text.basicSectionTitle) {
            Column {
                PDateRangePicker(
                    value = basicValue,
                    onValueChange = { basicValue = it },
                )
                ValueMirror(range = basicValue, label = text.selectedLabel)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 时间区间：showTime=true
        DemoSection(title = text.timeSectionTitle) {
            Column {
                PDateRangePicker(
                    value = timeValue,
                    onValueChange = { timeValue = it },
                    showTime = true,
                    startTime = LocalTime(9, 0),
                    endTime = LocalTime(18, 0),
                )
                ValueMirror(range = timeValue, label = text.selectedLabel)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 自定义快捷项
        DemoSection(title = text.presetSectionTitle) {
            Column {
                PDateRangePicker(
                    value = presetValue,
                    onValueChange = { presetValue = it },
                    presets = DateRangePickerDefaults.presets(today),
                )
                ValueMirror(range = presetValue, label = text.selectedLabel)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 边界与禁用：minDate/maxDate + disabledDate + maxSpanDays
        DemoSection(title = text.limitedSectionTitle) {
            Column {
                PDateRangePicker(
                    value = limitedValue,
                    onValueChange = { limitedValue = it },
                    minDate = today.minus(30, DateTimeUnit.DAY),
                    maxDate = today.plus(30, DateTimeUnit.DAY),
                    maxSpanDays = 14,
                )
                ValueMirror(range = limitedValue, label = text.selectedLabel)
                PText(
                    text = text.limitedHint,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        PText(text = text.codeTitle, style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(16.dp))
        CodeBlock(code = text.codeBlock)
    }
}

@Composable
private fun ValueMirror(range: DateRange, label: String) {
    val display = when {
        range.isEmpty -> "$label∅"
        range.isPartial -> "$label${range.start} → ?"
        else -> "$label${range.start} → ${range.end}"
    }
    Spacer(modifier = Modifier.height(8.dp))
    PText(
        text = display,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.primary,
    )
}

// （日期加减直接使用 kotlinx-datetime 的 minus/plus 扩展）

@Composable
@ReadOnlyComposable
private fun dateRangePickerDemoText(): DateRangePickerDemoText =
    when (LocalLanguage.current) {
        Language.ZH_CN -> DateRangePickerDemoText(
            title = "DateRangePicker",
            subtitle = "日期 / 时间区间选择器（双面板 · pill-cap 高亮 · 快捷项）",
            basicSectionTitle = "基础用法",
            timeSectionTitle = "时间区间",
            presetSectionTitle = "快捷项",
            limitedSectionTitle = "边界与跨度限制",
            limitedHint = "仅可选最近 30 天，且区间跨度不超过 14 天",
            selectedLabel = "当前值：",
            codeTitle = "代码示例",
            codeBlock = """
                var value by remember { mutableStateOf(DateRange.Empty) }

                PDateRangePicker(
                    value = value,
                    onValueChange = { value = it },
                    showTime = true,
                    presets = DateRangePickerDefaults.presets(),
                    maxSpanDays = 14,
                )
            """.trimIndent(),
        )

        Language.EN_US -> DateRangePickerDemoText(
            title = "DateRangePicker",
            subtitle = "Date / datetime range picker (dual panel · pill-cap · presets)",
            basicSectionTitle = "Basic Usage",
            timeSectionTitle = "With Time",
            presetSectionTitle = "Presets",
            limitedSectionTitle = "Bounds & Max Span",
            limitedHint = "Selectable within 30 days, span no more than 14 days",
            selectedLabel = "Value: ",
            codeTitle = "Code Example",
            codeBlock = """
                var value by remember { mutableStateOf(DateRange.Empty) }

                PDateRangePicker(
                    value = value,
                    onValueChange = { value = it },
                    showTime = true,
                    presets = DateRangePickerDefaults.presets(),
                    maxSpanDays = 14,
                )
            """.trimIndent(),
        )
    }

private data class DateRangePickerDemoText(
    val title: String,
    val subtitle: String,
    val basicSectionTitle: String,
    val timeSectionTitle: String,
    val presetSectionTitle: String,
    val limitedSectionTitle: String,
    val limitedHint: String,
    val selectedLabel: String,
    val codeTitle: String,
    val codeBlock: String,
)
