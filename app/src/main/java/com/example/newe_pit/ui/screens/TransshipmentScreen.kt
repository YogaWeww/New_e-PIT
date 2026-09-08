package com.example.newe_pit.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.newe_pit.data.model.PartnerVessel
import com.example.newe_pit.ui.components.EPITPrimaryButton
import com.example.newe_pit.ui.theme.*

/**
 * Layar Alih Muat (Transshipment Screen)
 * Memungkinkan kapal pengangkut memilih kapal penangkap mitra sesuai SIKPI,
 * mengambil foto bukti, dan mencatat jenis/berat ikan yang dialihmuatkan.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransshipmentScreen(
    onNavigateBack: () -> Unit = {},
    onProceedToLandingReport: () -> Unit = {}
) {
    var selectedPartner by remember { mutableStateOf<PartnerVessel?>(null) }
    var showPhotoDialog by remember { mutableStateOf(false) }
    var showCatchDialog by remember { mutableStateOf(false) }
    var hasPhotoCaptured by remember { mutableStateOf(false) }
    var transshipmentCompleted by remember { mutableStateOf(false) }
    var toastMessage by remember { mutableStateOf<String?>(null) }

    // Daftar simulasi kapal penangkap mitra SIKPI aktif
    val partnerList = remember {
        mutableStateListOf(
            PartnerVessel("V-01", "KMN. MAJU JAYA 02", "SIKPI/718/2026/012", "SULAIMAN", 35),
            PartnerVessel("V-02", "KMN. SAMUDERA BARU", "SIKPI/718/2026/045", "AMIRUDDIN", 42),
            PartnerVessel("V-03", "KMN. BINTANG LAUT", "SIKPI/718/2026/089", "DARWIS", 30)
        )
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = NeutralCanvas
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            TopAppBar(
                title = {
                    Text(
                        text = "Alih Muatan (Transshipment)",
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

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 1. Instruksi Panduan KKP
                item {
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE0F7FA)),
                        border = BorderStroke(1.dp, ActionCyan.copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = PrimaryNavy,
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                text = "Pilih kapal penangkap mitra yang terdaftar dalam dokumen SIKPI aktif untuk melakukan serah terima muatan.",
                                fontSize = 12.sp,
                                color = PrimaryNavy,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }

                // 2. Daftar Kapal Mitra
                item {
                    Text(
                        text = "1. PILIH KAPAL PENANGKAP MITRA SIKPI",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF94A3B8),
                        letterSpacing = 1.sp
                    )
                }

                items(partnerList) { partner ->
                    val isChosen = selectedPartner?.id == partner.id
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedPartner = partner },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isChosen) Color(0xFFE0F7FA) else Color.White
                        ),
                        border = BorderStroke(
                            width = if (isChosen) 2.dp else 1.dp,
                            color = if (isChosen) ActionCyan else CardBorder
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .background(ActionCyan.copy(alpha = 0.15f), RoundedCornerShape(10.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.DirectionsBoat,
                                        contentDescription = null,
                                        tint = ActionCyan,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Column {
                                    Text(
                                        text = partner.vesselName,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PrimaryNavy
                                    )
                                    Text(
                                        text = "SIKPI: ${partner.permitNumber} (${partner.grossTonnage} GT)",
                                        fontSize = 11.sp,
                                        color = Color(0xFF64748B)
                                    )
                                    Text(
                                        text = "Nakhoda: ${partner.captainName}",
                                        fontSize = 11.sp,
                                        color = Color(0xFF64748B)
                                    )
                                }
                            }

                            RadioButton(
                                selected = isChosen,
                                onClick = { selectedPartner = partner },
                                colors = RadioButtonDefaults.colors(selectedColor = ActionCyan)
                            )
                        }
                    }
                }

                // 3. Dokumentasi Foto & Input Ikan
                if (selectedPartner != null) {
                    item {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "2. DOKUMENTASI & PENCATATAN",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF94A3B8),
                            letterSpacing = 1.sp
                        )
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = { showPhotoDialog = true },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = if (hasPhotoCaptured) StatusGreen else PrimaryNavy
                                ),
                                border = BorderStroke(1.5.dp, if (hasPhotoCaptured) StatusGreen else CardBorder)
                            ) {
                                Icon(
                                    imageVector = if (hasPhotoCaptured) Icons.Default.CheckCircle else Icons.Default.CameraAlt,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (hasPhotoCaptured) "Foto Tersimpan" else "Ambil Foto",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Button(
                                onClick = { showCatchDialog = true },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = ActionCyan, contentColor = PrimaryNavy)
                            ) {
                                Icon(imageVector = Icons.Default.SetMeal, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Isi Data Ikan",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    if (transshipmentCompleted) {
                        item {
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFD1FAE5))
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = StatusGreen)
                                    Text(
                                        text = "Data alih muat dengan ${selectedPartner?.vesselName} telah disimpan (900 kg Cakalang & Tongkol).",
                                        fontSize = 12.sp,
                                        color = PrimaryNavy,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(10.dp))
                        EPITPrimaryButton(
                            text = "SIMPAN DATA ALIH MUAT",
                            enabled = hasPhotoCaptured && transshipmentCompleted,
                            onClick = {
                                toastMessage = "Data Alih Muatan Berhasil Disimpan Permanen."
                                onProceedToLandingReport()
                            }
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(60.dp))
                }
            }
        }

        // Modal Dialog Simulasi Foto (Berada di dalam Surface utama)
        if (showPhotoDialog) {
            AlertDialog(
                onDismissRequest = { showPhotoDialog = false },
                title = { Text("Dokumentasi Alih Muat", fontWeight = FontWeight.Bold, color = PrimaryNavy) },
                text = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                                .background(Color(0xFF1E293B), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.CameraAlt, contentDescription = null, tint = ActionCyan, modifier = Modifier.size(42.dp))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Kamera Pengawas Alih Muat Aktif", color = Color.White, fontSize = 12.sp)
                                Text("Kapal: ${selectedPartner?.vesselName}", color = Color(0xFF94A3B8), fontSize = 10.sp)
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            hasPhotoCaptured = true
                            showPhotoDialog = false
                            toastMessage = "Foto kapal, nakhoda & muatan berhasil diambil."
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ActionCyan, contentColor = PrimaryNavy)
                    ) {
                        Text("Ambil & Simpan Foto", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    OutlinedButton(onClick = { showPhotoDialog = false }) {
                        Text("Batal")
                    }
                }
            )
        }

        // Modal Dialog Simulasi Input Data Ikan (Berada di dalam Surface utama)
        if (showCatchDialog) {
            AlertDialog(
                onDismissRequest = { showCatchDialog = false },
                title = { Text("Input Ikan Alih Muat", fontWeight = FontWeight.Bold, color = PrimaryNavy) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Spesies: Cakalang (SKJ) & Tongkol", fontSize = 12.sp, color = Color(0xFF64748B))
                        OutlinedTextField(
                            value = "900",
                            onValueChange = {},
                            label = { Text("Total Berat (Kg)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            transshipmentCompleted = true
                            showCatchDialog = false
                            toastMessage = "Data ikan alih muat berhasil dimasukkan."
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ActionCyan, contentColor = PrimaryNavy)
                    ) {
                        Text("Simpan", fontWeight = FontWeight.Bold)
                    }
                }
            )
        }

        // Toast Banner (Berada di dalam Surface utama)
        toastMessage?.let { msg ->
            LaunchedEffect(msg) {
                kotlinx.coroutines.delay(2400)
                toastMessage = null
            }
            Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.BottomCenter) {
                Surface(color = PrimaryNavy, shape = RoundedCornerShape(12.dp), shadowElevation = 6.dp) {
                    Text(msg, color = Color.White, fontSize = 12.sp, modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp))
                }
            }
        }
    }
}