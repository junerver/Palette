package xyz.junerver.compose.palette.components.button

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.material3.LocalContentColor
import xyz.junerver.compose.palette.components.loading.PLoading
import xyz.junerver.compose.palette.components.text.PText

@Immutable
data class ButtonColors(
    val containerColor: Color,
    val contentColor: Color,
    val borderColor: Color = Color.Transparent
)

@Composable
fun PButton(
    text: String,
    modifier: Modifier = Modifier,
    type: ButtonType = ButtonType.PRIMARY,
    size: ButtonSize = ButtonSize.LARGE,
    width: Dp = ButtonDefaults.defaultWidth(),
    disabled: Boolean = false,
    loading: Boolean = false,
    colors: ButtonColors? = null,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {
    val buttonColors = colors ?: buttonColorsOf(type)
    val localDisabled = disabled || loading
    val borderRadius = ButtonDefaults.borderRadius(size)
    val padding = ButtonDefaults.padding(size)
    val fontSize = ButtonDefaults.fontSize(size)
    val loadingSpacing = ButtonDefaults.loadingSpacing()
    val disabledAlpha = ButtonDefaults.disabledAlpha()

    Box(
        modifier
            .then(
                if (size != ButtonSize.SMALL) {
                    Modifier.widthIn(min = width)
                } else {
                    Modifier
                }
            )
            .clip(RoundedCornerShape(borderRadius))
            .background(buttonColors.containerColor)
            .border(
                border = BorderStroke(1.dp, buttonColors.borderColor),
                shape = RoundedCornerShape(borderRadius)
            )
            .clickable(enabled = !localDisabled) {
                if (!localDisabled) {
                    onClick?.invoke()
                }
            }
            .padding(padding)
            .alpha(if (disabled) disabledAlpha else 1f),
        contentAlignment = Alignment.Center
    ) {
        CompositionLocalProvider(LocalContentColor provides buttonColors.contentColor) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (loading) {
                    PLoading(color = buttonColors.contentColor)
                    Spacer(Modifier.width(loadingSpacing))
                } else if (leadingIcon != null) {
                    leadingIcon()
                    Spacer(Modifier.width(loadingSpacing))
                }

                PText(
                    text,
                    color = buttonColors.contentColor,
                    fontSize = fontSize
                )

                if (!loading && trailingIcon != null) {
                    Spacer(Modifier.width(loadingSpacing))
                    trailingIcon()
                }
            }
        }
    }
}

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
        ButtonType.OUTLINED -> ButtonColors(
            ButtonDefaults.outlinedContainerColor(),
            ButtonDefaults.outlinedContentColor(),
            ButtonDefaults.outlinedBorderColor()
        )
    }
}
