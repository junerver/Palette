package xyz.junerver.compose.palette.components.steps

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

data class StepItem(
    val title: String,
    val description: String? = null,
)

@Composable
fun PSteps(
    items: List<StepItem>,
    currentStep: Int,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(StepsDefaults.itemSpacing())
    ) {
        items.forEachIndexed { index, item ->
            val stateColor = when {
                index < currentStep -> StepsDefaults.doneColor()
                index == currentStep -> StepsDefaults.currentColor()
                else -> StepsDefaults.pendingColor()
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(StepsDefaults.rowSpacing()),
                verticalAlignment = Alignment.Top
            ) {
                val dotSize = StepsDefaults.dotSize()
                val lineWidth = StepsDefaults.lineWidth()
                val lineHeight = StepsDefaults.lineHeight()
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(dotSize)
                            .background(stateColor, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${index + 1}",
                            color = StepsDefaults.dotTextColor()
                        )
                    }
                    if (index < items.lastIndex) {
                        Spacer(
                            modifier = Modifier
                                .height(lineHeight)
                                .fillMaxWidth()
                                .padding(horizontal = (dotSize - lineWidth) / 2)
                                .size(lineWidth, lineHeight)
                                .background(StepsDefaults.pendingColor())
                        )
                    }
                }

                Column(
                    modifier = Modifier.padding(top = StepsDefaults.titleTopPadding()),
                    verticalArrangement = Arrangement.spacedBy(StepsDefaults.titleDescriptionSpacing())
                ) {
                    Text(
                        text = item.title,
                        style = StepsDefaults.titleTextStyle()
                    )
                    if (!item.description.isNullOrBlank()) {
                        Text(
                            text = item.description,
                            color = StepsDefaults.pendingColor(),
                            style = StepsDefaults.descriptionTextStyle()
                        )
                    }
                }
            }
        }
    }
}
