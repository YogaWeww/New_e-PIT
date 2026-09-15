package com.example.newe_pit.data.local

import androidx.room.TypeConverter
import com.example.newe_pit.data.model.SyncStatus

/**
 * Konversi Tipe Data Enum & Custom Types untuk Room Database
 */
class EPITTypeConverters {
    @TypeConverter
    fun fromSyncStatus(status: SyncStatus): String = status.name

    @TypeConverter
    fun toSyncStatus(statusStr: String): SyncStatus = try {
        SyncStatus.valueOf(statusStr)
    } catch (e: Exception) {
        SyncStatus.OFFLINE_PENDING
    }
}