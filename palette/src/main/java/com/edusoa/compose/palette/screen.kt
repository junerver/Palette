package com.edusoa.ideallecturer.suzhou.components

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
fun Screen(activity: Activity,
           title: String = "",
           verticalArrangement: Arrangement.Vertical = Arrangement.Top,
           horizontalAlignment: Alignment.Horizontal = Alignment.Start,
           content: @Composable ColumnScope.() -> Unit) {
    Surface(modifier = Modifier.fillMaxSize()) {
        ActivityContext.Provider(value = activity) {
            Column(
                verticalArrangement =verticalArrangement,
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