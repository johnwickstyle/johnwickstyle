package com.example.universalgallery.permissions

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.universalgallery.ui.components.PartialAccessBanner
import com.example.universalgallery.ui.components.PermissionRationaleCard

@Composable
fun MediaPermissionGate(
    type: MediaPermissionType,
    title: String,
    body: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val activity = context.findActivity()
    val lifecycleOwner = LocalLifecycleOwner.current
    var status by remember { mutableStateOf(currentStatus(context, type)) }
    var userResponded by remember { mutableStateOf(status !is MediaPermissionStatus.NotRequested) }

    fun refresh() {
        val checked = currentStatus(context, type)
        status = when {
            checked is MediaPermissionStatus.GrantedFull -> checked
            checked is MediaPermissionStatus.GrantedPartial -> checked
            !userResponded -> MediaPermissionStatus.NotRequested
            else -> {
                val permanentlyDenied = activity != null && type.runtimePermissions.all { permission ->
                    !PermissionHelper.isGranted(context, permission) &&
                        !ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)
                }
                MediaPermissionStatus.Denied(permanentlyDenied = permanentlyDenied)
            }
        }
    }

    val requestLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
    ) { result ->
        userResponded = true
        val permanentlyDenied = activity != null && result.any { (permission, granted) ->
            !granted && !ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)
        }
        status = when (type) {
            MediaPermissionType.Visual -> PermissionHelper.evaluateVisual(
                imagesGranted = result[android.Manifest.permission.READ_MEDIA_IMAGES] == true,
                videoGranted = result[android.Manifest.permission.READ_MEDIA_VIDEO] == true,
                selectedGranted = result[android.Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED] == true,
                userHasResponded = true,
                permanentlyDenied = permanentlyDenied,
            )
            MediaPermissionType.Audio -> PermissionHelper.evaluateAudio(
                audioGranted = result[android.Manifest.permission.READ_MEDIA_AUDIO] == true,
                userHasResponded = true,
                permanentlyDenied = permanentlyDenied,
            )
            MediaPermissionType.Camera -> PermissionHelper.evaluateCamera(
                cameraGranted = result[android.Manifest.permission.CAMERA] == true,
                userHasResponded = true,
                permanentlyDenied = permanentlyDenied,
            )
        }
        refresh()
    }

    val pickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickMultipleVisualMedia(),
    ) {
        refresh()
    }

    DisposableEffect(lifecycleOwner, type) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                refresh()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    when (val current = status) {
        MediaPermissionStatus.GrantedFull -> {
            Column(modifier = modifier.fillMaxSize()) {
                content()
            }
        }
        MediaPermissionStatus.GrantedPartial -> {
            Column(modifier = modifier.fillMaxSize()) {
                if (type == MediaPermissionType.Visual) {
                    PartialAccessBanner(
                        onAddPhotos = {
                            pickerLauncher.launch(
                                androidx.activity.result.PickVisualMediaRequest(
                                    ActivityResultContracts.PickVisualMedia.ImageAndVideo,
                                ),
                            )
                        },
                        onManageAccess = {
                            context.startActivity(PermissionHelper.appSettingsIntent(context))
                        },
                    )
                }
                content()
            }
        }
        MediaPermissionStatus.NotRequested,
        is MediaPermissionStatus.Denied,
        -> {
            PermissionRationaleCard(
                title = title,
                body = body,
                status = current,
                onAllowAccess = { requestLauncher.launch(type.runtimePermissions) },
                onOpenSettings = {
                    context.startActivity(PermissionHelper.appSettingsIntent(context))
                },
                modifier = modifier,
            )
        }
    }
}

private fun currentStatus(context: Context, type: MediaPermissionType): MediaPermissionStatus {
    return when (type) {
        MediaPermissionType.Visual -> PermissionHelper.checkVisual(context)
        MediaPermissionType.Audio -> PermissionHelper.checkAudio(context)
        MediaPermissionType.Camera -> PermissionHelper.checkCamera(context)
    }
}

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
