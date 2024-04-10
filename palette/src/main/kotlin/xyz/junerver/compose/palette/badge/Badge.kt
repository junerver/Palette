package xyz.junerver.compose.palette.badge

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Description:
 * @author Junerver
 * date: 2024/4/9-11:24
 * Email: junerver@gmail.com
 * Version: v1.0
 */
@Composable
fun PBadge(
    content: String? = null,
    size: Dp = 10.dp,
    color: Color = Color.Red,
    alignment: Alignment = Alignment.TopEnd,
    holder: (@Composable () -> Unit)? = null
) {
    Box {
        val density = LocalDensity.current
        // 徽章的真实宽度
        var localWidth by remember {
            mutableStateOf(0.dp)
        }
        // 计算偏移量
        val offsetX = when (alignment) {
            Alignment.TopEnd,
            Alignment.BottomEnd -> localWidth / 2 //向后偏移半格

            Alignment.TopCenter,
            Alignment.BottomCenter,
            Alignment.Center -> 0.dp

            Alignment.CenterStart -> -(localWidth + 8.dp) // 向前偏移8dp
            Alignment.CenterEnd -> localWidth + 8.dp//向后偏移8dp

            else -> -localWidth / 2 // 向前偏移半格
        }
        val offsetY = when (alignment) {
            Alignment.BottomStart,
            Alignment.BottomCenter,
            Alignment.BottomEnd -> size / 2 //向下偏移高度的一半

            Alignment.CenterEnd,
            Alignment.CenterStart,
            Alignment.Center -> 0.dp

            else -> -size / 2 //向上偏移高度的一半
        }

        holder?.invoke()
        Box(
            modifier = Modifier
                .align(alignment) //设定子元素的位置
                .widthIn(size) //限定宽度至少与高度一致，但是可以更宽，不能更小
                .height(size)
                .onSizeChanged { //监听容器尺寸变化
                    with(density) {
                        // toDp 函数是 Density 内部的扩展函数，必须通过 with(density) 来执行
                        localWidth = it.width.toDp()
                    }
                }
                .offset(x = offsetX, y = offsetY) //根据alignment配置偏移量
                .clip(if (localWidth > size) RoundedCornerShape(20.dp) else CircleShape)
                .background(color)
                .padding(horizontal = if (localWidth > size && content != null) 6.dp else 0.dp),
            contentAlignment = Alignment.Center //内容位置
        ) {
            // 徽章内的文本内容
            content?.let {
                Text(text = it, color = Color.White, fontSize = 12.sp)
            }
        }
    }
}