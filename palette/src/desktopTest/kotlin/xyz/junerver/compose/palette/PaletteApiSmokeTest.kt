package xyz.junerver.compose.palette

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import xyz.junerver.compose.palette.core.i18n.PaletteStrings
import xyz.junerver.compose.palette.core.theme.PaletteMaterialTheme
import kotlin.test.Test

class PaletteApiSmokeTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun paletteRootExports_shouldRenderSelectDataGridAndVirtualList() {
        rule.setContent {
            PaletteMaterialTheme(strings = PaletteStrings.enUS()) {
                Column {
                    PSelect(
                        options =
                            listOf(
                                SelectOption(label = "Pending", value = "pending"),
                                SelectOption(label = "Done", value = "done"),
                            ),
                        value = "done",
                        onValueChange = {},
                        placeholder = "Choose status",
                    )
                    PDataGrid(
                        rows = listOf("Palette", "Hooks"),
                        columns =
                            listOf(
                                DataGridColumn<String>(title = "Project", value = { it }),
                            ),
                    )
                    PVirtualList(
                        items = listOf("Item A", "Item B"),
                    )
                    Text("Theme dark: ${PaletteTheme.isDark}")
                    Text(PaletteTheme.strings.commandPalettePlaceholder)
                }
            }
        }

        rule.onNodeWithText("Done").assertTextEquals("Done")
        rule.onNodeWithText("Project").assertTextEquals("Project")
        rule.onNodeWithText("Palette").assertTextEquals("Palette")
        rule.onNodeWithText("Item A").assertTextEquals("Item A")
        rule.onNodeWithText("Item B").assertTextEquals("Item B")
        rule.onNodeWithText("Theme dark: false").assertTextEquals("Theme dark: false")
        rule.onNodeWithText("Type a command").assertTextEquals("Type a command")
    }
}
