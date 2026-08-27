package com.example.newe_pit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.newe_pit.ui.navigation.EPITMainAppHost
import com.example.newe_pit.ui.theme.EPITTheme
import com.example.newe_pit.ui.viewmodel.AuthViewModel
import com.example.newe_pit.ui.viewmodel.HomeViewModel
import com.example.newe_pit.ui.viewmodel.LogbookViewModel

/**
 * Main Activity Utama Aplikasi e-PIT Mobile.
 */
class MainActivity : ComponentActivity() {

    // Instansiasi ViewModel untuk alur aplikasi
    private val authViewModel: AuthViewModel by viewModels()
    private val homeViewModel: HomeViewModel by viewModels()
    private val logbookViewModel: LogbookViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        // Panggil Android Native Splash Screen API
        installSplashScreen()

        super.onCreate(savedInstanceState)

        setContent {
            EPITTheme {
                EPITMainAppHost(
                    authViewModel = authViewModel,
                    homeViewModel = homeViewModel,
                    logbookViewModel = logbookViewModel
                )
            }
        }
    }
}