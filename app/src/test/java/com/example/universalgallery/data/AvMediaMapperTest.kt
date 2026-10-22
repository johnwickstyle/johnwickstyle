package com.example.universalgallery.data

import com.example.universalgallery.data.model.MediaCursorRow
import com.example.universalgallery.data.model.MediaType
import com.example.universalgallery.data.repository.AvMediaMapper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class AvMediaMapperTest {
    @Test
    fun mapsValidVideoRow() {
        val item = AvMediaMapper.mapVideo(
            MediaCursorRow(
                id = 7L,
                displayName = "clip.mp4",
                mimeType = "video/mp4",
                sizeBytes = 2048L,
                width = 1920,
                height = 1080,
                durationMs = 12_500L,
                dateTakenMs = 1_700_000_000_000L,
                dateModifiedSeconds = 1_700_000L,
                bucketId = 3L,
                bucketName = "Movies",
            ),
        )
        assertNotNull(item)
        assertEquals(7L, item!!.id)
        assertEquals(MediaType.VIDEO, item.mediaType)
        assertEquals(12_500L, item.durationMs)
        assertEquals("content://media/external/video/media/7", item.uriString)
        assertEquals("Movies", item.bucketName)
    }

    @Test
    fun skipsNonVideoMime() {
        assertNull(
            AvMediaMapper.mapVideo(
                sample(id = 1L, mimeType = "image/jpeg"),
            ),
        )
    }

    @Test
    fun mapsValidAudioRow() {
        val item = AvMediaMapper.mapAudio(
            MediaCursorRow(
                id = 9L,
                displayName = "song.mp3",
                mimeType = "audio/mpeg",
                sizeBytes = 4096L,
                width = 0,
                height = 0,
                durationMs = 180_000L,
                dateTakenMs = 0L,
                dateModifiedSeconds = 1_700_000L,
                bucketId = 4L,
                bucketName = "Album A",
            ),
        )
        assertNotNull(item)
        assertEquals(MediaType.AUDIO, item!!.mediaType)
        assertEquals(180_000L, item.durationMs)
        assertEquals("content://media/external/audio/media/9", item.uriString)
        assertEquals("Album A", item.bucketName)
    }

    @Test
    fun skipsNonAudioMime() {
        assertNull(
            AvMediaMapper.mapAudio(
                sample(id = 2L, mimeType = "video/mp4"),
            ),
        )
    }

    @Test
    fun skipsNullAndNegativeIds() {
        assertNull(AvMediaMapper.mapVideo(sample(id = null, mimeType = "video/mp4")))
        assertNull(AvMediaMapper.mapAudio(sample(id = -1L, mimeType = "audio/mpeg")))
    }

    private fun sample(
        id: Long?,
        mimeType: String,
    ): MediaCursorRow = MediaCursorRow(
        id = id,
        displayName = "file",
        mimeType = mimeType,
        sizeBytes = 1L,
        width = 1,
        height = 1,
        durationMs = 1L,
        dateTakenMs = 1L,
        dateModifiedSeconds = 1L,
        bucketId = 1L,
        bucketName = "A",
    )
}
