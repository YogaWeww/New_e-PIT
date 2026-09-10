package com.example.newe_pit.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SetMeal
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.newe_pit.data.model.CatchItem
import com.example.newe_pit.ui.components.EPITCardContainer
import com.example.newe_pit.ui.components.EPITEmptyState
import com.example.newe_pit.ui.components.EPITPrimaryButton
import com.example.newe_pit.ui.theme.*

/**
 * Layar Laporan Pendaratan Trip & Permohonan STBLKK
 * Mengintegrasikan EPITEmptyState jika trip pelayaran belum memiliki catatan hasil tangkapan.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LandingReportScreen(
    onNavigateBack: () -> Unit = {},
    onFinishReport: () -> Unit = {}
) {
    var fuelInput by remember { mutableStateOf("450") }

    val arrivalTypeOptions = listOf("Biasa", "Darurat", "Karam/Hilang")
    var selectedArrivalType by remember { mutableStateOf(arrivalTypeOptions[0]) }
    var expandedArrivalDropdown by remember { mutableStateOf(false) }

    var isBongkarChecked by remember { mutableStateOf(true) }
    var isMuatChecked by remember { mutableStateOf(false) }
    var isIsiPerbekalanChecked by remember { mutableStateOf(false) }
    var isDockingChecked by remember { mutableStateOf(false) }

    val portOptions = listOf(
        "PP. Dobo",
        "PPN Ambon",
        "PP. Nizam Zachman Jakarta",
        "PPN Tual",
        "PPN Bitung",
        "PPS Kendari",
        "PPS Bungus"
    )
    var selectedPort by remember { mutableStateOf(portOptions[0]) }
    var expandedPortDropdown by remember { mutableStateOf(false) }

    var isConfirmed by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    // Simulasi daftar tangkapan trip (dapat diset kosong untuk menguji empty state)
    val tripCatchItems = remember {
        listOf(
            CatchItem(speciesName = "Cakalang [SKJ]", weightKg = 389, quantityCount = 42),
            CatchItem(speciesName = "Cumi-Cumi", weightKg = 399, quantityCount = 150)
        )
    }

    val totalCatchWeight = remember(tripCatchItems) { tripCatchItems.sumOf { it.weightKg } }

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
                        text = "Laporan Pendaratan & STBLKK",
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

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                EPITCardContainer {
                    Text(
                        text = "INFORMASI LOKASI & DURASI TRIP",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF94A3B8),
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "Lama Perendaman:", fontSize = 12.sp, color = Color(0xFF64748B))
                        Text(text = "180 Menit", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PrimaryNavy)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "Jumlah Tawur:", fontSize = 12.sp, color = Color(0xFF64748B))
                        Text(text = "3 Kali Tawur", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PrimaryNavy)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "Lama Perjalanan:", fontSize = 12.sp, color = Color(0xFF64748B))
                        Text(text = "5 Hari", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PrimaryNavy)
                    }
                }

                EPITCardContainer {
                    Text(
                        text = "JENIS & TUJUAN KEDATANGAN",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF94A3B8),
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Text(text = "Jenis Kedatangan", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PrimaryNavy)
                    Spacer(modifier = Modifier.height(6.dp))

                    ExposedDropdownMenuBox(
                        expanded = expandedArrivalDropdown,
                        onExpandedChange = { expandedArrivalDropdown = !expandedArrivalDropdown },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = selectedArrivalType,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedArrivalDropdown) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            shape = RoundedCornerShape(10.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = expandedArrivalDropdown,
                            onDismissRequest = { expandedArrivalDropdown = false }
                        ) {
                            arrivalTypeOptions.forEach { type ->
                                DropdownMenuItem(
                                    text = { Text(text = type, fontSize = 13.sp) },
                                    onClick = {
                                        selectedArrivalType = type
                                        expandedArrivalDropdown = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = "Tujuan Kedatangan", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PrimaryNavy)
                    Spacer(modifier = Modifier.height(4.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Checkbox(
                                    checked = isBongkarChecked,
                                    onCheckedChange = { isBongkarChecked = it },
                                    colors = CheckboxDefaults.colors(checkedColor = ActionCyan, checkmarkColor = PrimaryNavy)
                                )
                                Text(text = "Bongkar", fontSize = 12.sp, color = PrimaryNavy)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Checkbox(
                                    checked = isMuatChecked,
                                    onCheckedChange = { isMuatChecked = it },
                                    colors = CheckboxDefaults.colors(checkedColor = ActionCyan, checkmarkColor = PrimaryNavy)
                                )
                                Text(text = "Muat", fontSize = 12.sp, color = PrimaryNavy)
                            }
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Checkbox(
                                    checked = isIsiPerbekalanChecked,
                                    onCheckedChange = { isIsiPerbekalanChecked = it },
                                    colors = CheckboxDefaults.colors(checkedColor = ActionCyan, checkmarkColor = PrimaryNavy)
                                )
                                Text(text = "Isi Perbekalan", fontSize = 12.sp, color = PrimaryNavy)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Checkbox(
                                    checked = isDockingChecked,
                                    onCheckedChange = { isDockingChecked = it },
                                    colors = CheckboxDefaults.colors(checkedColor = ActionCyan, checkmarkColor = PrimaryNavy)
                                )
                                Text(text = "Docking", fontSize = 12.sp, color = PrimaryNavy)
                            }
                        }
                    }
                }

                EPITCardContainer {
                    Text(
                        text = "INPUT MANUAL OPERASIONAL",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF94A3B8),
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Text(text = "BBM Terpakai (Liter)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PrimaryNavy)
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = fuelInput,
                        onValueChange = { fuelInput = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = "Pelabuhan Pendaratan Tujuan", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PrimaryNavy)
                    Spacer(modifier = Modifier.height(6.dp))

                    ExposedDropdownMenuBox(
                        expanded = expandedPortDropdown,
                        onExpandedChange = { expandedPortDropdown = !expandedPortDropdown },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = selectedPort,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedPortDropdown) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            shape = RoundedCornerShape(10.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = expandedPortDropdown,
                            onDismissRequest = { expandedPortDropdown = false }
                        ) {
                            portOptions.forEach { port ->
                                DropdownMenuItem(
                                    text = { Text(text = port, fontSize = 13.sp) },
                                    onClick = {
                                        selectedPort = port
                                        expandedPortDropdown = false
                                    }
                                )
                            }
                        }
                    }
                }

                if (tripCatchItems.isEmpty()) {
                    EPITEmptyState(
                        title = "Belum Ada Tangkapan Trip Ini",
                        description = "Laporan pendaratan memerlukan ringkasan hasil tangkapan. Silakan isi e-Logbook tawur terlebih dahulu.",
                        icon = Icons.Default.SetMeal,
                        actionLabel = "Buka e-Logbook",
                        onActionClick = onNavigateBack,
                        iconBackgroundColor = Color(0xFFFEF3C7),
                        iconTint = Color(0xFFD97706),
                        compactMode = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    EPITCardContainer {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "RINGKASAN TOTAL TANGKAPAN TRIP",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF94A3B8),
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "${tripCatchItems.size} Spesies",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = ActionCyan
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))

                        tripCatchItems.forEach { item ->
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(text = "• ${item.speciesName}", fontSize = 12.sp, color = Color(0xFF475569))
                                Text(text = "${item.weightKg} Kg", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = PrimaryNavy)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = CardBorder)

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "TOTAL CATCH:", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = PrimaryNavy)
                            Text(text = "$totalCatchWeight Kg", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = ActionCyan)
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFFEF3C7), RoundedCornerShape(12.dp))
                        .border(1.dp, Color(0xFFFCD34D), RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isConfirmed,
                        onCheckedChange = { isConfirmed = it },
                        colors = CheckboxDefaults.colors(checkedColor = ActionCyan, checkmarkColor = PrimaryNavy)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Dengan ini saya menyatakan dengan sadar dan tanpa paksaan bahwa data di atas adalah benar dan setuju dengan ketentuan yang berlaku.",
                        fontSize = 11.sp,
                        color = Color(0xFF78350F),
                        lineHeight = 16.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                EPITPrimaryButton(
                    text = "LAPORKAN PENDARATAN",
                    icon = Icons.Default.Send,
                    enabled = isConfirmed && tripCatchItems.isNotEmpty(),
                    onClick = {
                        showSuccessDialog = true
                    }
                )

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }

    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { },
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
                    text = "Laporan Pendaratan Berhasil",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = PrimaryNavy
                )
            },
            text = {
                Text(
                    text = "Data perjalanan trip telah diterima server pusat KKP di $selectedPort. Dokumen STBLKK dan LPM kini sedang diproses.",
                    fontSize = 12.sp,
                    color = Color(0xFF64748B),
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccessDialog = false
                        onFinishReport()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ActionCyan, contentColor = PrimaryNavy),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Kembali ke Beranda", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            },
            containerColor = Color.White
        )
    }
}