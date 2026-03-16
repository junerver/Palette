package xyz.junerver.compose.palette.demo

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.Language
import xyz.junerver.compose.palette.LocalLanguage
import xyz.junerver.compose.palette.components.collapse.CollapseItemData
import xyz.junerver.compose.palette.components.collapse.PCollapse
import xyz.junerver.compose.palette.components.text.PText

@Composable
fun CollapseDemo() {
    val text = collapseDemoText()
    val items =
        remember {
            listOf(
                CollapseItemData(
                    key = "1",
                    title = text.consistencyTitle,
                ) {
                    PText(
                        text = text.consistencyContent,
                    )
                },
                CollapseItemData(
                    key = "2",
                    title = text.feedbackTitle,
                ) {
                    PText(
                        text = text.feedbackContent,
                    )
                },
                CollapseItemData(
                    key = "3",
                    title = text.efficiencyTitle,
                ) {
                    PText(
                        text = text.efficiencyContent,
                    )
                },
            )
        }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
    ) {
        PText(
            text = text.title,
            style = MaterialTheme.typography.headlineMedium,
        )
        PText(
            text = text.subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(modifier = Modifier.height(32.dp))

        DemoSection(title = text.basicSectionTitle) {
            var expandedKeys by remember { mutableStateOf(setOf("1")) }

            PCollapse(
                items = items,
                expandedKeys = expandedKeys,
                onExpandChange = { expandedKeys = it },
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        DemoSection(title = text.accordionSectionTitle) {
            var accordionKeys by remember { mutableStateOf(emptySet<String>()) }

            PCollapse(
                items = items,
                accordion = true,
                expandedKeys = accordionKeys,
                onExpandChange = { accordionKeys = it },
            )
        }
    }
}

@Composable
@ReadOnlyComposable
private fun collapseDemoText(): CollapseDemoText =
    when (LocalLanguage.current) {
        Language.ZH_CN ->
            CollapseDemoText(
                title = "Collapse",
                subtitle = "折叠面板",
                basicSectionTitle = "基础用法",
                accordionSectionTitle = "手风琴模式",
                consistencyTitle = "Consistency 一致性",
                consistencyContent =
                    "Consistent with real life: in line with the process and logic of real life, and comply with languages and habits that the users are used to.\n\n" +
                        "Consistent within interface: all elements should be consistent, such as: design style, icons and texts, position of elements, etc.",
                feedbackTitle = "Feedback 反馈",
                feedbackContent =
                    "Operation feedback: enable the users to clearly perceive their operations by style updates and interactive effects.\n\n" +
                        "Visual feedback: reflect current state by updating or rearranging elements of the page.",
                efficiencyTitle = "Efficiency 效率",
                efficiencyContent =
                    "Simplify the process: keep operating process simple and intuitive.\n\n" +
                        "Definite and clear: enunciate your intentions clearly so that the users can quickly understand and make decisions.",
            )

        Language.EN_US ->
            CollapseDemoText(
                title = "Collapse",
                subtitle = "Collapse panel component.",
                basicSectionTitle = "Basic Usage",
                accordionSectionTitle = "Accordion Mode",
                consistencyTitle = "Consistency",
                consistencyContent =
                    "Consistent with real life: in line with the process and logic of real life, and comply with languages and habits that users are used to.\n\n" +
                        "Consistent within interface: all elements should be consistent, such as design style, icons and text, and element positioning.",
                feedbackTitle = "Feedback",
                feedbackContent =
                    "Operation feedback: enable users to clearly perceive operations through style updates and interaction effects.\n\n" +
                        "Visual feedback: reflect current state by updating or rearranging elements on the page.",
                efficiencyTitle = "Efficiency",
                efficiencyContent =
                    "Simplify the process: keep the operation flow simple and intuitive.\n\n" +
                        "Definite and clear: communicate intentions clearly so users can quickly understand and decide.",
            )
    }

private data class CollapseDemoText(
    val title: String,
    val subtitle: String,
    val basicSectionTitle: String,
    val accordionSectionTitle: String,
    val consistencyTitle: String,
    val consistencyContent: String,
    val feedbackTitle: String,
    val feedbackContent: String,
    val efficiencyTitle: String,
    val efficiencyContent: String,
)
