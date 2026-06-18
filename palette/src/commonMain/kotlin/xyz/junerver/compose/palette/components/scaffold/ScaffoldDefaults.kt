package xyz.junerver.compose.palette.components.scaffold

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.core.theme.PaletteMaterialTheme

object ScaffoldDefaults {
    @Composable
    fun colors(
        containerColor: Color = PaletteMaterialTheme.colorScheme.background,
    ): ScaffoldColors = ScaffoldColors(
        containerColor = containerColor,
    )

    @Composable
    fun contentPadding(): PaddingValues = PaddingValues(0.dp)

    @Composable
    fun floatingActionButtonPadding(): PaddingValues = PaddingValues(16.dp)
}
