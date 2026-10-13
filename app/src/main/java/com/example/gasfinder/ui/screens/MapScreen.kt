package com.example.gasfinder.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.gasfinder.R
import com.example.gasfinder.data.GasStation
import com.example.gasfinder.data.RankedStation
import com.example.gasfinder.location.UserLocation
import org.osmdroid.tileprovider.tilesource.XYTileSource
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

/**
 * Источник тайлов карты.
 *
 * Данные — OpenStreetMap, стиль — стандартный osmde. Почему не штатный
 * сервер `tile.openstreetmap.org`: он блокирует обращения сторонних
 * приложений (ответ 403 «Access blocked») по правилам использования
 * добровольческих серверов, причём независимо от User-Agent.
 * Сервер немецкого сообщества OSM (`tile.openstreetmap.de`) отдаёт те же
 * данные и к сторонним клиентам относится терпимо. Варианты с CARTO
 * и Wikimedia проверялись, но требуют API-ключа либо отдают 403.
 * API-ключ проекту не нужен — это осознанное требование к стеку.
 */
private val OsmTiles = XYTileSource(
    "OpenStreetMapDE",
    0,
    19,
    256,
    ".png",
    arrayOf("https://tile.openstreetmap.de/"),
    "© OpenStreetMap contributors"
)

/**
 * Карта OpenStreetMap через osmdroid.
 *
 * osmdroid — это классический View, а не composable, поэтому он встраивается
 * через [AndroidView]. Жизненным циклом карты управляем вручную: без вызовов
 * onResume/onPause она продолжает тянуть тайлы в фоне и расходовать батарею.
 */
@Composable
fun MapScreen(
    origin: UserLocation,
    stations: List<RankedStation>,
    onSelect: (GasStation) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val mapView = remember {
        MapView(context).apply {
            setTileSource(OsmTiles)
            setMultiTouchControls(true)
            controller.setZoom(13.0)
            controller.setCenter(GeoPoint(origin.latitude, origin.longitude))
        }
    }

    // Плавно переводим камеру, когда пользователь нажал «Я здесь»
    // или координаты обновились.
    LaunchedEffect(origin.latitude, origin.longitude) {
        mapView.controller.animateTo(GeoPoint(origin.latitude, origin.longitude))
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            mapView.onDetach()
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        AndroidView(
            factory = { mapView },
            // clipToBounds обязателен: osmdroid рисует маркеры, выходящие за
            // границы карты (у пина якорь снизу, и он уходит вверх). Без обрезки
            // AndroidView не ограничивает отрисовку, и пины налезают на шапку
            // экрана, перекрывая текст.
            modifier = Modifier
                .fillMaxSize()
                .clipToBounds(),
            update = { map ->
                map.overlays.clear()

                // Метка «Я здесь»
                map.overlays.add(
                    Marker(map).apply {
                        position = GeoPoint(origin.latitude, origin.longitude)
                        title = context.getString(R.string.my_location)
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    }
                )

                // Метки АЗС
                stations.forEach { ranked ->
                    map.overlays.add(
                        Marker(map).apply {
                            position = GeoPoint(
                                ranked.station.latitude,
                                ranked.station.longitude
                            )
                            title = ranked.station.name
                            snippet = ranked.station.address
                            setOnMarkerClickListener { _, _ ->
                                onSelect(ranked.station)
                                true
                            }
                        }
                    )
                }
                map.invalidate()
            }
        )

        ExtendedFloatingActionButton(
            onClick = {
                mapView.controller.animateTo(GeoPoint(origin.latitude, origin.longitude))
                mapView.controller.setZoom(15.0)
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(painterResource(R.drawable.ic_location), contentDescription = null)
            Text(
                text = stringResource(R.string.my_location),
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}
