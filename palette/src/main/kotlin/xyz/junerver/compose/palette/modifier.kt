package xyz.junerver.compose.palette

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

/**
 * Description:
 *
 * @author Junerver date: 2024/3/28-16:26 Email: junerver@gmail.com
 *     Version: v1.0
 */
@Composable
fun Modifier.onClick(fn: () -> Unit) = this.clickable(
    interactionSource = remember { MutableInteractionSource() },
    indication = null
) {
    fn()
}