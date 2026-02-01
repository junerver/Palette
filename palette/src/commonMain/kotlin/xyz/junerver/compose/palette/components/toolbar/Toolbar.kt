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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import xyz.junerver.compose.palette.core.theme.PaletteTheme

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
                style = PaletteTheme.typography.title,
                fontSize = 20.sp,
            )
        }
        Spacer(modifier = Modifier.weight(1f))
    }
}
