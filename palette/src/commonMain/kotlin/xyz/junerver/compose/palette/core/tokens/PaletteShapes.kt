package xyz.junerver.compose.palette.core.tokens

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

@Immutable
class PaletteShapes(
    val small: Shape = RoundedCornerShape(4.dp),
    val medium: Shape = RoundedCornerShape(8.dp),
    val large: Shape = RoundedCornerShape(12.dp),
) {
    fun copy(
        small: Shape = this.small,
        medium: Shape = this.medium,
        large: Shape = this.large,
    ): PaletteShapes = PaletteShapes(
        small = small,
        medium = medium,
        large = large,
    )

    companion object {
        fun default() = PaletteShapes()
    }
}
