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
import xyz.junerver.compose.hooks.useCreation
import xyz.junerver.compose.palette.components.popup.PPopup
import xyz.junerver.compose.palette.components.text.PText

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
    val containerColor = ActionSheetDefaults.containerColor()
    val titleColor = ActionSheetDefaults.titleColor()
    val itemTextColor = ActionSheetDefaults.itemTextColor()
    val descriptionColor = ActionSheetDefaults.descriptionColor()
    val cancelTextColor = ActionSheetDefaults.cancelTextColor()
    val dividerColor = ActionSheetDefaults.dividerColor()
    val titleHeight = ActionSheetDefaults.titleHeight()
    val itemHeight = ActionSheetDefaults.itemHeight()
    val cancelHeight = ActionSheetDefaults.cancelHeight()
    val itemPadding = ActionSheetDefaults.itemPadding()
    val iconSpacing = ActionSheetDefaults.iconSpacing()
    val gapHeight = ActionSheetDefaults.gapHeight()
    val dividerThickness = ActionSheetDefaults.dividerThickness()
    val disabledAlpha = ActionSheetDefaults.disabledAlpha()
    val titleTextStyle = ActionSheetDefaults.titleTextStyle()
    val itemTextStyle = ActionSheetDefaults.itemTextStyle()
    val descriptionTextStyle = ActionSheetDefaults.descriptionTextStyle()
    val cancelTextStyle = ActionSheetDefaults.cancelTextStyle()

    PPopup(
        visible = visible,
        onClose = onDismiss,
        draggable = false,
        containerColor = containerColor
    ) {
        Column {
            title?.let {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(titleHeight)
                        .padding(itemPadding),
                    contentAlignment = Alignment.Center
                ) {
                    PText(
                        text = it,
                        color = titleColor,
                        style = titleTextStyle
                    )
                }
            }

            options.forEachIndexed { index, item ->
                if (index > 0 || title != null) {
                    HorizontalDivider(
                        color = dividerColor,
                        thickness = dividerThickness
                    )
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(itemHeight)
                        .alpha(if (item.disabled) disabledAlpha else 1f)
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
                        .padding(itemPadding),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    if (item.icon != null) {
                        item.icon.invoke()
                        Spacer(modifier = Modifier.width(iconSpacing))
                    }
                    PText(
                        text = item.label,
                        color = item.color ?: itemTextColor,
                        style = itemTextStyle
                    )
                    item.description?.let {
                        PText(
                            text = it,
                            color = descriptionColor,
                            style = descriptionTextStyle
                        )
                    }
                }
            }

            Spacer(
                modifier = Modifier
                    .height(gapHeight)
                    .fillMaxWidth()
                    .background(dividerColor)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(cancelHeight)
                    .clickable { onDismiss() },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                PText(
                    text = cancelText,
                    color = cancelTextColor,
                    style = cancelTextStyle
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
