package com.example.universalgallery.ui.player

enum class PlaybackFailure {
    UNSUPPORTED,
    IO,
    UNKNOWN,
}

object PlayerLogic {
    const val VIDEO_ROUTE_PATTERN = "watch/{mediaId}"
    const val AUDIO_ROUTE_PATTERN = "listen/{mediaId}"
    const val ARG_MEDIA_ID = "mediaId"

    val allowedSpeeds: List<Float> = listOf(0.5f, 0.75f, 1f, 1.25f, 1.5f, 2f)

    fun videoRoute(mediaId: Long): String = "watch/$mediaId"

    fun audioRoute(mediaId: Long): String = "listen/$mediaId"

    fun parseMediaId(raw: String?): Long? {
        val id = raw?.toLongOrNull() ?: return null
        return id.takeIf { it >= 0L }
    }

    fun clampPosition(positionMs: Long, durationMs: Long): Long {
        if (durationMs <= 0L) return 0L
        return positionMs.coerceIn(0L, durationMs)
    }

    fun canPlay(uriString: String, mimeType: String, requiredPrefix: String): Boolean {
        return uriString.startsWith("content://") && mimeType.startsWith(requiredPrefix)
    }

    fun mapFailure(isUnsupported: Boolean, isIo: Boolean): PlaybackFailure {
        return when {
            isUnsupported -> PlaybackFailure.UNSUPPORTED
            isIo -> PlaybackFailure.IO
            else -> PlaybackFailure.UNKNOWN
        }
    }

    fun clampSpeed(speed: Float): Float {
        val min = allowedSpeeds.first()
        val max = allowedSpeeds.last()
        return speed.coerceIn(min, max)
    }
}
