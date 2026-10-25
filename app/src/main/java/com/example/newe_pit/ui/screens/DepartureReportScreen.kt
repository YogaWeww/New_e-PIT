package com.example.newe_pit.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Anchor
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsBoat
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.newe_pit.ui.components.EPITCardContainer
import com.example.newe_pit.ui.components.EPITPrimaryButton
import com.example.newe_pit.ui.theme.ActionCyan
import com.example.newe_pit.ui.theme.CardBorder
import com.example.newe_pit.ui.theme.PrimaryNavy
import com.example.newe_pit.ui.theme.StatusGreen
import com.example.newe_pit.ui.viewmodel.AuthViewModel

/**
 * Layar Pengajuan Keberangkatan Kapal (SLO & SPB)
 * Sesuai Standar KKP (Halaman 6 PDF Port to Port):
 * - Informasi Kapal & eBKP Aktif
 * - Seleksi Pelabuhan Asal & Pelabuhan Tujuan (SIPI)
 * - Deklarasi Muatan Awal / Initial Cargo (Ikan/Es/Perbekalan)
 * - Verifikasi Daftar Awak Kapal (ABK)
 * - Pengajuan Surat Laik Operasi (SLO) & Surat Persetujuan Berlayar (SPB)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DepartureReportScreen(
    authViewModel: AuthViewModel,
    onNavigateBack: () -> Unit,
    onDepartureSubmitted: () -> Unit
) {
    val userSession by authViewModel.userSession.collectAsState()

    // Step State: 1. Pelabuhan & Operasional, 2. Muatan Awal, 3. Verifikasi Awak Kapal (ABK)
    var currentStep by remember { mutableIntStateOf(1) }

    // Form Field Parameters
    val ports = remember { listOf("PPN Ambon", "PP. Dobo", "PPN Tual", "PPS Nizam Zachman", "PP. Klademak") }
    var selectedOriginPort by remember { mutableStateOf(userSession.homePort.ifBlank { "PPN Ambon" }) }
    var selectedDestinationPort by remember { mutableStateOf("PP. Dobo") }

    var hasInitialCargo by remember { mutableStateOf(false) }
    var initialCargoWeightStr by remember { mutableStateOf("0") }
    var initialCargoTypeStr by remember { mutableStateOf("Umpan & Es Balok") }

    var crewCountStr by remember { mutableStateOf("12") }
    var captainNameStr by remember { mutableStateOf(userSession.captainName.ifBlank { "SUPRIYANTO" }) }
    var isCrewRosterVerified by remember { mutableStateOf(false) }

    var isSubmitting by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Pengajuan SLO & SPB Keberangkatan",
                        fontSize = 17.sp,
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // HEADER INFORMASI IDENTITAS KAPAL
            EPITCardContainer {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "KAPAL PEMOHON SLO & SPB",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF64748B)
                        )
                        Text(
                            text = userSession.vesselName.ifBlank { "KMN. DIGITALISASI 01" },
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = PrimaryNavy
                        )
                    }

                    Surface(
                        color = ActionCyan.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "eBKP: ${userSession.noregBkp.ifBlank { "A00029" }}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryNavy,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            // STEPPER PROGRESS INDICATOR
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                listOf(
                    1 to "1. Rute Pelabuhan",
                    2 to "2. Muatan Awal",
                    3 to "3. Daftar ABK"
                ).forEach { (stepNum, stepLabel) ->
                    val isActive = currentStep == stepNum
                    val isCompleted = currentStep > stepNum

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.clickable { currentStep = stepNum }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(
                                    color = when {
                                        isCompleted -> StatusGreen
                                        isActive -> ActionCyan
                                        else -> Color(0xFFCBD5E1)
                                    },
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isCompleted) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            } else {
                                Text(
                                    text = stepNum.toString(),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryNavy
                                )
                            }
                        }
                        Text(
                            text = stepLabel,
                            fontSize = 11.sp,
                            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
                            color = if (isActive) PrimaryNavy else Color(0xFF64748B)
                        )
                    }
                }
            }

            // BAGIAN 1: RUTE PELABUHAN ASAL & TUJUAN
            AnimatedVisibility(visible = currentStep == 1) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "INFORMASI PELABUHAN BERTAKDIR (SIPI)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF94A3B8),
                        letterSpacing = 1.sp
                    )

                    EPITCardContainer {
                        Text(
                            text = "Pelabuhan Asal Keberangkatan",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryNavy
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        ports.forEach { port ->
                            val isSelected = selectedOriginPort == port
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) Color(0xFFE0F7FA) else Color(0xFFF8FAFC),
                                border = androidx.compose.foundation.BorderStroke(
                                    width = if (isSelected) 1.5.dp else 1.dp,
                                    color = if (isSelected) ActionCyan else CardBorder
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 6.dp)
                                    .clickable { selectedOriginPort = port }
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Anchor,
                                            contentDescription = null,
                                            tint = if (isSelected) ActionCyan else Color(0xFF64748B),
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Text(
                                            text = port,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = PrimaryNavy
                                        )
                                    }
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = { selectedOriginPort = port },
                                        colors = RadioButtonDefaults.colors(selectedColor = ActionCyan)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Rencana Pelabuhan Tujuan",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryNavy
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        ports.filter { it != selectedOriginPort }.forEach { port ->
                            val isSelected = selectedDestinationPort == port
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) Color(0xFFE0F7FA) else Color(0xFFF8FAFC),
                                border = androidx.compose.foundation.BorderStroke(
                                    width = if (isSelected) 1.5.dp else 1.dp,
                                    color = if (isSelected) ActionCyan else CardBorder
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 6.dp)
                                    .clickable { selectedDestinationPort = port }
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.LocationOn,
                                            contentDescription = null,
                                            tint = if (isSelected) ActionCyan else Color(0xFF64748B),
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Text(
                                            text = port,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = PrimaryNavy
                                        )
                                    }
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = { selectedDestinationPort = port },
                                        colors = RadioButtonDefaults.colors(selectedColor = ActionCyan)
                                    )
                                }
                            }
                        }
                    }

                    EPITPrimaryButton(
                        text = "Lanjut ke Muatan Awal",
                        onClick = { currentStep = 2 }
                    )
                }
            }

            // BAGIAN 2: DEKLARASI MUATAN AWAL (INITIAL CARGO)
            AnimatedVisibility(visible = currentStep == 2) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "DEKLARASI MUATAN AWAL SEBELUM BERTOLAK",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF94A3B8),
                        letterSpacing = 1.sp
                    )

                    EPITCardContainer {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Membawa Muatan Ikan/Es/Umpan?",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryNavy
                            )
                            Switch(
                                checked = hasInitialCargo,
                                onCheckedChange = { hasInitialCargo = it },
                                colors = SwitchDefaults.colors(checkedThumbColor = ActionCyan)
                            )
                        }

                        if (hasInitialCargo) {
                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = initialCargoTypeStr,
                                onValueChange = { initialCargoTypeStr = it },
                                label = { Text("Jenis Muatan Awal") },
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = initialCargoWeightStr,
                                onValueChange = { initialCargoWeightStr = it.filter { c -> c.isDigit() } },
                                label = { Text("Total Berat Muatan Awal (Kg)") },
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            )
                        } else {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Kapal bertolak dalam kondisi palka kosong (tanpa muatan awal).",
                                fontSize = 11.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { currentStep = 1 },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Kembali", color = PrimaryNavy, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { currentStep = 3 },
                            colors = ButtonDefaults.buttonColors(containerColor = ActionCyan, contentColor = PrimaryNavy),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1.5f)
                        ) {
                            Text("Lanjut ke Daftar ABK", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // BAGIAN 3: VERIFIKASI AWAK KAPAL (ABK) & PENGAJUAN
            AnimatedVisibility(visible = currentStep == 3) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "DAFTAR AWAK KAPAL (ABK) & SUBMISI",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF94A3B8),
                        letterSpacing = 1.sp
                    )

                    EPITCardContainer {
                        OutlinedTextField(
                            value = captainNameStr,
                            onValueChange = { captainNameStr = it },
                            label = { Text("Nama Nakhoda Bertanggung Jawab") },
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = crewCountStr,
                            onValueChange = { crewCountStr = it.filter { c -> c.isDigit() } },
                            label = { Text("Jumlah Total ABK / Awak Kapal") },
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Surface(
                            color = Color(0xFFFFFBEB),
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFCD34D)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Checkbox(
                                    checked = isCrewRosterVerified,
                                    onCheckedChange = { isCrewRosterVerified = it },
                                    colors = CheckboxDefaults.colors(checkedColor = ActionCyan)
                                )
                                Text(
                                    text = "Saya memverifikasi bahwa seluruh daftar ABK telah memiliki Buku Pelaut / sertifikat kelaikan sesuai regulasi KKP.",
                                    fontSize = 11.sp,
                                    color = Color(0xFF78350F),
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { currentStep = 2 },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Kembali", color = PrimaryNavy, fontWeight = FontWeight.Bold)
                        }

                        EPITPrimaryButton(
                            text = "Kirim Permohonan SLO & SPB",
                            icon = Icons.Default.Send,
                            enabled = isCrewRosterVerified,
                            isLoading = isSubmitting,
                            onClick = {
                                isSubmitting = true
                                showSuccessDialog = true
                            },
                            modifier = Modifier.weight(2f)
                        )
                    }
                }
            }
        }
    }

    // DIALOG KONFIRMASI PENGAJUAN SLO & SPB
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { /* Lock Modal */ },
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
                    text = "Permohonan SLO & SPB Terkirim!",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryNavy,
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Permohonan Surat Laik Operasi (SLO) dan Surat Persetujuan Berlayar (SPB) dari $selectedOriginPort menuju $selectedDestinationPort telah diteruskan ke Satwas SDKP.",
                        fontSize = 12.sp,
                        color = Color(0xFF475569),
                        textAlign = TextAlign.Center,
                        lineHeight = 17.sp
                    )

                    Surface(
                        color = Color(0xFFF1F5F9),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Status: Verifikasi Petugas SDKP",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryNavy,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccessDialog = false
                        isSubmitting = false
                        onDepartureSubmitted()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ActionCyan, contentColor = PrimaryNavy),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(text = "Selesai & Ke Beranda", fontWeight = FontWeight.Bold)
                }
            },
            containerColor = Color.White
        )
    }
}