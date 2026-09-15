package com.example.newe_pit.data.local

import androidx.room.Embedded
import androidx.room.Relation

/**
 * Relasi 1-to-Many: 1 Haul Record memiliki banyak Catch Items.
 */
data class HaulWithCatchItems(
    @Embedded val haulRecord: HaulRecordEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "haulRecordId"
    )
    val catchItems: List<CatchItemEntity>
)