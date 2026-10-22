package com.example.universalgallery.data

import com.example.universalgallery.data.model.ImageCursorRow
import com.example.universalgallery.data.model.MediaSortOrder
import com.example.universalgallery.data.model.MediaType
import com.example.universalgallery.data.repository.ImageMediaMapper
import com.example.universalgallery.ui.components.formatDuration
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ImageMediaMapperTest {
    @Test
    fun mapsValidImageRow() {
        val item = ImageMediaMapper.map(
            ImageCursorRow(
                id = 42L,
                displayName = "DSC_0001.jpg",
                mimeType = "image/jpeg",
                sizeBytes = 1024L,
                width = 100,
                height = 80,
                dateTakenMs = 1_700_000_000_000L,
                dateModifiedSeconds = 1_700_000L,
                bucketId = 9L,
                bucketName = "Camera",
            ),
        )
        assertNotNull(item)
        assertEquals(42L, item!!.id)
        assertEquals("DSC_0001.jpg", item.displayName)
        assertEquals(MediaType.IMAGE, item.mediaType)
        assertEquals("Camera", item.bucketName)
        assertEquals(1_700_000_000_000L, item.dateTakenEpochMs)
    }

    @Test
    fun skipsNullId() {
        assertNull(
            ImageMediaMapper.map(
                ImageCursorRow(
                    id = null,
                    displayName = "a.jpg",
                    mimeType = "image/jpeg",
                    sizeBytes = 1L,
                    width = 1,
                    height = 1,
                    dateTakenMs = 1L,
                    dateModifiedSeconds = 1L,
                    bucketId = 1L,
                    bucketName = "A",
                ),
            ),
        )
    }

    @Test
    fun skipsNonImageMime() {
        assertNull(
            ImageMediaMapper.map(
                ImageCursorRow(
                    id = 1L,
                    displayName = "clip.mp4",
                    mimeType = "video/mp4",
                    sizeBytes = 1L,
                    width = 1,
                    height = 1,
                    dateTakenMs = 1L,
                    dateModifiedSeconds = 1L,
                    bucketId = 1L,
                    bucketName = "A",
                ),
            ),
        )
    }

    @Test
    fun groupsAlbumsByBucket() {
        val first = ImageMediaMapper.map(sampleRow(id = 1, bucketId = 10, bucketName = "Camera", taken = 20))!!
        val second = ImageMediaMapper.map(sampleRow(id = 2, bucketId = 10, bucketName = "Camera", taken = 30))!!
        val third = ImageMediaMapper.map(sampleRow(id = 3, bucketId = 11, bucketName = "Downloads", taken = 10))!!
        val albums = ImageMediaMapper.groupAlbums(listOf(first, second, third))
        assertEquals(2, albums.size)
        val camera = albums.first { it.name == "Camera" }
        assertEquals(2, camera.itemCount)
        assertEquals(second.uriString, camera.coverUriString)
    }

    @Test
    fun dateDescSqlContainsDateTaken() {
        assertTrue(MediaSortOrder.DATE_DESC.sql.contains("datetaken"))
        assertTrue(MediaSortOrder.DATE_DESC.sql.contains("DESC"))
    }

    @Test
    fun durationFormat() {
        assertEquals("0:05", formatDuration(5_000L))
        assertEquals("1:01", formatDuration(61_000L))
    }

    private fun sampleRow(
        id: Long,
        bucketId: Long,
        bucketName: String,
        taken: Long,
    ): ImageCursorRow = ImageCursorRow(
        id = id,
        displayName = "img_$id.jpg",
        mimeType = "image/jpeg",
        sizeBytes = 10L,
        width = 10,
        height = 10,
        dateTakenMs = taken,
        dateModifiedSeconds = 1L,
        bucketId = bucketId,
        bucketName = bucketName,
    )
}
