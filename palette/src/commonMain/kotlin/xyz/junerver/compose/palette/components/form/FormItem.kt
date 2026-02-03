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
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.palette.core.theme.PaletteTheme

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
                        .width(FormDefaults.LabelWidth)
                        .padding(top = 6.dp) // Align with common input height
                        .padding(end = FormDefaults.LabelInputSpacing),
                    horizontalArrangement = Arrangement.End
                ) {
                    if (required) {
                        Text(
                            text = "* ",
                            color = PaletteTheme.colors.error,
                            fontSize = FormDefaults.LabelFontSize
                        )
                    }
                    Text(
                        text = "$label :",
                        color = FormDefaults.labelColor,
                        fontSize = FormDefaults.LabelFontSize,
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
                    modifier = Modifier.padding(bottom = FormDefaults.LabelInputSpacing),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (required) {
                        Text(
                            text = "* ",
                            color = PaletteTheme.colors.error,
                            fontSize = FormDefaults.LabelFontSize
                        )
                    }
                    Text(
                        text = label,
                        color = FormDefaults.labelColor,
                        fontSize = FormDefaults.LabelFontSize
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
            modifier = Modifier.padding(top = FormDefaults.InputHelperSpacing)
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
                        fontSize = FormDefaults.HelperFontSize
                    )
                }
            }
            
            if (error == null && help != null) {
                Text(
                    text = help,
                    color = FormDefaults.helpColor,
                    fontSize = FormDefaults.HelperFontSize
                )
            }
        }
    }
}
