package xyz.junerver.compose.palette.components.swipe

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

enum class SwipeDismissDirection {
    StartToEnd,
    EndToStart
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PSwipeToDismissBox(
    modifier: Modifier = Modifier,
    enableDismissFromStartToEnd: Boolean = true,
    enableDismissFromEndToStart: Boolean = true,
    onDismiss: (SwipeDismissDirection) -> Boolean,
    backgroundContent: @Composable RowScope.() -> Unit,
    content: @Composable RowScope.() -> Unit,
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { dismissValue ->
            when (dismissValue) {
                SwipeToDismissBoxValue.StartToEnd -> onDismiss(SwipeDismissDirection.StartToEnd)
                SwipeToDismissBoxValue.EndToStart -> onDismiss(SwipeDismissDirection.EndToStart)
                SwipeToDismissBoxValue.Settled -> false
            }
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        modifier = modifier,
        backgroundContent = backgroundContent,
        content = content,
        enableDismissFromStartToEnd = enableDismissFromStartToEnd,
        enableDismissFromEndToStart = enableDismissFromEndToStart
    )
}
