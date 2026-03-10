package xyz.junerver.compose.palette.components.upload

import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import xyz.junerver.compose.palette.core.i18n.PaletteStrings
import xyz.junerver.compose.palette.core.theme.PaletteMaterialTheme
import kotlin.test.Test

class UploadUiTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun upload_shouldRenderFileStatusesAndInvokeTriggerCallback() {
        val files =
            listOf(
                UploadFile(name = "logo.png", mimeType = "image/png", sizeBytes = 512),
                UploadFile(name = "report.pdf", mimeType = "application/pdf", sizeBytes = 256),
                UploadFile(name = "banner.jpg", mimeType = "image/jpeg", sizeBytes = 2_048),
            )

        rule.setContent {
            var triggerCount by mutableIntStateOf(0)
            var fileCount by mutableStateOf(0)

            PaletteMaterialTheme(strings = PaletteStrings.enUS()) {
                PUpload(
                    files = files,
                    onFilesChange = {
                        triggerCount += 1
                        fileCount = it.size
                    },
                    acceptedTypes = listOf("image/*"),
                    maxSizeBytes = 1_024,
                )
                Text("Triggered: $triggerCount/$fileCount")
            }
        }

        rule.onNodeWithText("logo.png (ready)").assertTextEquals("logo.png (ready)")
        rule.onNodeWithText("report.pdf (type rejected)").assertTextEquals("report.pdf (type rejected)")
        rule.onNodeWithText("banner.jpg (size rejected)").assertTextEquals("banner.jpg (size rejected)")

        rule.onNodeWithText("Select files").performClick()
        rule.onNodeWithText("Triggered: 1/3").assertTextEquals("Triggered: 1/3")
    }

    @Test
    fun upload_shouldIgnoreTriggerClickWhenDisabled() {
        rule.setContent {
            var triggerCount by mutableIntStateOf(0)

            PaletteMaterialTheme(strings = PaletteStrings.enUS()) {
                PUpload(
                    files = emptyList(),
                    onFilesChange = { triggerCount += 1 },
                    disabled = true,
                )
                Text("Triggered: $triggerCount")
            }
        }

        rule.onNodeWithText("Select files").performClick()
        rule.onNodeWithText("Triggered: 0").assertTextEquals("Triggered: 0")
    }

    @Test
    fun upload_shouldTreatAllFilesAsReadyWhenNoRestrictionsConfigured() {
        val files =
            listOf(
                UploadFile(name = "notes.txt", mimeType = "text/plain", sizeBytes = 8_192),
                UploadFile(name = "archive.zip", mimeType = "application/zip", sizeBytes = 16_384),
            )

        rule.setContent {
            PaletteMaterialTheme(strings = PaletteStrings.enUS()) {
                PUpload(
                    files = files,
                    onFilesChange = {},
                    acceptedTypes = emptyList(),
                    maxSizeBytes = null,
                )
            }
        }

        rule.onNodeWithText("notes.txt (ready)").assertTextEquals("notes.txt (ready)")
        rule.onNodeWithText("archive.zip (ready)").assertTextEquals("archive.zip (ready)")
    }
}
