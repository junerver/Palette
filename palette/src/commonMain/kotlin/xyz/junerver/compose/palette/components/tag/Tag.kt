package xyz.junerver.compose.palette.components.tag

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.core.theme.PaletteTheme

enum class TagVariant {
    Default, Outlined
}

@Composable
fun PTag(
    text: String,
    modifier: Modifier = Modifier,
    variant: TagVariant = TagVariant.Default,
    closable: Boolean = false,
    onClose: (() -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    colors: TagColors = when (variant) {
        TagVariant.Default -> TagDefaults.defaultColors()
        TagVariant.Outlined -> TagDefaults.outlinedColors()
    }
) {
    val shape = RoundedCornerShape(TagDefaults.CornerRadius)
    
    Surface(
        modifier = modifier.height(TagDefaults.Height),
        shape = shape,
        color = colors.containerColor,
        contentColor = colors.contentColor,
        border = if (variant == TagVariant.Outlined) {
            BorderStroke(TagDefaults.BorderWidth, colors.borderColor)
        } else null,
        onClick = onClick ?: {}
    ) {
        Row(
            modifier = Modifier.padding(horizontal = TagDefaults.HorizontalPadding),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                style = PaletteTheme.typography.body
            )
            
            if (closable && onClose != null) {
                IconButton(
                    onClick = onClose,
                    modifier = Modifier.size(TagDefaults.CloseButtonSize)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}
