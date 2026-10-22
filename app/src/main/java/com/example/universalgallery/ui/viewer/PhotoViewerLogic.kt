package com.example.universalgallery.ui.viewer

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object PhotoViewerLogic {
    const val ROUTE_PATTERN = "photo/{mediaId}"
    const val ARG_MEDIA_ID = "mediaId"
    const val MIN_SCALE = 1f
    const val MAX_SCALE = 5f

    fun route(mediaId: Long): String = "photo/$mediaId"

    fun parseMediaId(raw: String?): Long? {
        val id = raw?.toLongOrNull() ?: return null
        return id.takeIf { it >= 0L }
    }

    fun formatFileSize(bytes: Long): String {
        if (bytes <= 0L) return "0 B"
        if (bytes < 1024L) return "$bytes B"
        val kb = bytes / 1024.0
        if (bytes < 1024L * 1024L) {
            return String.format(Locale.US, "%.1f KB", kb)
        }
        val mb = bytes / (1024.0 * 1024.0)
        if (bytes < 1024L * 1024L * 1024L) {
            return String.format(Locale.US, "%.1f MB", mb)
        }
        val gb = bytes / (1024.0 * 1024.0 * 1024.0)
        return String.format(Locale.US, "%.1f GB", gb)
    }

    fun formatDimensions(width: Int, height: Int): String {
        if (width <= 0 || height <= 0) return ""
        return "$width × $height"
    }

    fun formatDate(epochMs: Long): String {
        if (epochMs <= 0L) return ""
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US)
        formatter.timeZone = TimeZone.getTimeZone("UTC")
        return formatter.format(Date(epochMs))
    }

    fun clampScale(scale: Float): Float = scale.coerceIn(MIN_SCALE, MAX_SCALE)

    fun adjacentIndex(current: Int, delta: Int, size: Int): Int? {
        if (size <= 0) return null
        val next = current + delta
        return next.takeIf { it in 0 until size }
    }

    fun canShare(uriString: String, mimeType: String): Boolean {
        return uriString.startsWith("content://") && mimeType.startsWith("image/")
    }
}
