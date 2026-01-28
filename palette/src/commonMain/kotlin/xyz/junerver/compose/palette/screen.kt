package xyz.junerver.compose.palette

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density

/**
 * Description:
 *
 * @author Junerver date: 2024/3/22-8:44 Email: junerver@gmail.com Version:
 *     v1.0
 */

val ActivityContext = compositionLocalOf<PlatformActivity?> { null }

@Composable
fun Screen(
    activity: PlatformActivity,
    title: String = "",
    draftWidth: Float = 480f,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    content: @Composable ColumnScope.() -> Unit
) {
    val fontScale = LocalDensity.current.fontScale
    val screenWidth = getScreenWidthPx()
    val density = screenWidth / draftWidth

    CompositionLocalProvider(
        LocalDensity provides Density(
            density = density,
            fontScale = fontScale
        )
    ) {
        Surface(modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()) {
            CompositionLocalProvider(ActivityContext provides activity) {
                Column(
                    verticalArrangement = verticalArrangement,
                    horizontalAlignment = horizontalAlignment,
                    modifier = Modifier
                        .background(color = Color.White)
                        .fillMaxSize()
                ) {
                    if (title.isNotBlank()) {
                        Toolbar(title = title, onIconClick = {
                            activity.finish()
                        })
                    }
                    content()
                }
            }
        }
    }
}

/**
 * Expect declaration for platform-specific activity interface
 */
expect interface PlatformActivity {
    fun finish()
}

/**
 * Expect function to get screen width in pixels
 */
@Composable
expect fun getScreenWidthPx(): Float
