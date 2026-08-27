package com.example.newe_pit.data.repository

import com.example.newe_pit.data.model.UserRole
import com.example.newe_pit.data.model.UserSession
import com.example.newe_pit.data.model.VesselInfo
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Repositori untuk Autentikasi, Verifikasi eBKP, dan Aktivasi Akun.
 * Menggunakan data simulasi (Dummy) sebelum dihubungkan ke Firebase Auth/Firestore.
 */
class AuthRepository {

    private val _currentUserSession = MutableStateFlow(
        UserSession(
            noregBkp = "A00029",
            email = "digitalisasi01@maganghub.co.id",
            vesselName = "KMN. DIGITALISASI 01",
            role = UserRole.CAPTAIN,
            isLoggedIn = false
        )
    )
    val currentUserSession: Flow<UserSession> = _currentUserSession.asStateFlow()

    /**
     * Memverifikasi nomor eBKP kapal ke database
     */
    suspend fun verifyBkp(noregBkp: String): Result<VesselInfo> {
        delay(800) // Simulasi delay jaringan
        // Menerima "A00029" maupun "A000029"
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
     * Memproses masuk (Sign In) pengguna
     */
    suspend fun signIn(noregBkp: String, pass: String): Result<UserSession> {
        delay(600)
        return if (noregBkp.isNotBlank() && pass.length >= 6) {
            val session = _currentUserSession.value.copy(
                noregBkp = noregBkp,
                isLoggedIn = true
            )
            _currentUserSession.value = session
            Result.success(session)
        } else {
            Result.failure(Exception("Noreg BKP atau Password salah."))
        }
    }

    /**
     * Mengaktifkan akun pengguna baru
     */
    suspend fun activateAccount(role: UserRole, email: String, pass: String): Result<UserSession> {
        delay(1000)
        val session = _currentUserSession.value.copy(
            email = email,
            role = role,
            isLoggedIn = true
        )
        _currentUserSession.value = session
        return Result.success(session)
    }

    fun signOut() {
        _currentUserSession.value = UserSession()
    }
}