package com.example.newe_pit.data.model

/**
 * Domain Model untuk 1 Tawur/Hauling Terintegrasi dengan Akun Kapal (noregBkp)
 */
data class HaulRecord(
    val id: Int = 0,
    val noregBkp: String = "",
    val haulNumber: Int = 0,
    val timestampFormatted: String = "",
    val totalWeightKg: Int = 0,
    val totalQuantityCount: Int = 0,
    val catchItems: List<CatchItem> = emptyList(),
    val startLatitude: Double = 0.0,
    val startLongitude: Double = 0.0,
    val endLatitude: Double = 0.0,
    val endLongitude: Double = 0.0,
    val wppRegion: String = "",
    val soakDurationMinutes: Long = 0L,
    val syncStatus: SyncStatus = SyncStatus.OFFLINE_PENDING
)