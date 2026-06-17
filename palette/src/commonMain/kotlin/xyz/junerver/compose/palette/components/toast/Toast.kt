package xyz.junerver.compose.palette.components.toast

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.center
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import kotlinx.coroutines.delay
import xyz.junerver.compose.hooks.useCreation
import xyz.junerver.compose.hooks.useLatestState
import xyz.junerver.compose.hooks.useState
import xyz.junerver.compose.palette.components.loading.PLoading
import xyz.junerver.compose.palette.core.theme.PaletteTheme

enum class ToastIcon {
    SUCCESS,
    FAIL,
    LOADING,
    NONE
}

@Composable
fun PToast(
    visible: Boolean,
    title: String,
    icon: ToastIcon = ToastIcon.NONE,
    duration: Long = ToastDefaults.defaultDuration(),
    mask: Boolean = false,
    onClose: () -> Unit
) {
    val hasIcon = icon != ToastIcon.NONE
    val (localVisible, setLocalVisible) = useState(visible)
    val latestOnClose = useLatestState(onClose)
    val animationDuration = ToastDefaults.animationDuration()
    val exitDelay = ToastDefaults.exitDelay()
    val iconSize = ToastDefaults.iconSize()
    val noIconWidth = ToastDefaults.noIconWidth()
    val noIconMinHeight = ToastDefaults.noIconMinHeight()
    val loadingSize = ToastDefaults.loadingSize()
    val iconSpacing = ToastDefaults.iconSpacing()
    val iconTextStyle = ToastDefaults.iconTextStyle()
    val noIconTextStyle = ToastDefaults.noIconTextStyle()
    val iconTextMaxWidthPx = ToastDefaults.iconTextMaxWidthPx()

    LaunchedEffect(visible, duration, title) {
        if (visible && duration > 0) {
            delay(duration)
            latestOnClose.value()
        }
    }

    LaunchedEffect(visible, exitDelay) {
        if (!visible) {
            delay(exitDelay)
        }
        setLocalVisible(visible)
    }

    val positionProvider = remember {
        object : PopupPositionProvider {
            override fun calculatePosition(
                anchorBounds: IntRect,
                windowSize: IntSize,
                layoutDirection: LayoutDirection,
                popupContentSize: IntSize
            ): IntOffset {
                return windowSize.center - popupContentSize.center
            }
        }
    }

    if (visible || localVisible) {
        Popup(popupPositionProvider = positionProvider) {
            Box(
                modifier = if (mask) {
                    Modifier.fillMaxSize()
                } else {
                    Modifier.toastSize(hasIcon, iconSize, noIconWidth, noIconMinHeight)
                },
                contentAlignment = Alignment.Center
            ) {
                AnimatedVisibility(
                    visible = visible && localVisible,
                    enter = fadeIn() + scaleIn(tween(animationDuration), initialScale = 0.8f),
                    exit = fadeOut() + scaleOut(tween(animationDuration), targetScale = 0.8f)
                ) {
                    Box(
                        modifier = Modifier
                            .toastSize(hasIcon, iconSize, noIconWidth, noIconMinHeight)
                            .clip(
                                if (icon != ToastIcon.NONE) {
                                    RoundedCornerShape(ToastDefaults.iconBorderRadius())
                                } else {
                                    RoundedCornerShape(ToastDefaults.noIconBorderRadius())
                                }
                            )
                            .background(ToastDefaults.backgroundColor()),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            when (icon) {
                                ToastIcon.LOADING -> {
                                    PLoading(
                                        size = loadingSize,
                                        color = ToastDefaults.textColor()
                                    )
                                    Spacer(modifier = Modifier.height(iconSpacing))
                                }

                                ToastIcon.SUCCESS,
                                ToastIcon.FAIL -> {
                                    Icon(
                                        imageVector = if (icon == ToastIcon.SUCCESS) {
                                            Icons.Outlined.Check
                                        } else {
                                            Icons.Filled.Info
                                        },
                                        contentDescription = if (icon == ToastIcon.SUCCESS) {
                                            PaletteTheme.strings.toastSuccessContentDescription
                                        } else {
                                            PaletteTheme.strings.toastFailContentDescription
                                        },
                                        modifier = Modifier.size(loadingSize),
                                        tint = ToastDefaults.textColor()
                                    )
                                }

                                else -> {}
                            }

                            val textMeasurer = rememberTextMeasurer()
                            val textLayoutResult = remember(title, iconTextStyle) {
                                textMeasurer.measure(title, iconTextStyle)
                            }
                            val textStyle = if (hasIcon && textLayoutResult.size.width <= iconTextMaxWidthPx) {
                                iconTextStyle
                            } else {
                                noIconTextStyle
                            }
                            Text(
                                text = title,
                                color = ToastDefaults.textColor(),
                                style = textStyle,
                                modifier = Modifier.padding(
                                    horizontal = ToastDefaults.textPaddingHorizontal(),
                                    vertical = ToastDefaults.textPaddingVertical()
                                ),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun Modifier.toastSize(
    hasIcon: Boolean,
    iconSize: Dp,
    noIconWidth: Dp,
    noIconMinHeight: Dp,
): Modifier {
    return if (hasIcon) {
        this.size(iconSize)
    } else {
        this
            .width(noIconWidth)
            .heightIn(noIconMinHeight)
    }
}

@Stable
interface ToastState {
    val visible: Boolean
    fun show(
        title: String,
        icon: ToastIcon = ToastIcon.NONE,
        duration: Long = ToastDefaults.DefaultDuration,
        mask: Boolean = false
    )
    fun hide()
}

@Composable
fun rememberToastState(): ToastState {
    val state = useCreation { ToastStateImpl() }.current

    state.props?.let { props ->
        PToast(
            visible = state.visible,
            title = props.title,
            icon = props.icon,
            duration = props.duration,
            mask = props.mask
        ) {
            state.hide()
        }
    }

    return state
}

private class ToastStateImpl : ToastState {
    override var visible by mutableStateOf(false)
    var props by mutableStateOf<ToastProps?>(null)
        private set

    override fun show(title: String, icon: ToastIcon, duration: Long, mask: Boolean) {
        props = ToastProps(title, icon, duration, mask)
        visible = true
    }

    override fun hide() {
        visible = false
    }
}

private data class ToastProps(
    val title: String,
    val icon: ToastIcon,
    val duration: Long,
    val mask: Boolean
)

