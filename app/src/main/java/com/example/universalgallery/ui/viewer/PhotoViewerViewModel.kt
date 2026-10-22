package com.example.universalgallery.ui.viewer

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.universalgallery.data.model.MediaItem
import com.example.universalgallery.data.repository.MediaRepository
import com.example.universalgallery.data.repository.MediaStoreMediaRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class PhotoViewerUiState {
    data object Loading : PhotoViewerUiState()
    data object Missing : PhotoViewerUiState()
    data class Ready(val item: MediaItem) : PhotoViewerUiState()
    data class Error(val canRetry: Boolean = true) : PhotoViewerUiState()
}

class PhotoViewerViewModel(
    application: Application,
    private val mediaId: Long?,
    private val repository: MediaRepository = MediaStoreMediaRepository(application.contentResolver),
) : AndroidViewModel(application) {

    private val _state = MutableStateFlow<PhotoViewerUiState>(PhotoViewerUiState.Loading)
    val state: StateFlow<PhotoViewerUiState> = _state.asStateFlow()

    init {
        load()
    }

    fun load() {
        val id = mediaId
        if (id == null) {
            _state.value = PhotoViewerUiState.Missing
            return
        }
        viewModelScope.launch {
            _state.value = PhotoViewerUiState.Loading
            try {
                val item = repository.getImage(id)
                _state.value = if (item == null) {
                    PhotoViewerUiState.Missing
                } else {
                    PhotoViewerUiState.Ready(item)
                }
            } catch (throwable: CancellationException) {
                throw throwable
            } catch (_: Exception) {
                _state.value = PhotoViewerUiState.Error()
            }
        }
    }

    companion object {
        fun factory(application: Application, mediaId: Long?): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return PhotoViewerViewModel(
                        application = application,
                        mediaId = mediaId,
                    ) as T
                }
            }
        }
    }
}
