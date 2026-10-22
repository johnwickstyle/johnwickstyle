package com.example.universalgallery.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.universalgallery.R
import com.example.universalgallery.data.model.MediaItem
import com.example.universalgallery.ui.images.MediaListUiState

@Composable
fun MediaGrid(
    state: MediaListUiState,
    onRetry: () -> Unit,
    onItemClick: (MediaItem) -> Unit,
    emptyTitle: String,
    emptyBody: String,
    modifier: Modifier = Modifier,
    errorTitle: String? = null,
    errorBody: String? = null,
) {
    when (state) {
        MediaListUiState.Loading -> {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        MediaListUiState.Empty -> {
            UnavailableSection(
                title = emptyTitle,
                body = emptyBody,
                modifier = modifier,
            )
        }
        is MediaListUiState.Error -> {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = errorTitle ?: stringResource(R.string.media_load_error_title),
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                )
                Text(
                    text = errorBody ?: stringResource(R.string.media_load_error_body),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 8.dp, bottom = 16.dp),
                )
                if (state.canRetry) {
                    Button(onClick = onRetry) {
                        Text(stringResource(R.string.media_retry))
                    }
                }
            }
        }
        is MediaListUiState.Success -> {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 118.dp),
                modifier = modifier.fillMaxSize(),
                contentPadding = PaddingValues(2.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                items(state.items, key = { it.id }) { item ->
                    MediaThumbnailItem(item = item, onClick = onItemClick)
                }
            }
        }
    }
}
