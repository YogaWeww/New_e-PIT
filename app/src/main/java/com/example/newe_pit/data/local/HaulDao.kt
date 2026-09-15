package com.example.newe_pit.data.local

import androidx.room.*
import com.example.newe_pit.data.model.SyncStatus
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) untuk Operasi CRUD Hauling & Catch Items
 */
@Dao
interface HaulDao {

    @Transaction
    @Query("SELECT * FROM haul_records ORDER BY id DESC")
    fun getAllHaulsWithItemsFlow(): Flow<List<HaulWithCatchItems>>

    @Transaction
    @Query("SELECT * FROM haul_records ORDER BY id DESC")
    suspend fun getAllHaulsWithItems(): List<HaulWithCatchItems>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHaulRecord(haul: HaulRecordEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCatchItems(items: List<CatchItemEntity>)

    @Query("UPDATE haul_records SET syncStatus = :status WHERE id = :haulId")
    suspend fun updateSyncStatus(haulId: Long, status: SyncStatus)

    @Query("DELETE FROM haul_records")
    suspend fun clearAllHauls()
}