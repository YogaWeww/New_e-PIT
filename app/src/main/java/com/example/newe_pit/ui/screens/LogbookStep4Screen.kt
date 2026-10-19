package com.example.newe_pit.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.SetMeal
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.newe_pit.ui.components.EPITCardContainer
import com.example.newe_pit.ui.components.EPITPrimaryButton
import com.example.newe_pit.ui.theme.ActionCyan
import com.example.newe_pit.ui.theme.PrimaryNavy
import com.example.newe_pit.ui.theme.StopRed
import com.example.newe_pit.ui.viewmodel.AuthViewModel
import com.example.newe_pit.ui.viewmodel.LogbookViewModel

/**
 * Layar Logbook Step 4 / Review Keranjang:
 * Meninjau ringkasan hasil tangkapan dan menyimpan data tawur utuh
 * yang terikat secara resmi dengan nomor eBKP akun kapal aktif.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogbookStep4Screen(
    authViewModel: AuthViewModel,
    logbookViewModel: LogbookViewModel,
    onNavigateBack: () -> Unit,
    onSaveSuccess: () -> Unit
) {
    // 1. Ambil data sesi kapal aktif dari AuthViewModel (termasuk noregBkp)
    val userSession by authViewModel.userSession.collectAsState()
    val cartItems by logbookViewModel.cartItems.collectAsState()
    val userMessage by logbookViewModel.userMessage.collectAsState()

    val totalWeight = cartItems.sumOf { it.weightKg }
    val totalQty = cartItems.sumOf { it.quantityCount }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(userMessage) {
        userMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            logbookViewModel.clearUserMessage()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Ringkasan Hasil Tangkapan",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryNavy
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = PrimaryNavy
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF8FAFC))
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // KARTU AKUN KAPAL PENGIKAT
                EPITCardContainer {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "KAPAL TERKAIT",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF64748B)
                            )
                            Text(
                                text = if (userSession.vesselName.isNotBlank()) userSession.vesselName else "KMN. DIGITALISASI 01",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryNavy
                            )
                        }

                        Surface(
                            color = ActionCyan.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "eBKP: ${userSession.noregBkp.ifBlank { "-" }}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryNavy,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                // RINGKASAN TOTAL
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        color = PrimaryNavy,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Total Berat", fontSize = 11.sp, color = Color(0xFF94A3B8))
                            Text("$totalWeight Kg", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = ActionCyan)
                        }
                    }

                    Surface(
                        color = PrimaryNavy,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Total Ekor", fontSize = 11.sp, color = Color(0xFF94A3B8))
                            Text("$totalQty Ekor", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                        }
                    }
                }

                Text(
                    text = "RINCIAN ITEM SPESIES (${cartItems.size} Jenis)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF94A3B8),
                    letterSpacing = 1.sp
                )

                // DAFTAR ITEM TANGKAPAN
                if (cartItems.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Keranjang masih kosong.", color = Color(0xFF94A3B8), fontSize = 13.sp)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(cartItems) { item ->
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.SetMeal,
                                            contentDescription = null,
                                            tint = ActionCyan,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Column {
                                            Text(
                                                text = item.speciesName,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = PrimaryNavy
                                            )
                                            Text(
                                                text = "${item.weightKg} Kg | ${item.quantityCount} Ekor",
                                                fontSize = 11.sp,
                                                color = Color(0xFF64748B)
                                            )
                                        }
                                    }

                                    IconButton(
                                        onClick = { logbookViewModel.removeItemFromCart(item.id) }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Hapus",
                                            tint = StopRed,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // TOMBOL UTAMA SIMPAN DATA TAWUR
            EPITPrimaryButton(
                text = "Simpan Data Tawur Ini",
                icon = Icons.Default.Save,
                enabled = cartItems.isNotEmpty(),
                onClick = {
                    // 2. Panggil fungsi simpan dengan menyertakan userSession.noregBkp
                    logbookViewModel.saveCurrentHaul(
                        noregBkp = userSession.noregBkp,
                        onSuccess = onSaveSuccess
                    )
                }
            )
        }
    }
}