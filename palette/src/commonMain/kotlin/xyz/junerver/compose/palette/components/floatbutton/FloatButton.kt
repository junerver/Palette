package xyz.junerver.compose.palette.components.floatbutton

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Shape
import xyz.junerver.compose.palette.components.text.PText

@Composable
fun PFloatButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: FloatButtonShape = FloatButtonShape.Circle,
    icon: (@Composable () -> Unit)? = null,
    text: String? = null,
) {
    val resolvedShape: Shape = when (shape) {
        FloatButtonShape.Circle -> CircleShape
        FloatButtonShape.Square -> RoundedCornerShape(FloatButtonDefaults.cornerRadius())
    }
    val containerColor = FloatButtonDefaults.containerColor()

    Box(
        modifier = modifier
            .size(FloatButtonDefaults.size())
            .shadow(FloatButtonDefaults.elevation(), resolvedShape)
            .clip(resolvedShape)
            .background(containerColor)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            icon?.invoke()
            if (text != null) {
                if (icon != null) {
                    Spacer(modifier = Modifier.height(FloatButtonDefaults.textPadding()))
                }
                PText(
                    text = text,
                    color = FloatButtonDefaults.textColor(),
                    style = FloatButtonDefaults.textStyle()
                )
            }
        }
    }
}
