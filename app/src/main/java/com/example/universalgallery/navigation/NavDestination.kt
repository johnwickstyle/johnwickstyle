package com.example.universalgallery.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AudioFile
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Videocam
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.universalgallery.R

sealed class NavDestination(
    val route: String,
    @StringRes val labelRes: Int,
    val icon: ImageVector,
) {
    data object Images : NavDestination(
        route = "images",
        labelRes = R.string.destination_image,
        icon = Icons.Outlined.Image,
    )

    data object Videos : NavDestination(
        route = "videos",
        labelRes = R.string.destination_video,
        icon = Icons.Outlined.Videocam,
    )

    data object Audio : NavDestination(
        route = "audio",
        labelRes = R.string.destination_audio,
        icon = Icons.Outlined.AudioFile,
    )

    data object Documents : NavDestination(
        route = "documents",
        labelRes = R.string.destination_documents,
        icon = Icons.Outlined.Description,
    )

    data object Folders : NavDestination(
        route = "folders",
        labelRes = R.string.destination_folder,
        icon = Icons.Outlined.Folder,
    )

    companion object {
        val bottomDestinations: List<NavDestination> = listOf(
            Images,
            Videos,
            Audio,
            Documents,
            Folders,
        )

        fun fromRoute(route: String?): NavDestination {
            val base = route?.substringBefore("/")
            return bottomDestinations.firstOrNull { it.route == route || it.route == base } ?: Images
        }

        fun isFullScreenViewer(route: String?): Boolean {
            if (route == null) return false
            return route.startsWith("photo/") ||
                route == "photo/{mediaId}" ||
                route.startsWith("watch/") ||
                route == "watch/{mediaId}" ||
                route.startsWith("listen/") ||
                route == "listen/{mediaId}" ||
                route.startsWith("document/") ||
                route == "document/{encodedUri}"
        }
    }
}
