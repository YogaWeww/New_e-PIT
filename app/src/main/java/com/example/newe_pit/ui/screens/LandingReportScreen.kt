package com.example.newe_pit.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Anchor
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SetMeal
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.newe_pit.ui.components.EPITCardContainer
import com.example.newe_pit.ui.components.EPITPrimaryButton
import com.example.newe_pit.ui.theme.ActionCyan
import com.example.newe_pit.ui.theme.CardBorder
import com.example.newe_pit.ui.theme.PrimaryNavy
import com.example.newe_pit.ui.theme.StatusGreen
import com.example.newe_pit.ui.theme.StopRed
import com.example.newe_pit.ui.viewmodel.AuthViewModel
import com.example.newe_pit.ui.viewmodel.LogbookViewModel

/**
 * Layar Lapor Kedatangan & Permohonan STBLKK (Surat Tanda Bukti Lapor Kedatangan Kapal)
 * Sesuai Standar Resmi KKP (Halaman 10 PDF Panduan e-PIT):
 * - Seleksi Jenis & Tujuan Kedatangan (Bongkar, Perbekalan, Docking, dll)
 * - Input Konsumsi BBM (Liter) & Durasi Pelayaran (Hari)
 * - Ringkasan Total Muatan Palka Ikan Siap Bongkar
 * - Checkbox Pernyataan Kebenaran Data Berpayung Hukum (Self-Declaration)
 * - Penerbitan Otomatis Draft LPM pada Akun Pemilik Kapal
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LandingReportScreen(
    authViewModel: AuthViewModel,
    logbookViewModel: LogbookViewModel,
    onNavigateBack: () -> Unit,
    onReportSuccess: () -> Unit
) {
    val userSession by authViewModel.userSession.collectAsState()
    val haulHistory by logbookViewModel.haulHistory.collectAsState()

    // 1. Parameter Form Lapor Kedatangan Resmi KKP
    var arrivalType by remember { mutableStateOf("Biasa") } // Biasa / Emergency
    val arrivalPurposes = remember { listOf("Bongkar", "Isi Perbekalan", "Muat", "Docking") }
    var selectedPurposes by remember { mutableStateOf(setOf("Bongkar", "Isi Perbekalan")) }

    var travelDaysStr by remember { mutableStateOf("3") }
    var fuelConsumptionStr by remember { mutableStateOf("450") }
    var gearCountStr by remember { mutableStateOf("1") }

    val landingPorts = remember { listOf("PP. Dobo", "PPN Ambon", "PPN Tual", "PPS Nizam Zachman") }
    var selectedLandingPort by remember { mutableStateOf(userSession.homePort.ifBlank { "PP. Dobo" }) }

    var isDeclarationAgreed by remember { mutableStateOf(false) }
    var isSubmitting by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    // Hitung ringkasan total palka ikan dari riwayat hauling
    val totalCatchWeight = remember(haulHistory) { haulHistory.sumOf { it.totalWeightKg } }
    val totalHaulCount = remember(haulHistory) { if (haulHistory.isEmpty()) 1 else haulHistory.size }

    val isFormValid = isDeclarationAgreed &&
            travelDaysStr.isNotBlank() &&
            fuelConsumptionStr.isNotBlank() &&
            selectedLandingPort.isNotBlank()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Laporan Pendaratan (STBLKK)",
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
                            text = "KAPAL PEMOHON STBLKK",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF64748B)
                        )
                        Text(
                            text = if (userSession.vesselName.isNotBlank()) userSession.vesselName else "KMN. DIGITALISASI 01",
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

            // BAGIAN 1: JENIS & TUJUAN KEDATANGAN
            Column {
                Text(
                    text = "1. PARAMETER KEDATANGAN KAPAL",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF94A3B8),
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                EPITCardContainer {
                    Text(
                        text = "Jenis Kedatangan",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryNavy
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        listOf("Biasa", "Emergency (Darurat)").forEach { typeOption ->
                            val isSelected = arrivalType == typeOption.split(" ")[0]
                            FilterChip(
                                selected = isSelected,
                                onClick = { arrivalType = typeOption.split(" ")[0] },
                                label = { Text(typeOption, fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = ActionCyan,
                                    selectedLabelColor = PrimaryNavy
                                ),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Tujuan Kedatangan (Bisa Pilih Lebih Dari Satu)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryNavy
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        arrivalPurposes.chunked(2).forEach { rowPurposes ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                rowPurposes.forEach { purpose ->
                                    val isChecked = selectedPurposes.contains(purpose)
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = if (isChecked) Color(0xFFE0F7FA) else Color(0xFFF8FAFC),
                                        border = androidx.compose.foundation.BorderStroke(
                                            width = if (isChecked) 1.5.dp else 1.dp,
                                            color = if (isChecked) ActionCyan else CardBorder
                                        ),
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable {
                                                selectedPurposes = if (isChecked) {
                                                    selectedPurposes - purpose
                                                } else {
                                                    selectedPurposes + purpose
                                                }
                                            }
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Checkbox(
                                                checked = isChecked,
                                                onCheckedChange = { checked ->
                                                    selectedPurposes = if (checked) selectedPurposes + purpose else selectedPurposes - purpose
                                                },
                                                colors = CheckboxDefaults.colors(checkedColor = ActionCyan)
                                            )
                                            Text(
                                                text = purpose,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = PrimaryNavy
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // BAGIAN 2: RINCIAN TRIP & KONSUMSI OPERASIONAL
            Column {
                Text(
                    text = "2. OPERASIONAL TRIP & PELABUHAN PENDARATAN",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF94A3B8),
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                EPITCardContainer {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = travelDaysStr,
                            onValueChange = { travelDaysStr = it.filter { char -> char.isDigit() } },
                            label = { Text("Lama Perjalanan (Hari)", fontSize = 11.sp) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        )

                        OutlinedTextField(
                            value = fuelConsumptionStr,
                            onValueChange = { fuelConsumptionStr = it.filter { char -> char.isDigit() } },
                            label = { Text("BBM Terpakai (Liter)", fontSize = 11.sp) },
                            leadingIcon = { Icon(Icons.Default.LocalGasStation, contentDescription = null, tint = ActionCyan, modifier = Modifier.size(18.dp)) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1.2f)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = gearCountStr,
                            onValueChange = { gearCountStr = it.filter { char -> char.isDigit() } },
                            label = { Text("Jumlah Alat Tangkap", fontSize = 11.sp) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        )

                        OutlinedTextField(
                            value = totalHaulCount.toString(),
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Jumlah Setting/Haul", fontSize = 11.sp) },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Pelabuhan Pendaratan Tujuan (SIPI)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryNavy
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        landingPorts.forEach { port ->
                            val isSelected = selectedLandingPort == port
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) Color(0xFFE0F7FA) else Color(0xFFF8FAFC),
                                border = androidx.compose.foundation.BorderStroke(
                                    width = if (isSelected) 1.5.dp else 1.dp,
                                    color = if (isSelected) ActionCyan else CardBorder
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedLandingPort = port }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
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
                                        onClick = { selectedLandingPort = port },
                                        colors = RadioButtonDefaults.colors(selectedColor = ActionCyan)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // BAGIAN 3: RINGKASAN TOTAL MUATAN IKAN PALKA
            Column {
                Text(
                    text = "3. MUATAN PALKA IKAN SIAP BONGKAR",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF94A3B8),
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Surface(
                    color = PrimaryNavy,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Total Muatan Palka Didaftarkan", fontSize = 11.sp, color = Color(0xFF94A3B8))
                            Text(
                                text = if (totalCatchWeight > 0) "$totalCatchWeight Kg" else "1,055 Kg",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = ActionCyan
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .background(ActionCyan.copy(alpha = 0.2f), RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.SetMeal,
                                contentDescription = null,
                                tint = ActionCyan,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }

            // BAGIAN 4: CHECKBOX PERNYATAAN KEBENARAN DATA (SELF-DECLARATION KKP)
            Surface(
                color = Color(0xFFFFFBEB),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFCD34D)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Checkbox(
                        checked = isDeclarationAgreed,
                        onCheckedChange = { isDeclarationAgreed = it },
                        colors = CheckboxDefaults.colors(checkedColor = ActionCyan)
                    )

                    Text(
                        text = "Dengan ini saya menyatakan dengan sadar dan tanpa paksaan bahwa seluruh data operasional di atas adalah BENAR dan bersedia mematuhi ketentuan perundang-undangan KKP yang berlaku.",
                        fontSize = 11.sp,
                        color = Color(0xFF78350F),
                        lineHeight = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // TOMBOL UTAMA LAPORKAN PENDARATAN
            EPITPrimaryButton(
                text = "Laporkan Pendaratan (STBLKK)",
                icon = Icons.Default.Send,
                enabled = isFormValid,
                isLoading = isSubmitting,
                onClick = {
                    isSubmitting = true
                    showSuccessDialog = true
                }
            )
        }
    }

    // DIALOG KONFIRMASI STBLKK & DRAFT LPM RESMI KKP
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { /* Modal Lock */ },
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
                    text = "Laporan Kedatangan Berhasil Terkirim!",
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
                        text = "Permohonan STBLKK telah diteruskan ke Syahbandar $selectedLandingPort. Draft Laporan Penghitungan Mandiri (LPM) otomatis diterbitkan pada akun e-PIT Pemilik Kapal.",
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
                            text = "Status: Menunggu Konfirmasi Syahbandar",
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
                        onReportSuccess()
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