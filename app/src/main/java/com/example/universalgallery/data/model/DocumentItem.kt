package com.example.universalgallery.data.model

data class DocumentItem(
    val uriString: String,
    val displayName: String,
    val mimeType: String,
    val sizeBytes: Long,
    val lastModifiedEpochMs: Long,
)
