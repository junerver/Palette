package xyz.junerver.compose.palette.components.drawer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.window.Popup

@Composable
fun PDrawer(
    visible: Boolean,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
    placement: DrawerPlacement = DrawerPlacement.End,
    width: Dp = DrawerDefaults.width(),
    elevation: Dp = DrawerDefaults.elevation(),
    contentPadding: Dp = DrawerDefaults.contentPadding(),
    animationDurationMillis: Int = DrawerDefaults.animationDurationMillis(),
    containerColor: Color = DrawerDefaults.containerColor(),
    overlayColor: Color = DrawerDefaults.overlayColor(),
    content: @Composable () -> Unit,
) {
    if (!visible) return

    Popup(onDismissRequest = onClose) {
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(animationSpec = tween(animationDurationMillis)),
            exit = fadeOut(animationSpec = tween(animationDurationMillis))
        ) {
            Row(
                modifier = modifier.fillMaxSize()
            ) {
                if (placement == DrawerPlacement.End) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize()
                            .background(overlayColor)
                            .clickable(onClick = onClose)
                    )
                }

                AnimatedVisibility(
                    visible = visible,
                    enter = slideInHorizontally(
                        animationSpec = tween(animationDurationMillis)
                    ) { if (placement == DrawerPlacement.End) it else -it },
                    exit = slideOutHorizontally(
                        animationSpec = tween(animationDurationMillis)
                    ) { if (placement == DrawerPlacement.End) it else -it }
                ) {
                    Box(
                        modifier = Modifier
                            .width(width)
                            .fillMaxSize()
                            .shadow(elevation)
                            .background(containerColor)
                            .padding(contentPadding)
                    ) {
                        content()
                    }
                }

                if (placement == DrawerPlacement.Start) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize()
                            .background(overlayColor)
                            .clickable(onClick = onClose)
                    )
                }
            }
        }
    }
}
