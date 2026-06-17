package xyz.junerver.compose.palette.components.skeleton

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp

@Composable
fun PSkeletonCircle(
    size: Dp = SkeletonDefaults.circleSize(),
    isActive: Boolean = true,
    backgroundColor: Color = SkeletonDefaults.backgroundColor()
) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(backgroundColor)
            .shimmerLoading(isActive)
    )
}

@Composable
fun PSkeletonSquare(
    size: Dp = SkeletonDefaults.squareSize(),
    borderRadius: Dp = SkeletonDefaults.squareBorderRadius(),
    isActive: Boolean = true,
    backgroundColor: Color = SkeletonDefaults.backgroundColor()
) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(RoundedCornerShape(borderRadius))
            .background(backgroundColor)
            .shimmerLoading(isActive)
    )
}

@Composable
fun PSkeletonRectangle(
    height: Dp = SkeletonDefaults.rectangleHeight(),
    borderRadius: Dp = SkeletonDefaults.rectangleBorderRadius(),
    isActive: Boolean = true,
    backgroundColor: Color = SkeletonDefaults.backgroundColor()
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(borderRadius))
            .background(backgroundColor)
            .shimmerLoading(isActive)
    )
}

@Composable
fun PSkeletonLine(
    width: Dp = SkeletonDefaults.lineLongWidth(),
    height: Dp = SkeletonDefaults.lineHeight(),
    borderRadius: Dp = SkeletonDefaults.lineBorderRadius(),
    isActive: Boolean = true,
    backgroundColor: Color = SkeletonDefaults.backgroundColor()
) {
    Box(
        modifier = Modifier
            .size(width = width, height = height)
            .clip(RoundedCornerShape(borderRadius))
            .background(backgroundColor)
            .shimmerLoading(isActive)
    )
}
