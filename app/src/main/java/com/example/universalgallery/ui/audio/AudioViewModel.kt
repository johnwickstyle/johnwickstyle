package com.example.universalgallery.ui.audio

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

class AudioViewModel(
    application: Application,
    private val repository: MediaRepository = MediaStoreMediaRepository(application.contentResolver),
) : AndroidViewModel(application) {

    private val _audio = MutableStateFlow<MediaListUiState>(MediaListUiState.Loading)
    val audio: StateFlow<MediaListUiState> = _audio.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _audio.value = MediaListUiState.Loading
            repository.observeAudio()
                .catch { throwable ->
                    if (throwable is CancellationException) throw throwable
                    _audio.value = MediaListUiState.Error()
                }
                .collect { items ->
                    _audio.value = if (items.isEmpty()) {
                        MediaListUiState.Empty
                    } else {
                        MediaListUiState.Success(items)
                    }
                }
        }
    }
}
