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
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.Language
import xyz.junerver.compose.palette.LocalLanguage
import xyz.junerver.compose.palette.components.screen.Screen
import xyz.junerver.compose.palette.components.screen.ScreenDefaults
import xyz.junerver.compose.palette.components.text.PText

@Composable
fun ScreenDemo() {
    val text = screenDemoText()

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
            Screen(title = text.screenTitle) {
                Text(text = text.contentAreaText)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = text.customColorSectionTitle) {
            Screen(
                title = text.customBackgroundTitle,
                colors =
                    ScreenDefaults.colors(
                        backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    ),
            ) {
                Text(text = text.customBackgroundContent)
            }
        }
    }
}

@Composable
@ReadOnlyComposable
private fun screenDemoText(): ScreenDemoText =
    when (LocalLanguage.current) {
        Language.ZH_CN ->
            ScreenDemoText(
                title = "Screen",
                subtitle = "页面容器组件",
                basicSectionTitle = "基础用法",
                screenTitle = "页面标题",
                contentAreaText = "内容区域",
                customColorSectionTitle = "自定义颜色",
                customBackgroundTitle = "带自定义背景",
                customBackgroundContent = "自定义背景颜色",
            )

        Language.EN_US ->
            ScreenDemoText(
                title = "Screen",
                subtitle = "Screen container component.",
                basicSectionTitle = "Basic Usage",
                screenTitle = "Page Title",
                contentAreaText = "Content Area",
                customColorSectionTitle = "Custom Colors",
                customBackgroundTitle = "Custom Background",
                customBackgroundContent = "Custom background color",
            )
    }

private data class ScreenDemoText(
    val title: String,
    val subtitle: String,
    val basicSectionTitle: String,
    val screenTitle: String,
    val contentAreaText: String,
    val customColorSectionTitle: String,
    val customBackgroundTitle: String,
    val customBackgroundContent: String,
)
