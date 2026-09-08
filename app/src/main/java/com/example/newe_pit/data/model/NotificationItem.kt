package com.example.newe_pit.data.model

/**
 * Tipe Kategori Notifikasi e-PIT
 */
enum class NotificationType {
    DOCUMENT_EXPIRY, // Peringatan Dokumen
    SYNC_STATUS,     // Sinkronisasi Data Hauling
    WEATHER_WARNING  // Peringatan Dini Cuaca & Gelombang WPP
}

/**
 * Model Data Notifikasi
 */
data class NotificationItem(
    val id: String,
    val title: String,
    val description: String,
    val timestamp: String,
    val type: NotificationType,
    val isUnread: Boolean = true
)