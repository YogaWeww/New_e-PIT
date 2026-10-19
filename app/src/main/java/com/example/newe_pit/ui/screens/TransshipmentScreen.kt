package com.example.newe_pit.ui.screens

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DirectionsBoat
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoveToInbox
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.SetMeal
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import coil.compose.rememberAsyncImagePainter
import com.example.newe_pit.ui.components.EPITCardContainer
import com.example.newe_pit.ui.components.EPITPrimaryButton
import com.example.newe_pit.ui.components.EPITStepperControl
import com.example.newe_pit.ui.theme.ActionCyan
import com.example.newe_pit.ui.theme.CardBorder
import com.example.newe_pit.ui.theme.PrimaryNavy
import com.example.newe_pit.ui.theme.StatusGreen
import com.example.newe_pit.ui.theme.StopRed
import com.example.newe_pit.ui.viewmodel.AuthViewModel
import com.example.newe_pit.ui.viewmodel.LogbookViewModel
import java.io.File

/**
 * Data Model untuk Kapal Mitra Pengangkut (SIKPI Aktif)
 */
data class PartnerVessel(
    val id: String,
    val name: String,
    val sikpiNumber: String,
    val grossTonnage: Int
)

/**
 * Data Item Tangkapan yang akan Ditransfer
 */
data class TransshipmentItem(
    val speciesName: String,
    val availableWeightKg: Int,
    var transferredWeightKg: Int
)

/**
 * Membuat File Sementara untuk Menyimpan Hasil Foto Kamera
 */
private fun createTempImageUri(context: Context): Uri {
    val tempFile = File.createTempFile("epit_photo_", ".jpg", context.cacheDir).apply {
        createNewFile()
        deleteOnExit()
    }
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        tempFile
    )
}

