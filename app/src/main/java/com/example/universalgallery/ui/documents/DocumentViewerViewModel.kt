package com.example.universalgallery.ui.documents

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.universalgallery.data.model.DocumentItem
import com.example.universalgallery.data.repository.DocumentRepository
import com.example.universalgallery.data.repository.SafDocumentRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class DocumentViewerUiState {
    data object Loading : DocumentViewerUiState()
    data object Missing : DocumentViewerUiState()
    data class Pdf(val item: DocumentItem) : DocumentViewerUiState()
    data class Text(
        val item: DocumentItem,
        val body: String,
        val truncated: Boolean,
    ) : DocumentViewerUiState()
    data class Unsupported(val item: DocumentItem) : DocumentViewerUiState()
    data class Error(val canRetry: Boolean = true) : DocumentViewerUiState()
}

class DocumentViewerViewModel(
    application: Application,
    private val uriString: String?,
    private val repository: DocumentRepository = SafDocumentRepository(application),
) : AndroidViewModel(application) {

    private val _state = MutableStateFlow<DocumentViewerUiState>(DocumentViewerUiState.Loading)
    val state: StateFlow<DocumentViewerUiState> = _state.asStateFlow()

    init {
        load()
    }

    fun load() {
        val uri = uriString
        if (uri.isNullOrBlank() || !DocumentLogic.canOpenExternally(uri)) {
            _state.value = DocumentViewerUiState.Missing
            return
        }
        viewModelScope.launch {
            _state.value = DocumentViewerUiState.Loading
            try {
                val item = repository.getDocument(uri)
                if (item == null) {
                    _state.value = DocumentViewerUiState.Missing
                    return@launch
                }
                when (DocumentLogic.classify(item.mimeType)) {
                    DocumentKind.PDF -> _state.value = DocumentViewerUiState.Pdf(item)
                    DocumentKind.TEXT -> {
                        if (DocumentLogic.isTextTooLarge(item.sizeBytes) && item.sizeBytes > 0L) {
                            val (body, _) = repository.readText(uri, DocumentLogic.MAX_TEXT_BYTES)
                            _state.value = DocumentViewerUiState.Text(item, body, truncated = true)
                        } else {
                            val (body, truncated) = repository.readText(uri, DocumentLogic.MAX_TEXT_BYTES)
                            _state.value = DocumentViewerUiState.Text(item, body, truncated)
                        }
                    }
                    DocumentKind.UNSUPPORTED -> _state.value = DocumentViewerUiState.Unsupported(item)
                }
            } catch (throwable: CancellationException) {
                throw throwable
            } catch (_: Exception) {
                _state.value = DocumentViewerUiState.Error()
            }
        }
    }

    companion object {
        fun factory(application: Application, uriString: String?): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return DocumentViewerViewModel(
                        application = application,
                        uriString = uriString,
                    ) as T
                }
            }
        }
    }
}
