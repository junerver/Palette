package xyz.junerver.compose.palette.components.button

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import xyz.junerver.compose.palette.components.loading.PLoading

@Composable
fun PButton(
    text: String,
    modifier: Modifier = Modifier,
    type: ButtonType = ButtonType.PRIMARY,
    size: ButtonSize = ButtonSize.LARGE,
    width: Dp = ButtonDefaults.defaultWidth(),
    disabled: Boolean = false,
    loading: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    val colors = buttonColorsOf(type)
    val localDisabled = disabled || loading
    val borderRadius = ButtonDefaults.borderRadius(size)
    val padding = ButtonDefaults.padding(size)
    val fontSize = ButtonDefaults.fontSize(size)
    val loadingSpacing = ButtonDefaults.loadingSpacing()
    val disabledAlpha = ButtonDefaults.disabledAlpha()

    Box(
        Modifier
            .width(if (size != ButtonSize.SMALL) width else Dp.Unspecified)
            .clip(RoundedCornerShape(borderRadius))
            .clickable(enabled = !localDisabled) {
                if (!localDisabled) {
                    onClick?.invoke()
                }
            }
            .background(colors.containerColor)
            .padding(padding)
            .alpha(if (disabled) disabledAlpha else 1f)
            .then(modifier),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (loading) {
                PLoading(color = colors.contentColor)
                Spacer(Modifier.width(loadingSpacing))
            }

            Text(
                text,
                color = colors.contentColor,
                fontSize = fontSize
            )
        }
    }
}

private data class ButtonColors(
    val containerColor: Color,
    val contentColor: Color
)

@Composable
private fun buttonColorsOf(type: ButtonType): ButtonColors {
    return when (type) {
        ButtonType.PRIMARY -> ButtonColors(
            ButtonDefaults.primaryContainerColor(),
            ButtonDefaults.primaryContentColor()
        )
        ButtonType.DANGER -> ButtonColors(
            ButtonDefaults.dangerContainerColor(),
            ButtonDefaults.dangerContentColor()
        )
        ButtonType.PLAIN -> ButtonColors(
            ButtonDefaults.plainContainerColor(),
            ButtonDefaults.plainContentColor()
        )
    }
}
