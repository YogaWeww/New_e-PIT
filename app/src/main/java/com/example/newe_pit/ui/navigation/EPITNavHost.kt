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
import com.example.newe_pit.ui.screens.*
import com.example.newe_pit.ui.viewmodel.AuthViewModel
import com.example.newe_pit.ui.viewmodel.HomeViewModel
import com.example.newe_pit.ui.viewmodel.LogbookViewModel

/**
 * Komponen Utama Navigasi Aplikasi e-PIT.
 * Mengintegrasikan Scaffold, NavHost, BottomBar, serta seluruh rute layanan.
 */
@Composable
fun EPITMainAppHost(
    navController: NavHostController = rememberNavController(),
    authViewModel: AuthViewModel,
    homeViewModel: HomeViewModel,
    logbookViewModel: LogbookViewModel
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Home.route

    // Layar Auth & Sub-Layar tidak menampilkan BottomBar agar tampilan fokus
    val hideBottomBar = currentRoute in listOf(
        Screen.Onboarding.route,
        Screen.SignIn.route,
        Screen.VerifyBkp.route,
        Screen.Activation.route,
        Screen.Transshipment.route,
        Screen.LandingReport.route,
        Screen.ServiceLinks.route,
        Screen.HelpSupport.route,
        Screen.QuotaDetail.route,
        Screen.HaulHistory.route
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
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            // Onboarding
            composable(Screen.Onboarding.route) {
                OnboardingScreen(
                    onFinishOnboarding = {
                        navController.navigate(Screen.Home.route) {
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
                            popUpTo(Screen.SignIn.route) { inclusive = true }
                        }
                    }
                )
            }

            // Rute Utama Beranda
            composable(Screen.Home.route) {
                HomeScreen(
                    homeViewModel = homeViewModel,
                    onNavigateToLogbook = {
                        navController.navigate(Screen.LogbookStep1.route)
                    },
                    onNavigateToHaulHistory = {
                        navController.navigate(Screen.HaulHistory.route)
                    },
                    onNavigateToTransshipment = {
                        navController.navigate(Screen.Transshipment.route)
                    },
                    onNavigateToLandingReport = {
                        navController.navigate(Screen.LandingReport.route)
                    },
                    onNavigateToServiceLinks = {
                        navController.navigate(Screen.ServiceLinks.route)
                    },
                    onNavigateToHelpSupport = {
                        navController.navigate(Screen.HelpSupport.route)
                    },
                    onNavigateToQuotaDetail = {
                        navController.navigate(Screen.QuotaDetail.route)
                    }
                )
            }

            // Rute Layar Khusus Riwayat Hauling
            composable(Screen.HaulHistory.route) {
                HaulHistoryScreen(
                    logbookViewModel = logbookViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToLogbook = { navController.navigate(Screen.LogbookStep1.route) }
                )
            }

            // Rute Logbook (4 Tahapan)
            composable(Screen.LogbookStep1.route) {
                LogbookStep1Screen(
                    logbookViewModel = logbookViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToNextStep = { navController.navigate(Screen.LogbookStep2.route) },
                    onNavigateToTransshipment = { navController.navigate(Screen.Transshipment.route) }
                )
            }
            composable(Screen.LogbookStep2.route) {
                LogbookStep2Screen(
                    logbookViewModel = logbookViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToNextStep = { navController.navigate(Screen.LogbookStep3.route) }
                )
            }
            composable(Screen.LogbookStep3.route) {
                LogbookStep3Screen(
                    logbookViewModel = logbookViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToCart  = { navController.navigate(Screen.LogbookStep4.route) }
                )
            }
            composable(Screen.LogbookStep4.route) {
                LogbookStep4Screen(
                    logbookViewModel = logbookViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToHome = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    }
                )
            }

            // Rute Alih Muat (Transshipment)
            composable(Screen.Transshipment.route) {
                TransshipmentScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onProceedToLandingReport = {
                        navController.navigate(Screen.LandingReport.route)
                    }
                )
            }

            // Rute Laporan Pendaratan Trip & STBLKK
            composable(Screen.LandingReport.route) {
                LandingReportScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onFinishReport = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    }
                )
            }

            // Rute Rincian Kuota
            composable(Screen.QuotaDetail.route) {
                QuotaDetailScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // Rute Layanan Pendukung & Bantuan
            composable(Screen.ServiceLinks.route) {
                ServiceLinksScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable(Screen.HelpSupport.route) {
                HelpSupportScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // Rute Dompet Dokumen Kapal
            composable(Screen.Documents.route) {
                DocumentScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // Rute Notifikasi & Informasi
            composable(Screen.Notif.route) {
                NotificationScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // Rute Profil Kapal
            composable(Screen.Profile.route) {
                ProfileScreen(
                    authViewModel = authViewModel,
                    onNavigateToServiceLinks = {
                        navController.navigate(Screen.ServiceLinks.route)
                    },
                    onNavigateToHelpSupport = {
                        navController.navigate(Screen.HelpSupport.route)
                    },
                    onSignOut = {
                        navController.navigate(Screen.SignIn.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}