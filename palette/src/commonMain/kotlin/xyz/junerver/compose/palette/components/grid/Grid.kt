package xyz.junerver.compose.palette.components.grid

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.LayoutScopeMarker
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun PRow(
    modifier: Modifier = Modifier,
    gutter: Dp = GridDefaults.DefaultGutter,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalAlignment: Alignment.Vertical = Alignment.Top,
    content: @Composable PRowScope.() -> Unit,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = if (gutter > 0.dp) {
            Arrangement.spacedBy(gutter)
        } else {
            horizontalArrangement
        },
        verticalAlignment = verticalAlignment,
    ) {
        val scope = PRowScopeImpl(this)
        scope.content()
    }
}

@LayoutScopeMarker
@Immutable
interface PRowScope {
    @Composable
    fun PCol(
        span: Int = 24,
        offset: Int = 0,
        modifier: Modifier = Modifier,
        content: @Composable () -> Unit,
    )
}

private class PRowScopeImpl(private val rowScope: RowScope) : PRowScope {
    @Composable
    override fun PCol(
        span: Int,
        offset: Int,
        modifier: Modifier,
        content: @Composable () -> Unit,
    ) {
        val spanFraction = GridDefaults.colWidth(span.coerceIn(1, 24))
        val offsetFraction = GridDefaults.colOffset(offset.coerceIn(0, 23))

        Box(
            modifier = with(rowScope) {
                modifier
                    .weight(spanFraction)
                    .then(
                        if (offsetFraction > 0f) {
                            Modifier.padding(start = (offsetFraction * 100).dp)
                        } else {
                            Modifier
                        }
                    )
            }
        ) {
            content()
        }
    }
}
