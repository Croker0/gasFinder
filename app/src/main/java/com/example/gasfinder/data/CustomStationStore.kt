package com.example.gasfinder.data

import org.json.JSONArray
import org.json.JSONObject

/**
 * Хранилище АЗС, добавленных пользователем.
 *
 * Список сериализуется в JSON и лежит в SharedPreferences: для нескольких
 * записей этого достаточно, а отдельная база данных (Room) избыточна.
 */
class CustomStationStore(private val prefs: PreferencesRepository) {

    fun load(): List<GasStation> {
        val raw = prefs.customStationsJson
        if (raw.isBlank()) return emptyList()
        return runCatching {
            val array = JSONArray(raw)
            (0 until array.length()).mapNotNull { index -> toStation(array.optJSONObject(index)) }
        }.getOrDefault(emptyList())
    }

    fun add(station: GasStation) {
        prefs.customStationsJson = toJson(load() + station)
    }

    fun remove(id: Int) {
        prefs.customStationsJson = toJson(load().filterNot { it.id == id })
    }

    /** Следующий свободный отрицательный идентификатор. */
    fun nextId(): Int = (load().minOfOrNull { it.id } ?: 0) - 1

    private fun toJson(list: List<GasStation>): String {
        val array = JSONArray()
        list.forEach { station ->
            val obj = JSONObject()
            obj.put("id", station.id)
            obj.put("name", station.name)
            obj.put("brand", station.brand)
            obj.put("address", station.address)
            obj.put("lat", station.latitude)
            obj.put("lon", station.longitude)
            val prices = JSONObject()
            station.prices.forEach { (fuel, value) -> prices.put(fuel.name, value) }
            obj.put("prices", prices)
            obj.put("open24", station.isOpen24)
            obj.put("cafe", station.hasCafe)
            obj.put("shop", station.hasShop)
            obj.put("wash", station.hasCarWash)
            array.put(obj)
        }
        return array.toString()
    }

    private fun toStation(obj: JSONObject?): GasStation? {
        if (obj == null) return null
        val prices = mutableMapOf<FuelType, Double>()
        obj.optJSONObject("prices")?.let { json ->
            json.keys().forEach { key ->
                FuelType.fromName(key)?.let { fuel -> prices[fuel] = json.optDouble(key, 0.0) }
            }
        }
        return GasStation(
            id = obj.optInt("id"),
            name = obj.optString("name"),
            brand = obj.optString("brand"),
            address = obj.optString("address"),
            latitude = obj.optDouble("lat"),
            longitude = obj.optDouble("lon"),
            prices = prices,
            isOpen24 = obj.optBoolean("open24"),
            hasCafe = obj.optBoolean("cafe"),
            hasShop = obj.optBoolean("shop"),
            hasCarWash = obj.optBoolean("wash"),
            isCustom = true
        )
    }
}
