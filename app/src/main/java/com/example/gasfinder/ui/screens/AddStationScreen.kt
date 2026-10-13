package com.example.gasfinder.ui.screens

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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.text.KeyboardOptions
import com.example.gasfinder.R
import com.example.gasfinder.data.FuelType
import com.example.gasfinder.location.UserLocation
import com.example.gasfinder.util.Geo

/** Данные формы новой АЗС. */
data class NewStationForm(
    val name: String,
    val brand: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val prices: Map<FuelType, Double>,
    val isOpen24: Boolean,
    val hasCafe: Boolean,
    val hasShop: Boolean,
    val hasCarWash: Boolean
)

/**
 * Форма добавления собственной АЗС.
 * Координаты по умолчанию подставляются из текущего местоположения.
 */
@Composable
fun AddStationScreen(
    origin: UserLocation,
    onSave: (NewStationForm) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    var name by remember { mutableStateOf("") }
    var brand by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var latitude by remember { mutableStateOf(Geo.decimal(origin.latitude, 5)) }
    var longitude by remember { mutableStateOf(Geo.decimal(origin.longitude, 5)) }
    var isOpen24 by remember { mutableStateOf(false) }
    var hasCafe by remember { mutableStateOf(false) }
    var hasShop by remember { mutableStateOf(true) }
    var hasCarWash by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf(false) }

    val priceInputs = remember {
        mutableStateMapOf<FuelType, String>().apply {
            FuelType.entries.forEach { put(it, "") }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.add_station_title),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text(stringResource(R.string.field_name)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = error && name.isBlank()
        )
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = brand,
            onValueChange = { brand = it },
            label = { Text(stringResource(R.string.field_brand)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = address,
            onValueChange = { address = it },
            label = { Text(stringResource(R.string.field_address)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = error && address.isBlank()
        )
        Spacer(Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = latitude,
                onValueChange = { latitude = it },
                label = { Text(stringResource(R.string.field_latitude)) },
                modifier = Modifier.weight(1f),
                singleLine = true
            )
            OutlinedTextField(
                value = longitude,
                onValueChange = { longitude = it },
                label = { Text(stringResource(R.string.field_longitude)) },
                modifier = Modifier.weight(1f),
                singleLine = true
            )
        }

        Spacer(Modifier.height(16.dp))
        Text(text = stringResource(R.string.prices), style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(6.dp))
        FuelType.entries.forEach { fuel ->
            OutlinedTextField(
                value = priceInputs[fuel].orEmpty(),
                onValueChange = { priceInputs[fuel] = it },
                label = { Text("${fuel.label}, ₽/л") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )
        }

        Spacer(Modifier.height(8.dp))
        Text(text = stringResource(R.string.amenities), style = MaterialTheme.typography.titleMedium)
        CheckRow(stringResource(R.string.amenity_24), isOpen24) { isOpen24 = it }
        CheckRow(stringResource(R.string.amenity_cafe), hasCafe) { hasCafe = it }
        CheckRow(stringResource(R.string.amenity_shop), hasShop) { hasShop = it }
        CheckRow(stringResource(R.string.amenity_wash), hasCarWash) { hasCarWash = it }

        if (error) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.required_field),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(Modifier.height(20.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = {
                    val lat = latitude.replace(',', '.').toDoubleOrNull()
                    val lon = longitude.replace(',', '.').toDoubleOrNull()
                    if (name.isBlank() || address.isBlank() || lat == null || lon == null) {
                        error = true
                    } else {
                        error = false
                        onSave(
                            NewStationForm(
                                name = name.trim(),
                                brand = brand.trim(),
                                address = address.trim(),
                                latitude = lat,
                                longitude = lon,
                                prices = priceInputs
                                    .mapNotNull { (fuel, raw) ->
                                        raw.replace(',', '.').toDoubleOrNull()?.let { fuel to it }
                                    }
                                    .toMap(),
                                isOpen24 = isOpen24,
                                hasCafe = hasCafe,
                                hasShop = hasShop,
                                hasCarWash = hasCarWash
                            )
                        )
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Text(stringResource(R.string.save))
            }
            OutlinedButton(onClick = onCancel, modifier = Modifier.weight(1f)) {
                Text(stringResource(R.string.cancel))
            }
        }

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun CheckRow(label: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Checkbox(checked = checked, onCheckedChange = onChange)
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
    }
}
