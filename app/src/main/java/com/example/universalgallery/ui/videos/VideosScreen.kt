package com.example.universalgallery.ui.videos

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
import com.example.universalgallery.ui.components.MediaGrid
import com.example.universalgallery.ui.components.MediaTopTabRow
import com.example.universalgallery.ui.components.UnavailableSection

private val VideoTabs = listOf(
    R.string.tab_videos,
    R.string.tab_format,
    R.string.tab_folder,
    R.string.tab_private,
)

@Composable
fun VideosScreen(
    modifier: Modifier = Modifier,
    onItemClick: (MediaItem) -> Unit = {},
    viewModel: VideosViewModel = viewModel(),
) {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    val videosState by viewModel.videos.collectAsStateWithLifecycle()

    Column(modifier = modifier.fillMaxSize()) {
        MediaTopTabRow(
            tabs = VideoTabs,
            selectedIndex = selectedTab,
            onTabSelected = { selectedTab = it },
        )
        MediaPermissionGate(
            type = MediaPermissionType.Visual,
            title = stringResource(R.string.permission_visual_title),
            body = stringResource(R.string.permission_visual_body),
            modifier = Modifier.fillMaxSize(),
        ) {
            when (selectedTab) {
                0 -> MediaGrid(
                    state = videosState,
                    onRetry = viewModel::refresh,
                    onItemClick = onItemClick,
                    emptyTitle = stringResource(R.string.videos_empty_title),
                    emptyBody = stringResource(R.string.videos_empty_body),
                    errorTitle = stringResource(R.string.videos_load_error_title),
                )
                else -> UnavailableSection(title = stringResource(VideoTabs[selectedTab]))
            }
        }
    }
}
