package com.example.newe_pit.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.newe_pit.ui.components.EPITOutlinedButton
import com.example.newe_pit.ui.components.EPITPrimaryButton
import com.example.newe_pit.ui.theme.ActionCyan
import com.example.newe_pit.ui.theme.PrimaryNavy
import com.example.newe_pit.ui.theme.StatusGreen
import com.example.newe_pit.ui.theme.StopRed
import com.example.newe_pit.ui.viewmodel.AuthViewModel
import com.example.newe_pit.ui.viewmodel.BkpVerificationUiState

/**
 * Layar Verifikasi 6-Digit Nomor Buku Kapal Perikanan (eBKP).
 * Menggunakan 6 Kotak Input Terpisah & Modal Dialog Konfirmasi.
 */
@Composable
fun VerifyBkpScreen(
    authViewModel: AuthViewModel,
    onNavigateBack: () -> Unit,
    onNavigateNext: () -> Unit
) {
    var codeDigits by remember { mutableStateOf(listOf("A", "0", "0", "0", "2", "9")) }
    val focusRequesters = remember { List(6) { FocusRequester() } }
    val verificationState by authViewModel.verificationState.collectAsState()

    val fullCode = codeDigits.joinToString("")

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                // Top Navigation Back Button
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable { onNavigateBack() }
                        .padding(vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Kembali",
                        tint = PrimaryNavy,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Kembali",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryNavy
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Registrasi Akun Kapal",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryNavy,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Masukkan Nomor Buku Kapal Perikanan (eBKP) Anda untuk memulai proses aktivasi akun",
                    fontSize = 13.sp,
                    color = Color(0xFF64748B),
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(36.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    codeDigits.forEachIndexed { index, char ->
                        OutlinedTextField(
                            value = char,
                            onValueChange = { newValue ->
                                if (newValue.length <= 1) {
                                    val newList = codeDigits.toMutableList()
                                    newList[index] = newValue.uppercase()
                                    codeDigits = newList

                                    if (newValue.isNotEmpty() && index < 5) {
                                        focusRequesters[index + 1].requestFocus()
                                    }
                                }
                            },
                            singleLine = true,
                            textStyle = LocalTextStyle.current.copy(
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                color = PrimaryNavy
                            ),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ActionCyan,
                                unfocusedBorderColor = Color(0xFFCBD5E1),
                                focusedContainerColor = Color(0xFFF8FAFC),
                                unfocusedContainerColor = Color(0xFFF8FAFC)
                            ),
                            modifier = Modifier
                                .width(48.dp)
                                .height(56.dp)
                                .focusRequester(focusRequesters[index])
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                EPITPrimaryButton(
                    text = "Verifikasi BKP",
                    isLoading = verificationState is BkpVerificationUiState.Loading,
                    onClick = {
                        authViewModel.verifyBkp(fullCode)
                    }
                )
            }

            Text(
                text = "Butuh bantuan? Hubungi Helpdesk KKP",
                fontSize = 12.sp,
                color = Color(0xFF64748B),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )
        }
    }

    when (val state = verificationState) {
        is BkpVerificationUiState.Success -> {
            AlertDialog(
                onDismissRequest = { authViewModel.resetVerificationState() },
                icon = {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = StatusGreen,
                        modifier = Modifier.size(48.dp)
                    )
                },
                title = {
                    Text(
                        text = "Kapal Ditemukan",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = PrimaryNavy
                    )
                },
                text = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF8FAFC), RoundedCornerShape(12.dp))
                            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Text(text = "Nama Kapal: ${state.vesselInfo.vesselName}", fontSize = 12.sp, color = PrimaryNavy, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Ukuran GT: ${state.vesselInfo.grossTonnage} GT", fontSize = 12.sp, color = PrimaryNavy, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Pemilik: ${state.vesselInfo.ownerName}", fontSize = 12.sp, color = PrimaryNavy, fontWeight = FontWeight.Medium)
                    }
                },
                confirmButton = {
                    EPITPrimaryButton(
                        text = "Lanjut",
                        onClick = {
                            authViewModel.resetVerificationState()
                            onNavigateNext()
                        },
                        modifier = Modifier.width(110.dp)
                    )
                },
                dismissButton = {
                    EPITOutlinedButton(
                        text = "Batal",
                        onClick = { authViewModel.resetVerificationState() },
                        borderColor = StopRed,
                        textColor = StopRed,
                        modifier = Modifier.width(100.dp)
                    )
                },
                containerColor = Color.White
            )
        }

        is BkpVerificationUiState.Error -> {
            AlertDialog(
                onDismissRequest = { authViewModel.resetVerificationState() },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = StopRed,
                        modifier = Modifier.size(48.dp)
                    )
                },
                title = {
                    Text(
                        text = "Nomor eBKP Tidak Ditemukan",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = PrimaryNavy
                    )
                },
                text = {
                    Text(
                        text = state.message,
                        fontSize = 13.sp,
                        color = Color(0xFF64748B),
                        textAlign = TextAlign.Center
                    )
                },
                confirmButton = {
                    EPITPrimaryButton(
                        text = "Kembali",
                        onClick = { authViewModel.resetVerificationState() },
                        backgroundColor = StopRed,
                        contentColor = Color.White
                    )
                },
                containerColor = Color.White
            )
        }

        else -> {}
    }
}