package xyz.junerver.compose.palette.components.carousel

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <T> PCarousel(
    items: List<T>,
    modifier: Modifier = Modifier,
    autoPlay: Boolean = false,
    autoPlayInterval: Long = 3000L,
    showIndicator: Boolean = true,
    showArrows: Boolean = true,
    prevArrow: (@Composable (onClick: () -> Unit) -> Unit)? = null,
    nextArrow: (@Composable (onClick: () -> Unit) -> Unit)? = null,
    itemContent: @Composable (T) -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { items.size })
    val scope = rememberCoroutineScope()

    // Custom draggable state for desktop mouse drag support
    val draggableState = rememberDraggableState { delta ->
        scope.launch {
            pagerState.scroll {
                scrollBy(-delta)
            }
        }
    }

    LaunchedEffect(autoPlay) {
        if (autoPlay && items.isNotEmpty()) {
            while (true) {
                delay(autoPlayInterval)
                if (!pagerState.isScrollInProgress) {
                    val nextPage = (pagerState.currentPage + 1) % items.size
                    pagerState.animateScrollToPage(nextPage)
                }
            }
        }
    }

    Box(modifier = modifier) {
        HorizontalPager(
            state = pagerState,
            userScrollEnabled = false,
            modifier = Modifier
                .fillMaxSize()
                .draggable(
                    state = draggableState,
                    orientation = Orientation.Horizontal,
                    onDragStopped = { velocity ->
                        scope.launch {
                            val currentPageOffset = pagerState.currentPageOffsetFraction
                            val targetPage = when {
                                // Fast swipe left (negative velocity)
                                velocity < -500f -> (pagerState.currentPage + 1).coerceAtMost(items.size - 1)
                                // Fast swipe right (positive velocity)
                                velocity > 500f -> (pagerState.currentPage - 1).coerceAtLeast(0)
                                // Slow drag - decide by offset
                                abs(currentPageOffset) > 0.3f -> {
                                    if (currentPageOffset > 0) {
                                        (pagerState.currentPage + 1).coerceAtMost(items.size - 1)
                                    } else {
                                        (pagerState.currentPage - 1).coerceAtLeast(0)
                                    }
                                }
                                // Snap back to current page
                                else -> pagerState.currentPage
                            }
                            pagerState.animateScrollToPage(targetPage)
                        }
                    }
                ),
            beyondViewportPageCount = 1,
            key = { it }
        ) { page ->
            itemContent(items[page])
        }

        if (showArrows && items.size > 1) {
            // Previous arrow
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 16.dp)
            ) {
                if (prevArrow != null) {
                    prevArrow {
                        scope.launch {
                            val prevPage = if (pagerState.currentPage == 0) items.size - 1 else pagerState.currentPage - 1
                            pagerState.animateScrollToPage(prevPage)
                        }
                    }
                } else {
                    IconButton(
                        onClick = {
                            scope.launch {
                                val prevPage = if (pagerState.currentPage == 0) items.size - 1 else pagerState.currentPage - 1
                                pagerState.animateScrollToPage(prevPage)
                            }
                        },
                        modifier = Modifier.size(CarouselDefaults.ArrowContainerSize),
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
                }
            }

            // Next arrow
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 16.dp)
            ) {
                if (nextArrow != null) {
                    nextArrow {
                        scope.launch {
                            val nextPage = (pagerState.currentPage + 1) % items.size
                            pagerState.animateScrollToPage(nextPage)
                        }
                    }
                } else {
                    IconButton(
                        onClick = {
                            scope.launch {
                                val nextPage = (pagerState.currentPage + 1) % items.size
                                pagerState.animateScrollToPage(nextPage)
                            }
                        },
                        modifier = Modifier.size(CarouselDefaults.ArrowContainerSize),
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
                    val targetSize = if (isActive) CarouselDefaults.ActiveIndicatorSize else CarouselDefaults.InactiveIndicatorSize
                    val color by animateColorAsState(targetColor)
                    val size by animateDpAsState(targetSize)
                    
                    Box(
                        modifier = Modifier
                            .padding(horizontal = CarouselDefaults.IndicatorSpacing / 2)
                            .size(size)
                            .clip(CircleShape)
                            .background(color)
                            .semantics {
                                contentDescription = "Go to slide ${index + 1}"
                            }
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
