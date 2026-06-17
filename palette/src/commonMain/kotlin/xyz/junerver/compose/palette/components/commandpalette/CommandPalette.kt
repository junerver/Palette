package xyz.junerver.compose.palette.components.commandpalette

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import xyz.junerver.compose.palette.components.textfield.BorderTextField
import xyz.junerver.compose.palette.core.spec.ComponentSize
import xyz.junerver.compose.palette.core.theme.PaletteTheme

@Composable
fun PCommandPalette(
    commands: List<CommandAction>,
    query: String,
    highlightedIndex: Int,
    modifier: Modifier = Modifier,
    onCommandClick: (CommandAction) -> Unit = {},
) {
    val filtered = remember(commands, query) {
        filterCommands(commands, query)
    }

    LazyColumn(
        modifier = modifier
            .widthIn(max = CommandPaletteDefaults.width())
            .heightIn(max = CommandPaletteDefaults.maxHeight())
            .background(CommandPaletteDefaults.containerColor()),
        verticalArrangement = Arrangement.spacedBy(CommandPaletteDefaults.itemSpacing())
    ) {
        item {
            BorderTextField(
                value = query,
                onValueChange = {},
                placeholder = PaletteTheme.strings.commandPalettePlaceholder,
                enabled = false,
                size = ComponentSize.Small,
            )
        }
        itemsIndexed(
            items = filtered,
            key = { _, action -> action.id }
        ) { index, action ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        if (index == highlightedIndex) CommandPaletteDefaults.highlightedContainerColor()
                        else androidx.compose.ui.graphics.Color.Transparent
                    )
                    .clickable { onCommandClick(action) }
                    .padding(CommandPaletteDefaults.itemPadding())
            ) {
                Text(
                    text = action.title,
                    color = CommandPaletteDefaults.titleColor(),
                    style = CommandPaletteDefaults.titleTextStyle()
                )
                if (!action.subtitle.isNullOrBlank()) {
                    Text(
                        text = action.subtitle,
                        color = CommandPaletteDefaults.subtitleColor(),
                        style = CommandPaletteDefaults.subtitleTextStyle()
                    )
                }
            }
        }
    }
}
