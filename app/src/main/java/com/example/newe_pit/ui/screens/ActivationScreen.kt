package com.example.newe_pit.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.newe_pit.data.model.UserRole
import com.example.newe_pit.ui.components.EPITPasswordField
import com.example.newe_pit.ui.components.EPITPrimaryButton
import com.example.newe_pit.ui.components.EPITTextField
import com.example.newe_pit.ui.theme.ActionCyan
import com.example.newe_pit.ui.theme.CardBorder
import com.example.newe_pit.ui.theme.CardSurface
import com.example.newe_pit.ui.theme.PrimaryNavy
import com.example.newe_pit.ui.viewmodel.AuthViewModel

/**
 * Layar Form Aktivasi Akun e-PIT Mobile.
 * Memungkinkan pengguna memilih Peran (Pemilik Kapal / Nakhoda),
 * meninjau nama kapal, serta membuat email dan password akun.
 */
@Composable
fun ActivationScreen(
    authViewModel: AuthViewModel,
    onNavigateBack: () -> Unit,
    onNavigateHome: () -> Unit
) {
    var selectedRole by remember { mutableStateOf(UserRole.SHIP_OWNER) }
    var email by remember { mutableStateOf("digitalisasi01@maganghub.co.id") }
    var password by remember { mutableStateOf("password123") }
    var confirmPassword by remember { mutableStateOf("password123") }
    var passwordError by remember { mutableStateOf<String?>(null) }

    val userSession by authViewModel.userSession.collectAsState()
    val isLoading by authViewModel.isLoading.collectAsState()
    val errorMessage by authViewModel.errorMessage.collectAsState()

    val vesselName = if (userSession.vesselName.isNotBlank()) {
        userSession.vesselName
    } else {
        "KMN. DIGITALISASI 01"
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
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

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Aktivasi Akun",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryNavy,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Silakan isi data di bawah ini dengan benar",
                    fontSize = 13.sp,
                    color = Color(0xFF64748B),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(28.dp))

                Text(
                    text = "Pilih Otoritas Akun / Peran:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryNavy
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .background(Color(0xFFF1F5F9), RoundedCornerShape(12.dp))
                        .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
                        .padding(4.dp)
                ) {
                    // Option 1: Pemilik Kapal
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .background(
                                color = if (selectedRole == UserRole.SHIP_OWNER) ActionCyan else Color.Transparent,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable { selectedRole = UserRole.SHIP_OWNER },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Pemilik Kapal",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (selectedRole == UserRole.SHIP_OWNER) PrimaryNavy else Color(0xFF64748B)
                        )
                    }

                    // Option 2: Nakhoda
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .background(
                                color = if (selectedRole == UserRole.CAPTAIN) ActionCyan else Color.Transparent,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable { selectedRole = UserRole.CAPTAIN },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Nakhoda",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (selectedRole == UserRole.CAPTAIN) PrimaryNavy else Color(0xFF64748B)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Nama Kapal",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryNavy
                )

                Spacer(modifier = Modifier.height(6.dp))

                EPITTextField(
                    value = vesselName,
                    onValueChange = {},
                    placeholder = "Nama Kapal",
                    leadingIcon = Icons.Default.Badge,
                    isReadOnly = true,
                    helperText = "* Data sesuai dokumen SIPI/KKP (Terisi otomatis)"
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Alamat Email",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryNavy
                )

                Spacer(modifier = Modifier.height(6.dp))

                EPITTextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = "Alamat Email",
                    leadingIcon = Icons.Default.Email,
                    helperText = "* Kode OTP / verifikasi akan dikirim ke email ini"
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Buat Password",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryNavy
                )

                Spacer(modifier = Modifier.height(6.dp))

                EPITPasswordField(
                    value = password,
                    onValueChange = {
                        password = it
                        passwordError = null
                    },
                    placeholder = "Buat Password",
                    leadingIcon = Icons.Default.Lock
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Konfirmasi Password",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryNavy
                )

                Spacer(modifier = Modifier.height(6.dp))

                EPITPasswordField(
                    value = confirmPassword,
                    onValueChange = {
                        confirmPassword = it
                        passwordError = null
                    },
                    placeholder = "Konfirmasi Password",
                    leadingIcon = Icons.Default.Lock
                )

                if (passwordError != null || errorMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = passwordError ?: errorMessage ?: "",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                EPITPrimaryButton(
                    text = "Aktivasi Akun",
                    icon = Icons.Default.RocketLaunch,
                    isLoading = isLoading,
                    onClick = {
                        if (password != confirmPassword) {
                            passwordError = "Konfirmasi password tidak cocok."
                        } else if (password.length < 6) {
                            passwordError = "Password minimal 6 karakter."
                        } else {
                            authViewModel.activateAccount(
                                role = selectedRole,
                                email = email,
                                pass = password
                            ) {
                                onNavigateHome()
                            }
                        }
                    }
                )
            }
        }
    }
}