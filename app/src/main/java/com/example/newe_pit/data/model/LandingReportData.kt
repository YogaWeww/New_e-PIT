package com.example.newe_pit.data.model

/**
 * Model Data Laporan Pendaratan Trip dan STBLKK
 */
data class LandingReportData(
    val fuelConsumedLiter: Int = 450,
    val selectedPort: String = "PP. Nizam Zachman Jakarta",
    val isLegalConfirmed: Boolean = false
)