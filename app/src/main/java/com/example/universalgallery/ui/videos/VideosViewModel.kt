package com.example.universalgallery.ui.videos

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.universalgallery.data.repository.MediaRepository
import com.example.universalgallery.data.repository.MediaStoreMediaRepository
import com.example.universalgallery.ui.images.MediaListUiState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class VideosViewModel(
    application: Application,
    private val repository: MediaRepository = MediaStoreMediaRepository(application.contentResolver),
) : AndroidViewModel(application) {

    private val _videos = MutableStateFlow<MediaListUiState>(MediaListUiState.Loading)
    val videos: StateFlow<MediaListUiState> = _videos.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _videos.value = MediaListUiState.Loading
            repository.observeVideos()
                .catch { throwable ->
                    if (throwable is CancellationException) throw throwable
                    _videos.value = MediaListUiState.Error()
                }
                .collect { items ->
                    _videos.value = if (items.isEmpty()) {
                        MediaListUiState.Empty
                    } else {
                        MediaListUiState.Success(items)
                    }
                }
        }
    }
}
