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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn
import xyz.junerver.compose.hooks.useState
import xyz.junerver.compose.palette.Language
import xyz.junerver.compose.palette.LocalLanguage
import xyz.junerver.compose.palette.components.CodeBlock
import xyz.junerver.compose.palette.components.calendar.PCalendar
import xyz.junerver.compose.palette.components.text.PText

@Suppress("NOTHING_TO_INLINE")
private inline fun todayDate(): LocalDate = LocalDate(2026, 6, 16)

@Composable
fun CalendarDemo() {
    val text = calendarDemoText()
    val today = todayDate()
    val selectedDateState = androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf<LocalDate?>(null) }
    val selectedDate = selectedDateState.value
    val rangeSelectedDateState = androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf<LocalDate?>(null) }
    val rangeSelectedDate = rangeSelectedDateState.value

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
    ) {
        PText(
            text = text.title,
            style = MaterialTheme.typography.headlineMedium,
        )
        PText(
            text = text.subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(modifier = Modifier.height(32.dp))

        DemoSection(title = text.basicSectionTitle) {
            PCalendar(
                selectedDate = null,
                onDateSelect = {},
                today = today,
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = text.selectedSectionTitle) {
            Column {
                PCalendar(
                    selectedDate = selectedDate,
                    onDateSelect = { selectedDateState.value = it },
                    today = today,
                )
                if (selectedDate != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    PText(
                        text = "${text.selectedLabel}$selectedDate",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = text.rangeSectionTitle) {
            Column {
                PCalendar(
                    selectedDate = rangeSelectedDate,
                    onDateSelect = { rangeSelectedDateState.value = it },
                    today = today,
                    minDate = today.minus(7, DateTimeUnit.DAY),
                    maxDate = today.plus(7, DateTimeUnit.DAY),
                )
                Spacer(modifier = Modifier.height(8.dp))
                PText(
                    text = "${text.rangeLabel}${today.minus(7, DateTimeUnit.DAY)} ~ ${today.plus(7, DateTimeUnit.DAY)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        PText(
            text = text.codeTitle,
            style = MaterialTheme.typography.titleMedium,
        )

        Spacer(modifier = Modifier.height(16.dp))

        CodeBlock(
            code = text.codeBlock,
        )
    }
}

@Composable
@ReadOnlyComposable
private fun calendarDemoText(): CalendarDemoText =
    when (LocalLanguage.current) {
        Language.ZH_CN ->
            CalendarDemoText(
                title = "Calendar",
                subtitle = "日历组件",
                basicSectionTitle = "基础用法",
                selectedSectionTitle = "选择日期",
                rangeSectionTitle = "日期范围限制",
                selectedLabel = "已选择：",
                rangeLabel = "可选范围：",
                codeTitle = "代码示例",
                codeBlock =
                    """
                    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
                    val (selectedDate, setSelectedDate) = useState<LocalDate?>(null)

                    PCalendar(
                        selectedDate = selectedDate,
                        onDateSelect = setSelectedDate,
                        today = today,
                        minDate = today.minus(7, DateTimeUnit.DAY),
                        maxDate = today.plus(7, DateTimeUnit.DAY)
                    )
                    """.trimIndent(),
            )

        Language.EN_US ->
            CalendarDemoText(
                title = "Calendar",
                subtitle = "Calendar component",
                basicSectionTitle = "Basic Usage",
                selectedSectionTitle = "Select Date",
                rangeSectionTitle = "Date Range",
                selectedLabel = "Selected: ",
                rangeLabel = "Range: ",
                codeTitle = "Code Example",
                codeBlock =
                    """
                    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
                    val (selectedDate, setSelectedDate) = useState<LocalDate?>(null)

                    PCalendar(
                        selectedDate = selectedDate,
                        onDateSelect = setSelectedDate,
                        today = today,
                        minDate = today.minus(7, DateTimeUnit.DAY),
                        maxDate = today.plus(7, DateTimeUnit.DAY)
                    )
                    """.trimIndent(),
            )
    }

private data class CalendarDemoText(
    val title: String,
    val subtitle: String,
    val basicSectionTitle: String,
    val selectedSectionTitle: String,
    val rangeSectionTitle: String,
    val selectedLabel: String,
    val rangeLabel: String,
    val codeTitle: String,
    val codeBlock: String,
)
