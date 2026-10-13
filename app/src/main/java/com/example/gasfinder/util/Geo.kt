package com.example.gasfinder.util

import java.util.Locale
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Геометрия и форматирование. Вынесено отдельно, чтобы не тянуть контекст Android
 * и оставить чистые функции, которые легко покрыть unit-тестами.
 */
object Geo {

    private const val EARTH_RADIUS_KM = 6371.0088

    /**
     * Расстояние между двумя точками по формуле гаверсинусов, в километрах.
     * Формула корректно работает на малых расстояниях (в пределах города),
     * где плоская аппроксимация уже даёт заметную ошибку.
     */
    fun distanceKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2) +
            cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLon / 2).pow(2)
        return 2 * EARTH_RADIUS_KM * asin(min(1.0, sqrt(a)))
    }

    /** «850 м», «3,4 км», «126 км» — человекочитаемое расстояние. */
    fun formatDistance(km: Double): String = when {
        km < 1.0 -> "${(km * 1000).roundToInt()} м"
        km < 10.0 -> decimal(km, 1) + " км"
        else -> "${km.roundToInt()} км"
    }

    /** «58,40» — цена с запятой как разделителем (русская локаль). */
    fun formatPrice(value: Double): String = decimal(value, 2)

    /** Форматирование с фиксированным числом знаков и запятой-разделителем. */
    fun decimal(value: Double, digits: Int): String =
        String.format(Locale.US, "%.${digits}f", value).replace('.', ',')
}
