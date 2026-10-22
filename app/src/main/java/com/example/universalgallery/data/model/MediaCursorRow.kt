package com.example.universalgallery.data.model

data class MediaCursorRow(
    val id: Long?,
    val displayName: String?,
    val mimeType: String?,
    val sizeBytes: Long?,
    val width: Int?,
    val height: Int?,
    val durationMs: Long?,
    val dateTakenMs: Long?,
    val dateModifiedSeconds: Long?,
    val bucketId: Long?,
    val bucketName: String?,
)
