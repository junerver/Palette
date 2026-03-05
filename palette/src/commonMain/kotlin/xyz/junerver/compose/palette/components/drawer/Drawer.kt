package xyz.junerver.compose.palette.components.drawer

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.Popup

@Composable
fun PDrawer(
    visible: Boolean,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
    placement: DrawerPlacement = DrawerPlacement.End,
    width: androidx.compose.ui.unit.Dp = DrawerDefaults.Width,
    containerColor: Color = DrawerDefaults.containerColor(),
    overlayColor: Color = DrawerDefaults.overlayColor(),
    content: @Composable () -> Unit,
) {
    if (!visible) return

    Popup(onDismissRequest = onClose) {
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(),
            exit = fadeOut()
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
                    enter = slideInHorizontally { if (placement == DrawerPlacement.End) it else -it },
                    exit = slideOutHorizontally { if (placement == DrawerPlacement.End) it else -it }
                ) {
                    Box(
                        modifier = Modifier
                            .width(width)
                            .fillMaxSize()
                            .background(containerColor)
                            .padding(DrawerDefaults.ContentPadding)
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
