package com.example.universalgallery.ui.player

import android.app.Application
import android.view.ViewGroup
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.ui.PlayerView
import com.example.universalgallery.R

@Composable
fun VideoPlayerRoute(
    mediaId: String?,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val parsedId = PlayerLogic.parseMediaId(mediaId)
    val viewModel: PlaybackViewModel = viewModel(
        key = "watch-$mediaId",
        factory = PlaybackViewModel.factory(
            application = context.applicationContext as Application,
            mediaId = parsedId,
            kind = PlaybackKind.VIDEO,
        ),
    )
    val state by viewModel.state.collectAsStateWithLifecycle()
    VideoPlayerScreen(
        state = state,
        playerViewModel = viewModel,
        onBack = onBack,
        onRetry = viewModel::load,
        modifier = modifier,
    )
}

@Composable
fun VideoPlayerScreen(
    state: PlaybackUiState,
    playerViewModel: PlaybackViewModel,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BackHandler(onBack = onBack)
    var speed by remember { mutableFloatStateOf(1f) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black),
    ) {
        when (state) {
            PlaybackUiState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Color.White,
                )
            }
            PlaybackUiState.Missing -> {
                PlayerMessage(
                    title = stringResource(R.string.player_missing_title),
                    body = stringResource(R.string.player_missing_body),
                    onBack = onBack,
                )
            }
            is PlaybackUiState.Failed -> {
                PlayerMessage(
                    title = failureTitle(state.failure),
                    body = failureBody(state.failure),
                    onBack = onBack,
                    onRetry = if (state.canRetry) onRetry else null,
                )
            }
            is PlaybackUiState.Ready -> {
                AndroidView(
                    factory = { context ->
                        PlayerView(context).apply {
                            useController = true
                            layoutParams = ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT,
                            )
                        }
                    },
                    update = { view ->
                        view.player = playerViewModel.player
                    },
                    modifier = Modifier.fillMaxSize(),
                )
                Column(modifier = Modifier.fillMaxSize()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Black.copy(alpha = 0.45f))
                            .padding(horizontal = 4.dp),
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
                            text = state.item.displayName,
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f),
                        )
                    }
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        items(PlayerLogic.allowedSpeeds) { value ->
                            FilterChip(
                                selected = speed == value,
                                onClick = {
                                    speed = value
                                    playerViewModel.setSpeed(value)
                                },
                                label = { Text("${value}x") },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
internal fun PlayerMessage(
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

@Composable
internal fun failureTitle(failure: PlaybackFailure): String {
    return when (failure) {
        PlaybackFailure.UNSUPPORTED -> stringResource(R.string.player_unsupported_title)
        PlaybackFailure.IO -> stringResource(R.string.player_io_title)
        PlaybackFailure.UNKNOWN -> stringResource(R.string.player_error_title)
    }
}

@Composable
internal fun failureBody(failure: PlaybackFailure): String {
    return when (failure) {
        PlaybackFailure.UNSUPPORTED -> stringResource(R.string.player_unsupported_body)
        PlaybackFailure.IO -> stringResource(R.string.player_io_body)
        PlaybackFailure.UNKNOWN -> stringResource(R.string.player_error_body)
    }
}
