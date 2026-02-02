package xyz.junerver.compose.palette.components.empty

import androidx.compose.foundation.layout.*
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import xyz.junerver.compose.palette.core.theme.PaletteTheme

@Composable
fun PEmpty(
    modifier: Modifier = Modifier,
    icon: (@Composable () -> Unit)? = null,
    title: String? = null,
    description: String? = null,
    action: (@Composable () -> Unit)? = null,
    iconColor: Color = EmptyDefaults.iconColor(),
    titleColor: Color = EmptyDefaults.titleColor(),
    descriptionColor: Color = EmptyDefaults.descriptionColor()
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        icon?.let {
            CompositionLocalProvider(LocalContentColor provides iconColor) {
                Box(modifier = Modifier.size(EmptyDefaults.IconSize)) {
                    it()
                }
            }
            Spacer(modifier = Modifier.height(EmptyDefaults.IconToTitle))
        }
        
        title?.let {
            Text(
                text = it,
                style = PaletteTheme.typography.title,
                color = titleColor,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(EmptyDefaults.TitleToDescription))
        }
        
        description?.let {
            Text(
                text = it,
                style = PaletteTheme.typography.body,
                color = descriptionColor,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(EmptyDefaults.DescriptionToAction))
        }
        
        action?.invoke()
    }
}