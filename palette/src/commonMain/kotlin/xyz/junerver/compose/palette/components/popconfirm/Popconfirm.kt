package xyz.junerver.compose.palette.components.popconfirm

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.hooks.useState
import xyz.junerver.compose.palette.components.alert.AlertType
import xyz.junerver.compose.palette.components.button.ButtonSize
import xyz.junerver.compose.palette.components.button.ButtonType
import xyz.junerver.compose.palette.components.button.PButton
import xyz.junerver.compose.palette.components.popover.PPopover
import xyz.junerver.compose.palette.components.text.PText

@Composable
fun PPopconfirm(
    title: String,
    description: String? = null,
    onConfirm: () -> Unit,
    onCancel: (() -> Unit)? = null,
    okText: String = "确定",
    cancelText: String = "取消",
    okType: AlertType = AlertType.Error,
    modifier: Modifier = Modifier,
    trigger: @Composable () -> Unit,
) {
    val (visible, setVisible) = useState(false)

    PPopover(
        modifier = modifier,
        visible = visible,
        onVisibleChange = { setVisible(it) },
        trigger = { trigger() },
        content = {
            Column {
                PText(
                    text = title,
                    color = PopconfirmDefaults.titleColor(),
                    fontSize = PopconfirmDefaults.TitleFontSize,
                    fontWeight = FontWeight.Bold
                )
                if (description != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    PText(
                        text = description,
                        color = PopconfirmDefaults.descriptionColor(),
                        fontSize = PopconfirmDefaults.DescriptionFontSize
                    )
                }
                Spacer(modifier = Modifier.height(PopconfirmDefaults.ButtonSpacing))
                Row {
                    PButton(
                        text = cancelText,
                        type = ButtonType.PLAIN,
                        size = ButtonSize.SMALL,
                        onClick = {
                            setVisible(false)
                            onCancel?.invoke()
                        }
                    )
                    Spacer(modifier = Modifier.width(PopconfirmDefaults.ButtonSpacing))
                    PButton(
                        text = okText,
                        type = okType.toButtonType(),
                        size = ButtonSize.SMALL,
                        onClick = {
                            setVisible(false)
                            onConfirm()
                        }
                    )
                }
            }
        }
    )
}

private fun AlertType.toButtonType(): ButtonType = when (this) {
    AlertType.Error -> ButtonType.DANGER
    AlertType.Warning -> ButtonType.DANGER
    AlertType.Info -> ButtonType.PRIMARY
    AlertType.Success -> ButtonType.PRIMARY
}
