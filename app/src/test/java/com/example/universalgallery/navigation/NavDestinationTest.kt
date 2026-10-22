package com.example.universalgallery.navigation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class NavDestinationTest {
    @Test
    fun bottomDestinationsHasFiveEntries() {
        assertEquals(5, NavDestination.bottomDestinations.size)
    }

    @Test
    fun bottomDestinationsAreInRequiredOrder() {
        val routes = NavDestination.bottomDestinations.map { it.route }
        assertEquals(
            listOf("images", "videos", "audio", "documents", "folders"),
            routes,
        )
    }

    @Test
    fun bottomDestinationRoutesAreUnique() {
        val routes = NavDestination.bottomDestinations.map { it.route }
        assertEquals(routes.size, routes.toSet().size)
    }

    @Test
    fun fromRouteFallsBackToImages() {
        assertEquals(NavDestination.Images, NavDestination.fromRoute(null))
        assertEquals(NavDestination.Images, NavDestination.fromRoute("unknown"))
        assertEquals(NavDestination.Videos, NavDestination.fromRoute("videos"))
    }

    @Test
    fun fullScreenViewerHidesMainChrome() {
        assertTrue(NavDestination.isFullScreenViewer("photo/42"))
        assertTrue(NavDestination.isFullScreenViewer("photo/{mediaId}"))
        assertTrue(NavDestination.isFullScreenViewer("watch/12"))
        assertTrue(NavDestination.isFullScreenViewer("listen/9"))
        assertTrue(NavDestination.isFullScreenViewer("document/abc"))
        assertFalse(NavDestination.isFullScreenViewer("images"))
        assertFalse(NavDestination.isFullScreenViewer(null))
    }
}
