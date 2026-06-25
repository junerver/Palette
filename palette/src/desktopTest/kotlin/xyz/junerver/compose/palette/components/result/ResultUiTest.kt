package xyz.junerver.compose.palette.components.result

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import xyz.junerver.compose.palette.core.theme.PaletteMaterialTheme
import kotlin.test.Test

class ResultUiTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun result_successStatusShowsDefaultTitle() {
        rule.setContent {
            PaletteMaterialTheme {
                PResult(status = ResultStatus.Success)
            }
        }

        rule.onNodeWithText("操作成功").assertIsDisplayed()
    }

    @Test
    fun result_errorStatusShowsDefaultTitle() {
        rule.setContent {
            PaletteMaterialTheme {
                PResult(status = ResultStatus.Error)
            }
        }

        rule.onNodeWithText("操作失败").assertIsDisplayed()
    }

    @Test
    fun result_customTitleOverridesDefault() {
        rule.setContent {
            PaletteMaterialTheme {
                PResult(
                    status = ResultStatus.Success,
                    title = "自定义标题",
                )
            }
        }

        rule.onNodeWithText("自定义标题").assertIsDisplayed()
    }

    @Test
    fun result_withSubtitleShowsSubtitle() {
        rule.setContent {
            PaletteMaterialTheme {
                PResult(
                    status = ResultStatus.Info,
                    subtitle = "这是副标题",
                )
            }
        }

        rule.onNodeWithText("这是副标题").assertIsDisplayed()
    }
}
