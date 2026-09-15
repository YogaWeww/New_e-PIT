package com.example.newe_pit.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Tabel `catch_items`: Menyimpan rincian spesies per item tangkapan dalam tawur.
 */
@Entity(
    tableName = "catch_items",
    foreignKeys = [
        ForeignKey(
            entity = HaulRecordEntity::class,
            parentColumns = ["id"],
            childColumns = ["haulRecordId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["haulRecordId"])]
)
data class CatchItemEntity(
    @PrimaryKey
    val id: String,
    val haulRecordId: Long,
    val speciesName: String,
    val weightKg: Int,
    val quantityCount: Int
)