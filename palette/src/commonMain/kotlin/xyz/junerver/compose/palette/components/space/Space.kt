package xyz.junerver.compose.palette.components.space

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp

enum class SpaceDirection {
    Horizontal, Vertical
}

enum class SpaceSize {
    Small, Medium, Large
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PSpace(
    modifier: Modifier = Modifier,
    direction: SpaceDirection = SpaceDirection.Horizontal,
    size: Dp = SpaceDefaults.MediumSpacing,
    wrap: Boolean = false,
    align: Alignment.Vertical = Alignment.CenterVertically,
    content: @Composable () -> Unit,
) {
    when {
        direction == SpaceDirection.Horizontal && wrap -> FlowRow(
            modifier = modifier,
            horizontalArrangement = Arrangement.spacedBy(size),
            content = { content() },
        )
        direction == SpaceDirection.Horizontal -> Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.spacedBy(size),
            verticalAlignment = align,
            content = { content() },
        )
        else -> Column(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(size),
            content = { content() },
        )
    }
}
