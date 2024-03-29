package com.edusoa.android.palette

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

/**
 * Description:
 *
 * @author Junerver @date: 2024/3/29-13:05
 * @email: junerver@gmail.com
 * @version: v1.0
 */
@Composable
fun CenterVerticallyRow(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    content: @Composable RowScope.() -> Unit
) {
    Row(
        horizontalArrangement = horizontalArrangement,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        content()
    }
}