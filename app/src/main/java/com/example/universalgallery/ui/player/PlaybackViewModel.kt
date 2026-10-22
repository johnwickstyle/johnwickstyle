package com.example.universalgallery.ui.player

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem as ExoMediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.universalgallery.data.model.MediaItem
import com.example.universalgallery.data.repository.MediaRepository
import com.example.universalgallery.data.repository.MediaStoreMediaRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class PlaybackKind {
    VIDEO,
    AUDIO,
}

sealed class PlaybackUiState {
    data object Loading : PlaybackUiState()
    data object Missing : PlaybackUiState()
    data class Ready(val item: MediaItem) : PlaybackUiState()
    data class Failed(val failure: PlaybackFailure, val canRetry: Boolean = true) : PlaybackUiState()
}

class PlaybackViewModel(
    application: Application,
    private val mediaId: Long?,
    private val kind: PlaybackKind,
    private val repository: MediaRepository = MediaStoreMediaRepository(application.contentResolver),
) : AndroidViewModel(application) {

    val player: ExoPlayer = ExoPlayer.Builder(application).build()

    private val _state = MutableStateFlow<PlaybackUiState>(PlaybackUiState.Loading)
    val state: StateFlow<PlaybackUiState> = _state.asStateFlow()

    private val listener = object : Player.Listener {
        override fun onPlayerError(error: PlaybackException) {
            val failure = PlayerLogic.mapFailure(
                isUnsupported = error.errorCode == PlaybackException.ERROR_CODE_DECODING_FAILED ||
                    error.errorCode == PlaybackException.ERROR_CODE_PARSING_CONTAINER_UNSUPPORTED ||
                    error.errorCode == PlaybackException.ERROR_CODE_PARSING_MANIFEST_UNSUPPORTED ||
                    error.errorCode == PlaybackException.ERROR_CODE_DECODER_INIT_FAILED ||
                    error.errorCode == PlaybackException.ERROR_CODE_DECODER_QUERY_FAILED,
                isIo = error.errorCode == PlaybackException.ERROR_CODE_IO_UNSPECIFIED ||
                    error.errorCode == PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_FAILED ||
                    error.errorCode == PlaybackException.ERROR_CODE_IO_FILE_NOT_FOUND ||
                    error.errorCode == PlaybackException.ERROR_CODE_IO_BAD_HTTP_STATUS ||
                    error.errorCode == PlaybackException.ERROR_CODE_IO_NO_PERMISSION,
            )
            _state.value = PlaybackUiState.Failed(failure)
        }
    }

    init {
        player.addListener(listener)
        load()
    }

    fun load() {
        val id = mediaId
        if (id == null) {
            _state.value = PlaybackUiState.Missing
            return
        }
        viewModelScope.launch {
            _state.value = PlaybackUiState.Loading
            try {
                val item = when (kind) {
                    PlaybackKind.VIDEO -> repository.getVideo(id)
                    PlaybackKind.AUDIO -> repository.getAudio(id)
                }
                if (item == null) {
                    _state.value = PlaybackUiState.Missing
                    return@launch
                }
                val prefix = if (kind == PlaybackKind.VIDEO) "video/" else "audio/"
                if (!PlayerLogic.canPlay(item.uriString, item.mimeType, prefix)) {
                    _state.value = PlaybackUiState.Failed(PlaybackFailure.UNSUPPORTED, canRetry = false)
                    return@launch
                }
                player.setMediaItem(ExoMediaItem.fromUri(Uri.parse(item.uriString)))
                player.prepare()
                player.playWhenReady = true
                _state.value = PlaybackUiState.Ready(item)
            } catch (throwable: CancellationException) {
                throw throwable
            } catch (_: Exception) {
                _state.value = PlaybackUiState.Failed(PlaybackFailure.UNKNOWN)
            }
        }
    }

    fun setSpeed(speed: Float) {
        player.setPlaybackSpeed(PlayerLogic.clampSpeed(speed))
    }

    override fun onCleared() {
        player.removeListener(listener)
        player.release()
        super.onCleared()
    }

    companion object {
        fun factory(
            application: Application,
            mediaId: Long?,
            kind: PlaybackKind,
        ): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return PlaybackViewModel(
                        application = application,
                        mediaId = mediaId,
                        kind = kind,
                    ) as T
                }
            }
        }
    }
}
