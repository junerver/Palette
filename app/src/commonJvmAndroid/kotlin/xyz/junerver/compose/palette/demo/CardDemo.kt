package xyz.junerver.compose.palette.demo

import androidx.compose.foundation.layout.Column
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
import xyz.junerver.compose.palette.Language
import xyz.junerver.compose.palette.LocalLanguage
import xyz.junerver.compose.palette.components.CodeBlock
import xyz.junerver.compose.palette.components.card.CardVariant
import xyz.junerver.compose.palette.components.card.PCard
import xyz.junerver.compose.palette.components.text.PText

@Composable
fun CardDemo() {
    val text = cardDemoText()

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

        DemoSection(title = text.elevatedSectionTitle) {
            PCard(
                modifier = Modifier.fillMaxWidth(),
            ) {
                PText(
                    text = text.elevatedCardTitle,
                    style = MaterialTheme.typography.titleMedium,
                )
                Spacer(modifier = Modifier.height(8.dp))
                PText(
                    text = text.elevatedCardDescription,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = text.filledSectionTitle) {
            PCard(
                modifier = Modifier.fillMaxWidth(),
                variant = CardVariant.Filled,
            ) {
                PText(
                    text = text.filledCardTitle,
                    style = MaterialTheme.typography.titleMedium,
                )
                Spacer(modifier = Modifier.height(8.dp))
                PText(
                    text = text.filledCardDescription,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = text.outlinedSectionTitle) {
            PCard(
                modifier = Modifier.fillMaxWidth(),
                variant = CardVariant.Outlined,
            ) {
                PText(
                    text = text.outlinedCardTitle,
                    style = MaterialTheme.typography.titleMedium,
                )
                Spacer(modifier = Modifier.height(8.dp))
                PText(
                    text = text.outlinedCardDescription,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = text.clickableSectionTitle) {
            PCard(
                modifier = Modifier.fillMaxWidth(),
                onClick = { /* Handle click */ },
            ) {
                PText(
                    text = text.clickableCardTitle,
                    style = MaterialTheme.typography.titleMedium,
                )
                Spacer(modifier = Modifier.height(8.dp))
                PText(
                    text = text.clickableCardDescription,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        PText(
            text = text.codeTitle,
            style = MaterialTheme.typography.titleMedium,
        )

        Spacer(modifier = Modifier.height(16.dp))

        CodeBlock(
            code = text.codeBlock,
        )
    }
}

@Composable
@ReadOnlyComposable
private fun cardDemoText(): CardDemoText =
    when (LocalLanguage.current) {
        Language.ZH_CN ->
            CardDemoText(
                title = "Card",
                subtitle = "卡片组件",
                elevatedSectionTitle = "Elevated Card (Default)",
                elevatedCardTitle = "Elevated Card",
                elevatedCardDescription = "这是默认的卡片样式，具有阴影效果，适用于需要与背景分离的内容。",
                filledSectionTitle = "Filled Card",
                filledCardTitle = "Filled Card",
                filledCardDescription = "填充样式的卡片，使用表面颜色填充，视觉层级较低。",
                outlinedSectionTitle = "Outlined Card",
                outlinedCardTitle = "Outlined Card",
                outlinedCardDescription = "描边样式的卡片，带有边框但没有阴影，适用于边界清晰的内容区域。",
                clickableSectionTitle = "Clickable Card",
                clickableCardTitle = "Click Me",
                clickableCardDescription = "这是一个可点击的卡片，具有点击涟漪效果。",
                codeTitle = "代码示例",
                codeBlock =
                    """
                    PCard(
                        modifier = Modifier.fillMaxWidth(),
                        variant = CardVariant.Elevated,
                        onClick = { /* optional */ }
                    ) {
                        Text(text = "Card Title")
                        Text(text = "Card content goes here...")
                    }
                    """.trimIndent(),
            )

        Language.EN_US ->
            CardDemoText(
                title = "Card",
                subtitle = "Card component.",
                elevatedSectionTitle = "Elevated Card (Default)",
                elevatedCardTitle = "Elevated Card",
                elevatedCardDescription = "Default card style with shadow, suitable for separated content.",
                filledSectionTitle = "Filled Card",
                filledCardTitle = "Filled Card",
                filledCardDescription = "Filled card style with lower visual hierarchy.",
                outlinedSectionTitle = "Outlined Card",
                outlinedCardTitle = "Outlined Card",
                outlinedCardDescription = "Outlined style with border and no shadow for clear boundaries.",
                clickableSectionTitle = "Clickable Card",
                clickableCardTitle = "Click Me",
                clickableCardDescription = "A clickable card with ripple feedback.",
                codeTitle = "Code Example",
                codeBlock =
                    """
                    PCard(
                        modifier = Modifier.fillMaxWidth(),
                        variant = CardVariant.Elevated,
                        onClick = { /* optional */ }
                    ) {
                        Text(text = "Card Title")
                        Text(text = "Card content goes here...")
                    }
                    """.trimIndent(),
            )
    }

private data class CardDemoText(
    val title: String,
    val subtitle: String,
    val elevatedSectionTitle: String,
    val elevatedCardTitle: String,
    val elevatedCardDescription: String,
    val filledSectionTitle: String,
    val filledCardTitle: String,
    val filledCardDescription: String,
    val outlinedSectionTitle: String,
    val outlinedCardTitle: String,
    val outlinedCardDescription: String,
    val clickableSectionTitle: String,
    val clickableCardTitle: String,
    val clickableCardDescription: String,
    val codeTitle: String,
    val codeBlock: String,
)
