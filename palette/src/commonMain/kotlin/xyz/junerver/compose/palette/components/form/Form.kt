package xyz.junerver.compose.palette.components.form

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import xyz.junerver.compose.hooks.useform.Form
import xyz.junerver.compose.hooks.useform.FormInstance
import xyz.junerver.compose.hooks.useform.FormScope

val LocalFormLayout = staticCompositionLocalOf { FormDefaults.layout }
val LocalFormLabelPosition = staticCompositionLocalOf { FormDefaults.labelPosition }

@Composable
fun PForm(
    formInstance: FormInstance,
    modifier: Modifier = Modifier,
    layout: FormLayout = FormDefaults.layout,
    labelPosition: FormLabelPosition = FormDefaults.labelPosition,
    onSubmit: (Map<String, Any?>) -> Unit = {},
    content: @Composable FormScope.() -> Unit
) {
    CompositionLocalProvider(
        LocalFormLayout provides layout,
        LocalFormLabelPosition provides labelPosition
    ) {
        Form(
            formInstance = formInstance,
            onSubmit = onSubmit,
        ) {
            Column(
                modifier = modifier,
                verticalArrangement = Arrangement.spacedBy(FormDefaults.ItemSpacing)
            ) {
                content()
            }
        }
    }
}
