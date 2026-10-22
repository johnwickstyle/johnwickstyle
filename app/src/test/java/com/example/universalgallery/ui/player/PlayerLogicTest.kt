package com.example.universalgallery.ui.player

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PlayerLogicTest {
    @Test
    fun routesEncodeMediaId() {
        assertEquals("watch/12", PlayerLogic.videoRoute(12L))
        assertEquals("listen/9", PlayerLogic.audioRoute(9L))
        assertEquals("watch/{mediaId}", PlayerLogic.VIDEO_ROUTE_PATTERN)
        assertEquals("listen/{mediaId}", PlayerLogic.AUDIO_ROUTE_PATTERN)
    }

    @Test
    fun parseMediaIdRejectsInvalidValues() {
        assertEquals(12L, PlayerLogic.parseMediaId("12"))
        assertNull(PlayerLogic.parseMediaId(null))
        assertNull(PlayerLogic.parseMediaId("abc"))
        assertNull(PlayerLogic.parseMediaId("-4"))
    }

    @Test
    fun clampPositionStaysInsideDuration() {
        assertEquals(0L, PlayerLogic.clampPosition(-10L, 1_000L))
        assertEquals(1_000L, PlayerLogic.clampPosition(5_000L, 1_000L))
        assertEquals(250L, PlayerLogic.clampPosition(250L, 1_000L))
        assertEquals(0L, PlayerLogic.clampPosition(50L, 0L))
    }

    @Test
    fun canPlayRequiresMatchingContentUri() {
        assertTrue(PlayerLogic.canPlay("content://media/external/video/media/1", "video/mp4", "video/"))
        assertTrue(PlayerLogic.canPlay("content://media/external/audio/media/1", "audio/mpeg", "audio/"))
        assertFalse(PlayerLogic.canPlay("file:///sdcard/a.mp4", "video/mp4", "video/"))
        assertFalse(PlayerLogic.canPlay("content://media/external/video/media/1", "audio/mpeg", "video/"))
        assertFalse(PlayerLogic.canPlay("", "video/mp4", "video/"))
    }

    @Test
    fun mapFailureClassifiesUnsupportedAndIo() {
        assertEquals(PlaybackFailure.UNSUPPORTED, PlayerLogic.mapFailure(isUnsupported = true, isIo = false))
        assertEquals(PlaybackFailure.IO, PlayerLogic.mapFailure(isUnsupported = false, isIo = true))
        assertEquals(PlaybackFailure.UNKNOWN, PlayerLogic.mapFailure(isUnsupported = false, isIo = false))
    }

    @Test
    fun allowedSpeedsAreBounded() {
        assertEquals(listOf(0.5f, 0.75f, 1f, 1.25f, 1.5f, 2f), PlayerLogic.allowedSpeeds)
        assertEquals(0.5f, PlayerLogic.clampSpeed(0.1f), 0.001f)
        assertEquals(2f, PlayerLogic.clampSpeed(8f), 0.001f)
        assertEquals(1.25f, PlayerLogic.clampSpeed(1.25f), 0.001f)
    }
}
