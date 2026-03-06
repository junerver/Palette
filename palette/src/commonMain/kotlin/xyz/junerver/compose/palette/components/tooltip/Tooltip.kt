package xyz.junerver.compose.palette.components.tooltip

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import xyz.junerver.compose.hooks.useState
import xyz.junerver.compose.palette.core.util.clickableWithoutRipple

@Composable
fun PTooltip(
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    backgroundColor: Color = TooltipDefaults.backgroundColor(),
    textColor: Color = TooltipDefaults.textColor(),
    content: @Composable () -> Unit,
) {
    val (visible, setVisible) = useState(false)

    Column(
        modifier = modifier.clickableWithoutRipple(enabled = enabled) {
            setVisible(!visible)
        }
    ) {
        Box(modifier = Modifier.size(0.dp))
        Box {
            content()
        }

        if (visible) {
            Popup(
                alignment = Alignment.TopCenter,
                onDismissRequest = { setVisible(false) }
            ) {
                Box(
                    modifier = Modifier
                        .padding(bottom = TooltipDefaults.OffsetY)
                        .widthIn(max = 320.dp)
                        .clip(RoundedCornerShape(TooltipDefaults.CornerRadius))
                        .background(backgroundColor)
                        .padding(
                            horizontal = TooltipDefaults.HorizontalPadding,
                            vertical = TooltipDefaults.VerticalPadding
                        )
                ) {
                    Text(
                        text = text,
                        color = textColor
                    )
                }
            }
        }
    }
}
