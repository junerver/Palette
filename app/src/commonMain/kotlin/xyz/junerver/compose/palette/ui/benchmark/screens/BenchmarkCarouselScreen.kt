package xyz.junerver.compose.palette.ui.benchmark.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.components.carousel.PCarousel
import xyz.junerver.compose.palette.ui.benchmark.BenchmarkTags

@Composable
fun BenchmarkCarouselScreen() {
    val colors = listOf(
        Color(0xFFE57373),
        Color(0xFF64B5F6),
        Color(0xFF81C784),
        Color(0xFFFFD54F),
        Color(0xFFBA68C8),
    )
    Box(modifier = Modifier.fillMaxSize().testTag(BenchmarkTags.Carousel)) {
        PCarousel(
            items = colors,
            autoPlay = true,
            autoPlayInterval = 2000L,
            showIndicator = true,
            showArrows = false,
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
        ) { color ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color),
                contentAlignment = Alignment.Center
            ) {}
        }
    }
}
