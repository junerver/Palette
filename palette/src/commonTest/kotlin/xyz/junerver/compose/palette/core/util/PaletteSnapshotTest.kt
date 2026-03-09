package xyz.junerver.compose.palette.core.util

import androidx.compose.ui.graphics.Color
import xyz.junerver.compose.palette.core.tokens.PaletteColors
import xyz.junerver.compose.palette.core.tokens.PaletteShapes
import xyz.junerver.compose.palette.core.tokens.PaletteSpacing
import xyz.junerver.compose.palette.core.tokens.PaletteTypography
import kotlin.test.Test
import kotlin.test.assertEquals

class PaletteSnapshotTest {
    @Test
    fun paletteColorsSnapshot_shouldCopyRelevantFields() {
        val colors =
            PaletteColors(
                primary = Color(0xFF123456),
                onPrimary = Color.White,
                border = Color(0xFF222222),
                surface = Color(0xFFF5F5F5),
                onSurface = Color.Black,
                hint = Color(0xFF999999),
                error = Color(0xFFFF0000),
                onError = Color.White,
                success = Color(0xFF00FF00),
                warning = Color(0xFFFFFF00),
            )

        val snapshot = PaletteColorsSnapshot(colors)

        assertEquals(colors.primary, snapshot.primary)
        assertEquals(colors.border, snapshot.border)
        assertEquals(colors.warning, snapshot.warning)
    }

    @Test
    fun spacingShapeAndTypographySnapshots_shouldMirrorSourceObjects() {
        val spacing = PaletteSpacing.default()
        val shapes = PaletteShapes.default()
        val typography = PaletteTypography.default()

        assertEquals(spacing.medium, PaletteSpacingSnapshot(spacing).medium)
        assertEquals(shapes.medium, PaletteShapesSnapshot(shapes).medium)
        assertEquals(typography.body, PaletteTypographySnapshot(typography).body)
    }
}
