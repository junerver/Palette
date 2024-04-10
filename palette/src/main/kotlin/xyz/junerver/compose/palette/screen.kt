package xyz.junerver.compose.palette

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import xyz.junerver.compose.hooks.createContext
import xyz.junerver.kotlin.asBoolean

/**
 * Description:
 *
 * @author Junerver date: 2024/3/22-8:44 Email: junerver@gmail.com Version:
 *     v1.0
 */

val ActivityContext = createContext<Activity?>(null)

@Composable
fun Screen(
    activity: Activity,
    title: String = "",
    draftWitdh: Float = 480f,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    content: @Composable ColumnScope.() -> Unit
) {
    val fontScale = LocalDensity.current.fontScale
    val displayMetrics = LocalContext.current.resources.displayMetrics
    val widthPixels = displayMetrics.widthPixels
    CompositionLocalProvider(
        LocalDensity provides Density(
            density = widthPixels / draftWitdh,
            fontScale = fontScale
        )
    ) {
        Surface(modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()) {
            ActivityContext.Provider(value = activity) {
                Column(
                    verticalArrangement = verticalArrangement,
                    horizontalAlignment = horizontalAlignment,
                    modifier = Modifier
                        .background(color = Color.White)
                        .fillMaxSize()
                ) {
                    if (title.asBoolean()) {
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