package com.example.universalgallery.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@Composable
fun MediaTopTabRow(
    tabs: List<Int>,
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val labels = tabs.map { stringResource(it) }
    val useScrollable = tabs.size > 4
    if (useScrollable) {
        ScrollableTabRow(
            selectedTabIndex = selectedIndex,
            edgePadding = 16.dp,
            modifier = modifier.fillMaxWidth(),
        ) {
            TabItems(labels, selectedIndex, onTabSelected)
        }
    } else {
        TabRow(
            selectedTabIndex = selectedIndex,
            modifier = modifier.fillMaxWidth(),
        ) {
            TabItems(labels, selectedIndex, onTabSelected)
        }
    }
}

@Composable
private fun TabItems(
    labels: List<String>,
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit,
) {
    labels.forEachIndexed { index, label ->
        Tab(
            selected = selectedIndex == index,
            onClick = { onTabSelected(index) },
            text = { Text(label) },
        )
    }
}
