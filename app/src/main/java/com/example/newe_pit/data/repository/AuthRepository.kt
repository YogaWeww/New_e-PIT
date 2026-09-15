package com.example.newe_pit.data.repository

import android.content.Context
import com.example.newe_pit.data.local.EPITDatabase
import com.example.newe_pit.data.local.VesselProfileDao
import com.example.newe_pit.data.local.VesselProfileEntity
import com.example.newe_pit.data.model.UserRole
import com.example.newe_pit.data.model.UserSession
import com.example.newe_pit.data.model.VesselInfo
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

/**
 * Repositori Autentikasi & Profil Terintegrasi Room Database Lokal HP.
 */
class AuthRepository(
    private val vesselProfileDao: VesselProfileDao? = null
) {

    companion object {
        @Volatile
        private var INSTANCE: AuthRepository? = null

        val instance: AuthRepository
            get() = INSTANCE ?: synchronized(this) {
                val instance = AuthRepository()
                INSTANCE = instance
                instance
            }

        fun initialize(context: Context): AuthRepository {
            return INSTANCE ?: synchronized(this) {
                val db = EPITDatabase.getDatabase(context)
                val instance = AuthRepository(db.vesselProfileDao())
                INSTANCE = instance
                instance
            }
        }
    }

    private val _fallbackUserSession = MutableStateFlow(
        UserSession(
            noregBkp = "A00029",
            email = "digitalisasi01@maganghub.co.id",
            vesselName = "KMN. DIGITALISASI 01",
            role = UserRole.CAPTAIN,
            isLoggedIn = true
        )
    )

    /**
     * Observable Stream untuk Sesi Pengguna Aktif dari Room DB
     */
    val currentUserSession: Flow<UserSession> = vesselProfileDao?.getActiveSessionFlow()?.map { entity ->
        entity?.toDomainModel() ?: UserSession()
    } ?: _fallbackUserSession.asStateFlow()

    /**
     * Memverifikasi nomor eBKP kapal ke registry KKP
     */
    suspend fun verifyBkp(noregBkp: String): Result<VesselInfo> {
        delay(800)
        return if (noregBkp.equals("A00029", ignoreCase = true) || noregBkp.equals("A000029", ignoreCase = true)) {
            Result.success(
                VesselInfo(
                    bkpNumber = "A00029",
                    vesselName = "KMN. DIGITALISASI 01",
                    grossTonnage = 48,
                    ownerName = "PT. BA***",
                    sloActive = true,
                    spbActive = true,
                    remainingQuotaKg = 5101.0,
                    totalQuotaKg = 10000.0
                )
            )
        } else {
            Result.failure(Exception("Nomor eBKP tidak ditemukan dalam registry KKP."))
        }
    }

    /**
     * Memproses masuk (Sign In) dan menyimpan profil permanen ke Room DB
     */
    suspend fun signIn(noregBkp: String, pass: String): Result<UserSession> {
        delay(600)
        if (noregBkp.isBlank() || pass.length < 6) {
            return Result.failure(Exception("Noreg BKP atau Password salah."))
        }

        val profileEntity = VesselProfileEntity(
            noregBkp = noregBkp,
            vesselName = "KMN. DIGITALISASI 01",
            grossTonnage = 48,
            ownerName = "PT. BA***",
            captainName = "SUPRIYANTO",
            email = "digitalisasi01@maganghub.co.id",
            role = UserRole.CAPTAIN,
            sloActive = true,
            spbActive = true,
            totalQuotaKg = 10000.0,
            remainingQuotaKg = 5101.0,
            isLoggedIn = true,
            rememberMe = true
        )

        vesselProfileDao?.logoutAllUsers()
        vesselProfileDao?.saveProfile(profileEntity)

        val session = profileEntity.toDomainModel()
        _fallbackUserSession.value = session
        return Result.success(session)
    }

    /**
     * Mengaktifkan akun pengguna baru & simpan ke Room DB
     */
    suspend fun activateAccount(role: UserRole, email: String, pass: String): Result<UserSession> {
        delay(900)
        val profileEntity = VesselProfileEntity(
            noregBkp = "A00029",
            vesselName = "KMN. DIGITALISASI 01",
            grossTonnage = 48,
            ownerName = "PT. BA***",
            captainName = "SUPRIYANTO",
            email = email,
            role = role,
            sloActive = true,
            spbActive = true,
            totalQuotaKg = 10000.0,
            remainingQuotaKg = 5101.0,
            isLoggedIn = true,
            rememberMe = true
        )

        vesselProfileDao?.logoutAllUsers()
        vesselProfileDao?.saveProfile(profileEntity)

        val session = profileEntity.toDomainModel()
        _fallbackUserSession.value = session
        return Result.success(session)
    }

    /**
     * Keluar dari akun (Logout) & hapus sesi di Room DB
     */
    suspend fun signOut() {
        vesselProfileDao?.logoutAllUsers()
        _fallbackUserSession.value = UserSession()
    }

    private fun VesselProfileEntity.toDomainModel(): UserSession {
        return UserSession(
            noregBkp = noregBkp,
            email = email,
            vesselName = vesselName,
            role = role,
            isLoggedIn = isLoggedIn
        )
    }
}