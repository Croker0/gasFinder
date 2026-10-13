package com.example.gasfinder.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Looper
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/** Координаты пользователя. */
data class UserLocation(val latitude: Double, val longitude: Double)

/**
 * Получение местоположения через FusedLocationProviderClient
 * (библиотека play-services-location из методички).
 *
 * Используется два источника:
 *  - lastKnown() — последняя известная позиция: приходит мгновенно и заполняет
 *    интерфейс, пока GPS ещё «прогревается»;
 *  - locationUpdates() — поток уточнений по мере движения.
 */
class LocationProvider(context: Context) {

    private val appContext = context.applicationContext
    private val client = LocationServices.getFusedLocationProviderClient(appContext)

    /** Есть ли уже выданное разрешение на геолокацию. */
    fun hasPermission(): Boolean {
        val fine = ContextCompat.checkSelfPermission(
            appContext, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarse = ContextCompat.checkSelfPermission(
            appContext, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        return fine || coarse
    }

    /** Последняя известная позиция или null, если её нет. */
    @SuppressLint("MissingPermission")
    suspend fun lastKnown(): UserLocation? {
        if (!hasPermission()) return null
        return suspendCancellableCoroutine { continuation ->
            client.lastLocation
                .addOnSuccessListener { location ->
                    continuation.resume(
                        location?.let { UserLocation(it.latitude, it.longitude) }
                    )
                }
                .addOnFailureListener { continuation.resume(null) }
        }
    }

    /**
     * Поток обновлений местоположения.
     * При отсутствии разрешения поток сразу закрывается — вызывающий код
     * перейдёт в состояние «нужно разрешение».
     */
    @SuppressLint("MissingPermission")
    fun locationUpdates(): Flow<UserLocation> = callbackFlow {
        if (!hasPermission()) {
            close()
            return@callbackFlow
        }
        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, UPDATE_INTERVAL_MS)
            .setMinUpdateIntervalMillis(MIN_UPDATE_INTERVAL_MS)
            .setWaitForAccurateLocation(false)
            .build()

        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { location ->
                    trySend(UserLocation(location.latitude, location.longitude))
                }
            }
        }

        client.requestLocationUpdates(request, callback, Looper.getMainLooper())
        awaitClose { client.removeLocationUpdates(callback) }
    }

    private companion object {
        const val UPDATE_INTERVAL_MS = 5_000L
        const val MIN_UPDATE_INTERVAL_MS = 2_000L
    }
}
