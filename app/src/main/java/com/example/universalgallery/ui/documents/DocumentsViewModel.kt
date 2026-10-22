package com.example.universalgallery.ui.documents

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.universalgallery.data.model.DocumentItem
import com.example.universalgallery.data.repository.DocumentRepository
import com.example.universalgallery.data.repository.SafDocumentRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

sealed class DocumentListUiState {
    data object Loading : DocumentListUiState()
    data object Empty : DocumentListUiState()
    data class Success(val items: List<DocumentItem>) : DocumentListUiState()
    data class Error(val canRetry: Boolean = true) : DocumentListUiState()
}

class DocumentsViewModel(
    application: Application,
    private val repository: DocumentRepository = SafDocumentRepository(application),
) : AndroidViewModel(application) {

    private val _documents = MutableStateFlow<DocumentListUiState>(DocumentListUiState.Loading)
    val documents: StateFlow<DocumentListUiState> = _documents.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _documents.value = DocumentListUiState.Loading
            repository.observeDocuments()
                .catch { throwable ->
                    if (throwable is CancellationException) throw throwable
                    _documents.value = DocumentListUiState.Error()
                }
                .collect { items ->
                    _documents.value = if (items.isEmpty()) {
                        DocumentListUiState.Empty
                    } else {
                        DocumentListUiState.Success(items)
                    }
                }
        }
    }

    fun add(uris: List<Uri>) {
        if (uris.isEmpty()) return
        viewModelScope.launch {
            try {
                repository.persist(uris)
            } catch (throwable: CancellationException) {
                throw throwable
            } catch (_: Exception) {
                _documents.value = DocumentListUiState.Error()
            }
        }
    }
}
