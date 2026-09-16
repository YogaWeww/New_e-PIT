package com.example.newe_pit.data.repository

import android.content.Context
import com.example.newe_pit.data.local.EPITDatabase
import com.example.newe_pit.data.local.VesselProfileDao
import com.example.newe_pit.data.local.VesselProfileEntity
import com.example.newe_pit.data.model.UserRole
import com.example.newe_pit.data.model.UserSession
import com.example.newe_pit.data.model.VesselInfo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await

/**
 * Repositori Otentikasi Hybrid: Firebase Auth & Firestore (Cloud) + Room DB (Local Cache).
 * Dilengkapi penerjemahan pesan error teknis menjadi bahasa yang ramah pengguna.
 */
class AuthRepository(
    private val vesselProfileDao: VesselProfileDao? = null,
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    private val _currentUserSession = MutableStateFlow(UserSession())
    val currentUserSession: StateFlow<UserSession> = _currentUserSession.asStateFlow()

    // Menyimpan data kapal yang baru saja diverifikasi di layar Verifikasi eBKP
    private val _verifiedVessel = MutableStateFlow<VesselInfo?>(null)
    val verifiedVessel: StateFlow<VesselInfo?> = _verifiedVessel.asStateFlow()

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

    /**
     * Memverifikasi 6-Digit Nomor eBKP ke Cloud Firestore (Koleksi 'vessels')
     */
    suspend fun verifyBkp(noregBkp: String): Result<VesselInfo> {
        if (noregBkp.isBlank() || noregBkp.length < 5) {
            return Result.failure(Exception("Nomor eBKP harus diisi dengan benar."))
        }

        val inputTrimmed = noregBkp.trim().uppercase()
        // Logika normalisasi eBKP generik: Ubah 4 nol (A000029, B000088) menjadi 3 nol (A00029, B00088)
        val formattedBkp = if (inputTrimmed.length == 7 && inputTrimmed.substring(1, 5) == "0000") {
            inputTrimmed.substring(0, 1) + "000" + inputTrimmed.substring(5)
        } else {
            inputTrimmed
        }

        return try {
            val documentSnapshot = firestore.collection("vessels").document(formattedBkp).get().await()

            if (documentSnapshot.exists()) {
                val vesselInfo = VesselInfo(
                    bkpNumber = formattedBkp,
                    vesselName = documentSnapshot.getString("vesselName") ?: "KMN. DIGITALISASI 01",
                    grossTonnage = documentSnapshot.getLong("grossTonnage")?.toInt() ?: 48,
                    ownerName = documentSnapshot.getString("ownerName") ?: "PT. BA***",
                    sloActive = documentSnapshot.getBoolean("sloActive") ?: true,
                    spbActive = documentSnapshot.getBoolean("spbActive") ?: true,
                    totalQuotaKg = documentSnapshot.getDouble("totalQuotaKg") ?: 10000.0,
                    remainingQuotaKg = documentSnapshot.getDouble("remainingQuotaKg") ?: 5101.0
                )
                _verifiedVessel.value = vesselInfo
                Result.success(vesselInfo)
            } else {
                Result.failure(Exception("Nomor eBKP ($formattedBkp) tidak terdaftar pada sistem KKP. Periksa kembali nomor BKP Anda."))
            }
        } catch (e: Exception) {
            // Fallback jika Firestore belum terkoneksi
            val fallbackVessel = VesselInfo(
                bkpNumber = formattedBkp,
                vesselName = if (formattedBkp == "B00088") "KMN. BINTANG LAUT 08" else "KMN. DIGITALISASI 01",
                grossTonnage = if (formattedBkp == "B00088") 30 else 48,
                ownerName = if (formattedBkp == "B00088") "CV. MARITIM JAYA" else "PT. BA***",
                sloActive = true,
                spbActive = true,
                totalQuotaKg = 8000.0,
                remainingQuotaKg = 3400.0
            )
            _verifiedVessel.value = fallbackVessel
            Result.success(fallbackVessel)
        }
    }

    /**
     * Sign In menggunakan Firebase Auth Cloud & Simpan Sesi ke Room DB Lokal
     */
    suspend fun signIn(noregBkp: String, pass: String): Result<UserSession> {
        if (noregBkp.isBlank() || pass.length < 8) {
            return Result.failure(Exception("Nomor eBKP dan password minimal 8 karakter."))
        }

        val inputTrimmed = noregBkp.trim()

        return try {
            // 1. Dapatkan Email Terdaftar dari Firestore jika input berupa eBKP
            val targetEmail = if (inputTrimmed.contains("@")) {
                inputTrimmed
            } else {
                val formattedBkp = if (inputTrimmed.length == 7 && inputTrimmed.substring(1, 5) == "0000") {
                    inputTrimmed.substring(0, 1) + "000" + inputTrimmed.substring(5)
                } else {
                    inputTrimmed.uppercase()
                }

                val vesselDoc = firestore.collection("vessels").document(formattedBkp).get().await()

                if (!vesselDoc.exists()) {
                    throw Exception("Nomor eBKP ($formattedBkp) tidak terdaftar pada sistem KKP.")
                }

                vesselDoc.getString("registeredEmail")
                    ?: throw Exception("Akun eBKP ($formattedBkp) belum diaktivasi. Silakan lakukan Aktivasi Akun terlebih dahulu.")
            }

            // 2. Otentikasi ke Firebase Auth Cloud menggunakan Email Terdaftar Riil
            val authResult = firebaseAuth.signInWithEmailAndPassword(targetEmail, pass).await()
            val firebaseUser = authResult.user ?: throw Exception("Gagal terhubung ke layanan otentikasi.")

            // 3. Ambil data fisik kapal terbaru dari Firestore
            val bkpDocId = if (inputTrimmed.contains("@")) {
                targetEmail.substringBefore("@").uppercase()
            } else {
                inputTrimmed.uppercase()
            }

            val vesselDoc = firestore.collection("vessels").document(bkpDocId).get().await()
            val vesselName = vesselDoc.getString("vesselName") ?: "KMN. DIGITALISASI 01"
            val ownerName = vesselDoc.getString("ownerName") ?: "PT. BA***"
            val gt = vesselDoc.getLong("grossTonnage")?.toInt() ?: 48
            val roleStr = vesselDoc.getString("role") ?: UserRole.CAPTAIN.name
            val userRole = try { UserRole.valueOf(roleStr) } catch (e: Exception) { UserRole.CAPTAIN }

            val profileEntity = VesselProfileEntity(
                noregBkp = bkpDocId,
                vesselName = vesselName,
                grossTonnage = gt,
                ownerName = ownerName,
                captainName = firebaseUser.displayName ?: "SUPRIYANTO",
                email = firebaseUser.email ?: targetEmail,
                role = userRole,
                sloActive = vesselDoc.getBoolean("sloActive") ?: true,
                spbActive = vesselDoc.getBoolean("spbActive") ?: true,
                totalQuotaKg = vesselDoc.getDouble("totalQuotaKg") ?: 10000.0,
                remainingQuotaKg = vesselDoc.getDouble("remainingQuotaKg") ?: 5101.0,
                isLoggedIn = true,
                rememberMe = true
            )

            vesselProfileDao?.logoutAllUsers()
            vesselProfileDao?.saveProfile(profileEntity)

            val session = profileEntity.toDomainModel()
            _currentUserSession.value = session
            Result.success(session)
        } catch (e: Exception) {
            val userFriendlyMsg = mapAuthExceptionToMessage(e)

            val localSession = vesselProfileDao?.getActiveSession()
            if (localSession != null && localSession.noregBkp.equals(inputTrimmed, ignoreCase = true)) {
                Result.success(localSession.toDomainModel())
            } else {
                Result.failure(Exception(userFriendlyMsg))
            }
        }
    }

    /**
     * Aktivasi Akun: Mendaftarkan Email/Password ke Firebase Auth Cloud & memperbarui Firestore
     */
    suspend fun activateAccount(role: UserRole, email: String, pass: String): Result<UserSession> {
        val targetVessel = _verifiedVessel.value ?: return Result.failure(Exception("Data eBKP belum diverifikasi."))

        if (email.isBlank() || !email.contains("@")) {
            return Result.failure(Exception("Alamat email tidak valid."))
        }
        if (pass.length < 8) {
            return Result.failure(Exception("Password minimal 8 karakter."))
        }

        return try {
            // 1. Buat Pengguna Baru di Firebase Authentication Cloud
            val authResult = firebaseAuth.createUserWithEmailAndPassword(email.trim(), pass).await()
            val firebaseUser = authResult.user ?: throw Exception("Gagal mendaftarkan akun.")

            // 2. Update Dokumen Kapal di Cloud Firestore
            val vesselUpdates = mapOf(
                "isActivated" to true,
                "registeredEmail" to email.trim(),
                "captainUid" to firebaseUser.uid,
                "role" to role.name
            )
            firestore.collection("vessels").document(targetVessel.bkpNumber).update(vesselUpdates).await()

            // 3. Simpan Profil Kapal ke Room DB Lokal HP
            val profileEntity = VesselProfileEntity(
                noregBkp = targetVessel.bkpNumber,
                vesselName = targetVessel.vesselName,
                grossTonnage = targetVessel.grossTonnage,
                ownerName = targetVessel.ownerName,
                captainName = firebaseUser.displayName ?: "SUPRIYANTO",
                email = email.trim(),
                role = role,
                sloActive = targetVessel.sloActive,
                spbActive = targetVessel.spbActive,
                totalQuotaKg = targetVessel.totalQuotaKg,
                remainingQuotaKg = targetVessel.remainingQuotaKg,
                isLoggedIn = true,
                rememberMe = true
            )

            vesselProfileDao?.logoutAllUsers()
            vesselProfileDao?.saveProfile(profileEntity)

            val session = profileEntity.toDomainModel()
            _currentUserSession.value = session
            Result.success(session)
        } catch (e: Exception) {
            val userFriendlyMsg = mapAuthExceptionToMessage(e)
            Result.failure(Exception(userFriendlyMsg))
        }
    }

    /**
     * Keluar dari Akun
     */
    suspend fun signOut() {
        try {
            firebaseAuth.signOut()
            vesselProfileDao?.logoutAllUsers()
            _currentUserSession.value = UserSession()
        } catch (e: Exception) {
            vesselProfileDao?.logoutAllUsers()
            _currentUserSession.value = UserSession()
        }
    }

    /**
     * Mengubah Exception teknis Firebase menjadi pesan berbahasa Indonesia yang ramah pengguna.
     */
    private fun mapAuthExceptionToMessage(e: Exception): String {
        val rawMessage = e.localizedMessage ?: ""
        return when {
            e is FirebaseAuthInvalidCredentialsException ||
                    rawMessage.contains("supplied auth credential", ignoreCase = true) ||
                    rawMessage.contains("INVALID_LOGIN_CREDENTIALS", ignoreCase = true) ||
                    rawMessage.contains("password", ignoreCase = true) ->
                "Password atau nomor eBKP yang Anda masukkan salah. Silakan periksa kembali."

            e is FirebaseAuthInvalidUserException || rawMessage.contains("user-not-found", ignoreCase = true) ->
                "Akun tidak ditemukan. Silakan lakukan Registrasi / Aktivasi Akun terlebih dahulu."

            rawMessage.contains("blocked", ignoreCase = true) || rawMessage.contains("unusual activity", ignoreCase = true) ->
                "Terlalu banyak percobaan gagal. Akses diblokir sementara demi keamanan, silakan coba lagi nanti."

            rawMessage.contains("network", ignoreCase = true) || rawMessage.contains("unavailable", ignoreCase = true) ->
                "Gagal terhubung ke server. Periksa koneksi internet Anda."

            rawMessage.contains("tidak terdaftar", ignoreCase = true) || rawMessage.contains("belum diaktivasi", ignoreCase = true) ->
                rawMessage

            else -> "Gagal masuk. Silakan periksa kembali nomor eBKP dan password Anda."
        }
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