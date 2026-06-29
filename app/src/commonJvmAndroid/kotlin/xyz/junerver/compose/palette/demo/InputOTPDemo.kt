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
import xyz.junerver.compose.hooks.useState
import xyz.junerver.compose.palette.Language
import xyz.junerver.compose.palette.LocalLanguage
import xyz.junerver.compose.palette.components.CodeBlock
import xyz.junerver.compose.palette.components.inputotp.PInputOTP
import xyz.junerver.compose.palette.components.text.PText

@Composable
fun InputOTPDemo() {
    val text = inputOTPDemoText()

    val (otp6, setOtp6) = useState("")
    val (otp4, setOtp4) = useState("")
    val (otpMasked, setOtpMasked) = useState("")

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
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                PInputOTP(
                    length = 6,
                    value = otp6,
                    onValueChange = { setOtp6(it) },
                )
                PText(text = "${text.valueText}: $otp6")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = text.fourDigitSectionTitle) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                PInputOTP(
                    length = 4,
                    value = otp4,
                    onValueChange = { setOtp4(it) },
                )
                PText(text = "${text.valueText}: $otp4")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        DemoSection(title = text.maskedSectionTitle) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                PInputOTP(
                    length = 6,
                    value = otpMasked,
                    onValueChange = { setOtpMasked(it) },
                    masked = true,
                )
                PText(text = "${text.valueText}: $otpMasked")
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
private fun inputOTPDemoText(): InputOTPDemoText =
    when (LocalLanguage.current) {
        Language.ZH_CN ->
            InputOTPDemoText(
                title = "InputOTP",
                subtitle = "验证码输入组件",
                basicSectionTitle = "基础用法（6 位）",
                fourDigitSectionTitle = "4 位验证码",
                maskedSectionTitle = "掩码输入",
                valueText = "当前值",
                codeTitle = "代码示例",
                codeBlock =
                    """
                    val (otp, setOtp) = useState("")

                    PInputOTP(
                        length = 6,
                        value = otp,
                        onValueChange = { setOtp(it) },
                    )

                    PInputOTP(
                        length = 4,
                        value = otp,
                        onValueChange = { setOtp(it) },
                        masked = true,
                    )
                    """.trimIndent(),
            )

        Language.EN_US ->
            InputOTPDemoText(
                title = "InputOTP",
                subtitle = "OTP input component.",
                basicSectionTitle = "Basic Usage (6 digits)",
                fourDigitSectionTitle = "4-digit OTP",
                maskedSectionTitle = "Masked Input",
                valueText = "Current value",
                codeTitle = "Code Example",
                codeBlock =
                    """
                    val (otp, setOtp) = useState("")

                    PInputOTP(
                        length = 6,
                        value = otp,
                        onValueChange = { setOtp(it) },
                    )

                    PInputOTP(
                        length = 4,
                        value = otp,
                        onValueChange = { setOtp(it) },
                        masked = true,
                    )
                    """.trimIndent(),
            )
    }

private data class InputOTPDemoText(
    val title: String,
    val subtitle: String,
    val basicSectionTitle: String,
    val fourDigitSectionTitle: String,
    val maskedSectionTitle: String,
    val valueText: String,
    val codeTitle: String,
    val codeBlock: String,
)
