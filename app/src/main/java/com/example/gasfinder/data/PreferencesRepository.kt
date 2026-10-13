package com.example.gasfinder.data

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

/**
 * Обёртка над SharedPreferences — «разделяемыми настройками» из методички.
 *
 * Скалярные значения читаются синхронно (get / set), а для тех настроек, которые
 * влияют на интерфейс, отдаются ещё и Flow: так UI перестраивается сам, без
 * ручного обновления состояния после каждой записи.
 */
class PreferencesRepository(context: Context) {

    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    // ------------------------------------------------------------------
    // Скалярные настройки
    // ------------------------------------------------------------------

    /** Пройден ли онбординг при первом запуске. */
    var onboardingDone: Boolean
        get() = prefs.getBoolean(KEY_ONBOARDING_DONE, false)
        set(value) = prefs.edit { putBoolean(KEY_ONBOARDING_DONE, value) }

    /** Демонстрационная локация (центр Екатеринбурга) вместо реального GPS. */
    var demoLocation: Boolean
        get() = prefs.getBoolean(KEY_DEMO_LOCATION, false)
        set(value) = prefs.edit { putBoolean(KEY_DEMO_LOCATION, value) }

    /** Предпочитаемый вид топлива — по умолчанию АИ-95. */
    var preferredFuel: FuelType
        get() = FuelType.fromName(prefs.getString(KEY_PREFERRED_FUEL, null)) ?: FuelType.AI95
        set(value) = prefs.edit { putString(KEY_PREFERRED_FUEL, value.name) }

    /** Показывать расстояние в километрах (иначе в метрах, если близко). */
    var showKilometers: Boolean
        get() = prefs.getBoolean(KEY_SHOW_KM, true)
        set(value) = prefs.edit { putBoolean(KEY_SHOW_KM, value) }

    /** Показывать только круглосуточные АЗС. */
    var onlyOpen24: Boolean
        get() = prefs.getBoolean(KEY_ONLY_24, false)
        set(value) = prefs.edit { putBoolean(KEY_ONLY_24, value) }

    /** Избранные АЗС — набор идентификаторов (демонстрация putStringSet/getStringSet). */
    var favorites: Set<String>
        get() = prefs.getStringSet(KEY_FAVORITES, emptySet()) ?: emptySet()
        set(value) = prefs.edit { putStringSet(KEY_FAVORITES, value) }

    /** АЗС, добавленные пользователем, в виде JSON-строки. */
    var customStationsJson: String
        get() = prefs.getString(KEY_CUSTOM_STATIONS, "") ?: ""
        set(value) = prefs.edit { putString(KEY_CUSTOM_STATIONS, value) }

    // ------------------------------------------------------------------
    // Реактивные потоки
    // ------------------------------------------------------------------

    /**
     * Поток изменений конкретного ключа.
     *
     * callbackFlow создаёт Flow, в который значения отправляются вручную:
     * слушатель SharedPreferences отправляет сигнал при изменении ключа,
     * trySend сразу отдаёт текущее значение при подписке, а awaitClose
     * снимает подписку, когда Flow больше не нужен.
     */
    private fun observe(key: String): Flow<Unit> = callbackFlow {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, changedKey ->
            if (changedKey == key) trySend(Unit)
        }
        prefs.registerOnSharedPreferenceChangeListener(listener)
        trySend(Unit)
        awaitClose { prefs.unregisterOnSharedPreferenceChangeListener(listener) }
    }.conflate()

    /** Реактивное значение флага «демо-локация». */
    val demoLocationFlow: Flow<Boolean> =
        observe(KEY_DEMO_LOCATION).map { demoLocation }.distinctUntilChanged()

    /** Реактивное значение предпочитаемого топлива. */
    val preferredFuelFlow: Flow<FuelType> =
        observe(KEY_PREFERRED_FUEL).map { preferredFuel }.distinctUntilChanged()

    /** Реактивный набор избранного. */
    val favoritesFlow: Flow<Set<String>> =
        observe(KEY_FAVORITES).map { favorites }.distinctUntilChanged()

    /** Реактивный список пользовательских АЗС. */
    val customStationsFlow: Flow<String> =
        observe(KEY_CUSTOM_STATIONS).map { customStationsJson }.distinctUntilChanged()

    /** Сброс всех настроек к значениям по умолчанию. */
    fun reset() = prefs.edit { clear() }

    /** Признак того, что пользователь уже что-то сохранял (используется на экране профиля). */
    fun hasAnySetting(): Boolean = prefs.all.isNotEmpty()

    companion object {
        /** Имя набора настроек — как в методичке: "app_prefs". */
        const val PREFS_NAME = "app_prefs"

        private const val KEY_ONBOARDING_DONE = "onboarding_done"
        private const val KEY_DEMO_LOCATION = "demo_location"
        private const val KEY_PREFERRED_FUEL = "preferred_fuel"
        private const val KEY_SHOW_KM = "show_kilometers"
        private const val KEY_ONLY_24 = "only_open_24"
        private const val KEY_FAVORITES = "favorites"
        private const val KEY_CUSTOM_STATIONS = "custom_stations"
    }
}
