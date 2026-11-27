package merail.tools.packages.impl

import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import merail.tools.packages.api.model.InstalledAppInfo
import kotlin.time.Duration.Companion.milliseconds

private const val LEADING_ITEM_COUNT = 4
private const val PREFETCH = 30
private const val PREFETCH_DEBOUNCE_MS = 100L

@OptIn(FlowPreview::class)
@Composable
internal fun PackagesScreen(
    onBack: () -> Unit,
    onAppClick: (String) -> Unit,
    viewModel: PackagesViewModel = hiltViewModel<PackagesViewModel>(),
) {
    val searchApps by viewModel.searchedApps.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val selectedFilter by viewModel.selectedFilter.collectAsState()

    var query by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(listState, searchApps) {
        snapshotFlow {
            val visible = listState.layoutInfo.visibleItemsInfo
            val appIndices = visible
                .map { it.index - LEADING_ITEM_COUNT }
                .filter { it in searchApps.indices }

            if (appIndices.isEmpty()) {
                emptyList()
            } else {
                val from = (appIndices.min() - PREFETCH).coerceAtLeast(0)
                val to = (appIndices.max() + PREFETCH).coerceAtMost(searchApps.lastIndex)
                searchApps.subList(from, to + 1).map { it.packageName }
            }
        }
            .debounce(PREFETCH_DEBOUNCE_MS.milliseconds)
            .distinctUntilChanged()
            .collect { packageNames ->
                viewModel.loadIcons(packageNames)
            }
    }

    LazyColumn(
        state = listState,
        modifier = Modifier
            .systemBarsPadding()
            .fillMaxSize(),
    ) {
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.packages_back),
                    )
                }

                Text(
                    text = "Поиск приложений",
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f),
                )

                Spacer(modifier = Modifier.size(48.dp))
            }
        }

        item {
            OutlinedTextField(
                value = query,
                onValueChange = {
                    query = it
                    viewModel.search(query)
                },
                placeholder = {
                    Text(
                        text = stringResource(R.string.packages_search_placeholder),
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .padding(
                        horizontal = 12.dp,
                    )
                    .fillMaxWidth(),
            )
        }

        item {
            AppFilterRow(
                selectedFilter = selectedFilter,
                onFilterSelected = viewModel::setFilter,
            )
        }

        item {
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(16.dp),
            )
        }

        if (isLoading) {
            item {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                ) {
                    CircularProgressIndicator()
                }
            }
        } else {
            items(
                items = searchApps,
                key = { it.packageName },
            ) { app ->
                AppListItem(
                    appInfo = app,
                    onClick = { onAppClick(app.packageName) },
                )
            }
        }
    }
}

@Composable
private fun AppListItem(
    appInfo: InstalledAppInfo,
    onClick: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
        ) {
            appInfo.icon?.let { bitmap ->
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = appInfo.appName,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp)),
                )
            }
        }

        Column(
            modifier = Modifier
                .padding(
                    start = 12.dp,
                    end = 12.dp,
                ),
        ) {
            Text(
                text = appInfo.packageName,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
            )

            Text(
                text = appInfo.appName,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun AppFilterRow(
    selectedFilter: AppFilter,
    onFilterSelected: (AppFilter) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 12.dp),
    ) {
        AppFilter.entries.forEach { filter ->
            FilterChip(
                selected = filter == selectedFilter,
                onClick = { onFilterSelected(filter) },
                label = { Text(text = stringResource(filter.labelRes())) },
                modifier = Modifier.padding(end = 8.dp),
            )
        }
    }
}

@StringRes
private fun AppFilter.labelRes(): Int = when (this) {
    AppFilter.ALL -> R.string.packages_filter_all
    AppFilter.USER -> R.string.packages_filter_user
    AppFilter.SYSTEM -> R.string.packages_filter_system
    AppFilter.DEBUGGABLE -> R.string.packages_filter_debuggable
}
