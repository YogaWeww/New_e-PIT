package com.example.newe_pit.data

/**
 * Status Sinkronisasi Data e-Logbook ke Server KKP
 */
enum class SyncStatus {
    SYNCED,           // Terkirim (Hijau)
    OFFLINE_PENDING,  // Tersimpan di HP (Kuning)
    FAILED            // Gagal Kirim (Merah)
}