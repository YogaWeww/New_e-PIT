package com.example.newe_pit.data.model

/**
 * Model Data Kapal Penangkap Mitra SIKPI
 */
data class PartnerVessel(
    val id: String,
    val vesselName: String,
    val permitNumber: String,
    val captainName: String,
    val grossTonnage: Int,
    val isSelected: Boolean = false
)