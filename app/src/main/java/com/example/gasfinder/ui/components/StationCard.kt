package com.example.gasfinder.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.gasfinder.R
import com.example.gasfinder.data.FuelType
import com.example.gasfinder.data.RankedStation
import com.example.gasfinder.ui.theme.brandColor
import com.example.gasfinder.util.Geo

/**
 * Карточка АЗС в списке: название, адрес, расстояние и цена выбранного топлива.
 */
@Composable
fun StationCard(
    ranked: RankedStation,
    preferredFuel: FuelType,
    isFavorite: Boolean,
    isNearest: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val station = ranked.station
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isNearest) 4.dp else 1.dp)
    ) {
        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
            // Цветная полоса бренда — станции различаются визуально без логотипов
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .fillMaxHeight()
                    .background(brandColor(station.brand))
            )
            Column(modifier = Modifier.padding(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = station.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.weight(1f)
                    )
                    if (isFavorite) {
                        Icon(
                            painter = painterResource(R.drawable.ic_star),
                            contentDescription = stringResource(R.string.favorites),
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(Modifier.height(2.dp))
                Text(
                    text = station.address,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )

                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(R.drawable.ic_location),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = Geo.formatDistance(ranked.distanceKm),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                    if (isNearest) {
                        Spacer(Modifier.width(8.dp))
                        Tag(
                            text = stringResource(R.string.nearest_station),
                            container = MaterialTheme.colorScheme.secondaryContainer
                        )
                    }
                    Spacer(Modifier.weight(1f))
                    station.priceOf(preferredFuel)?.let { price ->
                        Text(
                            text = "${Geo.formatPrice(price)} ₽",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    if (station.isOpen24) Tag(stringResource(R.string.amenity_24))
                    if (station.hasCafe) Tag(stringResource(R.string.amenity_cafe))
                    if (station.hasShop) Tag(stringResource(R.string.amenity_shop))
                    if (station.hasCarWash) Tag(stringResource(R.string.amenity_wash))
                    if (station.isCustom) Tag(stringResource(R.string.my_stations))
                }
            }
        }
    }
}

/** Небольшая метка: удобства, «ближайшая», «моя АЗС». */
@Composable
fun Tag(
    text: String,
    container: Color = MaterialTheme.colorScheme.primary.copy(alpha = 0.10f)
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(container)
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(text = text, style = MaterialTheme.typography.labelSmall)
    }
}

/** Выбор вида топлива в виде ряда «чипов». */
@Composable
fun FuelSelector(
    selected: FuelType,
    onSelect: (FuelType) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        FuelType.entries.forEach { fuel ->
            val isSelected = fuel == selected
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        if (isSelected) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.primary.copy(alpha = 0.10f)
                    )
                    .clickable { onSelect(fuel) }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = fuel.label,
                    style = MaterialTheme.typography.labelLarge,
                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary
                    else MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
