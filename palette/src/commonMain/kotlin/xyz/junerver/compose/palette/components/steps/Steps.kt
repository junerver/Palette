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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

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
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items.forEachIndexed { index, item ->
            val stateColor = when {
                index < currentStep -> StepsDefaults.doneColor()
                index == currentStep -> StepsDefaults.currentColor()
                else -> StepsDefaults.pendingColor()
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Top
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(StepsDefaults.DotSize)
                            .background(stateColor, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${index + 1}",
                            color = Color.White
                        )
                    }
                    if (index < items.lastIndex) {
                        Spacer(
                            modifier = Modifier
                                .height(24.dp)
                                .fillMaxWidth()
                                .padding(horizontal = (StepsDefaults.DotSize - StepsDefaults.LineWidth) / 2)
                                .size(StepsDefaults.LineWidth, 24.dp)
                                .background(StepsDefaults.pendingColor())
                        )
                    }
                }

                Column(
                    modifier = Modifier.padding(top = 2.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(text = item.title)
                    if (!item.description.isNullOrBlank()) {
                        Text(
                            text = item.description,
                            color = StepsDefaults.pendingColor()
                        )
                    }
                }
            }
        }
    }
}
