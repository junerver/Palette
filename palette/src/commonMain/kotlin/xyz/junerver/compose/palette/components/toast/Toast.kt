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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.center
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import kotlinx.coroutines.delay
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
    duration: Long = ToastDefaults.DefaultDuration,
    mask: Boolean = false,
    onClose: () -> Unit
) {
    val hasIcon = icon != ToastIcon.NONE
    var localVisible by remember { mutableStateOf(visible) }

    LaunchedEffect(visible, duration, title) {
        if (visible && duration > 0) {
            delay(duration)
            onClose()
        }
    }

    LaunchedEffect(visible) {
        if (!visible) {
            delay(150)
        }
        localVisible = visible
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
                    Modifier.toastSize(hasIcon)
                },
                contentAlignment = Alignment.Center
            ) {
                AnimatedVisibility(
                    visible = visible && localVisible,
                    enter = fadeIn() + scaleIn(tween(ToastDefaults.AnimationDuration), initialScale = 0.8f),
                    exit = fadeOut() + scaleOut(tween(ToastDefaults.AnimationDuration), targetScale = 0.8f)
                ) {
                    Box(
                        modifier = Modifier
                            .toastSize(hasIcon)
                            .clip(
                                if (icon != ToastIcon.NONE) {
                                    RoundedCornerShape(ToastDefaults.IconBorderRadius)
                                } else {
                                    RoundedCornerShape(ToastDefaults.NoIconBorderRadius)
                                }
                            )
                            .background(ToastDefaults.backgroundColor()),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            when (icon) {
                                ToastIcon.LOADING -> {
                                    PLoading(
                                        size = ToastDefaults.LoadingSize,
                                        color = ToastDefaults.textColor()
                                    )
                                    Spacer(modifier = Modifier.height(ToastDefaults.IconSpacing))
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
                                        modifier = Modifier.size(ToastDefaults.LoadingSize),
                                        tint = ToastDefaults.textColor()
                                    )
                                }

                                else -> {}
                            }

                            val textMeasurer = rememberTextMeasurer()
                            val textLayoutResult = remember(title) {
                                textMeasurer.measure(title, TextStyle(fontSize = ToastDefaults.IconFontSize))
                            }
                            Text(
                                text = title,
                                color = ToastDefaults.textColor(),
                                fontSize = if (hasIcon && textLayoutResult.size.width <= 354) {
                                    ToastDefaults.IconFontSize
                                } else {
                                    ToastDefaults.NoIconFontSize
                                },
                                modifier = Modifier.padding(
                                    horizontal = ToastDefaults.TextPaddingHorizontal,
                                    vertical = ToastDefaults.TextPaddingVertical
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

private fun Modifier.toastSize(hasIcon: Boolean): Modifier {
    return if (hasIcon) {
        this.size(ToastDefaults.IconSize)
    } else {
        this
            .width(ToastDefaults.NoIconWidth)
            .heightIn(ToastDefaults.NoIconMinHeight)
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
    val state = remember { ToastStateImpl() }

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

