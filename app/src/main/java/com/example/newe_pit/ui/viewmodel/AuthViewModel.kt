package com.example.newe_pit.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newe_pit.data.model.UserRole
import com.example.newe_pit.data.model.UserSession
import com.example.newe_pit.data.model.VesselInfo
import com.example.newe_pit.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


/**
 * UI State untuk Verifikasi eBKP Modal Dialog
 */
sealed interface BkpVerificationUiState {
    object Idle : BkpVerificationUiState
    object Loading : BkpVerificationUiState
    data class Success(val vesselInfo: VesselInfo) : BkpVerificationUiState
    data class Error(val message: String) : BkpVerificationUiState
}

/**
 * ViewModel untuk Mengelola Alur Otentikasi, Verifikasi eBKP, dan Aktivasi Akun
 */
class AuthViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    // Sesi Pengguna saat ini
    val userSession: StateFlow<UserSession> = authRepository.currentUserSession
        .asStateFlow(viewModelScope, UserSession())

    // State Verifikasi BKP Modal
    private val _verificationState = MutableStateFlow<BkpVerificationUiState>(BkpVerificationUiState.Idle)
    val verificationState: StateFlow<BkpVerificationUiState> = _verificationState.asStateFlow()

    // Loading State untuk Sign In & Aktivasi
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Error Message Toast
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    /**
     * Memproses Masuk / Sign In pengguna
     */
    fun signIn(noregBkp: String, pass: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val result = authRepository.signIn(noregBkp, pass)
            _isLoading.value = false

            result.onSuccess {
                onSuccess()
            }.onFailure { exception ->
                _errorMessage.value = exception.message ?: "Gagal masuk."
            }
        }
    }

    /**
     * Memverifikasi 6-Digit Nomor eBKP
     */
    fun verifyBkp(noregBkp: String) {
        viewModelScope.launch {
            _verificationState.value = BkpVerificationUiState.Loading
            val result = authRepository.verifyBkp(noregBkp)

            result.onSuccess { vessel ->
                _verificationState.value = BkpVerificationUiState.Success(vessel)
            }.onFailure { exception ->
                _verificationState.value = BkpVerificationUiState.Error(
                    exception.message ?: "Nomor eBKP tidak ditemukan."
                )
            }
        }
    }

    /**
     * Reset Modal State saat dikutup atau dibatalkan
     */
    fun resetVerificationState() {
        _verificationState.value = BkpVerificationUiState.Idle
    }

    /**
     * Memproses Aktivasi Akun
     */
    fun activateAccount(role: UserRole, email: String, pass: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val result = authRepository.activateAccount(role, email, pass)
            _isLoading.value = false

            result.onSuccess {
                onSuccess()
            }.onFailure { exception ->
                _errorMessage.value = exception.message ?: "Gagal mengaktifkan akun."
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun signOut() {
        authRepository.signOut()
    }
}

/**
 * Extension helper untuk melestarikan Flow di ViewModelScope
 */
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