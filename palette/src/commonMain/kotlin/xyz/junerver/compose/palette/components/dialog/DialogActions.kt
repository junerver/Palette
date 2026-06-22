package xyz.junerver.compose.palette.components.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import xyz.junerver.compose.palette.components.text.PText

@Composable
fun RowScope.PDialogAction(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = DialogDefaults.okColor(),
    enabled: Boolean = true,
    disabledColor: Color = DialogDefaults.cancelColor().copy(alpha = 0.38f),
) {
    Box(
        modifier = modifier
            .weight(1f)
            .height(DialogDefaults.buttonHeight())
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        PText(
            text = text,
            color = if (enabled) color else disabledColor,
            style = DialogDefaults.buttonTextStyle(),
        )
    }
}

@Composable
fun RowScope.PDialogCancelAction(
    onClick: () -> Unit,
    text: String = "取消",
    enabled: Boolean = true,
) {
    PDialogAction(
        text = text,
        color = DialogDefaults.cancelColor(),
        enabled = enabled,
        onClick = onClick,
    )
}

@Composable
fun RowScope.PDialogConfirmAction(
    onClick: () -> Unit,
    text: String = "确定",
    color: Color = DialogDefaults.okColor(),
    enabled: Boolean = true,
) {
    PDialogAction(
        text = text,
        color = color,
        enabled = enabled,
        onClick = onClick,
    )
}

@Composable
fun RowScope.PDialogActionDivider() {
    Box(
        modifier = Modifier
            .size(DialogDefaults.dividerWidth(), DialogDefaults.buttonHeight())
            .background(DialogDefaults.dividerColor()),
    )
}
