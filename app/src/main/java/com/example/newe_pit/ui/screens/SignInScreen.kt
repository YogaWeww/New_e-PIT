package com.example.newe_pit.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.newe_pit.R
import com.example.newe_pit.ui.components.EPITPasswordField
import com.example.newe_pit.ui.components.EPITPrimaryButton
import com.example.newe_pit.ui.components.EPITTextField
import com.example.newe_pit.ui.theme.ActionCyan
import com.example.newe_pit.ui.theme.PrimaryNavy
import com.example.newe_pit.ui.viewmodel.AuthViewModel

/**
 * Layar Masuk (Sign In Screen)
 * Menggunakan logo resmi e-PIT (R.drawable.logo_epit), form terisolasi,
 * opsi tetap masuk, serta validasi akun nakhoda/pemilik.
 */
@Composable
fun SignInScreen(
    authViewModel: AuthViewModel,
    onNavigateHome: () -> Unit,
    onNavigateRegister: () -> Unit
) {
    var noregBkp by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var keepLoggedIn by remember { mutableStateOf(true) }
    var passwordError by remember { mutableStateOf<String?>(null) }

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
                Image(
                    painter = painterResource(id = R.drawable.logo_epit),
                    contentDescription = "Logo e-PIT",
                    modifier = Modifier
                        .size(100.dp)
                        .padding(4.dp)
                )

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
                    onValueChange = {
                        noregBkp = it
                        if (errorMessage != null) authViewModel.clearError()
                    },
                    placeholder = "Noreg BKP",
                    leadingIcon = Icons.Default.Badge
                )

                Spacer(modifier = Modifier.height(12.dp))

                EPITPasswordField(
                    value = password,
                    onValueChange = {
                        password = it
                        passwordError = null
                        if (errorMessage != null) authViewModel.clearError()
                    },
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

                if (passwordError != null || errorMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = passwordError ?: errorMessage ?: "",
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
                        if (password.length < 8) {
                            passwordError = "Password minimal 8 karakter."
                        } else {
                            passwordError = null
                            authViewModel.signIn(noregBkp, password) {
                                onNavigateHome()
                            }
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