package xyz.junerver.compose.palette.components.form

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign

@Composable
internal fun PFormItem(
    label: String,
    required: Boolean,
    error: String?,
    help: String?,
    layout: FormLayout,
    labelPosition: FormLabelPosition,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val isHorizontal = layout == FormLayout.Horizontal && labelPosition == FormLabelPosition.Left
    
     if (isHorizontal) {
         Row(
             modifier = modifier.fillMaxWidth(),
             verticalAlignment = Alignment.Top
         ) {
            // Label Column
            if (label.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .width(FormDefaults.labelWidth())
                        .padding(top = FormDefaults.labelTopPadding())
                        .padding(end = FormDefaults.labelInputSpacing()),
                    horizontalArrangement = Arrangement.End
                ) {
                    if (required) {
                        Text(
                            text = "* ",
                            color = FormDefaults.requiredColor,
                            style = FormDefaults.labelTextStyle()
                        )
                    }
                    Text(
                        text = "$label :",
                        color = FormDefaults.labelColor,
                        style = FormDefaults.labelTextStyle(),
                        textAlign = TextAlign.End
                    )
                }
            }
            
            // Input Column
            Column(modifier = Modifier.weight(1f)) {
                content()
                FormItemErrorAndHelp(error = error, help = help)
            }
        }
    } else {
        Column(modifier = modifier.fillMaxWidth()) {
            // Label Row
            if (label.isNotEmpty()) {
                Row(
                    modifier = Modifier.padding(bottom = FormDefaults.labelInputSpacing()),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (required) {
                        Text(
                            text = "* ",
                            color = FormDefaults.requiredColor,
                            style = FormDefaults.labelTextStyle()
                        )
                    }
                    Text(
                        text = label,
                        color = FormDefaults.labelColor,
                        style = FormDefaults.labelTextStyle()
                    )
                }
            }
            
            // Input Content
            content()
            FormItemErrorAndHelp(error = error, help = help)
        }
    }
}

@Composable
private fun FormItemErrorAndHelp(
    error: String?,
    help: String?
) {
    if (error != null || help != null) {
        Column(
            modifier = Modifier.padding(top = FormDefaults.inputHelperSpacing())
        ) {
            AnimatedVisibility(
                visible = error != null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                if (error != null) {
                    Text(
                        text = error,
                        color = FormDefaults.errorColor,
                        style = FormDefaults.helperTextStyle()
                    )
                }
            }
            
            if (error == null && help != null) {
                Text(
                    text = help,
                    color = FormDefaults.helpColor,
                    style = FormDefaults.helperTextStyle()
                )
            }
        }
    }
}
