package xyz.junerver.compose.palette.components.contextmenu

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import kotlinx.coroutines.delay
import xyz.junerver.compose.hooks.useCreation

data class ContextMenuItem(
    val label: String,
    val disabled: Boolean = false
)

@Stable
interface ContextMenuState {
    val visible: Boolean
    val menuWidth: Dp
    val itemHeight: Dp
    fun show(position: Offset, items: List<ContextMenuItem>)
    fun hide()
}

@Composable
fun rememberContextMenuState(
    menuWidth: Dp = ContextMenuDefaults.menuWidth(),
    itemHeight: Dp = ContextMenuDefaults.itemHeight()
): ContextMenuState {
    return useCreation { ContextMenuStateImpl(menuWidth, itemHeight) }.current
}

private class ContextMenuStateImpl(
    override val menuWidth: Dp,
    override val itemHeight: Dp
) : ContextMenuState {
    override var visible by mutableStateOf(false)
    var props by mutableStateOf<ContextMenuProps?>(null)
        private set

    override fun show(position: Offset, items: List<ContextMenuItem>) {
        props = ContextMenuProps(position, items)
        visible = true
    }

    override fun hide() {
        visible = false
        props = null
    }

    fun calculateLayout(density: Density, containerSize: IntSize): LayoutResult {
        val p = props ?: return LayoutResult()
        val menuWidthPx = with(density) { menuWidth.roundToPx() }
        val menuHeightDp: Dp = (p.items.size * itemHeight.value).dp
        val menuHeightPx = with(density) { menuHeightDp.roundToPx() }

        val isRight = p.position.x > containerSize.width / 2
        val isBottom = p.position.y > containerSize.height / 2

        return LayoutResult(
            offset = IntOffset(
                x = if (isRight) p.position.x.toInt() - menuWidthPx else p.position.x.toInt(),
                y = if (isBottom) p.position.y.toInt() - menuHeightPx else p.position.y.toInt()
            ),
            pivot = TransformOrigin(
                pivotFractionX = if (isRight) 1f else 0f,
                pivotFractionY = if (isBottom) 1f else 0f
            )
        )
    }

    data class LayoutResult(
        val offset: IntOffset = IntOffset.Zero,
        val pivot: TransformOrigin = TransformOrigin.Center
    )
}

private data class ContextMenuProps(
    val position: Offset,
    val items: List<ContextMenuItem>
)

@Composable
fun PContextMenu(
    state: ContextMenuState,
    onItemClick: (menuIndex: Int) -> Unit
) {
    val impl = state as? ContextMenuStateImpl ?: return

    var isVisible by remember { mutableStateOf(false) }
    var hasShown by remember { mutableStateOf(false) }
    val animationDurationMillis = ContextMenuDefaults.animationDurationMillis()
    val dismissAnimationDurationMillis = ContextMenuDefaults.dismissAnimationDurationMillis()

    LaunchedEffect(state.visible) {
        if (state.visible) {
            hasShown = true
            isVisible = true
        } else if (hasShown) {
            isVisible = false
            delay(dismissAnimationDurationMillis.toLong())
            hasShown = false
        }
    }

    val props = impl.props
    if (props == null) {
        isVisible = false
        return
    }

    val density = LocalDensity.current
    val containerSize = LocalWindowInfo.current.containerSize
    val layout = impl.calculateLayout(density, IntSize(containerSize.width, containerSize.height))

    Popup(
        offset = layout.offset,
        onDismissRequest = { isVisible = false },
        properties = PopupProperties(focusable = true)
    ) {
        val animationSpec = tween<Float>(
            durationMillis = animationDurationMillis,
            easing = LinearOutSlowInEasing
        )

        LaunchedEffect(isVisible) {
            if (!isVisible && hasShown) {
                delay(dismissAnimationDurationMillis.toLong())
                impl.hide()
            }
        }

        AnimatedVisibility(
            visible = isVisible,
            enter = scaleIn(
                initialScale = 0.4f,
                transformOrigin = layout.pivot,
                animationSpec = animationSpec
            ) + fadeIn(animationSpec),
            exit = scaleOut(
                targetScale = 0.4f,
                transformOrigin = layout.pivot,
                animationSpec = animationSpec
            ) + fadeOut(animationSpec)
        ) {
            ContextMenuContent(
                items = props.items,
                menuWidth = state.menuWidth,
                itemHeight = state.itemHeight,
                onItemClick = { index ->
                    isVisible = false
                    onItemClick(index)
                }
            )
        }
    }
}

@Composable
private fun ContextMenuContent(
    items: List<ContextMenuItem>,
    menuWidth: Dp,
    itemHeight: Dp,
    onItemClick: (Int) -> Unit
) {
    val cornerRadius = ContextMenuDefaults.cornerRadius()
    val menuShadowElevation = ContextMenuDefaults.shadowElevation()
    val textStyle = ContextMenuDefaults.textStyle()
    Box(modifier = Modifier.padding(ContextMenuDefaults.contentPadding())) {
        Column(
            modifier = Modifier
                .width(menuWidth)
                .graphicsLayer {
                    shadowElevation = menuShadowElevation.toPx()
                    shape = RoundedCornerShape(cornerRadius)
                    clip = true
                }
                .background(ContextMenuDefaults.containerColor())
        ) {
            items.forEachIndexed { index, item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(itemHeight)
                        .alpha(if (item.disabled) ContextMenuDefaults.disabledAlpha() else 1f)
                        .then(
                            if (!item.disabled) {
                                Modifier.clickable { onItemClick(index) }
                            } else {
                                Modifier
                            }
                        )
                        .padding(horizontal = ContextMenuDefaults.itemPaddingHorizontal()),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item.label,
                        style = textStyle,
                        color = if (item.disabled) {
                            ContextMenuDefaults.disabledTextColor()
                        } else {
                            ContextMenuDefaults.textColor()
                        }
                    )
                }
            }
        }
    }
}

fun Modifier.longPressContextMenu(
    state: ContextMenuState,
    items: List<ContextMenuItem>
): Modifier = composed {
    var windowPosition by remember { mutableStateOf(Offset.Zero) }
    val haptic = LocalHapticFeedback.current

    this
        .onGloballyPositioned {
            windowPosition = it.positionInWindow()
        }
        .pointerInput(Unit) {
            detectTapGestures(onLongPress = { touchOffset ->
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                state.show(windowPosition + touchOffset, items)
            })
        }
}