/**
 * Layar Alih Muatan Laut (Transshipment) Sesuai Standar KKP:
 * - Mengambil Foto Langsung Kamera (Direct Camera) / Galeri Opsional
 * - Seleksi Kapal Mitra SIKPI Aktif
 * - Validasi Sisa Stok Palka per Spesies
 * - Dialog Konfirmasi Penguncian Logbook Permanen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransshipmentScreen(
    authViewModel: AuthViewModel,
    logbookViewModel: LogbookViewModel,
    onNavigateBack: () -> Unit,
    onTransshipmentSuccess: () -> Unit
) {
    val context = LocalContext.current
    val userSession by authViewModel.userSession.collectAsState()
    val cartItems by logbookViewModel.cartItems.collectAsState()

    // 1. Daftar Kapal Mitra Terdaftar (SIKPI Aktif)
    val partnerVessels = remember {
        listOf(
            PartnerVessel("PGK-01", "KMN. SAMUDERA BAHARI 02", "SIKPI/012/2026", 60),
            PartnerVessel("PGK-02", "KMN. LAUTAN EMAS 08", "SIKPI/088/2026", 45),
            PartnerVessel("PGK-03", "KMN. BINTANG ANGKUT 03", "SIKPI/033/2026", 90)
        )
    }

    var selectedPartner by remember { mutableStateOf<PartnerVessel?>(null) }

    // 2. State Dokumentasi 3 Foto Wajib
    var photoVesselUri by remember { mutableStateOf<Uri?>(null) }
    var photoCaptainUri by remember { mutableStateOf<Uri?>(null) }
    var photoCargoUri by remember { mutableStateOf<Uri?>(null) }

    var currentPhotoType by remember { mutableStateOf(1) } // 1: Kapal, 2: Nakhoda, 3: Cargo
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }
    var showPhotoOptionBottomSheet by remember { mutableStateOf(false) }

    // Launcher Kamera Langsung (TakePicture)
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && tempCameraUri != null) {
            when (currentPhotoType) {
                1 -> photoVesselUri = tempCameraUri
                2 -> photoCaptainUri = tempCameraUri
                3 -> photoCargoUri = tempCameraUri
            }
        }
    }

    // Launcher Galeri (GetContent) sebagai Opsi Cadangan
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            when (currentPhotoType) {
                1 -> photoVesselUri = it
                2 -> photoCaptainUri = it
                3 -> photoCargoUri = it
            }
        }
    }

    // 3. State Item Tangkapan yang Ditransfer (Diselaraskan dengan Keranjang Logbook)
    val transferItems = remember(cartItems) {
        if (cartItems.isEmpty()) {
            mutableStateListOf(
                TransshipmentItem("Cakalang", 555, 0),
                TransshipmentItem("Tongkol Krakau", 320, 0),
                TransshipmentItem("Tuna Madidihang", 180, 0)
            )
        } else {
            mutableStateListOf(*cartItems.map {
                TransshipmentItem(it.speciesName, it.weightKg, 0)
            }.toTypedArray())
        }
    }

    var showLockWarningDialog by remember { mutableStateOf(false) }
    var isSubmitting by remember { mutableStateOf(false) }

    val isFormValid = selectedPartner != null &&
            photoVesselUri != null &&
            photoCaptainUri != null &&
            photoCargoUri != null &&
            transferItems.any { it.transferredWeightKg > 0 }

    Scaffold(
        topBar = {
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
            // HEADER INFORMASI KAPAL ASAL
            EPITCardContainer {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "KAPAL PENANGKAP (ASAL MUATAN)",
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

            // BAGIAN 1: PILIH KAPAL MITRA PENGANGKUT (SIKPI AKTIF)
            Column {
                Text(
                    text = "1. PILIH KAPAL MITRA PENGANGKUT (SIKPI AKTIF)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF94A3B8),
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                partnerVessels.forEach { partner ->
                    val isSelected = selectedPartner?.id == partner.id
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) Color(0xFFE0F7FA) else Color.White
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                            .border(
                                width = if (isSelected) 1.5.dp else 1.dp,
                                color = if (isSelected) ActionCyan else CardBorder,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { selectedPartner = partner }
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DirectionsBoat,
                                    contentDescription = null,
                                    tint = if (isSelected) ActionCyan else Color(0xFF64748B),
                                    modifier = Modifier.size(24.dp)
                                )
                                Column {
                                    Text(
                                        text = partner.name,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PrimaryNavy
                                    )
                                    Text(
                                        text = "SIKPI: ${partner.sikpiNumber} | ${partner.grossTonnage} GT",
                                        fontSize = 11.sp,
                                        color = Color(0xFF64748B),
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }

                            RadioButton(
                                selected = isSelected,
                                onClick = { selectedPartner = partner },
                                colors = RadioButtonDefaults.colors(selectedColor = ActionCyan)
                            )
                        }
                    }
                }
            }

            // BAGIAN 2: DOKUMENTASI FOTO WAJIB 3 SUDUT (STANDAR KKP)
            Column {
                Text(
                    text = "2. DOKUMENTASI FOTO BUKTI (WAJIB 3 FOTO)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF94A3B8),
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Foto 1: Fisik Kapal
                    PhotoCaptureTile(
                        title = "1. Fisik Kapal",
                        imageUri = photoVesselUri,
                        onClick = {
                            currentPhotoType = 1
                            showPhotoOptionBottomSheet = true
                        },
                        modifier = Modifier.weight(1f)
                    )

                    // Foto 2: Nakhoda / Dokumen
                    PhotoCaptureTile(
                        title = "2. Nakhoda/Izin",
                        imageUri = photoCaptainUri,
                        onClick = {
                            currentPhotoType = 2
                            showPhotoOptionBottomSheet = true
                        },
                        modifier = Modifier.weight(1f)
                    )

                    // Foto 3: Cargo / Ikan Geladak
                    PhotoCaptureTile(
                        title = "3. Ikan Geladak",
                        imageUri = photoCargoUri,
                        onClick = {
                            currentPhotoType = 3
                            showPhotoOptionBottomSheet = true
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // BAGIAN 3: ALOKASI TRANSFER IKAN PER SPESIES (VALIDASI SISA PALKA)
            Column {
                Text(
                    text = "3. RINCIAN IKAN DIALIHMUATKAN (PALKA SISA)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF94A3B8),
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                transferItems.forEachIndexed { index, item ->
                    EPITCardContainer {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SetMeal,
                                    contentDescription = null,
                                    tint = ActionCyan,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = item.speciesName,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryNavy
                                )
                            }

                            Surface(
                                color = StatusGreen.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "Sisa Palka: ${item.availableWeightKg} Kg",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = StatusGreen,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        EPITStepperControl(
                            label = "Jumlah Berat Ditransfer",
                            value = item.transferredWeightKg,
                            onValueChange = { newVal ->
                                val boundedVal = newVal.coerceIn(0, item.availableWeightKg)
                                transferItems[index] = item.copy(transferredWeightKg = boundedVal)
                            },
                            unit = "Kg",
                            quickSteps = listOf(10, 50)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            // RINGKASAN TOTAL DITRANSFER
            val totalTransferred = transferItems.sumOf { it.transferredWeightKg }

            Surface(
                color = PrimaryNavy,
                shape = RoundedCornerShape(12.dp),
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
                        Text("Total Muatan Dibatalkan/Ditransfer", fontSize = 11.sp, color = Color(0xFF94A3B8))
                        Text("$totalTransferred Kg", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = ActionCyan)
                    }

                    Icon(
                        imageVector = Icons.Default.MoveToInbox,
                        contentDescription = null,
                        tint = ActionCyan,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // TOMBOL UTAMA SIMPAN DATA ALIH MUATAN
            EPITPrimaryButton(
                text = "Simpan Data Alih Muatan",
                icon = Icons.Default.Lock,
                enabled = isFormValid,
                isLoading = isSubmitting,
                onClick = {
                    showLockWarningDialog = true
                }
            )
        }
    }

    // BOTTOM SHEET OPSIONAL PILIHAN SUMBER FOTO (KAMERA LANGSUNG / GALERI)
    if (showPhotoOptionBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showPhotoOptionBottomSheet = false },
            containerColor = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Pilih Sumber Foto Dokumen",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryNavy
                )

                // Pilihan 1: Ambil Kamera Langsung (Sangat Direkomendasikan KKP)
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE0F7FA)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            showPhotoOptionBottomSheet = false
                            try {
                                val uri = createTempImageUri(context)
                                tempCameraUri = uri
                                cameraLauncher.launch(uri)
                            } catch (e: Exception) {
                                // Fallback jika uri gagal dibuat
                                galleryLauncher.launch("image/*")
                            }
                        }
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = null,
                            tint = ActionCyan,
                            modifier = Modifier.size(28.dp)
                        )
                        Column {
                            Text(
                                text = "Ambil Foto Kamera Langsung",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryNavy
                            )
                            Text(
                                text = "Menggunakan kamera HP real-time (Standar KKP)",
                                fontSize = 11.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    }
                }

                // Pilihan 2: Pilih dari Galeri
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
                        .clickable {
                            showPhotoOptionBottomSheet = false
                            galleryLauncher.launch("image/*")
                        }
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhotoLibrary,
                            contentDescription = null,
                            tint = PrimaryNavy,
                            modifier = Modifier.size(28.dp)
                        )
                        Column {
                            Text(
                                text = "Pilih dari Galeri Foto",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryNavy
                            )
                            Text(
                                text = "Pilih foto yang sudah tersimpan di HP",
                                fontSize = 11.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }

    // DIALOG DIKUNCI PERMANEN SESUAI KETENTUAN PANDUAN KKP
    if (showLockWarningDialog) {
        AlertDialog(
            onDismissRequest = { showLockWarningDialog = false },
            icon = {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .background(StopRed.copy(alpha = 0.15f), RoundedCornerShape(23.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = StopRed,
                        modifier = Modifier.size(24.dp)
                    )
                }
            },
            title = {
                Text(
                    text = "Konfirmasi Penguncian Logbook",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryNavy,
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Text(
                    text = "Ketika data alih muatan disimpan, data setting/haul logbook sebelumnya akan DIKUNCI PERMANEN dan tidak dapat diubah kembali. Apakah Anda yakin?",
                    fontSize = 12.sp,
                    color = Color(0xFF475569),
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showLockWarningDialog = false
                        isSubmitting = true
                        onTransshipmentSuccess()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StopRed),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(text = "Ya, Kunci & Simpan", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showLockWarningDialog = false },
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(text = "Batal", color = PrimaryNavy, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = Color.White
        )
    }
}

/**
 * Komponen Tile Pengambilan Foto Bukti Dokumentasi
 */
@Composable
private fun PhotoCaptureTile(
    title: String,
    imageUri: Uri?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = modifier
            .height(110.dp)
            .border(
                width = 1.dp,
                color = if (imageUri != null) StatusGreen else CardBorder,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
    ) {
        if (imageUri != null) {
            Box(modifier = Modifier.fillMaxSize()) {
                Image(
                    painter = rememberAsyncImagePainter(imageUri),
                    contentDescription = title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .size(20.dp)
                        .background(StatusGreen, RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = null,
                    tint = ActionCyan,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = title,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryNavy,
                    textAlign = TextAlign.Center,
                    maxLines = 2
                )
            }
        }
    }
}