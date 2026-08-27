package com.example.newe_pit.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phonelink
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.newe_pit.ui.components.EPITPasswordField
import com.example.newe_pit.ui.components.EPITPrimaryButton
import com.example.newe_pit.ui.components.EPITTextField
import com.example.newe_pit.ui.theme.ActionCyan
import com.example.newe_pit.ui.theme.PrimaryNavy
import com.example.newe_pit.ui.viewmodel.AuthViewModel

/**
 * Layar Masuk (Sign In) e-PIT Mobile.
 * Menggunakan EPITTextField & EPITPasswordField dengan tombol "Tetap Masuk".
 */
@Composable
fun SignInScreen(
    authViewModel: AuthViewModel,
    onNavigateHome: () -> Unit,
    onNavigateRegister: () -> Unit
) {
    var noregBkp by remember { mutableStateOf("A000029") }
    var password by remember { mutableStateOf("password123") }
    var keepLoggedIn by remember { mutableStateOf(true) }

    val isLoading by authViewModel.isLoading.collectAsState()
    val errorMessage by authViewModel.errorMessage.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .background(Color(0xFFE0F7FA), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Phonelink,
                        contentDescription = "Logo e-PIT",
                        tint = ActionCyan,
                        modifier = Modifier.size(46.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Masuk",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryNavy
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Silakan Masuk dengan Akun Anda",
                    fontSize = 13.sp,
                    color = Color(0xFF64748B)
                )

                Spacer(modifier = Modifier.height(32.dp))

                EPITTextField(
                    value = noregBkp,
                    onValueChange = { noregBkp = it },
                    placeholder = "Noreg BKP",
                    leadingIcon = Icons.Default.Badge
                )

                Spacer(modifier = Modifier.height(12.dp))

                EPITPasswordField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = "Password",
                    leadingIcon = Icons.Default.Lock
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Row Checkbox & Lupa Password
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { keepLoggedIn = !keepLoggedIn }
                    ) {
                        Checkbox(
                            checked = keepLoggedIn,
                            onCheckedChange = { keepLoggedIn = it },
                            colors = CheckboxDefaults.colors(
                                checkedColor = ActionCyan,
                                uncheckedColor = Color(0xFFCBD5E1)
                            )
                        )
                        Text(
                            text = "Tetap Masuk",
                            fontSize = 12.sp,
                            color = Color(0xFF475569)
                        )
                    }

                    Text(
                        text = "Lupa Password?",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryNavy,
                        modifier = Modifier.clickable { /* Handle Lupa Password */ }
                    )
                }

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = errorMessage ?: "",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                EPITPrimaryButton(
                    text = "Masuk",
                    isLoading = isLoading,
                    onClick = {
                        authViewModel.signIn(noregBkp, password) {
                            onNavigateHome()
                        }
                    }
                )
            }

            Row(
                modifier = Modifier.padding(bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Belum Punya Akun? ",
                    fontSize = 12.sp,
                    color = Color(0xFF64748B)
                )
                Text(
                    text = "Buat Akun / Registrasi",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = ActionCyan,
                    modifier = Modifier.clickable { onNavigateRegister() }
                )
            }
        }
    }
}