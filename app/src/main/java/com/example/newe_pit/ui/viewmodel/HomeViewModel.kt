package com.example.newe_pit.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newe_pit.data.model.HaulRecord
import com.example.newe_pit.data.model.VesselInfo
import com.example.newe_pit.data.repository.AuthRepository
import com.example.newe_pit.data.repository.LogbookRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel untuk Dashboard Beranda (Home Screen)
 */
class HomeViewModel(
    private val authRepository: AuthRepository = AuthRepository(),
    private val logbookRepository: LogbookRepository = LogbookRepository()
) : ViewModel() {

    // Informasi Kuota & Profil Kapal
    private val _vesselInfo = MutableStateFlow(
        VesselInfo(
            bkpNumber = "A000029",
            vesselName = "KMN. DIGITALISASI 01",
            grossTonnage = 48,
            ownerName = "PT. BA***",
            sloActive = true,
            spbActive = true,
            remainingQuotaKg = 5101.0,
            totalQuotaKg = 10000.0
        )
    )
    val vesselInfo: StateFlow<VesselInfo> = _vesselInfo.asStateFlow()

    // Riwayat Hauling Terakhir untuk Dashboard
    val recentHauls: StateFlow<List<HaulRecord>> = logbookRepository.haulHistory
        .asStateFlow(viewModelScope, emptyList())

    // Toast Message
    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    fun showToast(msg: String) {
        _toastMessage.value = msg
    }

    fun clearToast() {
        _toastMessage.value = null
    }
}

private fun <T> kotlinx.coroutines.flow.Flow<T>.asStateFlow(
    scope: kotlinx.coroutines.CoroutineScope,
    initialValue: T
): StateFlow<T> {
    val state = MutableStateFlow(initialValue)
    scope.launch {
        collect { state.value = it }
    }
    return state.asStateFlow()
}