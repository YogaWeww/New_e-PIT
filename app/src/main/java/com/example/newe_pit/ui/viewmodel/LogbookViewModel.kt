package com.example.newe_pit.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newe_pit.data.model.CatchItem
import com.example.newe_pit.data.model.FishSpecies
import com.example.newe_pit.data.model.HaulRecord
import com.example.newe_pit.data.repository.LogbookRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class SettingState {
    READY_TO_SETTING,  // Layar Logbook-1 (GPS Aktif, Belum Turun Jaring)
    SOAKING_ACTIVE,    // Layar Logbook-2 (Sedang Direndam, Timer Berjalan)
    CATCH_PICKER,      // Layar Logbook-3 (Detail & Cart Tangkapan)
    REVIEW_HAUL        // Layar Logbook-4 (Daftar Tangkapan Tawur Ini)
}

/**
 * ViewModel untuk Mengelola Alur Kerja e-Logbook, Perendaman, dan Keranjang Spesies
 */
class LogbookViewModel(
    private val logbookRepository: LogbookRepository = LogbookRepository()
) : ViewModel() {

    // Status Tahapan Logbook
    private val _currentSettingState = MutableStateFlow(SettingState.READY_TO_SETTING)
    val currentSettingState: StateFlow<SettingState> = _currentSettingState.asStateFlow()

    // Timer Perendaman (Soak Time) dalam detik
    private val _soakTimeSeconds = MutableStateFlow(8115L) // Default simulasi 02:15:15
    val soakTimeSeconds: StateFlow<Long> = _soakTimeSeconds.asStateFlow()

    private var timerJob: Job? = null

    // Catalog Spesies
    val speciesCatalog: List<FishSpecies> = logbookRepository.getSpeciesCatalog()

    // State Item Keranjang & Histori Hauling
    val cartItems: StateFlow<List<CatchItem>> = logbookRepository.cartItems
        .asStateFlow(viewModelScope, emptyList())

    val haulHistory: StateFlow<List<HaulRecord>> = logbookRepository.haulHistory
        .asStateFlow(viewModelScope, emptyList())

    // Feedback Message Toast
    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    /**
     * Menjalankan Perendaman (Mulai Setting)
     */
    fun startSetting() {
        _currentSettingState.value = SettingState.SOAKING_ACTIVE
        startSoakTimer()
    }

    /**
     * Menghentikan Perendaman (Selesai Setting) & Buka Form Tangkapan
     */
    fun finishSetting() {
        stopSoakTimer()
        _currentSettingState.value = SettingState.CATCH_PICKER
    }

    private fun startSoakTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                _soakTimeSeconds.value += 1
            }
        }
    }

    private fun stopSoakTimer() {
        timerJob?.cancel()
        timerJob = null
    }

    /**
     * Menambahkan Spesies ke Keranjang Tangkapan
     */
    fun addItemToCart(speciesName: String, weightKg: Int, quantityCount: Int) {
        if (weightKg <= 0) {
            _userMessage.value = "Berat ikan harus lebih dari 0 kg."
            return
        }

        val newItem = CatchItem(
            speciesName = speciesName,
            weightKg = weightKg,
            quantityCount = quantityCount
        )
        logbookRepository.addItemToCart(newItem)
        _userMessage.value = "$speciesName ($weightKg kg) berhasil ditambahkan."
    }

    fun removeItemFromCart(itemId: String) {
        logbookRepository.removeItemFromCart(itemId)
    }

    /**
     * Menyimpan Data Tawur Utuh secara Permanen
     */
    fun saveCurrentHaul(onSuccess: () -> Unit) {
        viewModelScope.launch {
            val result = logbookRepository.saveCurrentHaul()
            result.onSuccess {
                _userMessage.value = "Data Tawur berhasil disimpan!"
                _currentSettingState.value = SettingState.READY_TO_SETTING
                _soakTimeSeconds.value = 0L
                onSuccess()
            }.onFailure { exception ->
                _userMessage.value = exception.message ?: "Gagal menyimpan data tawur."
            }
        }
    }

    fun navigateToState(state: SettingState) {
        _currentSettingState.value = state
    }

    fun clearUserMessage() {
        _userMessage.value = null
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