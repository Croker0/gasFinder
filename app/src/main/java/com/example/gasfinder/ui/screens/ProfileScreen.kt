package com.example.gasfinder.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.gasfinder.R
import com.example.gasfinder.data.FuelType
import com.example.gasfinder.data.GasStation
import com.example.gasfinder.data.RankedStation
import com.example.gasfinder.ui.components.FuelSelector
import com.example.gasfinder.util.Geo

/**
 * Профиль и настройки: демо-локация, предпочитаемое топливо, избранное
 * и список собственных АЗС. Все переключатели пишут в SharedPreferences.
 */
@Composable
fun ProfileScreen(
    demoLocationEnabled: Boolean,
    autoDemoFallback: Boolean,
    preferredFuel: FuelType,
    showKilometers: Boolean,
    favorites: Set<String>,
    stations: List<RankedStation>,
    onDemoLocationChange: (Boolean) -> Unit,
    onFuelChange: (FuelType) -> Unit,
    onShowKilometersChange: (Boolean) -> Unit,
    onReset: () -> Unit,
    onAddStation: () -> Unit,
    onSelectStation: (GasStation) -> Unit,
    modifier: Modifier = Modifier
) {
    val favoriteStations = stations.filter { favorites.contains(it.station.id.toString()) }
    val myStations = stations.filter { it.station.isCustom }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.profile_title),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(16.dp))

        // ---- Настройки ----
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                SwitchRow(
                    title = stringResource(R.string.settings_demo_location),
                    subtitle = stringResource(R.string.settings_demo_location_desc),
                    checked = demoLocationEnabled,
                    onChange = onDemoLocationChange
                )
                if (autoDemoFallback && !demoLocationEnabled) {
                    Text(
                        text = stringResource(R.string.demo_location_hint),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                SwitchRow(
                    title = stringResource(R.string.settings_units),
                    subtitle = null,
                    checked = showKilometers,
                    onChange = onShowKilometersChange
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                Text(
                    text = stringResource(R.string.settings_preferred_fuel),
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(Modifier.height(8.dp))
                FuelSelector(selected = preferredFuel, onSelect = onFuelChange)

                Spacer(Modifier.height(12.dp))
                OutlinedButton(onClick = onReset, modifier = Modifier.fillMaxWidth()) {
                    Text(stringResource(R.string.settings_reset))
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // ---- Избранное ----
        Text(text = stringResource(R.string.favorites), style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(6.dp))
        if (favoriteStations.isEmpty()) {
            Text(
                text = stringResource(R.string.no_favorites),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        } else {
            favoriteStations.forEach { ranked ->
                StationRow(ranked) { onSelectStation(ranked.station) }
            }
        }

        Spacer(Modifier.height(16.dp))

        // ---- Мои АЗС ----
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(R.string.my_stations),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )
            Button(onClick = onAddStation) {
                Text(stringResource(R.string.nav_add))
            }
        }
        Spacer(Modifier.height(6.dp))
        if (myStations.isEmpty()) {
            Text(
                text = stringResource(R.string.no_custom_stations),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        } else {
            myStations.forEach { ranked ->
                StationRow(ranked) { onSelectStation(ranked.station) }
            }
        }

        Spacer(Modifier.height(20.dp))

        // ---- О приложении ----
        Text(text = stringResource(R.string.about), style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(4.dp))
        Text(
            text = stringResource(R.string.about_text),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun SwitchRow(
    title: String,
    subtitle: String?,
    checked: Boolean,
    onChange: (Boolean) -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.bodyMedium)
            subtitle?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
        Switch(checked = checked, onCheckedChange = onChange)
    }
}

@Composable
private fun StationRow(ranked: RankedStation, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = ranked.station.name, style = MaterialTheme.typography.bodyMedium)
            Text(
                text = ranked.station.address,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
        Text(
            text = Geo.formatDistance(ranked.distanceKm),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary
        )
    }
}
