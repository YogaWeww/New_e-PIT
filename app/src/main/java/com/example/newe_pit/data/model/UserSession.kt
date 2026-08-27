package com.example.newe_pit.data.model

/**
 * Model Sesi Pengguna saat Login / Aktivasi
 */
data class UserSession(
    val noregBkp: String = "",
    val email: String = "",
    val vesselName: String = "",
    val role: UserRole = UserRole.UNASSIGNED,
    val isLoggedIn: Boolean = false
)