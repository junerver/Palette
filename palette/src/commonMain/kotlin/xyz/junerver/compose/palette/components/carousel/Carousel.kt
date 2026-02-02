package xyz.junerver.compose.palette.components.carousel

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <T> PCarousel(
    items: List<T>,
    modifier: Modifier = Modifier,
    autoPlay: Boolean = false,
    autoPlayInterval: Long = 3000L,
    showIndicator: Boolean = true,
    showArrows: Boolean = false,
    itemContent: @Composable (T) -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { items.size })
    val scope = rememberCoroutineScope()

    LaunchedEffect(autoPlay, pagerState.currentPage, pagerState.isScrollInProgress) {
        if (autoPlay && !pagerState.isScrollInProgress && items.isNotEmpty()) {
            delay(autoPlayInterval)
            val nextPage = (pagerState.currentPage + 1) % items.size
            pagerState.animateScrollToPage(nextPage)
        }
    }

    Box(modifier = modifier) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            itemContent(items[page])
        }

        if (showArrows && items.size > 1) {
            IconButton(
                onClick = {
                    scope.launch {
                        val prevPage = if (pagerState.currentPage == 0) items.size - 1 else pagerState.currentPage - 1
                        pagerState.animateScrollToPage(prevPage)
                    }
                },
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 16.dp)
                    .size(CarouselDefaults.ArrowContainerSize),
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = CarouselDefaults.arrowContainerColor(),
                    contentColor = CarouselDefaults.arrowContentColor()
                )
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Previous"
                )
            }

            IconButton(
                onClick = {
                    scope.launch {
                        val nextPage = (pagerState.currentPage + 1) % items.size
                        pagerState.animateScrollToPage(nextPage)
                    }
                },
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 16.dp)
                    .size(CarouselDefaults.ArrowContainerSize),
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = CarouselDefaults.arrowContainerColor(),
                    contentColor = CarouselDefaults.arrowContentColor()
                )
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Next"
                )
            }
        }

        if (showIndicator && items.size > 1) {
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(items.size) { index ->
                    val isActive = pagerState.currentPage == index
                    val targetColor = if (isActive) CarouselDefaults.activeIndicatorColor() else CarouselDefaults.indicatorColor()
                    val color by animateColorAsState(targetColor)
                    
                    Box(
                        modifier = Modifier
                            .padding(horizontal = CarouselDefaults.IndicatorSpacing / 2)
                            .size(CarouselDefaults.IndicatorSize)
                            .clip(CircleShape)
                            .background(color)
                            .clickable {
                                scope.launch {
                                    pagerState.animateScrollToPage(index)
                                }
                            }
                    )
                }
            }
        }
    }
}
