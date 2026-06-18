package xyz.junerver.compose.palette.components.bottomnavigation

import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import xyz.junerver.compose.palette.components.text.PText
import xyz.junerver.compose.palette.core.theme.PaletteMaterialTheme
import kotlin.test.Test
import kotlin.test.assertEquals

class BottomNavigationUiTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun bottomNavigation_shouldRenderItems() {
        rule.setContent {
            PaletteMaterialTheme {
                PBottomNavigation(
                    items = navigationItems(),
                    selectedKey = "home",
                    onItemClick = {},
                )
            }
        }

        rule.onNodeWithText("Home").assertTextEquals("H", "Home")
        rule.onNodeWithText("Settings").assertTextEquals("S", "Settings")
    }

    @Test
    fun bottomNavigation_shouldNotifyWhenEnabledItemClicked() {
        var selected = "home"

        rule.setContent {
            PaletteMaterialTheme {
                PBottomNavigation(
                    items = navigationItems(),
                    selectedKey = selected,
                    onItemClick = { selected = it },
                )
            }
        }

        rule.onNodeWithText("Settings").performClick()

        assertEquals("settings", selected)
    }

    @Test
    fun bottomNavigation_shouldIgnoreDisabledItemClick() {
        var selected = "home"

        rule.setContent {
            PaletteMaterialTheme {
                PBottomNavigation(
                    items = navigationItems(),
                    selectedKey = selected,
                    onItemClick = { selected = it },
                )
            }
        }

        rule.onNodeWithText("Disabled").performClick()

        assertEquals("home", selected)
    }

    private fun navigationItems(): List<BottomNavigationItem> = listOf(
        BottomNavigationItem(
            key = "home",
            label = "Home",
            icon = { PText("H") },
        ),
        BottomNavigationItem(
            key = "settings",
            label = "Settings",
            icon = { PText("S") },
        ),
        BottomNavigationItem(
            key = "disabled",
            label = "Disabled",
            icon = { PText("D") },
            disabled = true,
        ),
    )
}
