package xyz.junerver.compose.palette.components.backtop

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.hooks.useState

@Composable
fun PBacktop(
    scrollState: ScrollState,
    modifier: Modifier = Modifier,
    visibilityHeight: Int = BacktopDefaults.VisibilityHeight,
    right: Dp = 24.dp,
    bottom: Dp = 40.dp,
    onClick: (() -> Unit)? = null,
) {
    val visible by remember {
        derivedStateOf { scrollState.value > visibilityHeight }
    }

    val (scrollToTop, setScrollToTop) = useState(false)

    if (scrollToTop) {
        LaunchedEffect(Unit) {
            scrollState.animateScrollTo(0)
            setScrollToTop(false)
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(animationSpec = tween(BacktopDefaults.animationDurationMillis())),
            exit = fadeOut(animationSpec = tween(BacktopDefaults.animationDurationMillis())),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = -right, y = -bottom)
        ) {
            SmallFloatingActionButton(
                onClick = {
                    onClick?.invoke()
                    setScrollToTop(true)
                },
                modifier = Modifier.size(BacktopDefaults.size()),
                shape = RoundedCornerShape(BacktopDefaults.cornerRadius()),
                containerColor = BacktopDefaults.containerColor(),
                elevation = FloatingActionButtonDefaults.elevation(
                    defaultElevation = BacktopDefaults.elevation()
                ),
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowUp,
                    contentDescription = null,
                    tint = BacktopDefaults.iconColor(),
                    modifier = Modifier.size(BacktopDefaults.iconSize())
                )
            }
        }
    }
}
