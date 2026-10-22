package com.example.universalgallery.ui.documents

import java.util.Base64

enum class DocumentKind {
    PDF,
    TEXT,
    UNSUPPORTED,
}

object DocumentLogic {
    const val ROUTE_PATTERN = "document/{encodedUri}"
    const val ARG_ENCODED_URI = "encodedUri"
    const val MAX_TEXT_BYTES = 1_048_576L

    fun encodeUri(uri: String): String {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(uri.toByteArray(Charsets.UTF_8))
    }

    fun parseUri(encoded: String?): String? {
        if (encoded.isNullOrBlank()) return null
        return try {
            String(Base64.getUrlDecoder().decode(encoded), Charsets.UTF_8).takeIf { it.isNotBlank() }
        } catch (_: IllegalArgumentException) {
            null
        }
    }

    fun route(uri: String): String = "document/${encodeUri(uri)}"

    fun classify(mimeType: String): DocumentKind {
        val mime = mimeType.lowercase()
        return when {
            mime == "application/pdf" -> DocumentKind.PDF
            mime.startsWith("text/") -> DocumentKind.TEXT
            mime == "application/json" -> DocumentKind.TEXT
            else -> DocumentKind.UNSUPPORTED
        }
    }

    fun canRenderInApp(mimeType: String): Boolean {
        val kind = classify(mimeType)
        return kind == DocumentKind.PDF || kind == DocumentKind.TEXT
    }

    fun canOpenExternally(uriString: String): Boolean = uriString.startsWith("content://")

    fun isTextTooLarge(sizeBytes: Long): Boolean = sizeBytes > MAX_TEXT_BYTES
}
