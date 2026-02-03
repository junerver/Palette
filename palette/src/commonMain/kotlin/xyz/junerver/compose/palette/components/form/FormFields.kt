package xyz.junerver.compose.palette.components.form

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import xyz.junerver.compose.hooks.useform.FormScope
import xyz.junerver.compose.hooks.useform.Validator
import xyz.junerver.compose.hooks.useform.FormItemState
import xyz.junerver.compose.palette.components.textfield.BorderTextField

/**
 * Form text field with validation
 */
@Composable
fun FormScope.PFormTextField(
    name: String,
    modifier: Modifier = Modifier,
    label: String = "",
    placeholder: String = "",
    help: String? = null,
    vararg validators: Validator
) {
    FormItemWithState<String>(name = name, validators = validators) { state ->
        var value by state.value
        
        // Check if Required validator is present
        val isRequired = validators.any { it::class.simpleName?.contains("Required") == true }
        val showError = state.isTouched && !state.isValid
        val errorMessage = if (showError) state.errors.firstOrNull() else null
        
        val layout = LocalFormLayout.current
        val labelPosition = LocalFormLabelPosition.current
        
        PFormItem(
            label = label,
            required = isRequired,
            error = errorMessage,
            help = help,
            layout = layout,
            labelPosition = labelPosition,
            modifier = modifier
        ) {
            BorderTextField(
                value = value ?: "",
                onValueChange = { value = it },
                hint = placeholder
            )
        }
    }
}
