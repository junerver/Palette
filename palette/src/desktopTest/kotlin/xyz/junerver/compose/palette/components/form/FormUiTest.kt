package xyz.junerver.compose.palette.components.form

import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import xyz.junerver.compose.hooks.useform.Email
import xyz.junerver.compose.hooks.useform.Form
import xyz.junerver.compose.hooks.useform.FormInstance
import xyz.junerver.compose.hooks.useform.Required
import xyz.junerver.compose.hooks.useform.useForm
import xyz.junerver.compose.hooks.useform.useWatch
import xyz.junerver.compose.palette.core.theme.PaletteMaterialTheme
import kotlin.test.Test

class FormUiTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun form_shouldSubmitProgrammaticallyFilledValuesAcrossFieldTypes() {
        var form: FormInstance? = null
        var submitResult by mutableStateOf("Idle")

        rule.setContent {
            PaletteMaterialTheme {
                val instance =
                    with(Form) {
                        useForm()
                    }
                form = instance
                val email by
                    with(Form) {
                        useWatch<String>("email", instance)
                    }
                val agree by
                    with(Form) {
                        useWatch<Boolean>("agree", instance)
                    }
                val plan by
                    with(Form) {
                        useWatch<String>("plan", instance)
                    }
                val notify by
                    with(Form) {
                        useWatch<Boolean>("notify", instance)
                    }

                PForm(
                    formInstance = instance,
                    onSubmit = {
                        submitResult = "Submitted: ${it["email"]}|${it["agree"]}|${it["plan"]}|${it["notify"]}"
                    },
                ) {
                    PFormTextField(
                        name = "email",
                        label = "Email",
                        placeholder = "Enter email",
                        help = "We'll never share your email",
                        validators = arrayOf(Required(), Email()),
                    )
                    PFormCheckbox(
                        name = "agree",
                        label = "Agree",
                    )
                    PFormRadioGroup(
                        name = "plan",
                        label = "Plan",
                        options = listOf(
                            xyz.junerver.compose.palette.components.radio.RadioOption(label = "Starter", value = "starter"),
                            xyz.junerver.compose.palette.components.radio.RadioOption(label = "Pro", value = "pro"),
                        ),
                    )
                    PFormSwitch(
                        name = "notify",
                        label = "Notify",
                    )
                }

                Text("Watch email: ${email ?: "empty"}")
                Text("Watch agree: ${agree ?: false}")
                Text("Watch plan: ${plan ?: "empty"}")
                Text("Watch notify: ${notify ?: false}")
                Text(submitResult)
            }
        }

        rule.onNodeWithText("Email :").assertTextEquals("Email :")
        rule.onNodeWithText("We'll never share your email").assertTextEquals("We'll never share your email")
        rule.onNodeWithText("Watch email: empty").assertTextEquals("Watch email: empty")

        rule.runOnIdle {
            form!!.setFieldsValue(
                "email" to "dev@example.com",
                "agree" to true,
                "plan" to "pro",
                "notify" to true,
            )
        }

        rule.onNodeWithText("Watch email: dev@example.com").assertTextEquals("Watch email: dev@example.com")
        rule.onNodeWithText("Watch agree: true").assertTextEquals("Watch agree: true")
        rule.onNodeWithText("Watch plan: pro").assertTextEquals("Watch plan: pro")
        rule.onNodeWithText("Watch notify: true").assertTextEquals("Watch notify: true")

        rule.runOnIdle {
            form!!.submit(
                onSuccess = {
                    submitResult = "Submitted: ${it["email"]}|${it["agree"]}|${it["plan"]}|${it["notify"]}"
                },
                onError = {
                    submitResult = "Error: $it"
                },
            )
        }

        rule.onNodeWithText("Submitted: dev@example.com|true|pro|true").assertTextEquals("Submitted: dev@example.com|true|pro|true")
    }

    @Test
    fun form_shouldExposeValidationErrorsForInvalidSubmit() {
        var form: FormInstance? = null
        var submitResult by mutableStateOf("Idle")

        rule.setContent {
            PaletteMaterialTheme {
                val instance =
                    with(Form) {
                        useForm()
                    }
                form = instance

                PForm(formInstance = instance) {
                    PFormTextField(
                        name = "email",
                        label = "Email",
                        placeholder = "Enter email",
                        validators = arrayOf(Required(), Email()),
                    )
                }

                Text(submitResult)
            }
        }

        rule.runOnIdle {
            form!!.submit(
                onSuccess = { submitResult = "Submitted" },
                onError = { submitResult = it["email"]?.joinToString() ?: "No errors" },
            )
        }

        rule.onNodeWithText("this field is required").assertTextEquals("this field is required")
    }
}
