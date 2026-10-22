package com.example.universalgallery.data.model

data class MediaItem(
    val id: Long,
    val uriString: String,
    val displayName: String,
    val mimeType: String,
    val sizeBytes: Long,
    val width: Int,
    val height: Int,
    val durationMs: Long,
    val dateTakenEpochMs: Long,
    val dateModifiedEpochMs: Long,
    val bucketId: Long,
    val bucketName: String,
    val mediaType: MediaType,
)
