package xyz.junerver.compose.palette.components.card

import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CardDefaultsTest {
    @Test
    fun cardDefaults_shouldExposeExpectedSizingTokens() {
        assertTrue(CardDefaults.ContentPadding > 0.dp)
        assertTrue(CardDefaults.CornerRadius > 0.dp)
        assertTrue(CardDefaults.BorderWidth > 0.dp)
    }

    @Test
    fun cardVariant_shouldExposeStableEnumOrder() {
        assertEquals(
            listOf("Elevated", "Filled", "Outlined"),
            CardVariant.entries.map { it.name },
        )
    }
}
