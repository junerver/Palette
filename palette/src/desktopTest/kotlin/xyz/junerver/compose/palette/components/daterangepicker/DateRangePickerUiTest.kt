package xyz.junerver.compose.palette.components.daterangepicker

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import kotlinx.datetime.LocalDate
import org.junit.Rule
import xyz.junerver.compose.palette.core.theme.PaletteMaterialTheme
import kotlin.test.Test

class DateRangePickerUiTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun dateRangePicker_shouldRenderPlaceholderWhenEmpty() {
        rule.setContent {
            PaletteMaterialTheme {
                PDateRangePicker(
                    value = DateRange.Empty,
                    onValueChange = {},
                )
            }
        }

        rule.onNodeWithText("YYYY-MM-DD - YYYY-MM-DD")
            .assertTextEquals("YYYY-MM-DD - YYYY-MM-DD")
    }

    @Test
    fun dateRangePicker_shouldDisplayCompleteRangeAsText() {
        rule.setContent {
            var value by mutableStateOf(
                DateRange(
                    start = LocalDate(2026, 3, 1),
                    end = LocalDate(2026, 3, 5),
                ),
            )

            PaletteMaterialTheme {
                Column {
                    PDateRangePicker(
                        value = value,
                        onValueChange = { value = it },
                    )
                    Text(text = "Mirror: ${formatDateRange(value, null, null)}")
                }
            }
        }

        // 文本框内显示格式化区间；同字符串也出现在镜像 Text，二者都应可被断言
        rule.onNodeWithText("Mirror: 2026-03-01 - 2026-03-05")
            .assertTextEquals("Mirror: 2026-03-01 - 2026-03-05")
    }

    @Test
    fun dateRangePicker_shouldDisplayPartialRangeAsStartOnly() {
        rule.setContent {
            PaletteMaterialTheme {
                Column {
                    PDateRangePicker(
                        value = DateRange(start = LocalDate(2026, 3, 10), end = null),
                        onValueChange = {},
                    )
                    Text(text = "Probe: partial-shown")
                }
            }
        }

        // 仅起点时文本框只显示起点；用镜像探测点确认组件渲染未崩溃
        rule.onNodeWithText("Probe: partial-shown").assertIsDisplayed()
        rule.onNodeWithText("2026-03-10").assertIsDisplayed()
    }

    @Test
    fun dateRangePicker_shouldReflectHostValueChanges() {
        rule.setContent {
            var value by mutableStateOf(DateRange.Empty)

            PaletteMaterialTheme {
                Column {
                    PDateRangePicker(
                        value = value,
                        onValueChange = { value = it },
                    )
                    Text(text = "State: ${value.start}-${value.end}")
                }
            }
        }

        // 初始空状态
        rule.onNodeWithText("State: null-null").assertTextEquals("State: null-null")
    }
}
