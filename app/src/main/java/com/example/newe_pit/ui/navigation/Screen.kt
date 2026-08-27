package com.example.newe_pit.ui.navigation

/**
 * Sealed class yang mendefinisikan seluruh rute layar (destinasi) aplikasi e-PIT
 */
sealed class Screen(val route: String, val title: String) {
    // Auth Flow
    object SignIn : Screen("signin", "Masuk")
    object VerifyBkp : Screen("verify_bkp", "Verifikasi eBKP")
    object Activation : Screen("activation", "Aktivasi Akun")

    // Main App Flow (Bottom Navigation Destinations)
    object Home : Screen("home", "Beranda")
    object Documents : Screen("documents", "Dokumen")
    object Notif : Screen("notif", "Notifikasi")
    object Profile : Screen("profile", "Profil")

    // Logbook Flow
    object LogbookStep1 : Screen("logbook_1", "Logbook - Ready")
    object LogbookStep2 : Screen("logbook_2", "Logbook - Perendaman")
    object LogbookStep3 : Screen("logbook_3", "Detail Tangkapan")
    object LogbookStep4 : Screen("logbook_4", "Daftar Tangkapan")
}