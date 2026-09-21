package com.example.newe_pit.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.newe_pit.data.model.UserRole

/**
 * Tabel `vessel_profile`: Menyimpan data profil kapal dan sesi login terdaftar di HP.
 * Seluruh nilai default diatur netral ("" atau 0.0) agar 100% mengikuti data riil dari Firestore.
 */
@Entity(tableName = "vessel_profile")
data class VesselProfileEntity(
    @PrimaryKey
    val noregBkp: String,
    val vesselName: String,
    val grossTonnage: Int,
    val ownerName: String,
    val captainName: String,
    val email: String,
    val role: UserRole,
    val sloActive: Boolean = true,
    val spbActive: Boolean = true,
    val totalQuotaKg: Double = 0.0,
    val remainingQuotaKg: Double = 0.0,
    val isLoggedIn: Boolean = true,
    val rememberMe: Boolean = true,
    val sipiNumber: String = "",
    val selarMark: String = "",
    val homePort: String = "",
    val wppRegion: String = "",
    val gearType: String = "",
    val dimensionStr: String = "",
    val enginePower: String = "",
    val engineBrand: String = ""
)