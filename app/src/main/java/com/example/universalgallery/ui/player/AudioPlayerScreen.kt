package com.example.universalgallery.ui.player

import android.app.Application
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Pause
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.Player
import com.example.universalgallery.R
import com.example.universalgallery.ui.components.formatDuration
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

@Composable
fun AudioPlayerRoute(
    mediaId: String?,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val parsedId = PlayerLogic.parseMediaId(mediaId)
    val viewModel: PlaybackViewModel = viewModel(
        key = "listen-$mediaId",
        factory = PlaybackViewModel.factory(
            application = context.applicationContext as Application,
            mediaId = parsedId,
            kind = PlaybackKind.AUDIO,
        ),
    )
    val state by viewModel.state.collectAsStateWithLifecycle()
    AudioPlayerScreen(
        state = state,
        playerViewModel = viewModel,
        onBack = onBack,
        onRetry = viewModel::load,
        modifier = modifier,
    )
}

@Composable
fun AudioPlayerScreen(
    state: PlaybackUiState,
    playerViewModel: PlaybackViewModel,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BackHandler(onBack = onBack)
    val isReady = state is PlaybackUiState.Ready
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(if (isReady) MaterialTheme.colorScheme.surface else androidx.compose.ui.graphics.Color.Black),
    ) {
        when (state) {
            PlaybackUiState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
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
                val player = playerViewModel.player
                var playing by remember { mutableStateOf(player.isPlaying) }
                var position by remember { mutableLongStateOf(player.currentPosition) }
                var duration by remember { mutableLongStateOf(player.duration.coerceAtLeast(0L)) }
                var slider by remember { mutableFloatStateOf(0f) }
                var dragging by remember { mutableStateOf(false) }

                LaunchedEffect(player) {
                    val listener = object : Player.Listener {
                        override fun onIsPlayingChanged(isPlaying: Boolean) {
                            playing = isPlaying
                        }
                    }
                    player.addListener(listener)
                    try {
                        while (isActive) {
                            val dur = player.duration.coerceAtLeast(0L)
                            duration = dur
                            val pos = PlayerLogic.clampPosition(player.currentPosition, dur)
                            position = pos
                            if (!dragging && dur > 0L) {
                                slider = pos.toFloat() / dur.toFloat()
                            }
                            delay(250)
                        }
                    } finally {
                        player.removeListener(listener)
                    }
                }

                Column(modifier = Modifier.fillMaxSize()) {
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
                            text = stringResource(R.string.destination_audio),
                            style = MaterialTheme.typography.titleMedium,
                        )
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = state.item.displayName,
                            style = MaterialTheme.typography.headlineSmall,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Text(
                            text = state.item.bucketName,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 8.dp, bottom = 32.dp),
                        )
                        IconButton(
                            onClick = {
                                if (player.isPlaying) player.pause() else player.play()
                            },
                        ) {
                            Icon(
                                imageVector = if (playing) Icons.Outlined.Pause else Icons.Outlined.PlayArrow,
                                contentDescription = stringResource(
                                    if (playing) R.string.player_pause else R.string.player_play,
                                ),
                            )
                        }
                        Slider(
                            value = slider.coerceIn(0f, 1f),
                            onValueChange = {
                                dragging = true
                                slider = it
                            },
                            onValueChangeFinished = {
                                val dur = duration
                                if (dur > 0L) {
                                    player.seekTo(PlayerLogic.clampPosition((slider * dur).toLong(), dur))
                                }
                                dragging = false
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 24.dp),
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text(formatDuration(position), style = MaterialTheme.typography.labelMedium)
                            Text(formatDuration(duration), style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
            }
        }
    }
}
