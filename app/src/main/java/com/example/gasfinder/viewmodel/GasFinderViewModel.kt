package com.example.gasfinder.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gasfinder.data.CustomStationStore
import com.example.gasfinder.data.FuelType
import com.example.gasfinder.data.GasStation
import com.example.gasfinder.data.GasStationRepository
import com.example.gasfinder.data.PreferencesRepository
import com.example.gasfinder.data.RankedStation
import com.example.gasfinder.location.LocationProvider
import com.example.gasfinder.location.UserLocation
import com.example.gasfinder.util.Geo
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Состояние интерфейса. Один неизменяемый объект на весь экран —
 * это упрощает отладку: любое изменение UI видно как новый экземпляр.
 */
data class GasFinderUiState(
    val loading: Boolean = true,
    val onboardingDone: Boolean = true,
    val permissionGranted: Boolean = false,
    val location: UserLocation? = null,
    val demoLocationEnabled: Boolean = false,
    val autoDemoFallback: Boolean = false,
    val preferredFuel: FuelType = FuelType.AI95,
    val onlyOpen24: Boolean = false,
    val showKilometers: Boolean = true,
    val query: String = "",
    val stations: List<RankedStation> = emptyList(),
    val selected: GasStation? = null,
    val favorites: Set<String> = emptySet()
) {
    /** Ближайшая АЗС — первая в отсортированном списке. */
    val nearest: RankedStation? get() = stations.firstOrNull()

    /** true, когда расстояние считается от демонстрационной точки, а не от GPS. */
    val usingDemoLocation: Boolean
        get() = demoLocationEnabled || autoDemoFallback || location == null

    val customStations: List<GasStation> get() = stations.map { it.station }.filter { it.isCustom }
}

/**
 * ViewModel экрана: держит состояние, работает с настройками и геолокацией.
 *
 * UI ничего не знает про SharedPreferences и FusedLocationProviderClient —
 * он только читает [uiState] и вызывает методы этого класса (схема MVVM).
 */
class GasFinderViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = PreferencesRepository(application)
    private val customStore = CustomStationStore(prefs)
    private val locationProvider = LocationProvider(application)

    private val _uiState = MutableStateFlow(
        GasFinderUiState(
            onboardingDone = prefs.onboardingDone,
            permissionGranted = locationProvider.hasPermission(),
            demoLocationEnabled = prefs.demoLocation,
            preferredFuel = prefs.preferredFuel,
            onlyOpen24 = prefs.onlyOpen24,
            showKilometers = prefs.showKilometers,
            favorites = prefs.favorites
        )
    )
    val uiState: StateFlow<GasFinderUiState> = _uiState.asStateFlow()

    private var customStations: List<GasStation> = emptyList()
    private var timeoutJob: Job? = null

    init {
        // Настройки реактивно применяются к состоянию: изменил пользователь
        // переключатель — Flow сразу принёс новое значение в UI.
        viewModelScope.launch {
            prefs.demoLocationFlow.collect { enabled ->
                _uiState.update { it.copy(demoLocationEnabled = enabled) }
                refreshStations()
            }
        }
        viewModelScope.launch {
            prefs.preferredFuelFlow.collect { fuel ->
                _uiState.update { it.copy(preferredFuel = fuel) }
            }
        }
        viewModelScope.launch {
            prefs.favoritesFlow.collect { favorites ->
                _uiState.update { it.copy(favorites = favorites) }
            }
        }
        viewModelScope.launch {
            prefs.customStationsFlow.collect {
                customStations = customStore.load()
                refreshStations()
            }
        }

        customStations = customStore.load()
        refreshStations()

        if (locationProvider.hasPermission()) {
            startLocationUpdates()
        } else {
            // Разрешения ещё нет: показываем данные от демонстрационной точки,
            // чтобы экран не был пустым.
            _uiState.update { it.copy(loading = false) }
            refreshStations()
        }
    }

    // ------------------------------------------------------------------
    // Геолокация
    // ------------------------------------------------------------------

    /** Вызывается после ответа пользователя на запрос разрешения. */
    fun onPermissionResult(granted: Boolean) {
        _uiState.update { it.copy(permissionGranted = granted) }
        if (granted) {
            startLocationUpdates()
        } else {
            _uiState.update { it.copy(loading = false) }
            refreshStations()
        }
    }

    private fun startLocationUpdates() {
        timeoutJob?.cancel()

        viewModelScope.launch {
            val last = locationProvider.lastKnown()
            if (last != null) {
                applyLocation(last)
            }
        }

        viewModelScope.launch {
            locationProvider.locationUpdates().collect { location -> applyLocation(location) }
        }

        // Если за отведённое время координаты так и не пришли (эмулятор без
        // заданной точки, выключенный GPS), не держим пользователя в ожидании.
        timeoutJob = viewModelScope.launch {
            delay(LOCATION_TIMEOUT_MS)
            if (_uiState.value.location == null) {
                _uiState.update { it.copy(loading = false, autoDemoFallback = true) }
                refreshStations()
            }
        }
    }

    private fun applyLocation(location: UserLocation) {
        timeoutJob?.cancel()
        val farAway = Geo.distanceKm(
            location.latitude, location.longitude,
            GasStationRepository.DEMO_LATITUDE, GasStationRepository.DEMO_LONGITUDE
        ) > FAR_AWAY_KM

        _uiState.update {
            it.copy(
                location = location,
                loading = false,
                // Далеко от справочника (например, эмулятор в США) — включаем
                // демонстрационную точку, иначе «ближайшая» будет за тысячи км.
                autoDemoFallback = farAway && !it.demoLocationEnabled
            )
        }
        refreshStations()
    }

    // ------------------------------------------------------------------
    // Фильтры и поиск
    // ------------------------------------------------------------------

    fun setQuery(value: String) {
        _uiState.update { it.copy(query = value) }
        refreshStations()
    }

    fun setOnlyOpen24(enabled: Boolean) {
        prefs.onlyOpen24 = enabled
        _uiState.update { it.copy(onlyOpen24 = enabled) }
        refreshStations()
    }

    fun setPreferredFuel(fuel: FuelType) {
        prefs.preferredFuel = fuel
        _uiState.update { it.copy(preferredFuel = fuel) }
    }

    fun setDemoLocation(enabled: Boolean) {
        prefs.demoLocation = enabled
        _uiState.update { it.copy(demoLocationEnabled = enabled, autoDemoFallback = false) }
        refreshStations()
    }

    fun setShowKilometers(enabled: Boolean) {
        prefs.showKilometers = enabled
        _uiState.update { it.copy(showKilometers = enabled) }
    }

    // ------------------------------------------------------------------
    // Выбор АЗС и избранное
    // ------------------------------------------------------------------

    fun select(station: GasStation?) {
        _uiState.update { it.copy(selected = station) }
    }

    fun selectById(id: Int) {
        val station = _uiState.value.stations.firstOrNull { it.station.id == id }?.station
        select(station)
    }

    fun toggleFavorite(id: Int) {
        val key = id.toString()
        val current = prefs.favorites.toMutableSet()
        if (!current.add(key)) current.remove(key)
        prefs.favorites = current
        _uiState.update { it.copy(favorites = current.toSet()) }
    }

    fun isFavorite(id: Int): Boolean = _uiState.value.favorites.contains(id.toString())

    // ------------------------------------------------------------------
    // Свои АЗС
    // ------------------------------------------------------------------

    fun addStation(
        name: String,
        brand: String,
        address: String,
        latitude: Double,
        longitude: Double,
        prices: Map<FuelType, Double>,
        isOpen24: Boolean,
        hasCafe: Boolean,
        hasShop: Boolean,
        hasCarWash: Boolean
    ) {
        customStore.add(
            GasStation(
                id = customStore.nextId(),
                name = name,
                brand = brand.ifBlank { "Моя АЗС" },
                address = address,
                latitude = latitude,
                longitude = longitude,
                prices = prices,
                isOpen24 = isOpen24,
                hasCafe = hasCafe,
                hasShop = hasShop,
                hasCarWash = hasCarWash,
                isCustom = true
            )
        )
        customStations = customStore.load()
        refreshStations()
    }

    fun deleteStation(id: Int) {
        customStore.remove(id)
        customStations = customStore.load()
        if (_uiState.value.selected?.id == id) select(null)
        refreshStations()
    }

    // ------------------------------------------------------------------
    // Прочие действия
    // ------------------------------------------------------------------

    fun completeOnboarding() {
        prefs.onboardingDone = true
        _uiState.update { it.copy(onboardingDone = true) }
    }

    fun resetSettings() {
        prefs.reset()
        _uiState.update {
            it.copy(
                demoLocationEnabled = false,
                autoDemoFallback = false,
                preferredFuel = FuelType.AI95,
                onlyOpen24 = false,
                showKilometers = true,
                favorites = emptySet(),
                query = ""
            )
        }
        customStations = emptyList()
        refreshStations()
    }

    /** Точка, от которой считаются расстояния. */
    private fun origin(): UserLocation {
        val state = _uiState.value
        val useDemo = state.demoLocationEnabled || state.autoDemoFallback || state.location == null
        return if (useDemo) {
            UserLocation(GasStationRepository.DEMO_LATITUDE, GasStationRepository.DEMO_LONGITUDE)
        } else {
            state.location!!
        }
    }

    /** Пересчёт списка: расстояние → фильтры → сортировка «сначала ближайшие». */
    private fun refreshStations() {
        val state = _uiState.value
        val from = origin()
        val query = state.query.trim()

        val ranked = (GasStationRepository.stations + customStations)
            .map { station ->
                RankedStation(
                    station = station,
                    distanceKm = Geo.distanceKm(
                        from.latitude, from.longitude,
                        station.latitude, station.longitude
                    )
                )
            }
            .filter { !state.onlyOpen24 || it.station.isOpen24 }
            .filter { rankedStation ->
                query.isEmpty() ||
                    rankedStation.station.name.contains(query, ignoreCase = true) ||
                    rankedStation.station.address.contains(query, ignoreCase = true) ||
                    rankedStation.station.brand.contains(query, ignoreCase = true)
            }
            .sortedBy { it.distanceKm }

        _uiState.update { it.copy(stations = ranked) }
    }

    private companion object {
        /** Сколько ждать координаты, прежде чем перейти к демонстрационной точке. */
        const val LOCATION_TIMEOUT_MS = 6_000L

        /** Дальше этого расстояния от Екатеринбурга справочник считается «не нашим». */
        const val FAR_AWAY_KM = 200.0
    }
}
