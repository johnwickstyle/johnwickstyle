package com.example.universalgallery.ui.documents

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.universalgallery.R
import com.example.universalgallery.data.model.DocumentItem
import com.example.universalgallery.ui.components.MediaTopTabRow
import com.example.universalgallery.ui.components.UnavailableSection
import com.example.universalgallery.ui.viewer.PhotoViewerLogic

private val DocumentTabs = listOf(
    R.string.tab_documents,
    R.string.tab_format,
    R.string.tab_folder,
    R.string.tab_private,
)

@Composable
fun DocumentsScreen(
    modifier: Modifier = Modifier,
    onItemClick: (DocumentItem) -> Unit = {},
    viewModel: DocumentsViewModel = viewModel(),
) {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    val state by viewModel.documents.collectAsStateWithLifecycle()
    val picker = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenMultipleDocuments(),
    ) { uris ->
        viewModel.add(uris)
    }

    fun openPicker() {
        picker.launch(arrayOf("application/pdf", "text/*", "application/json", "*/*"))
    }

    Column(modifier = modifier.fillMaxSize()) {
        MediaTopTabRow(
            tabs = DocumentTabs,
            selectedIndex = selectedTab,
            onTabSelected = { selectedTab = it },
        )
        when (selectedTab) {
            0 -> DocumentLibrary(
                state = state,
                onRetry = viewModel::refresh,
                onAdd = ::openPicker,
                onItemClick = onItemClick,
            )
            else -> UnavailableSection(title = stringResource(DocumentTabs[selectedTab]))
        }
    }
}

@Composable
private fun DocumentLibrary(
    state: DocumentListUiState,
    onRetry: () -> Unit,
    onAdd: () -> Unit,
    onItemClick: (DocumentItem) -> Unit,
) {
    when (state) {
        DocumentListUiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        DocumentListUiState.Empty -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(R.string.documents_empty_title),
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                )
                Text(
                    text = stringResource(R.string.documents_empty_body),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 8.dp, bottom = 16.dp),
                )
                Button(onClick = onAdd) {
                    Text(stringResource(R.string.documents_add))
                }
            }
        }
        is DocumentListUiState.Error -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(R.string.documents_load_error_title),
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                )
                Text(
                    text = stringResource(R.string.media_load_error_body),
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
        is DocumentListUiState.Success -> {
            Column(modifier = Modifier.fillMaxSize()) {
                Button(
                    onClick = onAdd,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                ) {
                    Text(stringResource(R.string.documents_add))
                }
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 16.dp),
                ) {
                    items(state.items, key = { it.uriString }) { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onItemClick(item) }
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.displayName,
                                    style = MaterialTheme.typography.bodyLarge,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                                Text(
                                    text = "${item.mimeType} · ${PhotoViewerLogic.formatFileSize(item.sizeBytes)}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
