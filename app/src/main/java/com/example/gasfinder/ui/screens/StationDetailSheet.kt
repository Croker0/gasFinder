package com.example.gasfinder.ui.screens

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.gasfinder.R
import com.example.gasfinder.data.FuelType
import com.example.gasfinder.data.GasStation
import com.example.gasfinder.ui.components.Tag
import com.example.gasfinder.util.Geo

/**
 * Нижняя панель с подробностями выбранной АЗС.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StationDetailSheet(
    station: GasStation,
    distanceKm: Double?,
    isFavorite: Boolean,
    onDismiss: () -> Unit,
    onToggleFavorite: () -> Unit,
    onDelete: () -> Unit
) {
    val context = LocalContext.current
    // Раскрываем лист сразу целиком. По умолчанию ModalBottomSheet открывается
    // на половину высоты (PartiallyExpanded), и низ карточки вместе с кнопками
    // «Маршрут» и «В избранное» уходит за край экрана.
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                // Нижний отступ с запасом. Панель навигации Android перекрывает
                // низ окна диалога, а navigationBarsPadding() внутри отдельного
                // окна bottom sheet возвращает нулевые вставки — кнопки
                // «Маршрут» и «В избранное» оказывались под панелью.
                .padding(start = 20.dp, end = 20.dp, bottom = 84.dp)
        ) {

            Text(
                text = station.name,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "${station.brand} · ${station.address}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )

            distanceKm?.let {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "${stringResource(R.string.distance)}: ${Geo.formatDistance(it)}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                if (station.isOpen24) Tag(stringResource(R.string.amenity_24))
                if (station.hasCafe) Tag(stringResource(R.string.amenity_cafe))
                if (station.hasShop) Tag(stringResource(R.string.amenity_shop))
                if (station.hasCarWash) Tag(stringResource(R.string.amenity_wash))
                if (station.isCustom) Tag(stringResource(R.string.my_stations))
            }

            Spacer(Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(Modifier.height(12.dp))

            Text(
                text = stringResource(R.string.prices),
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(6.dp))
            FuelType.entries.forEach { fuel ->
                val price = station.priceOf(fuel)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = fuel.label, modifier = Modifier.weight(1f))
                    Text(
                        text = if (price != null) "${Geo.formatPrice(price)} ₽" else "—",
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
            Text(
                text = "Координаты: ${Geo.decimal(station.latitude, 5)}, ${Geo.decimal(station.longitude, 5)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )

            Spacer(Modifier.height(20.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { openRoute(context, station) },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(R.string.route))
                }
                OutlinedButton(onClick = onToggleFavorite) {
                    Icon(painterResource(R.drawable.ic_star), contentDescription = null)
                    Text(
                        text = stringResource(
                            if (isFavorite) R.string.remove_from_favorites else R.string.add_to_favorites
                        ),
                        modifier = Modifier.padding(start = 6.dp)
                    )
                }
            }

            if (station.isCustom) {
                Spacer(Modifier.height(4.dp))
                TextButton(onClick = onDelete) {
                    Icon(painterResource(R.drawable.ic_delete), contentDescription = null)
                    Text(
                        text = stringResource(R.string.station_deleted),
                        modifier = Modifier.padding(start = 6.dp)
                    )
                }
            }
        }
    }
}

/**
 * Открывает точку АЗС во внешнем картографическом приложении.
 * Используется неявный Intent: система сама предложит установленные карты.
 */
private fun openRoute(context: Context, station: GasStation) {
    val uri = Uri.parse("geo:${station.latitude},${station.longitude}?q=${station.latitude},${station.longitude}(${Uri.encode(station.name)})")
    val intent = Intent(Intent.ACTION_VIEW, uri).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    try {
        context.startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        // На устройстве нет приложения карт — открываем веб-версию.
        context.startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://www.openstreetmap.org/?mlat=${station.latitude}&mlon=${station.longitude}#map=17/${station.latitude}/${station.longitude}")
            ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }
}
