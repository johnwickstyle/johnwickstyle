package com.example.universalgallery.permissions

import android.Manifest

enum class MediaPermissionType(
    val runtimePermissions: Array<String>,
) {
    Visual(
        runtimePermissions = arrayOf(
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_VIDEO,
            Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED,
        ),
    ),
    Audio(
        runtimePermissions = arrayOf(
            Manifest.permission.READ_MEDIA_AUDIO,
        ),
    ),
    Camera(
        runtimePermissions = arrayOf(
            Manifest.permission.CAMERA,
        ),
    ),
}
