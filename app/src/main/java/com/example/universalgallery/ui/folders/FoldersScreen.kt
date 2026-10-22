package com.example.universalgallery.ui.folders

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.universalgallery.R
import com.example.universalgallery.ui.components.UnavailableSection

@Composable
fun FoldersScreen(modifier: Modifier = Modifier) {
    UnavailableSection(
        title = stringResource(R.string.folders_root_title),
        body = stringResource(R.string.folders_root_body),
        modifier = modifier,
    )
}
