package xyz.junerver.compose.palette.components.card

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

enum class CardVariant {
    Elevated, Filled, Outlined
}

@Composable
fun PCard(
    modifier: Modifier = Modifier,
    variant: CardVariant = CardVariant.Elevated,
    onClick: (() -> Unit)? = null,
    colors: CardColors = when (variant) {
        CardVariant.Elevated -> CardDefaults.elevatedColors()
        CardVariant.Filled -> CardDefaults.filledColors()
        CardVariant.Outlined -> CardDefaults.outlinedColors()
    },
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = RoundedCornerShape(CardDefaults.CornerRadius)
    
    Surface(
        modifier = modifier,
        shape = shape,
        color = colors.containerColor,
        contentColor = colors.contentColor,
        tonalElevation = if (variant == CardVariant.Elevated) CardDefaults.Elevation else 0.dp,
        border = if (variant == CardVariant.Outlined) {
            BorderStroke(CardDefaults.BorderWidth, colors.contentColor.copy(alpha = 0.12f))
        } else null,
        onClick = onClick ?: {}
    ) {
        Column(
            modifier = Modifier.padding(CardDefaults.ContentPadding),
            content = content
        )
    }
}
