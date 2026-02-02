package xyz.junerver.compose.palette.components.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import xyz.junerver.compose.palette.core.theme.PaletteTheme

@Composable
fun PDialog(
    title: String,
    content: String? = null,
    okText: String = "确定",
    cancelText: String = "取消",
    okColor: Color = DialogDefaults.okColor(),
    onOk: () -> Unit,
    onCancel: (() -> Unit)? = null,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(DialogDefaults.BorderRadius))
                .fillMaxWidth(0.8f)
                .background(PaletteTheme.colors.surface)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            top = DialogDefaults.TitlePaddingTop,
                            bottom = if (content != null) DialogDefaults.TitlePaddingBottom else 0.dp,
                            start = DialogDefaults.HorizontalPadding,
                            end = DialogDefaults.HorizontalPadding
                        ),
                    color = PaletteTheme.colors.onSurface,
                    fontSize = DialogDefaults.TitleFontSize,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                if (content != null) {
                    Text(
                        text = content,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = DialogDefaults.HorizontalPadding),
                        color = PaletteTheme.colors.onSurface,
                        fontSize = DialogDefaults.ContentFontSize,
                        textAlign = TextAlign.Center
                    )
                }
                Spacer(modifier = Modifier.height(DialogDefaults.ContentPaddingBottom))
                HorizontalDivider(color = PaletteTheme.colors.border)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if (onCancel != null) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(DialogDefaults.ButtonHeight)
                                .clickable(onClick = onCancel),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = cancelText,
                                color = PaletteTheme.colors.onSurface,
                                fontSize = DialogDefaults.ButtonFontSize,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(DialogDefaults.DividerWidth, DialogDefaults.ButtonHeight)
                                .background(PaletteTheme.colors.border)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(DialogDefaults.ButtonHeight)
                            .clickable(onClick = onOk),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = okText,
                            color = okColor,
                            fontSize = DialogDefaults.ButtonFontSize,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Stable
interface DialogState {
    val visible: Boolean
    fun show(
        title: String,
        content: String? = null,
        okText: String = "确定",
        cancelText: String = "取消",
        okColor: Color = Color.Unspecified,
        closeOnAction: Boolean = true,
        onCancel: (() -> Unit)? = {},
        onOk: (() -> Unit)? = null
    )
    fun hide()
}

@Composable
fun rememberDialogState(): DialogState {
    val state = remember { DialogStateImpl() }

    if (state.visible) {
        state.props?.let { props ->
            PDialog(
                title = props.title,
                content = props.content,
                okText = props.okText,
                cancelText = props.cancelText,
                okColor = if (props.okColor == Color.Unspecified) DialogDefaults.okColor() else props.okColor,
                onOk = {
                    props.onOk?.invoke()
                    if (props.closeOnAction) {
                        state.hide()
                    }
                },
                onCancel = if (props.onCancel != null) {
                    {
                        props.onCancel.invoke()
                        if (props.closeOnAction) {
                            state.hide()
                        }
                    }
                } else null,
                onDismiss = {
                    state.hide()
                }
            )
        }
    }

    return state
}

private class DialogStateImpl : DialogState {
    override var visible by mutableStateOf(false)
    var props by mutableStateOf<DialogProps?>(null)
        private set

    override fun show(
        title: String,
        content: String?,
        okText: String,
        cancelText: String,
        okColor: Color,
        closeOnAction: Boolean,
        onCancel: (() -> Unit)?,
        onOk: (() -> Unit)?
    ) {
        props = DialogProps(
            title,
            content,
            okText,
            cancelText,
            okColor,
            closeOnAction,
            onCancel,
            onOk
        )
        visible = true
    }

    override fun hide() {
        visible = false
    }
}

private data class DialogProps(
    val title: String,
    val content: String?,
    val okText: String,
    val cancelText: String,
    val okColor: Color,
    val closeOnAction: Boolean,
    val onCancel: (() -> Unit)?,
    val onOk: (() -> Unit)?
)
