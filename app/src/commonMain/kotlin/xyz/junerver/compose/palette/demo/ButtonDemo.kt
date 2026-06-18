package xyz.junerver.compose.palette.demo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.Language
import xyz.junerver.compose.palette.LocalLanguage
import xyz.junerver.compose.palette.components.CodeBlock
import xyz.junerver.compose.palette.components.button.ButtonColors
import xyz.junerver.compose.palette.components.button.ButtonSize
import xyz.junerver.compose.palette.components.button.ButtonType
import xyz.junerver.compose.palette.components.button.PButton
import xyz.junerver.compose.palette.components.text.PText

@Composable
fun ButtonDemo() {
    val text = buttonDemoText()

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

        DemoSection(title = text.typeSectionTitle) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                PButton(text = text.primaryButtonText, type = ButtonType.PRIMARY) {}
                PButton(text = text.dangerButtonText, type = ButtonType.DANGER) {}
                PButton(text = text.plainButtonText, type = ButtonType.PLAIN) {}
                PButton(text = text.outlinedButtonText, type = ButtonType.OUTLINED) {}
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = text.sizeSectionTitle) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                PButton(text = text.largeButtonText, size = ButtonSize.LARGE) {}
                PButton(text = text.mediumButtonText, size = ButtonSize.MEDIUM) {}
                PButton(text = text.smallButtonText, size = ButtonSize.SMALL) {}
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = text.iconSectionTitle) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                PButton(
                    text = text.primaryButtonText,
                    leadingIcon = {
                        Icon(Icons.Default.Check, contentDescription = null)
                    },
                ) {}
                PButton(
                    text = text.nextButtonText,
                    type = ButtonType.OUTLINED,
                    trailingIcon = {
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
                    },
                ) {}
                PButton(
                    text = text.customButtonText,
                    colors = ButtonColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                    ),
                ) {}
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = text.disabledSectionTitle) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                PButton(text = text.disabledButtonText, disabled = true) {}
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = text.loadingSectionTitle) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                var loading by remember { mutableStateOf(false) }
                PButton(
                    text = if (loading) text.loadingText else text.clickToLoadText,
                    loading = loading,
                    onClick = {
                        loading = true
                    },
                )
                PButton(text = text.resetButtonText) {
                    loading = false
                }
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
private fun buttonDemoText(): ButtonDemoText =
    when (LocalLanguage.current) {
        Language.ZH_CN ->
            ButtonDemoText(
                title = "Button",
                subtitle = "按钮组件",
                typeSectionTitle = "按钮类型",
                primaryButtonText = "Primary Button",
                dangerButtonText = "Danger Button",
                plainButtonText = "Plain Button",
                outlinedButtonText = "Outlined Button",
                sizeSectionTitle = "按钮尺寸",
                largeButtonText = "Large Button",
                mediumButtonText = "Medium Button",
                smallButtonText = "Small Button",
                iconSectionTitle = "图标与自定义颜色",
                nextButtonText = "Next",
                customButtonText = "Custom Button",
                disabledSectionTitle = "禁用状态",
                disabledButtonText = "Disabled Button",
                loadingSectionTitle = "加载状态",
                loadingText = "Loading...",
                clickToLoadText = "Click to Load",
                resetButtonText = "Reset",
                codeTitle = "代码示例",
                codeBlock =
                    """
                    PButton(
                        text = "Primary Button",
                        type = ButtonType.PRIMARY,
                        size = ButtonSize.LARGE,
                        leadingIcon = {
                            Icon(Icons.Default.Check, contentDescription = null)
                        },
                        onClick = { /* 处理点击 */ }
                    )
                    """.trimIndent(),
            )

        Language.EN_US ->
            ButtonDemoText(
                title = "Button",
                subtitle = "Button component.",
                typeSectionTitle = "Button Types",
                primaryButtonText = "Primary Button",
                dangerButtonText = "Danger Button",
                plainButtonText = "Plain Button",
                outlinedButtonText = "Outlined Button",
                sizeSectionTitle = "Button Sizes",
                largeButtonText = "Large Button",
                mediumButtonText = "Medium Button",
                smallButtonText = "Small Button",
                iconSectionTitle = "Icons and Custom Colors",
                nextButtonText = "Next",
                customButtonText = "Custom Button",
                disabledSectionTitle = "Disabled State",
                disabledButtonText = "Disabled Button",
                loadingSectionTitle = "Loading State",
                loadingText = "Loading...",
                clickToLoadText = "Click to Load",
                resetButtonText = "Reset",
                codeTitle = "Code Example",
                codeBlock =
                    """
                    PButton(
                        text = "Primary Button",
                        type = ButtonType.PRIMARY,
                        size = ButtonSize.LARGE,
                        leadingIcon = {
                            Icon(Icons.Default.Check, contentDescription = null)
                        },
                        onClick = { /* Handle click */ }
                    )
                    """.trimIndent(),
            )
    }

private data class ButtonDemoText(
    val title: String,
    val subtitle: String,
    val typeSectionTitle: String,
    val primaryButtonText: String,
    val dangerButtonText: String,
    val plainButtonText: String,
    val outlinedButtonText: String,
    val sizeSectionTitle: String,
    val largeButtonText: String,
    val mediumButtonText: String,
    val smallButtonText: String,
    val iconSectionTitle: String,
    val nextButtonText: String,
    val customButtonText: String,
    val disabledSectionTitle: String,
    val disabledButtonText: String,
    val loadingSectionTitle: String,
    val loadingText: String,
    val clickToLoadText: String,
    val resetButtonText: String,
    val codeTitle: String,
    val codeBlock: String,
)
