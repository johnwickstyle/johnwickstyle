package com.example.universalgallery.data.repository

import android.net.Uri
import com.example.universalgallery.data.model.DocumentItem
import kotlinx.coroutines.flow.Flow

interface DocumentRepository {
    fun observeDocuments(): Flow<List<DocumentItem>>
    suspend fun persist(uris: List<Uri>)
    suspend fun getDocument(uriString: String): DocumentItem?
    suspend fun readText(uriString: String, maxBytes: Long): Pair<String, Boolean>
}
