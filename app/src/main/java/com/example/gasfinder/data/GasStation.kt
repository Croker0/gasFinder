package com.example.gasfinder.data

/**
 * Автозаправочная станция.
 *
 * @param id          локальный идентификатор (отрицательный — у станций, добавленных пользователем)
 * @param prices      цены за литр по видам топлива, руб.
 * @param isCustom    true, если запись создана пользователем и хранится в SharedPreferences
 */
data class GasStation(
    val id: Int,
    val name: String,
    val brand: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val prices: Map<FuelType, Double> = emptyMap(),
    val isOpen24: Boolean = false,
    val hasCafe: Boolean = false,
    val hasShop: Boolean = false,
    val hasCarWash: Boolean = false,
    val isCustom: Boolean = false
) {
    fun priceOf(fuel: FuelType): Double? = prices[fuel]

    /** Самая низкая цена среди всех видов топлива на этой АЗС. */
    fun minPrice(): Double? = prices.values.minOrNull()

    /** Вид топлива с минимальной ценой. */
    fun cheapestFuel(): FuelType? = prices.minByOrNull { it.value }?.key
}

/**
 * АЗС вместе с посчитанным расстоянием до неё.
 * Результат ранжирования — то, что показывает список «сначала ближайшие».
 */
data class RankedStation(
    val station: GasStation,
    val distanceKm: Double
)
