package xyz.junerver.compose.palette.components.barcode

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import org.junit.Rule
import xyz.junerver.compose.palette.core.theme.PaletteMaterialTheme
import kotlin.test.Test

class BarcodeUiTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun barcode_rendersWithValue() {
        rule.setContent {
            PaletteMaterialTheme {
                PBarcode(
                    value = "1234567890",
                )
            }
        }

        rule.waitForIdle()
    }

    @Test
    fun barcode_withCustomTypeRenders() {
        rule.setContent {
            PaletteMaterialTheme {
                PBarcode(
                    value = "1234567890",
                    type = PaletteBarcodeType.Code128,
                )
            }
        }

        rule.waitForIdle()
    }
}
