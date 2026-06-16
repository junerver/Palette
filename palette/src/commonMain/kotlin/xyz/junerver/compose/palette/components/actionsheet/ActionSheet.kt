package xyz.junerver.compose.palette.components.actionsheet

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import xyz.junerver.compose.hooks.useCreation
import xyz.junerver.compose.palette.components.popup.PPopup
import xyz.junerver.compose.palette.components.text.PText
import xyz.junerver.compose.palette.core.theme.PaletteTheme

data class ActionSheetItem(
    val label: String,
    val description: String? = null,
    val color: Color? = null,
    val disabled: Boolean = false,
    val icon: (@Composable (() -> Unit))? = null
)

@Composable
fun PActionSheet(
    visible: Boolean,
    options: List<ActionSheetItem>,
    onDismiss: () -> Unit,
    onItemClick: (index: Int) -> Unit,
    title: String? = null,
    cancelText: String = "取消"
) {
    PPopup(
        visible = visible,
        onClose = onDismiss,
        draggable = false
    ) {
        Column {
            title?.let {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(ActionSheetDefaults.TitleHeight)
                        .padding(ActionSheetDefaults.ItemPadding),
                    contentAlignment = Alignment.Center
                ) {
                    PText(
                        text = it,
                        color = ActionSheetDefaults.titleColor(),
                        fontSize = ActionSheetDefaults.TitleFontSize
                    )
                }
            }

            options.forEachIndexed { index, item ->
                if (index > 0 || title != null) {
                    HorizontalDivider(
                        color = ActionSheetDefaults.dividerColor(),
                        thickness = 0.5.dp
                    )
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(ActionSheetDefaults.ItemHeight)
                        .alpha(if (item.disabled) ActionSheetDefaults.DisabledAlpha else 1f)
                        .then(
                            if (!item.disabled) {
                                Modifier.clickable {
                                    onDismiss()
                                    onItemClick(index)
                                }
                            } else {
                                Modifier
                            }
                        )
                        .padding(ActionSheetDefaults.ItemPadding),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    if (item.icon != null) {
                        item.icon.invoke()
                        Spacer(modifier = Modifier.width(ActionSheetDefaults.IconSpacing))
                    }
                    PText(
                        text = item.label,
                        color = item.color ?: ActionSheetDefaults.itemTextColor(),
                        fontSize = ActionSheetDefaults.ItemFontSize
                    )
                    item.description?.let {
                        PText(
                            text = it,
                            color = ActionSheetDefaults.descriptionColor(),
                            fontSize = ActionSheetDefaults.DescriptionFontSize
                        )
                    }
                }
            }

            Spacer(
                modifier = Modifier
                    .height(ActionSheetDefaults.GapHeight)
                    .fillMaxWidth()
                    .background(ActionSheetDefaults.dividerColor())
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(ActionSheetDefaults.CancelHeight)
                    .clickable { onDismiss() },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                PText(
                    text = cancelText,
                    color = ActionSheetDefaults.cancelTextColor(),
                    fontSize = ActionSheetDefaults.CancelFontSize
                )
            }
        }
    }
}

@Stable
interface ActionSheetState {
    val visible: Boolean
    fun show(
        options: List<ActionSheetItem>,
        title: String? = null,
        onItemClick: (index: Int) -> Unit
    )
    fun hide()
}

@Composable
fun rememberActionSheetState(): ActionSheetState {
    val state = useCreation { ActionSheetStateImpl() }.current

    if (state.visible) {
        state.props?.let { props ->
            PActionSheet(
                visible = state.visible,
                options = props.options,
                onDismiss = { state.hide() },
                onItemClick = props.onItemClick,
                title = props.title
            )
        }
    }

    return state
}

private class ActionSheetStateImpl : ActionSheetState {
    override var visible by mutableStateOf(false)
    var props by mutableStateOf<ActionSheetProps?>(null)
        private set

    override fun show(
        options: List<ActionSheetItem>,
        title: String?,
        onItemClick: (index: Int) -> Unit
    ) {
        props = ActionSheetProps(options, title, onItemClick)
        visible = true
    }

    override fun hide() {
        visible = false
    }
}

private data class ActionSheetProps(
    val options: List<ActionSheetItem>,
    val title: String? = null,
    val onItemClick: (index: Int) -> Unit
)
