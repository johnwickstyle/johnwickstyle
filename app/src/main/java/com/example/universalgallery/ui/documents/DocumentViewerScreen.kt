package com.example.universalgallery.ui.documents

import android.app.Application
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.OpenInNew
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.universalgallery.R
import com.example.universalgallery.data.model.DocumentItem
import kotlinx.coroutines.launch
import android.graphics.Bitmap
import android.graphics.Color as AndroidColor

@Composable
fun DocumentViewerRoute(
    encodedUri: String?,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val uri = DocumentLogic.parseUri(encodedUri)
    val viewModel: DocumentViewerViewModel = viewModel(
        key = encodedUri,
        factory = DocumentViewerViewModel.factory(context.applicationContext as Application, uri),
    )
    val state by viewModel.state.collectAsStateWithLifecycle()
    DocumentViewerScreen(
        state = state,
        onBack = onBack,
        onRetry = viewModel::load,
        modifier = modifier,
    )
}

@Composable
fun DocumentViewerScreen(
    state: DocumentViewerUiState,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val openFailed = stringResource(R.string.documents_open_failed)
    BackHandler(onBack = onBack)

    fun openExternal(item: DocumentItem) {
        if (!DocumentLogic.canOpenExternally(item.uriString)) {
            scope.launch { snackbarHostState.showSnackbar(openFailed) }
            return
        }
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(Uri.parse(item.uriString), item.mimeType)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        try {
            context.startActivity(intent)
        } catch (_: ActivityNotFoundException) {
            scope.launch { snackbarHostState.showSnackbar(openFailed) }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface),
    ) {
        when (state) {
            DocumentViewerUiState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            DocumentViewerUiState.Missing -> {
                DocumentMessage(
                    title = stringResource(R.string.documents_missing_title),
                    body = stringResource(R.string.documents_missing_body),
                    onBack = onBack,
                )
            }
            is DocumentViewerUiState.Error -> {
                DocumentMessage(
                    title = stringResource(R.string.documents_viewer_error_title),
                    body = stringResource(R.string.documents_viewer_error_body),
                    onBack = onBack,
                    onRetry = if (state.canRetry) onRetry else null,
                )
            }
            is DocumentViewerUiState.Unsupported -> {
                Column(modifier = Modifier.fillMaxSize()) {
                    ViewerBar(title = state.item.displayName, onBack = onBack, onOpen = { openExternal(state.item) })
                    DocumentMessage(
                        title = stringResource(R.string.documents_unsupported_title),
                        body = stringResource(R.string.documents_unsupported_body),
                        onBack = onBack,
                        extra = {
                            Button(onClick = { openExternal(state.item) }) {
                                Text(stringResource(R.string.documents_open_external))
                            }
                        },
                    )
                }
            }
            is DocumentViewerUiState.Text -> {
                Column(modifier = Modifier.fillMaxSize()) {
                    ViewerBar(title = state.item.displayName, onBack = onBack, onOpen = { openExternal(state.item) })
                    if (state.truncated) {
                        Text(
                            text = stringResource(R.string.documents_text_truncated),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        )
                    }
                    Text(
                        text = state.body.ifBlank { stringResource(R.string.documents_text_empty) },
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .horizontalScroll(rememberScrollState())
                            .padding(16.dp),
                    )
                }
            }
            is DocumentViewerUiState.Pdf -> {
                Column(modifier = Modifier.fillMaxSize()) {
                    ViewerBar(title = state.item.displayName, onBack = onBack, onOpen = { openExternal(state.item) })
                    PdfPages(uriString = state.item.uriString)
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
private fun ViewerBar(
    title: String,
    onBack: () -> Unit,
    onOpen: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                contentDescription = stringResource(R.string.viewer_back),
            )
        }
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        IconButton(onClick = onOpen) {
            Icon(
                    imageVector = Icons.AutoMirrored.Outlined.OpenInNew,
                contentDescription = stringResource(R.string.documents_open_external),
            )
        }
    }
}

@Composable
private fun PdfPages(uriString: String) {
    val context = LocalContext.current
    val renderer = remember(uriString) {
        try {
            val pfd = context.contentResolver.openFileDescriptor(Uri.parse(uriString), "r")
            if (pfd != null) PdfRenderer(pfd) else null
        } catch (_: Exception) {
            null
        }
    }
    DisposableEffect(renderer) {
        onDispose { renderer?.close() }
    }
    if (renderer == null || renderer.pageCount <= 0) {
        Text(
            text = stringResource(R.string.documents_pdf_failed),
            modifier = Modifier.padding(24.dp),
            style = MaterialTheme.typography.bodyMedium,
        )
        return
    }
    var pageIndex by remember { mutableIntStateOf(0) }
    val pageCount = renderer.pageCount
    Column(modifier = Modifier.fillMaxSize()) {
        val bitmap = remember(pageIndex, renderer) {
            renderer.openPage(pageIndex).use { page ->
                val bmp = Bitmap.createBitmap(
                    page.width.coerceAtLeast(1),
                    page.height.coerceAtLeast(1),
                    Bitmap.Config.ARGB_8888,
                )
                bmp.eraseColor(AndroidColor.WHITE)
                page.render(bmp, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                bmp
            }
        }
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = stringResource(R.string.documents_pdf_page, pageIndex + 1, pageCount),
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .horizontalScroll(rememberScrollState())
                .background(Color.White),
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Button(
                onClick = { pageIndex = (pageIndex - 1).coerceAtLeast(0) },
                enabled = pageIndex > 0,
            ) {
                Text(stringResource(R.string.documents_prev_page))
            }
            Text(
                text = stringResource(R.string.documents_pdf_page, pageIndex + 1, pageCount),
                style = MaterialTheme.typography.labelLarge,
            )
            Button(
                onClick = { pageIndex = (pageIndex + 1).coerceAtMost(pageCount - 1) },
                enabled = pageIndex < pageCount - 1,
            ) {
                Text(stringResource(R.string.documents_next_page))
            }
        }
    }
}

@Composable
private fun DocumentMessage(
    title: String,
    body: String,
    onBack: () -> Unit,
    onRetry: (() -> Unit)? = null,
    extra: (@Composable () -> Unit)? = null,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = title, style = MaterialTheme.typography.titleLarge)
        Text(
            text = body,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
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
        extra?.invoke()
    }
}
