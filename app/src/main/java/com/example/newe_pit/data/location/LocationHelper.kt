package com.example.newe_pit.data.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.coroutines.resume

/**
 * Helper class untuk membaca koordinat GPS perangkat secara langsung.
 * Mendukung pembacaan paralel dari GPS_PROVIDER, NETWORK_PROVIDER, dan PASSIVE_PROVIDER
 * untuk mengatasi batasan penghemat daya bawaan vendor HP (Oppo/Realme/Xiaomi).
 */
object LocationHelper {

    /**
     * Memeriksa apakah provider GPS atau Network pada perangkat sedang aktif.
     */
    fun isGpsActive(context: Context): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
            ?: return false
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    /**
     * Membaca koordinat GPS terbaru dari perangkat (Latitude & Longitude).
     * Mendaftarkan listener ke GPS & Network provider sekaligus agar tetap mendapat posisi di dalam ruangan.
     */
    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(context: Context): Location? {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
            ?: return null

        if (!isGpsActive(context)) {
            return null
        }

        // 1. Ambil lokasi terakhir dari seluruh provider yang tersedia
        val providers = listOf(
            LocationManager.GPS_PROVIDER,
            LocationManager.NETWORK_PROVIDER,
            LocationManager.PASSIVE_PROVIDER
        )

        var bestLastLocation: Location? = null
        for (provider in providers) {
            try {
                if (locationManager.isProviderEnabled(provider)) {
                    val loc = locationManager.getLastKnownLocation(provider)
                    if (loc != null) {
                        if (bestLastLocation == null || loc.time > bestLastLocation.time) {
                            bestLastLocation = loc
                        }
                    }
                }
            } catch (_: Exception) {}
        }

        // Jika lokasi terakhir ada dan belum terlalu usang (< 60 detik), langsung gunakan
        if (bestLastLocation != null && (System.currentTimeMillis() - bestLastLocation.time) < 60_000) {
            return bestLastLocation
        }

        // 2. Minta pembaruan lokasi aktif secara langsung dengan timeout 5 detik
        return withTimeoutOrNull(5000L) {
            suspendCancellableCoroutine { continuation ->
                val listeners = mutableListOf<LocationListener>()

                val activeProviders = providers.filter {
                    try { locationManager.isProviderEnabled(it) } catch (_: Exception) { false }
                }

                if (activeProviders.isEmpty()) {
                    if (continuation.isActive) continuation.resume(bestLastLocation)
                    return@suspendCancellableCoroutine
                }

                // Handler serentak untuk pembacaan pertama dari provider mana pun yang tercepat
                for (provider in activeProviders) {
                    val listener = object : LocationListener {
                        override fun onLocationChanged(location: Location) {
                            listeners.forEach {
                                try { locationManager.removeUpdates(it) } catch (_: Exception) {}
                            }
                            if (continuation.isActive) {
                                continuation.resume(location)
                            }
                        }

                        @Deprecated("Deprecated in Java")
                        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
                        override fun onProviderEnabled(provider: String) {}
                        override fun onProviderDisabled(provider: String) {}
                    }

                    listeners.add(listener)

                    try {
                        locationManager.requestLocationUpdates(
                            provider,
                            0L,
                            0f,
                            listener,
                            Looper.getMainLooper()
                        )
                    } catch (_: Exception) {}
                }

                continuation.invokeOnCancellation {
                    listeners.forEach {
                        try { locationManager.removeUpdates(it) } catch (_: Exception) {}
                    }
                }
            }
        } ?: bestLastLocation
    }
}