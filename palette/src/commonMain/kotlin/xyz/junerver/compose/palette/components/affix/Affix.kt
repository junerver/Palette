package xyz.junerver.compose.palette.components.affix

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex

@Composable
fun PAffix(
    modifier: Modifier = Modifier,
    offset: Dp = AffixDefaults.DefaultOffset,
    position: AffixPosition = AffixPosition.Top,
    content: @Composable BoxScope.() -> Unit,
) {
    val density = LocalDensity.current
    var isAffixed by remember { mutableStateOf(false) }
    var contentHeightPx by remember { mutableStateOf(0) }
    var parentHeightPx by remember { mutableStateOf(0) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .onGloballyPositioned { coordinates ->
                val posInParent = coordinates.positionInParent()
                contentHeightPx = coordinates.size.height
                parentHeightPx = coordinates.parentCoordinates?.size?.height ?: 0
                isAffixed = when (position) {
                    AffixPosition.Top -> posInParent.y <= 0f
                    AffixPosition.Bottom -> posInParent.y + coordinates.size.height >= parentHeightPx
                }
            }
    ) {
        if (isAffixed) {
            val offsetY = when (position) {
                AffixPosition.Top -> offset
                AffixPosition.Bottom -> with(density) {
                    parentHeightPx.toDp() - contentHeightPx.toDp() - offset
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .zIndex(AffixDefaults.zIndex)
                    .offset(y = offsetY),
                content = content,
            )
        } else {
            content()
        }
    }
}
