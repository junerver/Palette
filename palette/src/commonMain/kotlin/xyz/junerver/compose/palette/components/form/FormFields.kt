package xyz.junerver.compose.palette.components.form

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import xyz.junerver.compose.hooks.useform.FormScope
import xyz.junerver.compose.hooks.useform.Validator
import xyz.junerver.compose.hooks.useform.FormItemState
import xyz.junerver.compose.palette.components.textfield.BorderTextField
import xyz.junerver.compose.palette.components.checkbox.ColoredCheckBox
import xyz.junerver.compose.palette.components.radio.PRadio
import xyz.junerver.compose.palette.components.radio.RadioOption
import xyz.junerver.compose.palette.components.radio.PRadioGroup
import xyz.junerver.compose.palette.components.switch.PSwitch
import xyz.junerver.compose.palette.core.spec.ComponentSize
import xyz.junerver.compose.palette.core.spec.ComponentStatus

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
    size: ComponentSize = ComponentSize.Medium,
    vararg validators: Validator
) {
    FormItemWithState<String>(name = name, validators = validators) { state ->
        var value by state.value
        
        val isRequired = validators.any { it::class.simpleName?.contains("Required") == true }
        val showError = state.isTouched && !state.isValid
        val errorMessage = if (showError) state.errors.firstOrNull() else null
        
        val layout = LocalFormLayout.current
        val labelPosition = LocalFormLabelPosition.current
        
        val status = when {
            showError -> ComponentStatus.Error
            else -> ComponentStatus.Default
        }
        
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
                placeholder = placeholder,
                size = size,
                status = status
            )
        }
    }
}

/**
 * Form checkbox with validation
 */
@Composable
fun FormScope.PFormCheckbox(
    name: String,
    modifier: Modifier = Modifier,
    label: String = "",
    help: String? = null,
    size: ComponentSize = ComponentSize.Medium,
    vararg validators: Validator
) {
    FormItemWithState<Boolean>(name = name, validators = validators) { state ->
        var value by state.value
        
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
            ColoredCheckBox(
                checked = value ?: false,
                onCheckedChange = { value = it },
                size = size
            )
        }
    }
}

/**
 * Form radio group with validation
 */
@Composable
fun <T : Any> FormScope.PFormRadioGroup(
    name: String,
    options: List<RadioOption<T>>,
    modifier: Modifier = Modifier,
    label: String = "",
    help: String? = null,
    size: ComponentSize = ComponentSize.Medium,
    vararg validators: Validator
) {
    FormItemWithState<T>(name = name, validators = validators) { state ->
        var value by state.value
        
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
            PRadioGroup(
                options = options,
                value = value,
                size = size,
                onChange = { value = it }
            )
        }
    }
}

/**
 * Form switch with validation
 */
@Composable
fun FormScope.PFormSwitch(
    name: String,
    modifier: Modifier = Modifier,
    label: String = "",
    help: String? = null,
    vararg validators: Validator
) {
    FormItemWithState<Boolean>(name = name, validators = validators) { state ->
        var value by state.value
        
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
            PSwitch(
                checked = value ?: false,
                onChange = { value = it }
            )
        }
    }
}
