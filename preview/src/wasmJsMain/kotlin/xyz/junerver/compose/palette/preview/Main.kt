package xyz.junerver.compose.palette.preview

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ComposeViewport
import xyz.junerver.compose.palette.components.badge.PBadge
import xyz.junerver.compose.palette.components.button.PButton
import xyz.junerver.compose.palette.components.chart.ChartData
import xyz.junerver.compose.palette.components.chart.ChartOptions
import xyz.junerver.compose.palette.components.chart.ChartSeries
import xyz.junerver.compose.palette.components.chart.ChartSpec
import xyz.junerver.compose.palette.components.chart.PChart
import xyz.junerver.compose.palette.core.theme.PaletteMaterialTheme

/**
 * Interactive component preview panel. Renders a left sidebar of component categories and a right
 * pane showing the selected component live. Serves both as the docs-site playground (embedded via
 * iframe in playground.md) and as the runtime verification that palette renders in the browser.
 */
@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    ComposeViewport("ComposeTarget") {
        PaletteMaterialTheme {
            PreviewApp()
        }
    }
}

private enum class SampleItem(val label: String) {
    BUTTON("Button"),
    BADGE("Badge"),
    BAR_CHART("Chart (Bar)"),
    LINE_CHART("Chart (Line)"),
    PIE_CHART("Chart (Pie)"),
}

@Composable
private fun renderSample(item: SampleItem) {
    when (item) {
        SampleItem.BUTTON -> ButtonSample()
        SampleItem.BADGE -> BadgeSample()
        SampleItem.BAR_CHART -> BarChartSample()
        SampleItem.LINE_CHART -> LineChartSample()
        SampleItem.PIE_CHART -> PieChartSample()
    }
}

@Composable
private fun PreviewApp() {
    var selected by remember { mutableStateOf(SampleItem.BUTTON) }
    Row(modifier = Modifier.fillMaxSize().background(Color(0xFFF7F8FA))) {
        // Sidebar
        Column(
            modifier = Modifier
                .width(180.dp)
                .fillMaxHeight()
                .background(Color.White)
                .padding(vertical = 12.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            Text(
                "Palette Preview",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                fontWeight = FontWeight.Bold,
            )
            SampleItem.entries.forEach { item ->
                val isSel = item == selected
                Text(
                    text = item.label,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selected = item }
                        .background(if (isSel) Color(0xFFE8F0FE) else Color.Transparent)
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    color = if (isSel) Color(0xFF1A73E8) else Color(0xFF202124),
                )
            }
        }
        // Render area
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(24.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White)
                .padding(24.dp),
            contentAlignment = Alignment.Center,
        ) {
            renderSample(selected)
        }
    }
}

@Composable
private fun ButtonSample() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        PButton(text = "Primary", onClick = {})
        PButton(text = "Disabled", disabled = true, onClick = {})
    }
}

@Composable
private fun BadgeSample() {
    Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
        PBadge(content = "New")
        PBadge(content = "99+")
        PBadge()
    }
}

@Composable
private fun BarChartSample() {
    PChart(
        spec = ChartSpec.Bar(),
        data = ChartData(
            series = listOf(
                ChartSeries("A", listOf(3f, 5f, 2f, 7f)),
                ChartSeries("B", listOf(4f, 2f, 6f, 3f)),
            ),
            categories = listOf("Q1", "Q2", "Q3", "Q4"),
        ),
        options = ChartOptions(title = "Quarterly", showLegend = true),
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
private fun LineChartSample() {
    PChart(
        spec = ChartSpec.Line(smooth = true),
        data = ChartData(
            series = listOf(ChartSeries("Trend", listOf(1f, 3f, 2f, 5f, 4f, 6f))),
            categories = listOf("1", "2", "3", "4", "5", "6"),
        ),
        options = ChartOptions(title = "Smooth Trend"),
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
private fun PieChartSample() {
    PChart(
        spec = ChartSpec.Pie(donut = true),
        data = ChartData(
            series = listOf(ChartSeries("Share", listOf(30f, 45f, 25f))),
            categories = listOf("Mobile", "Desktop", "Other"),
        ),
        options = ChartOptions(title = "Traffic Source"),
        modifier = Modifier.fillMaxWidth(),
    )
}
