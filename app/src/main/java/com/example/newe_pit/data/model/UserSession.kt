package com.example.newe_pit.data.model

/**
 * Model Sesi Pengguna saat Login / Aktivasi (Domain Model)
 * Seluruh nilai default diatur netral ("" / 0) tanpa data hardcode.
 * Memuat seluruh atribut legalitas, pangkalan, dan spesifikasi teknis kapal dari Firestore/Room.
 */
data class UserSession(
    val noregBkp: String = "",
    val email: String = "",
    val vesselName: String = "",
    val role: UserRole = UserRole.UNASSIGNED,
    val isLoggedIn: Boolean = false,
    val ownerName: String = "",
    val captainName: String = "",
    val sipiNumber: String = "",
    val selarMark: String = "",
    val homePort: String = "",
    val wppRegion: String = "",
    val gearType: String = "",
    val grossTonnage: Int = 0,
    val dimensionStr: String = "",
    val enginePower: String = "",
    val engineBrand: String = ""
)