package com.example.universalgallery.data.repository

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.DocumentsContract
import android.provider.OpenableColumns
import com.example.universalgallery.data.model.DocumentItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext

class SafDocumentRepository(
    private val context: Context,
) : DocumentRepository {

    private val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
    private val documents = MutableStateFlow<List<DocumentItem>>(emptyList())

    override fun observeDocuments(): Flow<List<DocumentItem>> {
        return documents
            .onStart { documents.value = loadAll() }
            .flowOn(Dispatchers.IO)
    }

    override suspend fun persist(uris: List<Uri>) = withContext(Dispatchers.IO) {
        val stored = storedUris().toMutableSet()
        uris.forEach { uri ->
            try {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION,
                )
                stored.add(uri.toString())
            } catch (_: SecurityException) {
                if (queryItem(uri) != null) {
                    stored.add(uri.toString())
                }
            }
        }
        prefs.edit().putStringSet(KEY_URIS, stored).apply()
        documents.value = loadAll()
    }

    override suspend fun getDocument(uriString: String): DocumentItem? = withContext(Dispatchers.IO) {
        queryItem(Uri.parse(uriString))
    }

    override suspend fun readText(uriString: String, maxBytes: Long): Pair<String, Boolean> {
        return withContext(Dispatchers.IO) {
            val uri = Uri.parse(uriString)
            context.contentResolver.openInputStream(uri)?.use { stream ->
                val limit = maxBytes.toInt().coerceAtLeast(0)
                val buffer = ByteArray(limit + 1)
                val read = stream.read(buffer, 0, buffer.size)
                if (read <= 0) return@withContext "" to false
                val truncated = read > limit
                val count = minOf(read, limit)
                String(buffer, 0, count, Charsets.UTF_8) to truncated
            } ?: ("" to false)
        }
    }

    private fun loadAll(): List<DocumentItem> {
        val items = mutableListOf<DocumentItem>()
        val valid = mutableSetOf<String>()
        storedUris().forEach { raw ->
            val item = queryItem(Uri.parse(raw))
            if (item != null) {
                items.add(item)
                valid.add(raw)
            }
        }
        if (valid.size != storedUris().size) {
            prefs.edit().putStringSet(KEY_URIS, valid).apply()
        }
        return items.sortedByDescending { it.lastModifiedEpochMs }
    }

    private fun storedUris(): Set<String> = prefs.getStringSet(KEY_URIS, emptySet()).orEmpty()

    private fun queryItem(uri: Uri): DocumentItem? {
        return try {
            context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                if (!cursor.moveToFirst()) return null
                val nameIdx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                val sizeIdx = cursor.getColumnIndex(OpenableColumns.SIZE)
                val mime = context.contentResolver.getType(uri).orEmpty()
                val modified = queryLastModified(uri)
                DocumentItem(
                    uriString = uri.toString(),
                    displayName = if (nameIdx >= 0) cursor.getString(nameIdx).orEmpty().ifBlank { "document" } else "document",
                    mimeType = mime.ifBlank { "application/octet-stream" },
                    sizeBytes = if (sizeIdx >= 0 && !cursor.isNull(sizeIdx)) cursor.getLong(sizeIdx) else 0L,
                    lastModifiedEpochMs = modified,
                )
            }
        } catch (_: SecurityException) {
            null
        } catch (_: IllegalArgumentException) {
            null
        }
    }

    private fun queryLastModified(uri: Uri): Long {
        return try {
            context.contentResolver.query(
                uri,
                arrayOf(DocumentsContract.Document.COLUMN_LAST_MODIFIED),
                null,
                null,
                null,
            )?.use { cursor ->
                if (!cursor.moveToFirst()) return 0L
                val idx = cursor.getColumnIndex(DocumentsContract.Document.COLUMN_LAST_MODIFIED)
                if (idx >= 0 && !cursor.isNull(idx)) cursor.getLong(idx) else 0L
            } ?: 0L
        } catch (_: Exception) {
            0L
        }
    }

    private companion object {
        const val PREFS = "saf_documents"
        const val KEY_URIS = "uris"
    }
}
