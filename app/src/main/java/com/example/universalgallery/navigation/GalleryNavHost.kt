package com.example.universalgallery.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.universalgallery.ui.audio.AudioScreen
import com.example.universalgallery.ui.documents.DocumentLogic
import com.example.universalgallery.ui.documents.DocumentViewerRoute
import com.example.universalgallery.ui.documents.DocumentsScreen
import com.example.universalgallery.ui.folders.FoldersScreen
import com.example.universalgallery.ui.images.ImagesScreen
import com.example.universalgallery.ui.player.AudioPlayerRoute
import com.example.universalgallery.ui.player.PlayerLogic
import com.example.universalgallery.ui.player.VideoPlayerRoute
import com.example.universalgallery.ui.videos.VideosScreen
import com.example.universalgallery.ui.viewer.PhotoViewerLogic
import com.example.universalgallery.ui.viewer.PhotoViewerRoute

@Composable
fun GalleryNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = NavDestination.Images.route,
        modifier = modifier,
    ) {
        composable(NavDestination.Images.route) {
            ImagesScreen(
                onItemClick = { item ->
                    navController.navigate(PhotoViewerLogic.route(item.id))
                },
            )
        }
        composable(NavDestination.Videos.route) {
            VideosScreen(
                onItemClick = { item ->
                    navController.navigate(PlayerLogic.videoRoute(item.id))
                },
            )
        }
        composable(NavDestination.Audio.route) {
            AudioScreen(
                onItemClick = { item ->
                    navController.navigate(PlayerLogic.audioRoute(item.id))
                },
            )
        }
        composable(NavDestination.Documents.route) {
            DocumentsScreen(
                onItemClick = { item ->
                    navController.navigate(DocumentLogic.route(item.uriString))
                },
            )
        }
        composable(NavDestination.Folders.route) { FoldersScreen() }
        composable(
            route = PhotoViewerLogic.ROUTE_PATTERN,
            arguments = listOf(
                navArgument(PhotoViewerLogic.ARG_MEDIA_ID) { type = NavType.StringType },
            ),
        ) { entry ->
            PhotoViewerRoute(
                mediaId = entry.arguments?.getString(PhotoViewerLogic.ARG_MEDIA_ID),
                onBack = { navController.popBackStack() },
            )
        }
        composable(
            route = PlayerLogic.VIDEO_ROUTE_PATTERN,
            arguments = listOf(
                navArgument(PlayerLogic.ARG_MEDIA_ID) { type = NavType.StringType },
            ),
        ) { entry ->
            VideoPlayerRoute(
                mediaId = entry.arguments?.getString(PlayerLogic.ARG_MEDIA_ID),
                onBack = { navController.popBackStack() },
            )
        }
        composable(
            route = PlayerLogic.AUDIO_ROUTE_PATTERN,
            arguments = listOf(
                navArgument(PlayerLogic.ARG_MEDIA_ID) { type = NavType.StringType },
            ),
        ) { entry ->
            AudioPlayerRoute(
                mediaId = entry.arguments?.getString(PlayerLogic.ARG_MEDIA_ID),
                onBack = { navController.popBackStack() },
            )
        }
        composable(
            route = DocumentLogic.ROUTE_PATTERN,
            arguments = listOf(
                navArgument(DocumentLogic.ARG_ENCODED_URI) { type = NavType.StringType },
            ),
        ) { entry ->
            DocumentViewerRoute(
                encodedUri = entry.arguments?.getString(DocumentLogic.ARG_ENCODED_URI),
                onBack = { navController.popBackStack() },
            )
        }
    }
}
