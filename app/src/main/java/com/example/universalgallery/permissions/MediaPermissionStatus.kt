package com.example.universalgallery.permissions

sealed class MediaPermissionStatus {
    data object NotRequested : MediaPermissionStatus()
    data object GrantedFull : MediaPermissionStatus()
    data object GrantedPartial : MediaPermissionStatus()
    data class Denied(val permanentlyDenied: Boolean) : MediaPermissionStatus()
}
