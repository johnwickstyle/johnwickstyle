package com.example.universalgallery.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.universalgallery.R
import com.example.universalgallery.permissions.MediaPermissionStatus

@Composable
fun PermissionRationaleCard(
    title: String,
    body: String,
    status: MediaPermissionStatus,
    onAllowAccess: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val permanentlyDenied = status is MediaPermissionStatus.Denied && status.permanentlyDenied
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = if (permanentlyDenied) {
                stringResource(R.string.permission_denied_title)
            } else {
                title
            },
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
        )
        Text(
            text = if (permanentlyDenied) {
                stringResource(R.string.permission_denied_body)
            } else {
                body
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp, bottom = 24.dp),
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (!permanentlyDenied) {
                Button(onClick = onAllowAccess) {
                    Text(stringResource(R.string.permission_allow_access))
                }
            }
            OutlinedButton(onClick = onOpenSettings) {
                Text(stringResource(R.string.permission_open_settings))
            }
        }
    }
}
