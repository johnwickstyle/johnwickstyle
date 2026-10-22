package com.example.universalgallery.data.repository

import com.example.universalgallery.data.model.MediaCursorRow
import com.example.universalgallery.data.model.MediaItem
import com.example.universalgallery.data.model.MediaType

object AvMediaMapper {
    fun videoUriString(id: Long): String = "content://media/external/video/media/$id"

    fun audioUriString(id: Long): String = "content://media/external/audio/media/$id"

    fun mapVideo(row: MediaCursorRow): MediaItem? = map(
        row = row,
        requiredPrefix = "video/",
        type = MediaType.VIDEO,
        uriForId = ::videoUriString,
        fallbackNamePrefix = "video_",
        defaultMime = "video/*",
        defaultBucket = "Unknown",
    )

    fun mapAudio(row: MediaCursorRow): MediaItem? = map(
        row = row,
        requiredPrefix = "audio/",
        type = MediaType.AUDIO,
        uriForId = ::audioUriString,
        fallbackNamePrefix = "audio_",
        defaultMime = "audio/*",
        defaultBucket = "Unknown",
    )

    private fun map(
        row: MediaCursorRow,
        requiredPrefix: String,
        type: MediaType,
        uriForId: (Long) -> String,
        fallbackNamePrefix: String,
        defaultMime: String,
        defaultBucket: String,
    ): MediaItem? {
        val id = row.id ?: return null
        if (id < 0L) return null
        val mime = row.mimeType?.lowercase().orEmpty()
        if (mime.isNotEmpty() && !mime.startsWith(requiredPrefix)) return null
        val dateTaken = row.dateTakenMs ?: 0L
        val dateModifiedMs = (row.dateModifiedSeconds ?: 0L) * 1000L
        return MediaItem(
            id = id,
            uriString = uriForId(id),
            displayName = row.displayName?.takeIf { it.isNotBlank() } ?: "$fallbackNamePrefix$id",
            mimeType = mime.ifBlank { defaultMime },
            sizeBytes = row.sizeBytes ?: 0L,
            width = row.width ?: 0,
            height = row.height ?: 0,
            durationMs = (row.durationMs ?: 0L).coerceAtLeast(0L),
            dateTakenEpochMs = if (dateTaken > 0L) dateTaken else dateModifiedMs,
            dateModifiedEpochMs = dateModifiedMs,
            bucketId = row.bucketId ?: 0L,
            bucketName = row.bucketName?.takeIf { it.isNotBlank() } ?: defaultBucket,
            mediaType = type,
        )
    }
}
