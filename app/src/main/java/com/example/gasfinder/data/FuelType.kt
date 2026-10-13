package com.example.gasfinder.data

/**
 * Виды топлива, которые продаются на автозаправочных станциях.
 */
enum class FuelType(val label: String, val shortLabel: String) {
    AI92("АИ-92", "92"),
    AI95("АИ-95", "95"),
    AI98("АИ-98", "98"),
    DT("ДТ", "ДТ");

    companion object {
        /** Восстановление значения из строки (используется при чтении из SharedPreferences). */
        fun fromName(name: String?): FuelType? =
            FuelType.entries.firstOrNull { it.name.equals(name, ignoreCase = true) }
    }
}
