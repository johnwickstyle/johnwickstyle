package com.example.universalgallery.ui.images

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.universalgallery.data.model.MediaAlbum
import com.example.universalgallery.data.model.MediaItem
import com.example.universalgallery.data.repository.MediaRepository
import com.example.universalgallery.data.repository.MediaStoreMediaRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

sealed class MediaListUiState {
    data object Loading : MediaListUiState()
    data object Empty : MediaListUiState()
    data class Success(val items: List<MediaItem>) : MediaListUiState()
    data class Error(val canRetry: Boolean = true) : MediaListUiState()
}

sealed class AlbumListUiState {
    data object Loading : AlbumListUiState()
    data object Empty : AlbumListUiState()
    data class Success(val albums: List<MediaAlbum>) : AlbumListUiState()
    data class Error(val canRetry: Boolean = true) : AlbumListUiState()
}

class ImagesViewModel(
    application: Application,
    private val repository: MediaRepository = MediaStoreMediaRepository(application.contentResolver),
) : AndroidViewModel(application) {

    private val _images = MutableStateFlow<MediaListUiState>(MediaListUiState.Loading)
    val images: StateFlow<MediaListUiState> = _images.asStateFlow()

    private val _albums = MutableStateFlow<AlbumListUiState>(AlbumListUiState.Loading)
    val albums: StateFlow<AlbumListUiState> = _albums.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _images.value = MediaListUiState.Loading
            repository.observeImages()
                .catch { throwable ->
                    if (throwable is CancellationException) throw throwable
                    _images.value = MediaListUiState.Error()
                }
                .collect { items ->
                    _images.value = if (items.isEmpty()) {
                        MediaListUiState.Empty
                    } else {
                        MediaListUiState.Success(items)
                    }
                }
        }
        viewModelScope.launch {
            _albums.value = AlbumListUiState.Loading
            repository.observeImageAlbums()
                .catch { throwable ->
                    if (throwable is CancellationException) throw throwable
                    _albums.value = AlbumListUiState.Error()
                }
                .collect { albums ->
                    _albums.value = if (albums.isEmpty()) {
                        AlbumListUiState.Empty
                    } else {
                        AlbumListUiState.Success(albums)
                    }
                }
        }
    }
}
