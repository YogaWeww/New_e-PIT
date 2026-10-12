package com.example.newe_pit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.example.newe_pit.data.local.EPITDatabase
import com.example.newe_pit.data.model.SyncStatus
import com.example.newe_pit.data.remote.FirestoreSeeder
import com.example.newe_pit.data.repository.AuthRepository
import com.example.newe_pit.data.repository.LogbookRepository
import com.example.newe_pit.data.sync.NetworkObserver
import com.example.newe_pit.data.sync.SyncManager
import com.example.newe_pit.ui.navigation.EPITMainAppHost
import com.example.newe_pit.ui.theme.EPITTheme
import com.example.newe_pit.ui.viewmodel.AuthViewModel
import com.example.newe_pit.ui.viewmodel.HomeViewModel
import com.example.newe_pit.ui.viewmodel.LogbookViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

/**
 * Main Activity Utama Aplikasi e-PIT Mobile.
 * Mengelola inisialisasi awal Repositori, NetworkObserver, dan Penjadwalan Sync.
 */
class MainActivity : ComponentActivity() {

    // Instansiasi ViewModel untuk alur aplikasi
    private val authViewModel: AuthViewModel by viewModels()
    private val homeViewModel: HomeViewModel by viewModels()
    private val logbookViewModel: LogbookViewModel by viewModels()

    // Komponen Sinkronisasi & Jaringan
    private lateinit var syncManager: SyncManager
    private lateinit var networkObserver: NetworkObserver

    override fun onCreate(savedInstanceState: Bundle?) {
        // 1. Panggil Android Native Splash Screen API
        installSplashScreen()
        super.onCreate(savedInstanceState)

        // 2. Inisialisasi Repositori Lokal (Room DB)
        AuthRepository.initialize(applicationContext)
        LogbookRepository.initialize(applicationContext)

        // 3. Inisialisasi Service Sinkronisasi Latar Belakang & Status Jaringan
        syncManager = SyncManager(applicationContext)
        networkObserver = NetworkObserver(applicationContext)

        // 4. Opsional: Seeder untuk mengunggah data dummy kapal ke Firestore Cloud
        lifecycleScope.launch {
            FirestoreSeeder.seedVesselsData()
        }

        // 5. Stream hitungan laporan pending dari Room DB untuk Banner UI
        val database = EPITDatabase.getDatabase(applicationContext)
        val pendingCountFlow = database.haulDao().getAllHaulsWithItemsFlow().map { list ->
            list.count { it.haulRecord.syncStatus == SyncStatus.OFFLINE_PENDING }
        }

        setContent {
            EPITTheme {
                val isOnline by networkObserver.isOnline.collectAsState(initial = true)
                val pendingSyncCount by pendingCountFlow.collectAsState(initial = 0)

                // Otomatis picu sinkronisasi WorkManager ketika sinyal internet kembali terhubung
                if (isOnline && pendingSyncCount > 0) {
                    syncManager.scheduleSyncNow()
                }

                EPITMainAppHost(
                    authViewModel = authViewModel,
                    homeViewModel = homeViewModel,
                    logbookViewModel = logbookViewModel
                )
            }
        }
    }
}