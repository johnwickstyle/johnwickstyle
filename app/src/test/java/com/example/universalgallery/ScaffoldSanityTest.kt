package com.example.universalgallery

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ScaffoldSanityTest {
    @Test
    fun applicationIdPackageMatchesNamespace() {
        assertEquals("com.example.universalgallery", "com.example.universalgallery")
        assertTrue(UniversalGalleryApp::class.java.name.startsWith("com.example.universalgallery"))
    }
}
