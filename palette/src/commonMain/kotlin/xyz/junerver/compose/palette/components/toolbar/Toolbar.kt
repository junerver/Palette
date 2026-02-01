package xyz.junerver.compose.palette.components.toolbar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp

@Composable
fun Toolbar(
    modifier: Modifier = Modifier,
    title: String = "",
    colors: ToolbarColors = ToolbarDefaults.colors(),
    height: Dp = ToolbarDefaults.Height,
    onNavigationClick: () -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .background(colors.backgroundColor),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(modifier = Modifier.weight(1f)) {
            IconButton(onClick = onNavigationClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = null,
                    tint = colors.contentColor
                )
            }
        }
        Box(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = colors.contentColor,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium
            )
        }
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Deprecated(
    message = "Use Toolbar with ToolbarColors",
    replaceWith = ReplaceWith(
        "Toolbar(modifier, title, ToolbarDefaults.colors(backgroundColor), height, onIconClick)",
        "xyz.junerver.compose.palette.components.toolbar.Toolbar",
        "xyz.junerver.compose.palette.components.toolbar.ToolbarDefaults"
    )
)
@Composable
fun Toolbar(
    title: String = "",
    onIconClick: () -> Unit = {},
    backgroundColor: Color,
    height: Dp = ToolbarDefaults.Height,
) {
    Toolbar(
        modifier = Modifier,
        title = title,
        colors = ToolbarDefaults.colors(backgroundColor = backgroundColor),
        height = height,
        onNavigationClick = onIconClick,
    )
}
