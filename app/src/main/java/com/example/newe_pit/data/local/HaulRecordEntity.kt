package com.example.newe_pit.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.newe_pit.data.model.SyncStatus

/**
 * Tabel `haul_records`: Menyimpan data utama 1 tawur/hauling di Room Database (SQLite Lokal).
 * Ditambahkan atribut `noregBkp` agar terikat ke akun kapal terdaftar.
 */
@Entity(tableName = "haul_records")
data class HaulRecordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val noregBkp: String = "",
    val haulNumber: Int,
    val timestampFormatted: String,
    val totalWeightKg: Int,
    val totalQuantityCount: Int,
    val startLatitude: Double,
    val startLongitude: Double,
    val endLatitude: Double,
    val endLongitude: Double,
    val wppRegion: String,
    val soakDurationMinutes: Long,
    val syncStatus: SyncStatus = SyncStatus.OFFLINE_PENDING
)