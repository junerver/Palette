package xyz.junerver.compose.palette.components.tour

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import xyz.junerver.compose.palette.components.button.PButton
import xyz.junerver.compose.palette.core.theme.PaletteTheme

@Composable
fun PTour(
    steps: List<TourStep>,
    currentIndex: Int,
    modifier: Modifier = Modifier,
    onPrevious: () -> Unit = {},
    onNext: () -> Unit = {},
    onFinish: () -> Unit = {},
) {
    val step = steps.getOrNull(currentIndex) ?: return
    val isLast = currentIndex >= steps.lastIndex

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(TourDefaults.CornerRadius))
            .background(TourDefaults.containerColor())
            .padding(TourDefaults.ContentPadding),
        verticalArrangement = Arrangement.spacedBy(TourDefaults.ContentPadding)
    ) {
        Text(text = step.title, color = TourDefaults.titleColor())
        if (!step.description.isNullOrBlank()) {
            Text(text = step.description, color = TourDefaults.descriptionColor())
        }
        Row(horizontalArrangement = Arrangement.spacedBy(TourDefaults.ContentPadding)) {
            PButton(
                text = if (isLast) PaletteTheme.strings.tourFinishText else PaletteTheme.strings.tourPreviousText,
                onClick = {
                    if (isLast) onFinish() else onPrevious()
                }
            )
            PButton(
                text = if (isLast) PaletteTheme.strings.tourCloseText else PaletteTheme.strings.tourNextText,
                onClick = {
                    if (isLast) onFinish() else onNext()
                }
            )
        }
    }
}
