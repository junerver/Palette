package xyz.junerver.compose.palette.demo

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.components.collapse.CollapseItemData
import xyz.junerver.compose.palette.components.collapse.PCollapse

@Composable
fun CollapseDemo() {
    val items = remember {
        listOf(
            CollapseItemData(
                key = "1",
                title = "Consistency 一致性"
            ) {
                Text(
                    text = "Consistent with real life: in line with the process and logic of real life, and comply with languages and habits that the users are used to.\n\n" +
                        "Consistent within interface: all elements should be consistent, such as: design style, icons and texts, position of elements, etc."
                )
            },
            CollapseItemData(
                key = "2",
                title = "Feedback 反馈"
            ) {
                Text(
                    text = "Operation feedback: enable the users to clearly perceive their operations by style updates and interactive effects.\n\n" +
                        "Visual feedback: reflect current state by updating or rearranging elements of the page."
                )
            },
            CollapseItemData(
                key = "3",
                title = "Efficiency 效率"
            ) {
                Text(
                    text = "Simplify the process: keep operating process simple and intuitive.\n\n" +
                        "Definite and clear: enunciate your intentions clearly so that the users can quickly understand and make decisions."
                )
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Text(
            text = "Collapse",
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = "折叠面板",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        DemoSection(title = "基础用法") {
            var expandedKeys by remember { mutableStateOf(setOf("1")) }

            PCollapse(
                items = items,
                expandedKeys = expandedKeys,
                onExpandChange = { expandedKeys = it }
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        DemoSection(title = "手风琴模式") {
            var accordionKeys by remember { mutableStateOf(emptySet<String>()) }

            PCollapse(
                items = items,
                accordion = true,
                expandedKeys = accordionKeys,
                onExpandChange = { accordionKeys = it }
            )
        }
    }
}
