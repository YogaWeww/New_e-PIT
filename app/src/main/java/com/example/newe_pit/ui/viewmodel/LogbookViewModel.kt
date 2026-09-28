package com.example.newe_pit.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newe_pit.data.location.LocationHelper
import com.example.newe_pit.data.model.CatchItem
import com.example.newe_pit.data.model.FishSpecies
import com.example.newe_pit.data.model.HaulRecord
import com.example.newe_pit.data.repository.LogbookRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class SettingState {
    READY_TO_SETTING,  // Layar Logbook-1 (GPS Aktif, Belum Turun Jaring)
    SOAKING_ACTIVE,    // Layar Logbook-2 (Sedang Direndam, Timer Berjalan)
    CATCH_PICKER,      // Layar Logbook-3 (Detail & Cart Tangkapan)
    REVIEW_HAUL        // Layar Logbook-4 (Daftar Tangkapan Tawur Ini)
}

/**
 * ViewModel untuk Mengelola Alur Kerja e-Logbook, Perendaman Dinamis (Mulai 0s), dan GPS Real-Time
 */
class LogbookViewModel(
    private val logbookRepository: LogbookRepository = LogbookRepository.instance
) : ViewModel() {

    // Status Tahapan Logbook
    private val _currentSettingState = MutableStateFlow(SettingState.READY_TO_SETTING)
    val currentSettingState: StateFlow<SettingState> = _currentSettingState.asStateFlow()

    // Timer Perendaman (Soak Time) Dimulai dari 0 detik secara dinamis
    private val _soakTimeSeconds = MutableStateFlow(0L)
    val soakTimeSeconds: StateFlow<Long> = _soakTimeSeconds.asStateFlow()

    private var timerJob: Job? = null

    // Status Loading Kalibrasi GPS
    private val _isGpsLoading = MutableStateFlow(false)
    val isGpsLoading: StateFlow<Boolean> = _isGpsLoading.asStateFlow()

    // Status Sakelar GPS Fisik HP (Aktif/Mati)
    private val _isGpsHardwareActive = MutableStateFlow(false)
    val isGpsHardwareActive: StateFlow<Boolean> = _isGpsHardwareActive.asStateFlow()

    // Koordinat GPS Real-time
    private val _startLatitude = MutableStateFlow(0.0)
    val startLatitude: StateFlow<Double> = _startLatitude.asStateFlow()

    private val _startLongitude = MutableStateFlow(0.0)
    val startLongitude: StateFlow<Double> = _startLongitude.asStateFlow()

    private val _endLatitude = MutableStateFlow(0.0)
    val endLatitude: StateFlow<Double> = _endLatitude.asStateFlow()

    private val _endLongitude = MutableStateFlow(0.0)
    val endLongitude: StateFlow<Double> = _endLongitude.asStateFlow()

    // Catalog Spesies dari Repositori
    val speciesCatalog: StateFlow<List<FishSpecies>> = logbookRepository.availableSpecies

    // State Item Keranjang & Histori Hauling
    val cartItems: StateFlow<List<CatchItem>> = logbookRepository.currentCart
    val haulHistory: StateFlow<List<HaulRecord>> = logbookRepository.haulHistory
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Feedback Message Toast / Snackbar
    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    /**
     * Memperbarui posisi koordinat GPS terkini dari perangkat secara asinkron dengan loading state.
     * Mengunci nilai 'Setting Awal' jika sedang di Step 1, dan hanya memperbarui 'Setting Akhir' jika sedang di Step 2 (Perendaman).
     */
    fun updateCurrentLocation(context: Context) {
        viewModelScope.launch {
            _isGpsLoading.value = true

            val isGpsActive = LocationHelper.isGpsActive(context)
            _isGpsHardwareActive.value = isGpsActive

            if (!isGpsActive) {
                _isGpsLoading.value = false
                // Hanya reset koordinat yang sesuai dengan tahapannya, JANGAN sentuh Setting Awal jika sudah di Step 2!
                if (_currentSettingState.value == SettingState.READY_TO_SETTING) {
                    _startLatitude.value = 0.0
                    _startLongitude.value = 0.0
                } else if (_currentSettingState.value == SettingState.SOAKING_ACTIVE) {
                    _endLatitude.value = 0.0
                    _endLongitude.value = 0.0
                }
                _userMessage.value = "Layanan Lokasi (GPS) HP belum aktif. Silakan aktifkan di Pengaturan."
                return@launch
            }

            val location = LocationHelper.getCurrentLocation(context)
            _isGpsLoading.value = false

            if (location != null) {
                if (_currentSettingState.value == SettingState.READY_TO_SETTING) {
                    // Update Setting Awal (Turun Jaring) di Step 1
                    _startLatitude.value = location.latitude
                    _startLongitude.value = location.longitude
                } else {
                    // Update Setting Akhir (Angkat Jaring) di Step 2
                    _endLatitude.value = location.latitude
                    _endLongitude.value = location.longitude
                }
                _userMessage.value = "Sinyal GPS Berhasil Diperbarui"
            } else {
                if (_currentSettingState.value == SettingState.READY_TO_SETTING) {
                    _startLatitude.value = 0.0
                    _startLongitude.value = 0.0
                }
                _userMessage.value = "Gagal mengunci sinyal GPS. Pastikan Anda berada di area terbuka."
            }
        }
    }

    /**
     * Menjalankan Perendaman (Mulai Setting / Turun Jaring)
     * Resets timer ke 0 dan mengunci GPS Awal
     */
    fun startSetting(context: Context? = null) {
        _soakTimeSeconds.value = 0L

        if (context != null) {
            viewModelScope.launch {
                val location = LocationHelper.getCurrentLocation(context)
                if (location != null) {
                    _startLatitude.value = location.latitude
                    _startLongitude.value = location.longitude
                }
            }
        }

        _currentSettingState.value = SettingState.SOAKING_ACTIVE
        startSoakTimer()
    }

    /**
     * Menghentikan Perendaman (Selesai Setting / Angkat Jaring)
     * Catat GPS Akhir & Buka Form Tangkapan
     */
    fun finishSetting(context: Context? = null) {
        stopSoakTimer()

        if (context != null) {
            viewModelScope.launch {
                val location = LocationHelper.getCurrentLocation(context)
                if (location != null) {
                    _endLatitude.value = location.latitude
                    _endLongitude.value = location.longitude
                } else if (_endLatitude.value == 0.0) {
                    _endLatitude.value = _startLatitude.value
                    _endLongitude.value = _startLongitude.value
                }
            }
        } else if (_endLatitude.value == 0.0) {
            _endLatitude.value = _startLatitude.value
            _endLongitude.value = _startLongitude.value
        }

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
        _userMessage.value = "Spesies dihapus dari keranjang."
    }

    fun clearCart() {
        logbookRepository.clearCart()
        _userMessage.value = "Keranjang tangkapan dikosongkan."
    }

    fun clearHaulHistory() {
        logbookRepository.clearHaulHistory()
        _userMessage.value = "Riwayat hauling dikosongkan."
    }

    /**
     * Menyimpan Data Tawur Utuh secara Permanen menggunakan Koordinat GPS Asli
     */
    fun saveCurrentHaul(onSuccess: () -> Unit) {
        viewModelScope.launch {
            val soakMinutes = if (_soakTimeSeconds.value < 60) 1L else _soakTimeSeconds.value / 60
            val result = logbookRepository.saveCurrentHaul(
                startLat = _startLatitude.value,
                startLong = _startLongitude.value,
                endLat = _endLatitude.value,
                endLong = _endLongitude.value,
                soakMinutes = soakMinutes
            )
            result.onSuccess { savedRecord ->
                _userMessage.value = "Data Tawur #${savedRecord.haulNumber} berhasil disimpan!"
                _currentSettingState.value = SettingState.READY_TO_SETTING
                _soakTimeSeconds.value = 0L
                _endLatitude.value = 0.0
                _endLongitude.value = 0.0
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