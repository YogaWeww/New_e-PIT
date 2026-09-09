package com.example.newe_pit.data.model

/**
 * Model Data Rincian Kuota Penangkapan Ikan Terukur (PIT)
 */
data class QuotaDetailData(
    val totalQuotaKg: Double = 10275.0,
    val realizedQuotaKg: Double = 5450.0,
    val remainingQuotaKg: Double = 4725.0,
    val wppArea: String = "WPP-NRI 718 (Laut Arafura)",
    val quotaCategory: String = "Ikan Pelagis Besar & Kecil",
    val validityPeriod: String = "01 Januari 2026 s/d 31 Desember 2026",
    val speciesBreakdown: List<QuotaSpeciesItem> = listOf(
        QuotaSpeciesItem("Cakalang (SKJ)", 2850.0, 52.3),
        QuotaSpeciesItem("Tuna Madidihang (YFT)", 1650.0, 30.2),
        QuotaSpeciesItem("Tongkol Krakau (BLT)", 950.0, 17.5)
    )
)

data class QuotaSpeciesItem(
    val speciesName: String,
    val weightKg: Double,
    val percentage: Double
)