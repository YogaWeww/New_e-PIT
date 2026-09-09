package com.example.newe_pit.data.model

/**
 * Model Data Tautan Layanan Eksternal (Web KKP & RFMOs)
 */
data class ServiceLinkItem(
    val id: String,
    val title: String,
    val category: String, // "Web KKP" atau "Web RFMOs"
    val url: String,
    val description: String? = null
)

/**
 * Model Kontak Bantuan & Helpdesk KKP
 */
data class HelpContactItem(
    val title: String,
    val contactValue: String,
    val actionUrl: String, // Format: https://wa.me/... atau mailto:...
    val isWhatsApp: Boolean = false,
    val isEmail: Boolean = false
)