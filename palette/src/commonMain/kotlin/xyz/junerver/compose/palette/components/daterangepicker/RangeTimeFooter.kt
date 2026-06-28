package xyz.junerver.compose.palette.components.daterangepicker

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalTime
import xyz.junerver.compose.palette.core.theme.PaletteTheme

// 滚轮每项高度与可见行数（图形编码常量，决定滚轮视觉密度）
private val WheelItemHeight = 32.dp
private const val WheelVisibleCount = 5

/**
 * 区间时间选择 footer：两个滚轮（起点 / 终点）各含小时与分钟列。
 *
 * 仅在 showTime=true 时由 [PDateRangePicker] 渲染。
 * 滚轮采用"拖拽 + 点击"双模式：拖拽滚动浏览、点击直接选中（移动端友好）。
 */
@Composable
internal fun RangeTimeFooter(
    startTime: LocalTime?,
    endTime: LocalTime?,
    onStartTimeChange: (LocalTime) -> Unit,
    onEndTimeChange: (LocalTime) -> Unit,
    minuteStep: Int,
    modifier: Modifier = Modifier,
) {
    val tokens = PaletteTheme.componentThemes.dateTime
    val dividerColor = PaletteTheme.colors.divider

    Column(modifier = modifier.fillMaxWidth()) {
        HorizontalDivider(color = dividerColor)
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TimeWheelGroup(
                label = "开始",
                time = startTime ?: LocalTime(0, 0),
                onTimeChange = onStartTimeChange,
                minuteStep = minuteStep,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "→",
                color = tokens.calendarHeaderColor,
                style = tokens.calendarHeaderTextStyle,
            )
            Spacer(modifier = Modifier.width(8.dp))
            TimeWheelGroup(
                label = "结束",
                time = endTime ?: LocalTime(23, 59),
                onTimeChange = onEndTimeChange,
                minuteStep = minuteStep,
            )
        }
    }
}

@Composable
private fun TimeWheelGroup(
    label: String,
    time: LocalTime,
    onTimeChange: (LocalTime) -> Unit,
    minuteStep: Int,
) {
    val tokens = PaletteTheme.componentThemes.dateTime
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            color = tokens.calendarDayOfWeekColor,
            style = tokens.calendarDayOfWeekTextStyle,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            // 小时列：0..23
            TimeWheel(
                items = (0..23).map { it.toString().padStart(2, '0') },
                selectedIndex = time.hour,
                onIndexChange = { h -> onTimeChange(LocalTime(h, time.minute)) },
            )
            Text(
                text = ":",
                color = tokens.calendarHeaderColor,
                style = tokens.calendarHeaderTextStyle,
            )
            // 分钟列：按 minuteStep 对齐
            val minuteOptions = buildList {
                var m = 0
                while (m < 60) {
                    add(m)
                    m += minuteStep.coerceAtLeast(1)
                }
            }
            val currentMinuteIndex = minuteOptions.indexOfFirst { it == time.minute }
                .let { if (it < 0) 0 else it }
            TimeWheel(
                items = minuteOptions.map { it.toString().padStart(2, '0') },
                selectedIndex = currentMinuteIndex,
                onIndexChange = { idx ->
                    val m = minuteOptions[idx]
                    onTimeChange(LocalTime(time.hour, m))
                },
            )
        }
    }
}

/**
 * 单列滚轮：居中高亮选中项，上下半透明渐隐。
 * 采用 LazyColumn + 拖拽手势 + 点击选中，兼容桌面与移动。
 */
@Composable
private fun TimeWheel(
    items: List<String>,
    selectedIndex: Int,
    onIndexChange: (Int) -> Unit,
) {
    val tokens = PaletteTheme.componentThemes.dateTime
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    val itemHeight = WheelItemHeight
    val halfVisible = WheelVisibleCount / 2

    // 外部 selectedIndex 变化时滚动到对应位置（居中）
    LaunchedEffect(selectedIndex) {
        listState.animateScrollToItem(selectedIndex)
    }

    Box(
        modifier = Modifier
            .width(44.dp)
            .height(itemHeight * WheelVisibleCount),
        contentAlignment = Alignment.Center,
    ) {
        LazyColumn(
            state = listState,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // 顶部占位，让首项能滚到中心
            item { Spacer(modifier = Modifier.height(itemHeight * halfVisible)) }
            itemsIndexed(items) { index, value ->
                val isSelected = index == selectedIndex
                Box(
                    modifier = Modifier
                        .height(itemHeight)
                        .padding(horizontal = 4.dp)
                        .pointerInput(items) {
                            detectVerticalDragGestures(
                                onDragEnd = {
                                    // 拖拽结束：吸附到最近项
                                    val center = listState.firstVisibleItemIndex +
                                        (WheelVisibleCount / 2)
                                    val target = center.coerceIn(0, items.lastIndex)
                                    scope.launch {
                                        listState.animateScrollToItem(target)
                                        onIndexChange(target)
                                    }
                                },
                            ) { _, dragAmount ->
                                scope.launch {
                                    listState.scrollBy(-dragAmount)
                                }
                            }
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = value,
                        color = if (isSelected) tokens.calendarRangeStartCapColor else tokens.calendarDisabledColor,
                        style = tokens.calendarHeaderTextStyle,
                        textAlign = TextAlign.Center,
                    )
                }
            }
            // 底部占位
            item { Spacer(modifier = Modifier.height(itemHeight * halfVisible)) }
        }

        // 选中指示带（中心横条背景）
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(itemHeight)
                .background(tokens.calendarRangeHoverColor),
        )
    }
}
