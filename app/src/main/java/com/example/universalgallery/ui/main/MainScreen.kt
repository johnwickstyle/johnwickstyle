package com.example.universalgallery.ui.main

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.universalgallery.R
import com.example.universalgallery.navigation.GalleryBottomBar
import com.example.universalgallery.navigation.GalleryNavHost
import com.example.universalgallery.navigation.NavDestination
import com.example.universalgallery.permissions.MediaPermissionStatus
import com.example.universalgallery.permissions.PermissionHelper
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val cameraUnavailable = stringResource(R.string.camera_unavailable)
    val cameraDenied = stringResource(R.string.permission_camera_denied)
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val currentDestination = NavDestination.fromRoute(currentRoute)
    val viewerVisible = NavDestination.isFullScreenViewer(currentRoute)

    fun launchCamera() {
        val captureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val canHandle = captureIntent.resolveActivity(context.packageManager) != null
        if (!canHandle) {
            scope.launch { snackbarHostState.showSnackbar(cameraUnavailable) }
            return
        }
        try {
            context.startActivity(captureIntent)
        } catch (_: ActivityNotFoundException) {
            scope.launch { snackbarHostState.showSnackbar(cameraUnavailable) }
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted ->
        if (granted) {
            launchCamera()
        } else {
            scope.launch { snackbarHostState.showSnackbar(cameraDenied) }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            if (!viewerVisible) {
                TopAppBar(
                    title = { Text(stringResource(currentDestination.labelRes)) },
                )
            }
        },
        bottomBar = {
            if (!viewerVisible) {
                GalleryBottomBar(
                    currentRoute = currentRoute,
                    onDestinationSelected = { destination ->
                        if (destination.route != currentRoute) {
                            navController.navigate(destination.route) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                )
            }
        },
        floatingActionButton = {
            if (!viewerVisible) {
                FloatingActionButton(
                    onClick = {
                        when (PermissionHelper.checkCamera(context)) {
                            MediaPermissionStatus.GrantedFull,
                            MediaPermissionStatus.GrantedPartial,
                            -> launchCamera()
                            MediaPermissionStatus.NotRequested,
                            is MediaPermissionStatus.Denied,
                            -> cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                        }
                    },
                ) {
                    Icon(
                        imageVector = Icons.Outlined.PhotoCamera,
                        contentDescription = stringResource(R.string.camera_action),
                    )
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        GalleryNavHost(
            navController = navController,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        )
    }
}
