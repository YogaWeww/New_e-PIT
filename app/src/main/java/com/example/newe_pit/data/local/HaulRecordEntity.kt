package com.example.newe_pit.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.newe_pit.data.model.SyncStatus

/**
 * Tabel `haul_records`: Menyimpan catatan utama per penarikan jaring/tawur.
 */
@Entity(tableName = "haul_records")
data class HaulRecordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val haulNumber: Int,
    val timestampFormatted: String,
    val totalWeightKg: Int,
    val totalQuantityCount: Int,
    val startLatitude: Double = 0.0,
    val startLongitude: Double = 0.0,
    val endLatitude: Double = 0.0,
    val endLongitude: Double = 0.0,
    val wppRegion: String = "WPP-NRI 718",
    val soakDurationMinutes: Long = 0,
    val syncStatus: SyncStatus = SyncStatus.OFFLINE_PENDING
)