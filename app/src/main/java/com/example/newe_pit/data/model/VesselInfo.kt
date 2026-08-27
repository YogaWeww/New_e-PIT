package com.example.newe_pit.data.model

/**
 * Model Informasi Profil Kapal & Kuota Tangkap
 */
data class VesselInfo(
    val bkpNumber: String = "",
    val vesselName: String = "",
    val grossTonnage: Int = 0,
    val ownerName: String = "",
    val sloActive: Boolean = true,
    val spbActive: Boolean = true,
    val remainingQuotaKg: Double = 0.0,
    val totalQuotaKg: Double = 0.0
)