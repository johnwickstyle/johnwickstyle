package com.example.universalgallery.ui.viewer

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PhotoViewerLogicTest {
    @Test
    fun routeEncodesMediaId() {
        assertEquals("photo/42", PhotoViewerLogic.route(42L))
        assertEquals("photo/{mediaId}", PhotoViewerLogic.ROUTE_PATTERN)
    }

    @Test
    fun parseMediaIdRejectsInvalidValues() {
        assertEquals(42L, PhotoViewerLogic.parseMediaId("42"))
        assertNull(PhotoViewerLogic.parseMediaId(null))
        assertNull(PhotoViewerLogic.parseMediaId(""))
        assertNull(PhotoViewerLogic.parseMediaId("abc"))
        assertNull(PhotoViewerLogic.parseMediaId("-1"))
    }

    @Test
    fun formatFileSizeUsesBinaryUnits() {
        assertEquals("0 B", PhotoViewerLogic.formatFileSize(0L))
        assertEquals("512 B", PhotoViewerLogic.formatFileSize(512L))
        assertEquals("1.0 KB", PhotoViewerLogic.formatFileSize(1024L))
        assertEquals("1.5 MB", PhotoViewerLogic.formatFileSize(1_572_864L))
    }

    @Test
    fun formatDimensionsOmitsUnknownSize() {
        assertEquals("100 × 80", PhotoViewerLogic.formatDimensions(100, 80))
        assertEquals("", PhotoViewerLogic.formatDimensions(0, 80))
        assertEquals("", PhotoViewerLogic.formatDimensions(100, 0))
    }

    @Test
    fun formatDateUsesUtc() {
        assertEquals("", PhotoViewerLogic.formatDate(0L))
        assertEquals("2023-11-14 22:13", PhotoViewerLogic.formatDate(1_700_000_000_000L))
    }

    @Test
    fun clampScaleStaysBetweenOneAndFive() {
        assertEquals(1f, PhotoViewerLogic.clampScale(0.2f), 0.001f)
        assertEquals(5f, PhotoViewerLogic.clampScale(9f), 0.001f)
        assertEquals(2.5f, PhotoViewerLogic.clampScale(2.5f), 0.001f)
    }

    @Test
    fun adjacentIndexStaysInBounds() {
        assertEquals(1, PhotoViewerLogic.adjacentIndex(current = 0, delta = 1, size = 3))
        assertNull(PhotoViewerLogic.adjacentIndex(current = 0, delta = -1, size = 3))
        assertNull(PhotoViewerLogic.adjacentIndex(current = 2, delta = 1, size = 3))
        assertNull(PhotoViewerLogic.adjacentIndex(current = 0, delta = 1, size = 0))
    }

    @Test
    fun canShareRequiresContentImageUri() {
        assertTrue(PhotoViewerLogic.canShare("content://media/external/images/media/1", "image/jpeg"))
        assertFalse(PhotoViewerLogic.canShare("file:///sdcard/a.jpg", "image/jpeg"))
        assertFalse(PhotoViewerLogic.canShare("content://media/external/images/media/1", "video/mp4"))
        assertFalse(PhotoViewerLogic.canShare("", "image/jpeg"))
    }
}
