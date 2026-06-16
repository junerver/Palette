package xyz.junerver.compose.palette.components.popup

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.delay
import xyz.junerver.compose.palette.core.theme.PaletteTheme
import xyz.junerver.compose.palette.core.util.clickableWithoutRipple
import kotlin.math.roundToInt

@Composable
fun PPopup(
    visible: Boolean,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
    title: String? = null,
    draggable: Boolean = true,
    content: @Composable ColumnScope.() -> Unit
) {
    var localVisible by remember { mutableStateOf(false) }

    LaunchedEffect(visible) {
        if (!visible) {
            delay(PopupDefaults.AnimationDuration.toLong() + 50L)
        }
        localVisible = visible
    }

    if (visible || localVisible) {
        PopupDialogContainer(
            visible = visible && localVisible,
            onClose = onClose
        ) {
            var height by remember { mutableIntStateOf(0) }
            val offsetY = remember { mutableIntStateOf(0) }
            val animatedOffsetY by animateIntAsState(
                targetValue = offsetY.intValue,
                label = "PopupDraggingAnimation"
            )

            Box(
                modifier = modifier
                    .fillMaxWidth()
                    .offset { IntOffset(x = 0, y = animatedOffsetY) }
                    .draggable(
                        state = rememberDraggableState { delta ->
                            handleDrag(offsetY, delta)
                        },
                        enabled = draggable,
                        orientation = Orientation.Vertical,
                        onDragStopped = {
                            handleDragStopped(offsetY, height, onClose)
                        }
                    )
                    .then(
                        if (draggable) {
                            Modifier.nestedScroll(
                                remember(height) {
                                    PopupNestedScrollConnection(offsetY, height, onClose)
                                }
                            )
                        } else {
                            Modifier
                        }
                    )
                    .clip(
                        RoundedCornerShape(
                            topStart = PopupDefaults.CornerRadius,
                            topEnd = PopupDefaults.CornerRadius
                        )
                    )
                    .background(PopupDefaults.containerColor())
                    .clickableWithoutRipple { }
                    .padding(PopupDefaults.ContentPadding)
                    .onSizeChanged { height = it.height }
            ) {
                Column {
                    if (draggable) {
                        DraggableLine()
                    }
                    title?.let {
                        PopupTitle(title = it)
                    }
                    content()
                }
            }
        }
    }
}

@Composable
private fun PopupDialogContainer(
    visible: Boolean,
    onClose: () -> Unit,
    content: @Composable () -> Unit
) {
    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickableWithoutRipple { onClose() },
            contentAlignment = Alignment.BottomCenter
        ) {
            AnimatedVisibility(
                visible = visible,
                enter = slideInVertically(
                    animationSpec = tween(PopupDefaults.AnimationDuration),
                    initialOffsetY = { it }
                ),
                exit = slideOutVertically(
                    animationSpec = tween(PopupDefaults.AnimationDuration),
                    targetOffsetY = { it }
                )
            ) {
                content()
            }
        }
    }
}

@Composable
private fun DraggableLine() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .offset(y = PopupDefaults.DraggableLineOffset)
            .padding(top = PopupDefaults.ContentPadding),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(PopupDefaults.DraggableLineLength, PopupDefaults.DraggableLineThickness)
                .background(
                    PopupDefaults.draggableLineColor(),
                    RoundedCornerShape(PopupDefaults.DraggableLineThickness / 2)
                )
        )
    }
}

@Composable
private fun PopupTitle(title: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(PopupDefaults.TitleHeight),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            color = PopupDefaults.titleColor(),
            fontSize = PopupDefaults.TitleFontSize,
            fontWeight = FontWeight.Bold
        )
    }
}

private class PopupNestedScrollConnection(
    private val offsetY: MutableIntState,
    private val height: Int,
    private val onClose: () -> Unit
) : NestedScrollConnection {
    override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
        if (source == NestedScrollSource.UserInput && offsetY.intValue > 0) {
            handleDrag(offsetY, available.y)
            return available
        }
        return Offset.Zero
    }

    override fun onPostScroll(
        consumed: Offset,
        available: Offset,
        source: NestedScrollSource
    ): Offset {
        if (source == NestedScrollSource.UserInput) {
            handleDrag(offsetY, available.y)
            return available
        }
        return Offset.Zero
    }

    override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
        handleDragStopped(offsetY, height, onClose)
        return available
    }
}

private fun handleDrag(offsetY: MutableIntState, delta: Float) {
    offsetY.intValue = (offsetY.intValue + delta.roundToInt()).coerceAtLeast(0)
}

private fun handleDragStopped(offsetY: MutableIntState, height: Int, onClose: () -> Unit) {
    if (offsetY.intValue > height * PopupDefaults.DragDismissThreshold) {
        onClose()
    } else {
        offsetY.intValue = 0
    }
}
