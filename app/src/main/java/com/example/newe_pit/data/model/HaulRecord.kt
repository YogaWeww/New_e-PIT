package com.example.newe_pit.data.model

/**
 * Model Catatan Hasil Tawur / Hauling Utuh
 */
data class HaulRecord(
    val id: Int,
    val haulNumber: Int,
    val timestampFormatted: String,
    val totalWeightKg: Int,
    val totalQuantityCount: Int,
    val catchItems: List<CatchItem> = emptyList(),
    val startLatitude: Double = 0.0,
    val startLongitude: Double = 0.0,
    val endLatitude: Double = 0.0,
    val endLongitude: Double = 0.0,
    val wppRegion: String = "WPP-NRI 718",
    val soakDurationMinutes: Long = 0,
    val syncStatus: SyncStatus = SyncStatus.OFFLINE_PENDING
)