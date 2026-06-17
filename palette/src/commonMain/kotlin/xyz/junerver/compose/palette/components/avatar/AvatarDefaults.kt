package xyz.junerver.compose.palette.components.avatar

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.core.theme.PaletteTheme

enum class AvatarSize(val size: Dp) {
    Small(24.dp),
    Medium(40.dp),
    Large(56.dp),
    XLarge(96.dp)
}

enum class AvatarShape {
    Circle,
    Square,
    RoundedRectangle,
}

object AvatarDefaults {
    @Composable
    fun size(size: AvatarSize): Dp = when (size) {
        AvatarSize.Small -> PaletteTheme.componentThemes.dataDisplay.avatarSmallSize
        AvatarSize.Medium -> PaletteTheme.componentThemes.dataDisplay.avatarMediumSize
        AvatarSize.Large -> PaletteTheme.componentThemes.dataDisplay.avatarLargeSize
        AvatarSize.XLarge -> PaletteTheme.componentThemes.dataDisplay.avatarExtraLargeSize
    }

    @Composable
    fun textStyle(size: AvatarSize): TextStyle = when (size) {
        AvatarSize.Small -> PaletteTheme.componentThemes.dataDisplay.avatarSmallTextStyle
        AvatarSize.Medium -> PaletteTheme.componentThemes.dataDisplay.avatarMediumTextStyle
        AvatarSize.Large,
        AvatarSize.XLarge -> PaletteTheme.componentThemes.dataDisplay.avatarLargeTextStyle
    }

    @Composable
    fun backgroundColor(): Color = PaletteTheme.componentThemes.dataDisplay.avatarBackgroundColor

    @Composable
    fun textColor(): Color = PaletteTheme.componentThemes.dataDisplay.avatarTextColor

    @Composable
    fun shape(shape: AvatarShape = AvatarShape.Circle): Shape =
        when (shape) {
            AvatarShape.Circle -> CircleShape
            AvatarShape.Square -> RectangleShape
            AvatarShape.RoundedRectangle -> RoundedCornerShape(PaletteTheme.componentThemes.dataDisplay.avatarRoundedCornerRadius)
        }
}
