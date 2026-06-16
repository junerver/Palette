package xyz.junerver.compose.palette.demo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.Language
import xyz.junerver.compose.palette.LocalLanguage
import xyz.junerver.compose.palette.components.CodeBlock
import xyz.junerver.compose.palette.components.qrcode.PQRCode
import xyz.junerver.compose.palette.components.text.PText

@Composable
fun QRCodeDemo() {
    val text = qrCodeDemoText()

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
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                PQRCode(value = "https://github.com/junerver/Palette")
                PText(
                    text = "https://github.com/junerver/Palette",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = text.sizeSectionTitle) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    PQRCode(
                        value = "Small",
                        size = 80.dp,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    PText(
                        text = "80dp",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    PQRCode(
                        value = "Medium",
                        size = 120.dp,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    PText(
                        text = "120dp",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    PQRCode(
                        value = "Large",
                        size = 160.dp,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    PText(
                        text = "160dp",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = text.colorSectionTitle) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    PQRCode(
                        value = "Blue",
                        color = Color(0xFF1890FF),
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    PText(
                        text = text.blueText,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    PQRCode(
                        value = "Green",
                        color = Color(0xFF52C41A),
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    PText(
                        text = text.greenText,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    PQRCode(
                        value = "Red",
                        color = Color(0xFFFF4D4F),
                        backgroundColor = Color(0xFFFFF1F0),
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    PText(
                        text = text.redText,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
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
private fun qrCodeDemoText(): QRCodeDemoText =
    when (LocalLanguage.current) {
        Language.ZH_CN ->
            QRCodeDemoText(
                title = "PQRCode",
                subtitle = "生成二维码的组件",
                basicSectionTitle = "基础用法",
                sizeSectionTitle = "不同尺寸",
                colorSectionTitle = "自定义颜色",
                blueText = "蓝色",
                greenText = "绿色",
                redText = "红色背景",
                codeTitle = "代码示例",
                codeBlock =
                    """
                    PQRCode(
                        value = "https://example.com",
                        size = 120.dp,
                        color = Color.Black,
                        backgroundColor = Color.White,
                    )
                    """.trimIndent(),
            )

        Language.EN_US ->
            QRCodeDemoText(
                title = "PQRCode",
                subtitle = "A component that generates QR codes.",
                basicSectionTitle = "Basic Usage",
                sizeSectionTitle = "Different Sizes",
                colorSectionTitle = "Custom Colors",
                blueText = "Blue",
                greenText = "Green",
                redText = "Red Background",
                codeTitle = "Code Example",
                codeBlock =
                    """
                    PQRCode(
                        value = "https://example.com",
                        size = 120.dp,
                        color = Color.Black,
                        backgroundColor = Color.White,
                    )
                    """.trimIndent(),
            )
    }

private data class QRCodeDemoText(
    val title: String,
    val subtitle: String,
    val basicSectionTitle: String,
    val sizeSectionTitle: String,
    val colorSectionTitle: String,
    val blueText: String,
    val greenText: String,
    val redText: String,
    val codeTitle: String,
    val codeBlock: String,
)
