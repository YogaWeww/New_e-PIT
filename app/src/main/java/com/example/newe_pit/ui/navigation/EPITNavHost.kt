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
import com.example.newe_pit.ui.screens.ActivationScreen
import com.example.newe_pit.ui.screens.OnboardingScreen
import com.example.newe_pit.ui.screens.SignInScreen
import com.example.newe_pit.ui.screens.VerifyBkpScreen
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
    val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Onboarding.route

    // Layar Onboarding & Auth tidak menampilkan BottomBar
    val hideBottomBar = currentRoute in listOf(
        Screen.Onboarding.route,
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
            startDestination = Screen.Onboarding.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            // Onboarding
            composable(Screen.Onboarding.route) {
                OnboardingScreen(
                    onFinishOnboarding = {
                        navController.navigate(Screen.SignIn.route) {
                            popUpTo(Screen.Onboarding.route) { inclusive = true }
                        }
                    }
                )
            }

            // Rute Auth
            composable(Screen.SignIn.route) {
                SignInScreen(
                    authViewModel = authViewModel,
                    onNavigateHome = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.SignIn.route) { inclusive = true }
                        }
                    },
                    onNavigateRegister = {
                        navController.navigate(Screen.VerifyBkp.route)
                    }
                )
            }
            composable(Screen.VerifyBkp.route) {
                VerifyBkpScreen(
                    authViewModel = authViewModel,
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onNavigateNext = {
                        navController.navigate(Screen.Activation.route)
                    }
                )
            }
            composable(Screen.Activation.route) {
                ActivationScreen(
                    authViewModel = authViewModel,
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onNavigateHome = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Onboarding.route) { inclusive = true }
                        }
                    }
                )
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