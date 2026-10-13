package com.example.gasfinder.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.gasfinder.R
import com.example.gasfinder.data.FuelType
import com.example.gasfinder.data.GasStation
import com.example.gasfinder.data.RankedStation
import com.example.gasfinder.ui.components.FuelSelector
import com.example.gasfinder.ui.components.StationCard

/**
 * Список АЗС, отсортированный по расстоянию, с поиском и фильтрами.
 */
@Composable
fun ListScreen(
    stations: List<RankedStation>,
    preferredFuel: FuelType,
    favorites: Set<String>,
    query: String,
    onlyOpen24: Boolean,
    onQueryChange: (String) -> Unit,
    onFuelChange: (FuelType) -> Unit,
    onOnlyOpen24Change: (Boolean) -> Unit,
    onSelect: (GasStation) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {

        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            OutlinedTextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                label = { Text(stringResource(R.string.search_hint)) },
                leadingIcon = { Icon(painterResource(R.drawable.ic_search), contentDescription = null) }
            )

            Spacer(Modifier.height(10.dp))
            Text(
                text = stringResource(R.string.filter_fuel),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            Spacer(Modifier.height(6.dp))
            FuelSelector(selected = preferredFuel, onSelect = onFuelChange)

            Spacer(Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(R.string.only_open_24),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )
                Switch(checked = onlyOpen24, onCheckedChange = onOnlyOpen24Change)
            }

            Text(
                text = "${stringResource(R.string.stations_found)}: ${stations.size}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }

        if (stations.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.list_empty),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                    start = 16.dp, end = 16.dp, top = 4.dp, bottom = 24.dp
                ),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(stations, key = { it.station.id }) { ranked ->
                    StationCard(
                        ranked = ranked,
                        preferredFuel = preferredFuel,
                        isFavorite = favorites.contains(ranked.station.id.toString()),
                        isNearest = ranked.station.id == stations.firstOrNull()?.station?.id,
                        onClick = { onSelect(ranked.station) }
                    )
                }
            }
        }
    }
}
