package com.example.universalgallery.permissions

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PermissionHelperTest {
    @Test
    fun visualFullAccessWhenImagesAndVideoGranted() {
        val status = PermissionHelper.evaluateVisual(
            imagesGranted = true,
            videoGranted = true,
            selectedGranted = false,
            userHasResponded = true,
        )
        assertEquals(MediaPermissionStatus.GrantedFull, status)
    }

    @Test
    fun visualPartialWhenOnlySelectedGranted() {
        val status = PermissionHelper.evaluateVisual(
            imagesGranted = false,
            videoGranted = false,
            selectedGranted = true,
            userHasResponded = true,
        )
        assertEquals(MediaPermissionStatus.GrantedPartial, status)
    }

    @Test
    fun visualPartialWhenOnlyImagesGranted() {
        val status = PermissionHelper.evaluateVisual(
            imagesGranted = true,
            videoGranted = false,
            selectedGranted = false,
            userHasResponded = true,
        )
        assertEquals(MediaPermissionStatus.GrantedPartial, status)
    }

    @Test
    fun visualNotRequestedBeforeUserResponds() {
        val status = PermissionHelper.evaluateVisual(
            imagesGranted = false,
            videoGranted = false,
            selectedGranted = false,
            userHasResponded = false,
        )
        assertEquals(MediaPermissionStatus.NotRequested, status)
    }

    @Test
    fun visualDeniedWhenUserRefuses() {
        val status = PermissionHelper.evaluateVisual(
            imagesGranted = false,
            videoGranted = false,
            selectedGranted = false,
            userHasResponded = true,
            permanentlyDenied = false,
        )
        assertEquals(MediaPermissionStatus.Denied(permanentlyDenied = false), status)
    }

    @Test
    fun visualPermanentlyDenied() {
        val status = PermissionHelper.evaluateVisual(
            imagesGranted = false,
            videoGranted = false,
            selectedGranted = false,
            userHasResponded = true,
            permanentlyDenied = true,
        )
        assertTrue(status is MediaPermissionStatus.Denied && status.permanentlyDenied)
    }

    @Test
    fun audioGranted() {
        assertEquals(
            MediaPermissionStatus.GrantedFull,
            PermissionHelper.evaluateAudio(audioGranted = true, userHasResponded = true),
        )
    }

    @Test
    fun audioDenied() {
        assertEquals(
            MediaPermissionStatus.Denied(permanentlyDenied = false),
            PermissionHelper.evaluateAudio(audioGranted = false, userHasResponded = true),
        )
    }

    @Test
    fun cameraNotRequested() {
        assertEquals(
            MediaPermissionStatus.NotRequested,
            PermissionHelper.evaluateCamera(cameraGranted = false, userHasResponded = false),
        )
    }
}
