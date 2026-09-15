package com.example.newe_pit.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * DAO untuk Operasi Database Profil Kapal & Sesi Login
 */
@Dao
interface VesselProfileDao {

    @Query("SELECT * FROM vessel_profile WHERE isLoggedIn = 1 LIMIT 1")
    fun getActiveSessionFlow(): Flow<VesselProfileEntity?>

    @Query("SELECT * FROM vessel_profile WHERE isLoggedIn = 1 LIMIT 1")
    suspend fun getActiveSession(): VesselProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveProfile(profile: VesselProfileEntity)

    @Query("UPDATE vessel_profile SET isLoggedIn = 0 WHERE noregBkp = :noregBkp")
    suspend fun logoutUser(noregBkp: String)

    @Query("UPDATE vessel_profile SET isLoggedIn = 0")
    suspend fun logoutAllUsers()

    @Query("UPDATE vessel_profile SET remainingQuotaKg = :newRemainingQuota WHERE noregBkp = :noregBkp")
    suspend fun updateRemainingQuota(noregBkp: String, newRemainingQuota: Double)
}