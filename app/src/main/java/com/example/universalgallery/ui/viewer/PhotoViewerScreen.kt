package com.example.universalgallery.ui.viewer

import android.app.Application
import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import com.example.universalgallery.R
import com.example.universalgallery.data.model.MediaItem
import kotlinx.coroutines.launch

@Composable
fun PhotoViewerRoute(
    mediaId: String?,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val parsedId = PhotoViewerLogic.parseMediaId(mediaId)
    val viewModel: PhotoViewerViewModel = viewModel(
        key = mediaId,
        factory = PhotoViewerViewModel.factory(context.applicationContext as Application, parsedId),
    )
    val state by viewModel.state.collectAsStateWithLifecycle()
    PhotoViewerScreen(
        state = state,
        onBack = onBack,
        onRetry = viewModel::load,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoViewerScreen(
    state: PhotoViewerUiState,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var chromeVisible by remember { mutableStateOf(true) }
    var detailsVisible by remember { mutableStateOf(false) }
    val shareUnavailable = stringResource(R.string.viewer_share_unavailable)
    val shareFailed = stringResource(R.string.viewer_share_failed)

    BackHandler(onBack = onBack)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black),
    ) {
        when (state) {
            PhotoViewerUiState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Color.White,
                )
            }
            PhotoViewerUiState.Missing -> {
                ViewerMessage(
                    title = stringResource(R.string.viewer_missing_title),
                    body = stringResource(R.string.viewer_missing_body),
                    onBack = onBack,
                )
            }
            is PhotoViewerUiState.Error -> {
                ViewerMessage(
                    title = stringResource(R.string.viewer_error_title),
                    body = stringResource(R.string.viewer_error_body),
                    onBack = onBack,
                    onRetry = if (state.canRetry) onRetry else null,
                )
            }
            is PhotoViewerUiState.Ready -> {
                ZoomablePhoto(
                    item = state.item,
                    onToggleChrome = { chromeVisible = !chromeVisible },
                    modifier = Modifier.fillMaxSize(),
                )
                if (chromeVisible) {
                    ViewerChrome(
                        item = state.item,
                        onBack = onBack,
                        onDetails = { detailsVisible = true },
                        onShare = {
                            if (!PhotoViewerLogic.canShare(state.item.uriString, state.item.mimeType)) {
                                scope.launch { snackbarHostState.showSnackbar(shareUnavailable) }
                                return@ViewerChrome
                            }
                            val shareUri = Uri.parse(state.item.uriString)
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = state.item.mimeType
                                putExtra(Intent.EXTRA_STREAM, shareUri)
                                clipData = ClipData.newRawUri(state.item.displayName, shareUri)
                                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            }
                            try {
                                context.startActivity(
                                    Intent.createChooser(intent, context.getString(R.string.viewer_share)),
                                )
                            } catch (_: ActivityNotFoundException) {
                                scope.launch { snackbarHostState.showSnackbar(shareFailed) }
                            }
                        },
                    )
                }
                if (detailsVisible) {
                    ModalBottomSheet(
                        onDismissRequest = { detailsVisible = false },
                        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                    ) {
                        PhotoDetailsSheet(item = state.item)
                    }
                }
            }
        }
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}

@Composable
private fun ZoomablePhoto(
    item: MediaItem,
    onToggleChrome: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var scale by remember { mutableFloatStateOf(1f) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }
    var loadFailed by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { onToggleChrome() },
                    onDoubleTap = {
                        if (scale > 1f) {
                            scale = 1f
                            offsetX = 0f
                            offsetY = 0f
                        } else {
                            scale = 2.5f
                        }
                    },
                )
            }
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    val nextScale = PhotoViewerLogic.clampScale(scale * zoom)
                    scale = nextScale
                    if (nextScale > 1f) {
                        offsetX += pan.x
                        offsetY += pan.y
                    } else {
                        offsetX = 0f
                        offsetY = 0f
                    }
                }
            },
        contentAlignment = Alignment.Center,
    ) {
        AsyncImage(
            model = item.uriString,
            contentDescription = item.displayName,
            contentScale = ContentScale.Fit,
            onState = { painterState ->
                loadFailed = painterState is AsyncImagePainter.State.Error
            },
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                    translationX = offsetX
                    translationY = offsetY
                },
        )
        if (loadFailed) {
            Text(
                text = stringResource(R.string.viewer_decode_failed),
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(24.dp),
            )
        }
    }
}

@Composable
private fun ViewerChrome(
    item: MediaItem,
    onBack: () -> Unit,
    onDetails: () -> Unit,
    onShare: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Black.copy(alpha = 0.55f))
                .padding(horizontal = 4.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = stringResource(R.string.viewer_back),
                    tint = Color.White,
                )
            }
            Text(
                text = item.displayName,
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
            IconButton(onClick = onDetails) {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = stringResource(R.string.viewer_details),
                    tint = Color.White,
                )
            }
            IconButton(onClick = onShare) {
                Icon(
                    imageVector = Icons.Outlined.Share,
                    contentDescription = stringResource(R.string.viewer_share),
                    tint = Color.White,
                )
            }
        }
    }
}

@Composable
private fun PhotoDetailsSheet(item: MediaItem) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp)
            .padding(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = stringResource(R.string.viewer_details_title),
            style = MaterialTheme.typography.titleLarge,
        )
        DetailRow(stringResource(R.string.viewer_detail_name), item.displayName)
        DetailRow(stringResource(R.string.viewer_detail_type), item.mimeType)
        DetailRow(stringResource(R.string.viewer_detail_size), PhotoViewerLogic.formatFileSize(item.sizeBytes))
        val dimensions = PhotoViewerLogic.formatDimensions(item.width, item.height)
        if (dimensions.isNotEmpty()) {
            DetailRow(stringResource(R.string.viewer_detail_dimensions), dimensions)
        }
        val taken = PhotoViewerLogic.formatDate(item.dateTakenEpochMs)
        if (taken.isNotEmpty()) {
            DetailRow(stringResource(R.string.viewer_detail_taken), taken)
        }
        if (item.bucketName.isNotBlank()) {
            DetailRow(stringResource(R.string.viewer_detail_album), item.bucketName)
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

@Composable
private fun ViewerMessage(
    title: String,
    body: String,
    onBack: () -> Unit,
    onRetry: (() -> Unit)? = null,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = title, color = Color.White, style = MaterialTheme.typography.titleLarge)
        Text(
            text = body,
            color = Color.White.copy(alpha = 0.8f),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 8.dp, bottom = 16.dp),
        )
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = onBack) {
                Text(stringResource(R.string.viewer_back))
            }
            if (onRetry != null) {
                Button(onClick = onRetry) {
                    Text(stringResource(R.string.media_retry))
                }
            }
        }
    }
}
