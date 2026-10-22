package com.example.universalgallery.permissions

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.core.content.ContextCompat

object PermissionHelper {
    fun isGranted(context: Context, permission: String): Boolean {
        return ContextCompat.checkSelfPermission(context, permission) ==
            PackageManager.PERMISSION_GRANTED
    }

    fun checkVisual(context: Context): MediaPermissionStatus {
        val images = isGranted(context, Manifest.permission.READ_MEDIA_IMAGES)
        val video = isGranted(context, Manifest.permission.READ_MEDIA_VIDEO)
        val selected = isGranted(context, Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED)
        return evaluateVisual(
            imagesGranted = images,
            videoGranted = video,
            selectedGranted = selected,
            userHasResponded = images || video || selected,
        )
    }

    fun checkAudio(context: Context): MediaPermissionStatus {
        val audio = isGranted(context, Manifest.permission.READ_MEDIA_AUDIO)
        return evaluateAudio(
            audioGranted = audio,
            userHasResponded = audio,
        )
    }

    fun checkCamera(context: Context): MediaPermissionStatus {
        return if (isGranted(context, Manifest.permission.CAMERA)) {
            MediaPermissionStatus.GrantedFull
        } else {
            MediaPermissionStatus.Denied(permanentlyDenied = false)
        }
    }

    fun evaluateVisual(
        imagesGranted: Boolean,
        videoGranted: Boolean,
        selectedGranted: Boolean,
        userHasResponded: Boolean,
        permanentlyDenied: Boolean = false,
    ): MediaPermissionStatus {
        if (imagesGranted && videoGranted) {
            return MediaPermissionStatus.GrantedFull
        }
        if (selectedGranted || imagesGranted || videoGranted) {
            return MediaPermissionStatus.GrantedPartial
        }
        if (!userHasResponded) {
            return MediaPermissionStatus.NotRequested
        }
        return MediaPermissionStatus.Denied(permanentlyDenied = permanentlyDenied)
    }

    fun evaluateAudio(
        audioGranted: Boolean,
        userHasResponded: Boolean,
        permanentlyDenied: Boolean = false,
    ): MediaPermissionStatus {
        if (audioGranted) {
            return MediaPermissionStatus.GrantedFull
        }
        if (!userHasResponded) {
            return MediaPermissionStatus.NotRequested
        }
        return MediaPermissionStatus.Denied(permanentlyDenied = permanentlyDenied)
    }

    fun evaluateCamera(
        cameraGranted: Boolean,
        userHasResponded: Boolean,
        permanentlyDenied: Boolean = false,
    ): MediaPermissionStatus {
        if (cameraGranted) {
            return MediaPermissionStatus.GrantedFull
        }
        if (!userHasResponded) {
            return MediaPermissionStatus.NotRequested
        }
        return MediaPermissionStatus.Denied(permanentlyDenied = permanentlyDenied)
    }

    fun appSettingsIntent(context: Context): Intent {
        return Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", context.packageName, null)
        }
    }
}
