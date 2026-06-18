package xyz.junerver.compose.palette.components.scaffold

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun PScaffold(
    modifier: Modifier = Modifier,
    colors: ScaffoldColors = ScaffoldDefaults.colors(),
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    floatingActionButton: @Composable BoxScope.() -> Unit = {},
    content: @Composable (PaddingValues) -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.containerColor),
    ) {
        topBar()
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxSize(),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(ScaffoldDefaults.contentPadding()),
            ) {
                content(ScaffoldDefaults.contentPadding())
            }
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(ScaffoldDefaults.floatingActionButtonPadding()),
                content = floatingActionButton,
            )
        }
        bottomBar()
    }
}

data class ScaffoldColors(
    val containerColor: Color,
)
