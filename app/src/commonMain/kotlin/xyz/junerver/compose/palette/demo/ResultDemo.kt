package xyz.junerver.compose.palette.demo

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.Language
import xyz.junerver.compose.palette.LocalLanguage
import xyz.junerver.compose.palette.components.CodeBlock
import xyz.junerver.compose.palette.components.button.ButtonType
import xyz.junerver.compose.palette.components.button.PButton
import xyz.junerver.compose.palette.components.result.PResult
import xyz.junerver.compose.palette.components.result.ResultStatus
import xyz.junerver.compose.palette.components.text.PText

@Composable
fun ResultDemo() {
    val text = resultDemoText()

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

        DemoSection(title = text.successSectionTitle) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                PResult(
                    status = ResultStatus.Success,
                    extra = {
                        PButton(text = text.backButtonText, type = ButtonType.PRIMARY) {}
                    },
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = text.errorSectionTitle) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                PResult(
                    status = ResultStatus.Error,
                    extra = {
                        PButton(text = text.retryButtonText, type = ButtonType.DANGER) {}
                    },
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = text.infoSectionTitle) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                PResult(status = ResultStatus.Info)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = text.notFoundSectionTitle) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                PResult(
                    status = ResultStatus.NotFound,
                    extra = {
                        PButton(text = text.backHomeButtonText, type = ButtonType.PRIMARY) {}
                    },
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
private fun resultDemoText(): ResultDemoText =
    when (LocalLanguage.current) {
        Language.ZH_CN ->
            ResultDemoText(
                title = "Result",
                subtitle = "结果展示组件",
                successSectionTitle = "成功",
                errorSectionTitle = "错误",
                infoSectionTitle = "信息",
                notFoundSectionTitle = "404",
                backButtonText = "返回",
                retryButtonText = "重试",
                backHomeButtonText = "回到首页",
                codeTitle = "代码示例",
                codeBlock =
                    """
                    PResult(
                        status = ResultStatus.Success,
                        extra = {
                            PButton(text = "返回") {}
                        },
                    )

                    PResult(status = ResultStatus.Error)
                    PResult(status = ResultStatus.NotFound)
                    """.trimIndent(),
            )

        Language.EN_US ->
            ResultDemoText(
                title = "Result",
                subtitle = "Result component.",
                successSectionTitle = "Success",
                errorSectionTitle = "Error",
                infoSectionTitle = "Info",
                notFoundSectionTitle = "404",
                backButtonText = "Back",
                retryButtonText = "Retry",
                backHomeButtonText = "Back Home",
                codeTitle = "Code Example",
                codeBlock =
                    """
                    PResult(
                        status = ResultStatus.Success,
                        extra = {
                            PButton(text = "Back") {}
                        },
                    )

                    PResult(status = ResultStatus.Error)
                    PResult(status = ResultStatus.NotFound)
                    """.trimIndent(),
            )
    }

private data class ResultDemoText(
    val title: String,
    val subtitle: String,
    val successSectionTitle: String,
    val errorSectionTitle: String,
    val infoSectionTitle: String,
    val notFoundSectionTitle: String,
    val backButtonText: String,
    val retryButtonText: String,
    val backHomeButtonText: String,
    val codeTitle: String,
    val codeBlock: String,
)
