package xyz.junerver.compose.palette.components.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import xyz.junerver.compose.palette.components.toolbar.Toolbar
import xyz.junerver.compose.palette.components.toolbar.ToolbarColors

expect interface PlatformActivity {
    fun finish()
}

val LocalPlatformActivity = compositionLocalOf<PlatformActivity?> { null }

@Composable
fun Screen(
    modifier: Modifier = Modifier,
    activity: PlatformActivity? = null,
    title: String = "",
    showToolbar: Boolean = title.isNotBlank(),
    colors: ScreenColors = ScreenDefaults.colors(),
    toolbarColors: ToolbarColors = ScreenDefaults.toolbarColors(),
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    content: @Composable ColumnScope.() -> Unit
) {
    CompositionLocalProvider(LocalPlatformActivity provides activity) {
        Surface(
            modifier = modifier
                .fillMaxSize()
                .systemBarsPadding(),
            color = colors.backgroundColor,
            contentColor = colors.contentColor,
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = verticalArrangement,
                horizontalAlignment = horizontalAlignment,
            ) {
                if (showToolbar) {
                    Toolbar(
                        title = title,
                        colors = toolbarColors,
                        onNavigationClick = { activity?.finish() }
                    )
                }
                content()
            }
        }
    }
}
