package xyz.junerver.compose.palette.components.toolbar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import xyz.junerver.compose.palette.components.text.PText

@Composable
fun Toolbar(
    modifier: Modifier = Modifier,
    title: String = "",
    colors: ToolbarColors = ToolbarDefaults.colors(),
    height: Dp = ToolbarDefaults.height(),
    onNavigationClick: () -> Unit = {},
) {
    PToolbar(
        modifier = modifier,
        title = title,
        colors = colors,
        height = height,
        navigationIcon = {
            IconButton(onClick = onNavigationClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = null,
                    modifier = Modifier.size(ToolbarDefaults.iconSize()),
                    tint = colors.contentColor
                )
            }
        }
    )
}

@Composable
fun PToolbar(
    modifier: Modifier = Modifier,
    title: String = "",
    colors: ToolbarColors = ToolbarDefaults.colors(),
    height: Dp = ToolbarDefaults.height(),
    navigationIcon: (@Composable () -> Unit)? = null,
    titleContent: (@Composable () -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .background(colors.backgroundColor),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.CenterStart
        ) {
            navigationIcon?.invoke()
        }
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center
        ) {
            if (titleContent != null) {
                titleContent()
            } else {
                PText(
                    text = title,
                    color = colors.contentColor,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                    style = ToolbarDefaults.titleTextStyle(),
                )
            }
        }
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.weight(1f))
            actions()
        }
    }
}
