package merail.tools.info.impl

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
internal fun InfoScreen(
    viewModel: InfoViewModel = hiltViewModel<InfoViewModel>(),
) {
    LazyColumn(
        modifier = Modifier
            .systemBarsPadding()
            .fillMaxSize(),
    ) {
        item {
            Text(
                text = stringResource(R.string.info_title),
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            )
        }

        item {
            DisplayInfoBlock(viewModel)
        }

        item {
            AndroidInfoBlock(viewModel)
        }

        item {
            DeviceInfoBlock(viewModel)
        }

        item {
            NetworkInfoBlock(viewModel)
        }
    }
}

@Composable
private fun DisplayInfoBlock(
    viewModel: InfoViewModel,
) {
    ExpandableItem(
        title = stringResource(R.string.info_display_title),
    ) {
        Text(
            text = stringResource(
                R.string.info_resolution_px,
                viewModel.displayWidth,
                viewModel.displayHeight,
            ),
            modifier = Modifier
                .padding(
                    bottom = 8.dp,
                )
                .fillMaxWidth(),
        )

        Text(
            text = stringResource(
                R.string.info_resolution_dp,
                viewModel.displayWidthDpi,
                viewModel.displayHeightDpi,
            ),
            modifier = Modifier
                .padding(
                    vertical = 8.dp,
                )
                .fillMaxWidth(),
        )

        Text(
            text = stringResource(R.string.Info_density, viewModel.displayDpi),
            modifier = Modifier
                .padding(
                    vertical = 8.dp,
                )
                .fillMaxWidth(),
        )

        Text(
            text = stringResource(
                R.string.info_density_qualifier,
                viewModel.displayDensityQualifier,
                viewModel.displayDensityQualifier.humanValue,
            ),
            modifier = Modifier
                .padding(
                    vertical = 8.dp,
                )
                .fillMaxWidth(),
        )

        Text(
            text = stringResource(R.string.info_refresh_rate, viewModel.displayRefreshRate),
            modifier = Modifier
                .padding(
                    top = 8.dp,
                )
                .fillMaxWidth(),
        )
    }
}

@Composable
private fun AndroidInfoBlock(
    viewModel: InfoViewModel,
) {
    ExpandableItem(
        title = stringResource(R.string.info_os_title),
    ) {
        Text(
            text = stringResource(
                R.string.info_android_version,
                viewModel.androidVersion.releaseValue,
                viewModel.androidVersion.humanValue,
                viewModel.androidVersion.sdkValue,
            ),
            modifier = Modifier
                .padding(
                    bottom = 8.dp,
                )
                .fillMaxWidth(),
        )

        Text(
            text = stringResource(R.string.info_security_patch, viewModel.patchLevel),
            modifier = Modifier
                .padding(
                    vertical = 8.dp,
                )
                .fillMaxWidth(),
        )

        Text(
            text = stringResource(R.string.info_build_id, viewModel.buildId),
            modifier = Modifier
                .padding(
                    top = 8.dp,
                )
                .fillMaxWidth(),
        )
    }
}

@Composable
private fun DeviceInfoBlock(
    viewModel: InfoViewModel,
) {
    ExpandableItem(
        title = stringResource(R.string.info_device_title),
    ) {
        Text(
            text = stringResource(R.string.info_manufacturer, viewModel.manufacturer),
            modifier = Modifier
                .padding(
                    bottom = 8.dp,
                )
                .fillMaxWidth(),
        )

        Text(
            text = stringResource(R.string.info_model, viewModel.model),
            modifier = Modifier
                .padding(
                    vertical = 8.dp,
                )
                .fillMaxWidth(),
        )

        Text(
            text = stringResource(R.string.info_brand, viewModel.brand),
            modifier = Modifier
                .padding(
                    vertical = 8.dp,
                )
                .fillMaxWidth(),
        )

        Text(
            text = stringResource(R.string.info_abis, viewModel.abis),
            modifier = Modifier
                .padding(
                    vertical = 8.dp,
                )
                .fillMaxWidth(),
        )

        Text(
            text = stringResource(R.string.info_cores_count, viewModel.coresCount),
            modifier = Modifier
                .padding(
                    vertical = 8.dp,
                )
                .fillMaxWidth(),
        )

        Text(
            text = stringResource(R.string.info_ram, viewModel.availableRam, viewModel.totalRam),
            modifier = Modifier
                .padding(
                    vertical = 8.dp,
                )
                .fillMaxWidth(),
        )

        Text(
            text = stringResource(
                R.string.info_storage,
                viewModel.availableStorage,
                viewModel.totalStorage,
            ),
            modifier = Modifier
                .padding(
                    top = 8.dp,
                )
                .fillMaxWidth(),
        )
    }
}

@Composable
private fun NetworkInfoBlock(
    viewModel: InfoViewModel,
) {
    ExpandableItem(
        title = stringResource(R.string.info_network_title),
    ) {
        Text(
            text = stringResource(
                R.string.info_network_state,
                if (viewModel.isConnected.collectAsState().value) {
                    stringResource(R.string.info_network_is_connected)
                } else {
                    stringResource(R.string.info_network_is_not_connected)
                },
            ),
            modifier = Modifier
                .padding(
                    bottom = 8.dp,
                )
                .fillMaxWidth(),
        )

        Text(
            text = stringResource(
                R.string.info_network_type,
                viewModel.networkConnection.collectAsState().value.humanValue,
            ),
            modifier = Modifier
                .padding(
                    vertical = 8.dp,
                )
                .fillMaxWidth(),
        )

        Text(
            text = stringResource(
                R.string.info_ip_address,
                viewModel.ipAddress.collectAsState().value,
            ),
            modifier = Modifier
                .padding(
                    vertical = 8.dp,
                )
                .fillMaxWidth(),
        )
    }
}

@Composable
private fun ExpandableItem(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 16.dp,
                vertical = 4.dp,
            )
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .animateContentSize()
            .clickable {
                expanded = expanded.not()
            }
            .padding(16.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth(),

        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f),
            )

            Icon(
                imageVector = if (expanded) {
                    Icons.Default.ExpandLess
                } else {
                    Icons.Default.ExpandMore
                },
                contentDescription = if (expanded) {
                    "Свернуть"
                } else {
                    "Развернуть"
                },
            )
        }

        if (expanded) {
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp),
            )

            HorizontalDivider(
                thickness = 2.dp,
                modifier = Modifier
                    .fillMaxWidth(),
            )

            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(16.dp),
            )

            content()
        }
    }
}