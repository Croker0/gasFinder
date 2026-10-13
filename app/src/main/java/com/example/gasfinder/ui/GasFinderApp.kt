package com.example.gasfinder.ui

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gasfinder.R
import com.example.gasfinder.data.GasStationRepository
import com.example.gasfinder.location.UserLocation
import com.example.gasfinder.ui.screens.AddStationScreen
import com.example.gasfinder.ui.screens.ListScreen
import com.example.gasfinder.ui.screens.MapScreen
import com.example.gasfinder.ui.screens.OnboardingScreen
import com.example.gasfinder.ui.screens.ProfileScreen
import com.example.gasfinder.ui.screens.StationDetailSheet
import com.example.gasfinder.util.Geo
import com.example.gasfinder.viewmodel.GasFinderViewModel

/** Вкладки нижней навигации. */
private enum class Tab { MAP, LIST, PROFILE }

/**
 * Корневой composable приложения: онбординг, три вкладки и нижняя панель
 * с подробностями выбранной АЗС.
 *
 * Навигация сделана на простом состоянии, а не на Navigation Component:
 * экранов всего три плюс форма, и полноценный граф маршрутов здесь был бы
 * избыточен. Переход на Navigation — в бэклоге после MVP.
 */
@Composable
fun GasFinderApp(viewModel: GasFinderViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var tab by remember { mutableStateOf(Tab.MAP) }
    var showAddForm by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        viewModel.onPermissionResult(result.values.any { it })
    }

    val requestPermission: () -> Unit = {
        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    LaunchedEffect(Unit) {
        if (state.onboardingDone && !state.permissionGranted) {
            requestPermission()
        }
    }

    if (!state.onboardingDone) {
        OnboardingScreen(
            onFinish = { viewModel.completeOnboarding() },
            onRequestPermission = requestPermission
        )
        return
    }

    // Точка отсчёта расстояний: либо реальные координаты, либо демонстрационные.
    val origin = if (state.usingDemoLocation || state.location == null) {
        UserLocation(GasStationRepository.DEMO_LATITUDE, GasStationRepository.DEMO_LONGITUDE)
    } else {
        state.location!!
    }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = tab == Tab.MAP,
                    onClick = { tab = Tab.MAP; showAddForm = false },
                    icon = { Icon(painterResource(R.drawable.ic_location), contentDescription = null) },
                    label = { Text(stringResource(R.string.nav_map)) }
                )
                NavigationBarItem(
                    selected = tab == Tab.LIST,
                    onClick = { tab = Tab.LIST },
                    icon = { Icon(painterResource(R.drawable.ic_list), contentDescription = null) },
                    label = { Text(stringResource(R.string.nav_list)) }
                )
                NavigationBarItem(
                    selected = tab == Tab.PROFILE,
                    onClick = { tab = Tab.PROFILE; showAddForm = false },
                    icon = { Icon(painterResource(R.drawable.ic_person), contentDescription = null) },
                    label = { Text(stringResource(R.string.nav_profile)) }
                )
            }
        },
        floatingActionButton = {
            if (tab == Tab.LIST && !showAddForm) {
                FloatingActionButton(onClick = { showAddForm = true }) {
                    Icon(
                        painter = painterResource(R.drawable.ic_add),
                        contentDescription = stringResource(R.string.nav_add)
                    )
                }
            }
        }
    ) { innerPadding ->
        // fillMaxWidth + clipToBounds — страховка от того, что вложенный
        // AndroidView с картой начнёт рисовать шире или выше своих границ
        // и перекроет соседние элементы интерфейса.
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxWidth()
                .clipToBounds()
        ) {
            NearestHeader(
                name = state.nearest?.station?.name,
                distance = state.nearest?.let { Geo.formatDistance(it.distanceKm) },
                usingDemoLocation = state.usingDemoLocation
            )

            Box(modifier = Modifier.weight(1f)) {
                when {
                    showAddForm -> AddStationScreen(
                        origin = origin,
                        onSave = { form ->
                            viewModel.addStation(
                                name = form.name,
                                brand = form.brand,
                                address = form.address,
                                latitude = form.latitude,
                                longitude = form.longitude,
                                prices = form.prices,
                                isOpen24 = form.isOpen24,
                                hasCafe = form.hasCafe,
                                hasShop = form.hasShop,
                                hasCarWash = form.hasCarWash
                            )
                            showAddForm = false
                            tab = Tab.LIST
                        },
                        onCancel = { showAddForm = false }
                    )

                    tab == Tab.MAP -> MapScreen(
                        origin = origin,
                        stations = state.stations,
                        onSelect = { viewModel.select(it) }
                    )

                    tab == Tab.LIST -> ListScreen(
                        stations = state.stations,
                        preferredFuel = state.preferredFuel,
                        favorites = state.favorites,
                        query = state.query,
                        onlyOpen24 = state.onlyOpen24,
                        onQueryChange = viewModel::setQuery,
                        onFuelChange = viewModel::setPreferredFuel,
                        onOnlyOpen24Change = viewModel::setOnlyOpen24,
                        onSelect = { viewModel.select(it) }
                    )

                    else -> ProfileScreen(
                        demoLocationEnabled = state.demoLocationEnabled,
                        autoDemoFallback = state.autoDemoFallback,
                        preferredFuel = state.preferredFuel,
                        showKilometers = state.showKilometers,
                        favorites = state.favorites,
                        stations = state.stations,
                        onDemoLocationChange = viewModel::setDemoLocation,
                        onFuelChange = viewModel::setPreferredFuel,
                        onShowKilometersChange = viewModel::setShowKilometers,
                        onReset = { viewModel.resetSettings() },
                        onAddStation = { showAddForm = true },
                        onSelectStation = { viewModel.select(it) }
                    )
                }
            }
        }
    }

    // Подробности выбранной АЗС — поверх всего экрана.
    state.selected?.let { station ->
        StationDetailSheet(
            station = station,
            distanceKm = state.stations.firstOrNull { it.station.id == station.id }?.distanceKm,
            isFavorite = state.favorites.contains(station.id.toString()),
            onDismiss = { viewModel.select(null) },
            onToggleFavorite = { viewModel.toggleFavorite(station.id) },
            onDelete = { viewModel.deleteStation(station.id) }
        )
    }
}

/**
 * Компактная строка сверху: какая АЗС ближайшая и на каком расстоянии.
 * Если расстояние считается от демонстрационной точки, честно об этом сообщаем.
 */
@Composable
private fun NearestHeader(
    name: String?,
    distance: String?,
    usingDemoLocation: Boolean
) {
    Surface(
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
            Text(
                text = stringResource(R.string.nearest_station),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
            )
            Spacer(Modifier.height(2.dp))
            if (name != null && distance != null) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = distance,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            } else {
                Text(
                    text = stringResource(R.string.location_searching),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
            if (usingDemoLocation) {
                Text(
                    text = stringResource(R.string.settings_demo_location),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                )
            }
        }
    }
}
