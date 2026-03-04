package xyz.junerver.compose.palette.demo

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import xyz.junerver.compose.palette.components.text.PText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.Language
import xyz.junerver.compose.palette.LocalLanguage
import xyz.junerver.compose.palette.components.CodeBlock
import xyz.junerver.compose.palette.components.rate.PRate

@Composable
fun RateDemo() {
    val text = rateDemoText()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        PText(
            text = text.title,
            style = MaterialTheme.typography.headlineMedium
        )
        PText(
            text = text.subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        DemoSection(title = text.basicSectionTitle) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                var rating1 by remember { mutableFloatStateOf(3f) }
                PRate(
                    value = rating1,
                    onChange = { rating1 = it }
                )
                Spacer(modifier = Modifier.height(8.dp))
                PText(
                    text = "${text.ratingPrefix}$rating1",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = text.halfSectionTitle) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                var rating2 by remember { mutableFloatStateOf(3.5f) }
                PRate(
                    value = rating2,
                    onChange = { rating2 = it },
                    allowHalf = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                PText(
                    text = "${text.ratingPrefix}$rating2",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = text.countSectionTitle) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                var rating3 by remember { mutableFloatStateOf(5f) }
                PRate(
                    value = rating3,
                    onChange = { rating3 = it },
                    count = 10
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = text.readonlySectionTitle) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                PRate(
                    value = 4f,
                    onChange = null
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        PText(
            text = text.codeTitle,
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        CodeBlock(
            code = text.codeBlock
        )
    }
}

@Composable
@ReadOnlyComposable
private fun rateDemoText(): RateDemoText = when (LocalLanguage.current) {
    Language.ZH_CN -> RateDemoText(
        title = "Rate",
        subtitle = "评分组件",
        basicSectionTitle = "基础用法",
        ratingPrefix = "评分: ",
        halfSectionTitle = "半星评分",
        countSectionTitle = "自定义数量",
        readonlySectionTitle = "只读模式",
        codeTitle = "代码示例",
        codeBlock = """
var rating by remember { mutableFloatStateOf(3f) }
PRate(
    value = rating,
    onChange = { rating = it },
    count = 5,
    allowHalf = true
)
        """.trimIndent(),
    )

    Language.EN_US -> RateDemoText(
        title = "Rate",
        subtitle = "Rating component.",
        basicSectionTitle = "Basic Usage",
        ratingPrefix = "Rating: ",
        halfSectionTitle = "Half-Star Rating",
        countSectionTitle = "Custom Count",
        readonlySectionTitle = "Read-Only Mode",
        codeTitle = "Code Example",
        codeBlock = """
var rating by remember { mutableFloatStateOf(3f) }
PRate(
    value = rating,
    onChange = { rating = it },
    count = 5,
    allowHalf = true
)
        """.trimIndent(),
    )
}

private data class RateDemoText(
    val title: String,
    val subtitle: String,
    val basicSectionTitle: String,
    val ratingPrefix: String,
    val halfSectionTitle: String,
    val countSectionTitle: String,
    val readonlySectionTitle: String,
    val codeTitle: String,
    val codeBlock: String,
)
