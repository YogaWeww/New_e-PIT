package com.example.newe_pit.data.model

/**
 * Status Tipe Dokumen untuk Visual Badge
 */
enum class DocStatusType {
    ACTIVE,      // Hijau (Berlaku)
    PENDING,     // Kuning (Menunggu Verifikasi / Tagihan)
    EXPIRING     // Merah (Segera Kedaluwarsa)
}

/**
 * Model Data Item Dokumen Perizinan Kapal
 */
data class VesselDocItem(
    val id: String,
    val title: String,
    val docNumber: String,
    val issuer: String,
    val validityDate: String,
    val statusLabel: String,
    val statusType: DocStatusType,
    val category: String, // "Aktif", "Proses", "Riwayat"
    val note: String? = null,
    val paymentAmount: String? = null
)