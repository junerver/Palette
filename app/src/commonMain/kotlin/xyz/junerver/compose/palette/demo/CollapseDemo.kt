package xyz.junerver.compose.palette.demo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.hooks.useState
import xyz.junerver.compose.palette.Language
import xyz.junerver.compose.palette.LocalLanguage
import xyz.junerver.compose.palette.components.button.ButtonSize
import xyz.junerver.compose.palette.components.button.ButtonType
import xyz.junerver.compose.palette.components.button.PButton
import xyz.junerver.compose.palette.components.collapse.CollapseItemData
import xyz.junerver.compose.palette.components.collapse.PCollapse
import xyz.junerver.compose.palette.components.text.PText

@Composable
fun CollapseDemo() {
    val text = collapseDemoText()
    val items =
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
            val (expandedKeys, setExpandedKeys) = useState(setOf("1"))

            PCollapse(
                items = items,
                expandedKeys = expandedKeys,
                onExpandChange = { setExpandedKeys(it) },
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        DemoSection(title = text.accordionSectionTitle) {
            val (accordionKeys, setAccordionKeys) = useState(emptySet<String>())

            PCollapse(
                items = items,
                accordion = true,
                expandedKeys = accordionKeys,
                onExpandChange = { setAccordionKeys(it) },
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        DemoSection(title = text.controlledSectionTitle) {
            val (controlledKeys, setControlledKeys) = useState(setOf("controlled-1"))
            val (itemCount, setItemCount) = useState(3)
            val (contentVersion, setContentVersion) = useState(1)
            val controlledItems =
                (1..itemCount).map { index ->
                    val key = "controlled-$index"
                    CollapseItemData(
                        key = key,
                        title = "${text.controlledItemTitlePrefix} $index",
                    ) {
                        PText(
                            text = "${text.controlledItemContentPrefix} $index, ${text.controlledContentVersionPrefix} $contentVersion",
                        )
                    }
                }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        PButton(
                            text = text.toggleFirstButtonText,
                            size = ButtonSize.SMALL,
                            type = ButtonType.PLAIN,
                        ) {
                            val firstKey = "controlled-1"
                            setControlledKeys(
                                if (firstKey in controlledKeys) {
                                    controlledKeys - firstKey
                                } else {
                                    controlledKeys + firstKey
                                },
                            )
                        }
                        PButton(
                            text = text.expandAllButtonText,
                            size = ButtonSize.SMALL,
                            type = ButtonType.PLAIN,
                        ) {
                            setControlledKeys((1..itemCount).map { "controlled-$it" }.toSet())
                        }
                        PButton(
                            text = text.collapseAllButtonText,
                            size = ButtonSize.SMALL,
                            type = ButtonType.PLAIN,
                        ) {
                            setControlledKeys(emptySet())
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        PButton(
                            text = text.updateContentButtonText,
                            size = ButtonSize.SMALL,
                            type = ButtonType.PLAIN,
                        ) {
                            setContentVersion(contentVersion + 1)
                        }
                        PButton(
                            text = text.addItemButtonText,
                            size = ButtonSize.SMALL,
                            type = ButtonType.PRIMARY,
                        ) {
                            val nextIndex = itemCount + 1
                            setItemCount(nextIndex)
                            setControlledKeys(controlledKeys + "controlled-$nextIndex")
                        }
                    }
                }

                PCollapse(
                    items = controlledItems,
                    expandedKeys = controlledKeys,
                    onExpandChange = { setControlledKeys(it) },
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        DemoSection(title = text.uncontrolledSectionTitle) {
            PCollapse(
                items = items,
                defaultExpandedKeys = setOf("1"),
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
                controlledSectionTitle = "外部控制",
                uncontrolledSectionTitle = "默认展开",
                toggleFirstButtonText = "切换第一项",
                expandAllButtonText = "全部展开",
                collapseAllButtonText = "全部收起",
                updateContentButtonText = "修改内容",
                addItemButtonText = "添加项",
                controlledItemTitlePrefix = "可控面板",
                controlledItemContentPrefix = "这是可由外部状态更新的第",
                controlledContentVersionPrefix = "内容版本",
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
                controlledSectionTitle = "Controlled",
                uncontrolledSectionTitle = "Default Expanded",
                toggleFirstButtonText = "Toggle First",
                expandAllButtonText = "Expand All",
                collapseAllButtonText = "Collapse All",
                updateContentButtonText = "Update Content",
                addItemButtonText = "Add Item",
                controlledItemTitlePrefix = "Controlled Panel",
                controlledItemContentPrefix = "Externally updated content for item",
                controlledContentVersionPrefix = "version",
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
    val controlledSectionTitle: String,
    val uncontrolledSectionTitle: String,
    val toggleFirstButtonText: String,
    val expandAllButtonText: String,
    val collapseAllButtonText: String,
    val updateContentButtonText: String,
    val addItemButtonText: String,
    val controlledItemTitlePrefix: String,
    val controlledItemContentPrefix: String,
    val controlledContentVersionPrefix: String,
    val consistencyTitle: String,
    val consistencyContent: String,
    val feedbackTitle: String,
    val feedbackContent: String,
    val efficiencyTitle: String,
    val efficiencyContent: String,
)
