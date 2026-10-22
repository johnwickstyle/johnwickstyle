package com.example.universalgallery.ui.documents

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class DocumentLogicTest {
    @Test
    fun routeRoundTripsUri() {
        val uri = "content://com.android.providers.media.documents/document/42"
        assertEquals("document/${DocumentLogic.encodeUri(uri)}", DocumentLogic.route(uri))
        assertEquals(uri, DocumentLogic.parseUri(DocumentLogic.encodeUri(uri)))
        assertEquals("document/{encodedUri}", DocumentLogic.ROUTE_PATTERN)
    }

    @Test
    fun parseUriRejectsBlank() {
        assertNull(DocumentLogic.parseUri(null))
        assertNull(DocumentLogic.parseUri(""))
    }

    @Test
    fun classifyMimeDetectsPdfTextAndUnsupported() {
        assertEquals(DocumentKind.PDF, DocumentLogic.classify("application/pdf"))
        assertEquals(DocumentKind.TEXT, DocumentLogic.classify("text/plain"))
        assertEquals(DocumentKind.TEXT, DocumentLogic.classify("application/json"))
        assertEquals(DocumentKind.TEXT, DocumentLogic.classify("text/html"))
        assertEquals(DocumentKind.UNSUPPORTED, DocumentLogic.classify("application/vnd.ms-excel"))
        assertEquals(DocumentKind.UNSUPPORTED, DocumentLogic.classify(""))
        assertEquals(DocumentKind.UNSUPPORTED, DocumentLogic.classify("video/mp4"))
    }

    @Test
    fun inAppRenderIsReadOnlyPdfOrText() {
        assertTrue(DocumentLogic.canRenderInApp("application/pdf"))
        assertTrue(DocumentLogic.canRenderInApp("text/plain"))
        assertFalse(DocumentLogic.canRenderInApp("application/msword"))
        assertFalse(DocumentLogic.canRenderInApp("image/jpeg"))
    }

    @Test
    fun canOpenExternallyRequiresContentUri() {
        assertTrue(DocumentLogic.canOpenExternally("content://com.android.providers.downloads.documents/document/1"))
        assertFalse(DocumentLogic.canOpenExternally("file:///sdcard/a.pdf"))
        assertFalse(DocumentLogic.canOpenExternally(""))
    }

    @Test
    fun textReadLimitIsOneMegabyte() {
        assertEquals(1_048_576L, DocumentLogic.MAX_TEXT_BYTES)
        assertTrue(DocumentLogic.isTextTooLarge(1_048_577L))
        assertFalse(DocumentLogic.isTextTooLarge(1024L))
    }
}
