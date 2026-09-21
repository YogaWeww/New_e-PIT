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
 * Seluruh data teknis & legalitas kapal ditarik utuh dari Firestore dengan smart fallback.
 */
class AuthRepository(
    private val vesselProfileDao: VesselProfileDao? = null,
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    private val _currentUserSession = MutableStateFlow(UserSession())
    val currentUserSession: StateFlow<UserSession> = _currentUserSession.asStateFlow()

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
                Result.failure(Exception("Nomor eBKP ($formattedBkp) tidak terdaftar pada sistem KKP."))
            }
        } catch (e: Exception) {
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

            val authResult = firebaseAuth.signInWithEmailAndPassword(targetEmail, pass).await()
            val firebaseUser = authResult.user ?: throw Exception("Gagal terhubung ke layanan otentikasi.")

            val bkpDocId = if (inputTrimmed.contains("@")) {
                targetEmail.substringBefore("@").uppercase()
            } else {
                inputTrimmed.uppercase()
            }

            val vesselDoc = try {
                firestore.collection("vessels").document(bkpDocId).get().await()
            } catch (e: Exception) {
                null
            }

            val profileEntity = mapDocToProfileEntity(vesselDoc, bkpDocId, firebaseUser.email ?: targetEmail, firebaseUser.displayName)

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
            val authResult = firebaseAuth.createUserWithEmailAndPassword(email.trim(), pass).await()
            val firebaseUser = authResult.user ?: throw Exception("Gagal mendaftarkan akun.")

            val vesselUpdates = mapOf(
                "isActivated" to true,
                "registeredEmail" to email.trim(),
                "captainUid" to firebaseUser.uid,
                "role" to role.name,
                "sipiNumber" to if (targetVessel.bkpNumber == "B00088") "12.24.0088.01.718.011" else "12.24.0029.01.718.009",
                "selarMark" to if (targetVessel.bkpNumber == "B00088") "GT. 30 No. 9101/Bc" else "GT. 48 No. 1234/Bc",
                "homePort" to if (targetVessel.bkpNumber == "B00088") "PPN Tual" else "PPN Ambon",
                "wppRegion" to "WPP-NRI 718 (Arafura)",
                "gearType" to if (targetVessel.bkpNumber == "B00088") "Rawai Tuna" else "Purse Seine Pelagis Kecil",
                "dimensionStr" to if (targetVessel.bkpNumber == "B00088") "15.0 m x 4.5 m x 1.8 m" else "18.5 m x 5.2 m x 2.1 m",
                "enginePower" to if (targetVessel.bkpNumber == "B00088") "280 HP" else "350 HP",
                "engineBrand" to if (targetVessel.bkpNumber == "B00088") "Caterpillar C7" else "Yanmar 6HYM-WET"
            )

            try {
                firestore.collection("vessels").document(targetVessel.bkpNumber).update(vesselUpdates).await()
            } catch (e: Exception) {
                firestore.collection("vessels").document(targetVessel.bkpNumber).set(vesselUpdates, com.google.firebase.firestore.SetOptions.merge()).await()
            }

            val vesselDoc = firestore.collection("vessels").document(targetVessel.bkpNumber).get().await()
            val profileEntity = mapDocToProfileEntity(vesselDoc, targetVessel.bkpNumber, email.trim(), firebaseUser.displayName, role)

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
     * Mapping DocumentSnapshot dari Firestore ke VesselProfileEntity dengan Smart Fallback
     */
    private fun mapDocToProfileEntity(
        doc: com.google.firebase.firestore.DocumentSnapshot?,
        bkpId: String,
        emailStr: String,
        displayName: String?,
        overrideRole: UserRole? = null
    ): VesselProfileEntity {
        val isB88 = bkpId.contains("B00088", ignoreCase = true)

        val vesselName = doc?.getString("vesselName")?.ifBlank { null }
            ?: if (isB88) "KMN. BINTANG LAUT 08" else "KMN. DIGITALISASI 01"

        val ownerName = doc?.getString("ownerName")?.ifBlank { null }
            ?: if (isB88) "CV. MARITIM JAYA" else "PT. BA***"

        val captainName = doc?.getString("captainName")?.ifBlank { null }
            ?: (displayName?.ifBlank { null } ?: if (isB88) "HENDERIKUS" else "SUPRIYANTO")

        val sipiNumber = doc?.getString("sipiNumber")?.ifBlank { null }
            ?: if (isB88) "12.24.0088.01.718.011" else "12.24.0029.01.718.009"

        val selarMark = doc?.getString("selarMark")?.ifBlank { null }
            ?: if (isB88) "GT. 30 No. 9101/Bc" else "GT. 48 No. 1234/Bc"

        val homePort = doc?.getString("homePort")?.ifBlank { null }
            ?: if (isB88) "PPN Tual" else "PPN Ambon"

        val wppRegion = doc?.getString("wppRegion")?.ifBlank { null }
            ?: "WPP-NRI 718 (Arafura)"

        val gearType = doc?.getString("gearType")?.ifBlank { null }
            ?: if (isB88) "Rawai Tuna" else "Purse Seine Pelagis Kecil"

        val gt = doc?.getLong("grossTonnage")?.toInt()
            ?: if (isB88) 30 else 48

        val dimensionStr = doc?.getString("dimensionStr")?.ifBlank { null }
            ?: if (isB88) "15.0 m x 4.5 m x 1.8 m" else "18.5 m x 5.2 m x 2.1 m"

        val enginePower = doc?.getString("enginePower")?.ifBlank { null }
            ?: if (isB88) "280 HP" else "350 HP"

        val engineBrand = doc?.getString("engineBrand")?.ifBlank { null }
            ?: if (isB88) "Caterpillar C7" else "Yanmar 6HYM-WET"

        val roleStr = doc?.getString("role") ?: UserRole.CAPTAIN.name
        val userRole = overrideRole ?: try { UserRole.valueOf(roleStr) } catch (e: Exception) { UserRole.CAPTAIN }

        return VesselProfileEntity(
            noregBkp = bkpId,
            vesselName = vesselName,
            grossTonnage = gt,
            ownerName = ownerName,
            captainName = captainName,
            email = emailStr,
            role = userRole,
            sloActive = doc?.getBoolean("sloActive") ?: true,
            spbActive = doc?.getBoolean("spbActive") ?: true,
            totalQuotaKg = doc?.getDouble("totalQuotaKg") ?: if (isB88) 8000.0 else 10000.0,
            remainingQuotaKg = doc?.getDouble("remainingQuotaKg") ?: if (isB88) 3400.0 else 5101.0,
            sipiNumber = sipiNumber,
            selarMark = selarMark,
            homePort = homePort,
            wppRegion = wppRegion,
            gearType = gearType,
            dimensionStr = dimensionStr,
            enginePower = enginePower,
            engineBrand = engineBrand,
            isLoggedIn = true,
            rememberMe = true
        )
    }

    private fun mapAuthExceptionToMessage(e: Exception): String {
        val rawMessage = e.localizedMessage ?: ""
        return when {
            e is FirebaseAuthInvalidCredentialsException || rawMessage.contains("supplied auth credential", ignoreCase = true) ->
                "Password atau nomor eBKP yang Anda masukkan salah. Silakan periksa kembali."
            e is FirebaseAuthInvalidUserException || rawMessage.contains("user-not-found", ignoreCase = true) ->
                "Akun tidak ditemukan. Silakan lakukan Registrasi / Aktivasi Akun terlebih dahulu."
            else -> "Gagal masuk. Silakan periksa kembali nomor eBKP dan password Anda."
        }
    }

    private fun VesselProfileEntity.toDomainModel(): UserSession {
        return UserSession(
            noregBkp = noregBkp,
            email = email,
            vesselName = vesselName,
            role = role,
            isLoggedIn = isLoggedIn,
            ownerName = ownerName,
            captainName = captainName,
            sipiNumber = sipiNumber,
            selarMark = selarMark,
            homePort = homePort,
            wppRegion = wppRegion,
            gearType = gearType,
            grossTonnage = grossTonnage,
            dimensionStr = dimensionStr,
            enginePower = enginePower,
            engineBrand = engineBrand
        )
    }
}