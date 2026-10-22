package com.example.universalgallery.data.repository

import android.content.ContentResolver
import android.database.ContentObserver
import android.database.Cursor
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import com.example.universalgallery.data.model.ImageCursorRow
import com.example.universalgallery.data.model.MediaAlbum
import com.example.universalgallery.data.model.MediaCursorRow
import com.example.universalgallery.data.model.MediaItem
import com.example.universalgallery.data.model.MediaSortOrder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MediaStoreMediaRepository(
    private val contentResolver: ContentResolver,
) : MediaRepository {

    override fun observeImages(sortOrder: MediaSortOrder): Flow<List<MediaItem>> {
        return observeUri(MediaStore.Images.Media.EXTERNAL_CONTENT_URI) {
            queryImages(sortOrder)
        }
    }

    override fun observeImageAlbums(sortOrder: MediaSortOrder): Flow<List<MediaAlbum>> {
        return observeImages(sortOrder).map(ImageMediaMapper::groupAlbums)
    }

    override fun observeVideos(sortOrder: MediaSortOrder): Flow<List<MediaItem>> {
        return observeUri(MediaStore.Video.Media.EXTERNAL_CONTENT_URI) {
            queryCollection(
                uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                projection = VIDEO_PROJECTION,
                sortOrder = sortOrder,
                mapper = { mapVideoRow(it) },
            )
        }
    }

    override fun observeAudio(sortOrder: MediaSortOrder): Flow<List<MediaItem>> {
        return observeUri(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI) {
            queryCollection(
                uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection = AUDIO_PROJECTION,
                sortOrder = sortOrder,
                mapper = { mapAudioRow(it) },
            )
        }
    }

    override suspend fun getImage(id: Long): MediaItem? = withContext(Dispatchers.IO) {
        queryById(
            uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection = IMAGE_PROJECTION,
            id = id,
            mapper = { mapImageRow(it) },
        )
    }

    override suspend fun getVideo(id: Long): MediaItem? = withContext(Dispatchers.IO) {
        queryById(
            uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            projection = VIDEO_PROJECTION,
            id = id,
            mapper = { mapVideoRow(it) },
        )
    }

    override suspend fun getAudio(id: Long): MediaItem? = withContext(Dispatchers.IO) {
        queryById(
            uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection = AUDIO_PROJECTION,
            id = id,
            mapper = { mapAudioRow(it) },
        )
    }

    private fun observeUri(
        uri: Uri,
        query: suspend () -> List<MediaItem>,
    ): Flow<List<MediaItem>> = callbackFlow {
        val observer = object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean) {
                launch { trySend(query()) }
            }
        }
        contentResolver.registerContentObserver(uri, true, observer)
        trySend(query())
        awaitClose { contentResolver.unregisterContentObserver(observer) }
    }.flowOn(Dispatchers.IO)

    private suspend fun queryImages(sortOrder: MediaSortOrder): List<MediaItem> {
        return queryCollection(
            uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection = IMAGE_PROJECTION,
            sortOrder = sortOrder,
            mapper = { mapImageRow(it) },
        )
    }

    private suspend fun queryCollection(
        uri: Uri,
        projection: Array<String>,
        sortOrder: MediaSortOrder,
        mapper: (Cursor) -> MediaItem?,
    ): List<MediaItem> = withContext(Dispatchers.IO) {
        val items = mutableListOf<MediaItem>()
        contentResolver.query(uri, projection, null, null, sortOrder.sql)?.use { cursor ->
            while (cursor.moveToNext()) {
                val mapped = mapper(cursor)
                if (mapped != null) items.add(mapped)
            }
        }
        items
    }

    private fun queryById(
        uri: Uri,
        projection: Array<String>,
        id: Long,
        mapper: (Cursor) -> MediaItem?,
    ): MediaItem? {
        if (id < 0L) return null
        return contentResolver.query(
            uri,
            projection,
            "${MediaStore.MediaColumns._ID}=?",
            arrayOf(id.toString()),
            null,
        )?.use { cursor ->
            if (!cursor.moveToFirst()) return null
            mapper(cursor)
        }
    }

    private fun mapImageRow(cursor: Cursor): MediaItem? {
        return ImageMediaMapper.map(
            ImageCursorRow(
                id = longOrNull(cursor, MediaStore.Images.Media._ID),
                displayName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)),
                mimeType = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.MIME_TYPE)),
                sizeBytes = longOrNull(cursor, MediaStore.Images.Media.SIZE),
                width = intOrNull(cursor, MediaStore.Images.Media.WIDTH),
                height = intOrNull(cursor, MediaStore.Images.Media.HEIGHT),
                dateTakenMs = longOrNull(cursor, MediaStore.Images.Media.DATE_TAKEN),
                dateModifiedSeconds = longOrNull(cursor, MediaStore.Images.Media.DATE_MODIFIED),
                bucketId = longOrNull(cursor, MediaStore.Images.Media.BUCKET_ID),
                bucketName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)),
            ),
        )
    }

    private fun mapVideoRow(cursor: Cursor): MediaItem? {
        return AvMediaMapper.mapVideo(
            MediaCursorRow(
                id = longOrNull(cursor, MediaStore.Video.Media._ID),
                displayName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)),
                mimeType = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE)),
                sizeBytes = longOrNull(cursor, MediaStore.Video.Media.SIZE),
                width = intOrNull(cursor, MediaStore.Video.Media.WIDTH),
                height = intOrNull(cursor, MediaStore.Video.Media.HEIGHT),
                durationMs = longOrNull(cursor, MediaStore.Video.Media.DURATION),
                dateTakenMs = longOrNull(cursor, MediaStore.Video.Media.DATE_TAKEN),
                dateModifiedSeconds = longOrNull(cursor, MediaStore.Video.Media.DATE_MODIFIED),
                bucketId = longOrNull(cursor, MediaStore.Video.Media.BUCKET_ID),
                bucketName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_DISPLAY_NAME)),
            ),
        )
    }

    private fun mapAudioRow(cursor: Cursor): MediaItem? {
        val album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM))
        val bucket = album?.takeIf { it.isNotBlank() } ?: "Unknown"
        return AvMediaMapper.mapAudio(
            MediaCursorRow(
                id = longOrNull(cursor, MediaStore.Audio.Media._ID),
                displayName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)),
                mimeType = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.MIME_TYPE)),
                sizeBytes = longOrNull(cursor, MediaStore.Audio.Media.SIZE),
                width = 0,
                height = 0,
                durationMs = longOrNull(cursor, MediaStore.Audio.Media.DURATION),
                dateTakenMs = 0L,
                dateModifiedSeconds = longOrNull(cursor, MediaStore.Audio.Media.DATE_MODIFIED),
                bucketId = 0L,
                bucketName = bucket,
            ),
        )
    }

    private fun longOrNull(cursor: Cursor, column: String): Long? {
        val index = cursor.getColumnIndexOrThrow(column)
        return if (cursor.isNull(index)) null else cursor.getLong(index)
    }

    private fun intOrNull(cursor: Cursor, column: String): Int? {
        val index = cursor.getColumnIndexOrThrow(column)
        return if (cursor.isNull(index)) null else cursor.getInt(index)
    }

    private companion object {
        val IMAGE_PROJECTION = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.MIME_TYPE,
            MediaStore.Images.Media.SIZE,
            MediaStore.Images.Media.WIDTH,
            MediaStore.Images.Media.HEIGHT,
            MediaStore.Images.Media.DATE_TAKEN,
            MediaStore.Images.Media.DATE_MODIFIED,
            MediaStore.Images.Media.BUCKET_ID,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
        )
        val VIDEO_PROJECTION = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.MIME_TYPE,
            MediaStore.Video.Media.SIZE,
            MediaStore.Video.Media.WIDTH,
            MediaStore.Video.Media.HEIGHT,
            MediaStore.Video.Media.DURATION,
            MediaStore.Video.Media.DATE_TAKEN,
            MediaStore.Video.Media.DATE_MODIFIED,
            MediaStore.Video.Media.BUCKET_ID,
            MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
        )
        val AUDIO_PROJECTION = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.MIME_TYPE,
            MediaStore.Audio.Media.SIZE,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATE_MODIFIED,
            MediaStore.Audio.Media.ALBUM,
        )
    }
}
