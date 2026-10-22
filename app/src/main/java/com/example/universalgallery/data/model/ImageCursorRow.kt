package com.example.universalgallery.data.model

data class ImageCursorRow(
    val id: Long?,
    val displayName: String?,
    val mimeType: String?,
    val sizeBytes: Long?,
    val width: Int?,
    val height: Int?,
    val dateTakenMs: Long?,
    val dateModifiedSeconds: Long?,
    val bucketId: Long?,
    val bucketName: String?,
)
