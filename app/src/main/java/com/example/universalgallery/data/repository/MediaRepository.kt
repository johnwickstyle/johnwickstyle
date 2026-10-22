package com.example.universalgallery.data.repository

import com.example.universalgallery.data.model.MediaAlbum
import com.example.universalgallery.data.model.MediaItem
import com.example.universalgallery.data.model.MediaSortOrder
import kotlinx.coroutines.flow.Flow

interface MediaRepository {
    fun observeImages(sortOrder: MediaSortOrder = MediaSortOrder.DATE_DESC): Flow<List<MediaItem>>
    fun observeImageAlbums(sortOrder: MediaSortOrder = MediaSortOrder.DATE_DESC): Flow<List<MediaAlbum>>
    fun observeVideos(sortOrder: MediaSortOrder = MediaSortOrder.DATE_DESC): Flow<List<MediaItem>>
    fun observeAudio(sortOrder: MediaSortOrder = MediaSortOrder.DATE_DESC): Flow<List<MediaItem>>
    suspend fun getImage(id: Long): MediaItem?
    suspend fun getVideo(id: Long): MediaItem?
    suspend fun getAudio(id: Long): MediaItem?
}
