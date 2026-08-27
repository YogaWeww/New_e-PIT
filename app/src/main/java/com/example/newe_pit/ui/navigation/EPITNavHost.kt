package com.example.newe_pit.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.newe_pit.ui.components.EPITBottomNavigationBar
import com.example.newe_pit.ui.viewmodel.AuthViewModel
import com.example.newe_pit.ui.viewmodel.HomeViewModel
import com.example.newe_pit.ui.viewmodel.LogbookViewModel

/**
 * Komponen Utama Navigasi Aplikasi e-PIT.
 * Mengintegrasikan Scaffold, NavHost, dan EPITBottomNavigationBar.
 */
@Composable
fun EPITMainAppHost(
    navController: NavHostController = rememberNavController(),
    authViewModel: AuthViewModel,
    homeViewModel: HomeViewModel,
    logbookViewModel: LogbookViewModel
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Screen.SignIn.route

    // Layar Auth (SignIn, Verify, Activation) tidak menampilkan BottomBar
    val hideBottomBar = currentRoute in listOf(
        Screen.SignIn.route,
        Screen.VerifyBkp.route,
        Screen.Activation.route
    )

    Scaffold(
        bottomBar = {
            if (!hideBottomBar) {
                EPITBottomNavigationBar(
                    currentRoute = currentRoute,
                    onNavigate = { targetRoute ->
                        navController.navigate(targetRoute) {
                            popUpTo(Screen.Home.route) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.SignIn.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            // Rute Auth akan kita hubungkan ke Composable Screen masing-masing
            composable(Screen.SignIn.route) {
                // Temporary Placeholder sebelum Screen dibuat
            }
            composable(Screen.VerifyBkp.route) {
                // Temporary Placeholder
            }
            composable(Screen.Activation.route) {
                // Temporary Placeholder
            }

            // Rute Utama & Logbook
            composable(Screen.Home.route) {
                // Temporary Placeholder
            }
            composable(Screen.LogbookStep1.route) {
                // Temporary Placeholder
            }
            composable(Screen.LogbookStep2.route) {
                // Temporary Placeholder
            }
            composable(Screen.LogbookStep3.route) {
                // Temporary Placeholder
            }
            composable(Screen.LogbookStep4.route) {
                // Temporary Placeholder
            }
            composable(Screen.Documents.route) {
                // Temporary Placeholder
            }
            composable(Screen.Notif.route) {
                // Temporary Placeholder
            }
            composable(Screen.Profile.route) {
                // Temporary Placeholder
            }
        }
    }
}