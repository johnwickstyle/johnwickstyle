package com.example.universalgallery.data.repository

import com.example.universalgallery.data.model.ImageCursorRow
import com.example.universalgallery.data.model.MediaAlbum
import com.example.universalgallery.data.model.MediaItem
import com.example.universalgallery.data.model.MediaType

object ImageMediaMapper {
    fun contentUriString(id: Long): String {
        return "content://media/external/images/media/$id"
    }

    fun map(row: ImageCursorRow): MediaItem? {
        val id = row.id ?: return null
        if (id < 0L) return null
        val mime = row.mimeType?.lowercase().orEmpty()
        if (mime.isNotEmpty() && !mime.startsWith("image/")) return null
        val dateTaken = row.dateTakenMs ?: 0L
        val dateModifiedMs = (row.dateModifiedSeconds ?: 0L) * 1000L
        return MediaItem(
            id = id,
            uriString = contentUriString(id),
            displayName = row.displayName?.takeIf { it.isNotBlank() } ?: "image_$id",
            mimeType = mime.ifBlank { "image/*" },
            sizeBytes = row.sizeBytes ?: 0L,
            width = row.width ?: 0,
            height = row.height ?: 0,
            durationMs = 0L,
            dateTakenEpochMs = if (dateTaken > 0L) dateTaken else dateModifiedMs,
            dateModifiedEpochMs = dateModifiedMs,
            bucketId = row.bucketId ?: 0L,
            bucketName = row.bucketName?.takeIf { it.isNotBlank() } ?: "Unknown",
            mediaType = MediaType.IMAGE,
        )
    }

    fun groupAlbums(items: List<MediaItem>): List<MediaAlbum> {
        return items
            .groupBy { it.bucketId }
            .map { (bucketId, grouped) ->
                val cover = grouped.maxByOrNull { it.dateTakenEpochMs }
                MediaAlbum(
                    bucketId = bucketId,
                    name = cover?.bucketName ?: "Unknown",
                    itemCount = grouped.size,
                    coverUriString = cover?.uriString,
                )
            }
            .sortedBy { it.name.lowercase() }
    }
}
