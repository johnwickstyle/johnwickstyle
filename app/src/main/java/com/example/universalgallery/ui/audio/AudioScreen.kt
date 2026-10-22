package com.example.universalgallery.ui.audio

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.universalgallery.R
import com.example.universalgallery.data.model.MediaItem
import com.example.universalgallery.permissions.MediaPermissionGate
import com.example.universalgallery.permissions.MediaPermissionType
import com.example.universalgallery.ui.components.AudioList
import com.example.universalgallery.ui.components.MediaTopTabRow
import com.example.universalgallery.ui.components.UnavailableSection

private val AudioTabs = listOf(
    R.string.tab_audios,
    R.string.tab_genre,
    R.string.tab_folder,
    R.string.tab_private,
)

@Composable
fun AudioScreen(
    modifier: Modifier = Modifier,
    onItemClick: (MediaItem) -> Unit = {},
    viewModel: AudioViewModel = viewModel(),
) {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    val audioState by viewModel.audio.collectAsStateWithLifecycle()

    Column(modifier = modifier.fillMaxSize()) {
        MediaTopTabRow(
            tabs = AudioTabs,
            selectedIndex = selectedTab,
            onTabSelected = { selectedTab = it },
        )
        MediaPermissionGate(
            type = MediaPermissionType.Audio,
            title = stringResource(R.string.permission_audio_title),
            body = stringResource(R.string.permission_audio_body),
            modifier = Modifier.fillMaxSize(),
        ) {
            when (selectedTab) {
                0 -> AudioList(
                    state = audioState,
                    onRetry = viewModel::refresh,
                    onItemClick = onItemClick,
                    emptyTitle = stringResource(R.string.audio_empty_title),
                    emptyBody = stringResource(R.string.audio_empty_body),
                )
                else -> UnavailableSection(title = stringResource(AudioTabs[selectedTab]))
            }
        }
    }
}
