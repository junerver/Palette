package xyz.junerver.compose.palette.components.slider

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SliderDefaultsTest {
    @Test
    fun sliderDefaults_shouldKeepThumbAndTrackSizingConsistent() {
        assertTrue(SliderDefaults.Height > SliderDefaults.TrackHeight)
        assertTrue(SliderDefaults.ThumbSize > SliderDefaults.TrackHeight)
        assertTrue(SliderDefaults.LabelWidth > SliderDefaults.LabelSpacing)
    }

    @Test
    fun sliderDefaults_shouldExposeExpectedDisabledAlpha() {
        assertEquals(0.5f, SliderDefaults.DisabledAlpha)
    }
}
