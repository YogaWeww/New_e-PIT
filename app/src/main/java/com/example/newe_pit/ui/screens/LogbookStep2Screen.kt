package com.example.newe_pit.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Anchor
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.newe_pit.R
import com.example.newe_pit.ui.components.EPITCardContainer
import com.example.newe_pit.ui.theme.ActionCyan
import com.example.newe_pit.ui.theme.PrimaryNavy
import com.example.newe_pit.ui.theme.StopRed
import com.example.newe_pit.ui.viewmodel.LogbookViewModel
import java.util.Locale

/**
 * Layar Logbook Step 2: Perendaman Jaring (Soak Time) & Kalibrasi GPS Akhir.
 * Menampilkan status "Mencari..." pada Setting Akhir jika GPS mati/sedang dikalibrasi.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogbookStep2Screen(
    logbookViewModel: LogbookViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToNextStep: () -> Unit
) {
    val context = LocalContext.current
    val soakTimeSeconds by logbookViewModel.soakTimeSeconds.collectAsState()
    val startLat by logbookViewModel.startLatitude.collectAsState()
    val startLong by logbookViewModel.startLongitude.collectAsState()
    val endLat by logbookViewModel.endLatitude.collectAsState()
    val endLong by logbookViewModel.endLongitude.collectAsState()
    val isGpsLoading by logbookViewModel.isGpsLoading.collectAsState()
    val isGpsHardwareActive by logbookViewModel.isGpsHardwareActive.collectAsState()
    val userMessage by logbookViewModel.userMessage.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(userMessage) {
        userMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            logbookViewModel.clearUserMessage()
        }
    }

    // Format waktu timer HH:MM:SS
    val hours = soakTimeSeconds / 3600
    val minutes = (soakTimeSeconds % 3600) / 60
    val seconds = soakTimeSeconds % 60
    val formattedTimer = String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds)

    // Tampilan GPS Awal (Terkunci dari Step 1)
    val displayStartLat = if (startLat != 0.0) String.format(Locale.US, "%.6f %s", Math.abs(startLat), if (startLat >= 0) "N" else "S") else "-"
    val displayStartLong = if (startLong != 0.0) String.format(Locale.US, "%.6f %s", Math.abs(startLong), if (startLong >= 0) "E" else "W") else "-"

    // Tampilan GPS Akhir (Mencari... jika GPS Mati, sedang Loading, atau belum terkunci)
    val displayEndLat = when {
        isGpsLoading -> "Mencari..."
        !isGpsHardwareActive -> "GPS Nonaktif"
        endLat != 0.0 -> String.format(Locale.US, "%.6f %s", Math.abs(endLat), if (endLat >= 0) "N" else "S")
        else -> "Mencari..."
    }

    val displayEndLong = when {
        isGpsLoading -> "Mencari..."
        !isGpsHardwareActive -> "GPS Nonaktif"
        endLong != 0.0 -> String.format(Locale.US, "%.6f %s", Math.abs(endLong), if (endLong >= 0) "E" else "W")
        else -> "Mencari..."
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Logbook (Perendaman)",
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
            // 1. HERO HEADER KAPAL
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(18.dp))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.gambar_kapal),
                    contentDescription = "Kapal",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f))
                            )
                        )
                )
                Text(
                    text = "KMN. DIGITALISASI 01",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                )
            }

            // 2. KARTU STATUS PERENDAMAN & TIMER
            EPITCardContainer {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.HourglassTop,
                        contentDescription = null,
                        tint = Color(0xFFD97706),
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "ALAT TANGKAP SEDANG DIREKAM / DIRENDAM",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFFD97706),
                        letterSpacing = 0.5.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // BOX TIMER SOAK TIME
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(PrimaryNavy, RoundedCornerShape(16.dp))
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Durasi Perendaman (Soak Time)",
                            fontSize = 12.sp,
                            color = Color(0xFF94A3B8)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = formattedTimer,
                            fontSize = 34.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = ActionCyan,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 2.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // KOTAK RINCIAN KOORDINAT GPS AWAL & AKHIR
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF1F5F9), RoundedCornerShape(12.dp))
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Column {
                        Text(
                            text = "Setting Awal (Turun Jaring):",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF64748B)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Lat: $displayStartLat | Long: $displayStartLong",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = PrimaryNavy,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    HorizontalDivider(color = Color(0xFFE2E8F0), thickness = 1.dp)

                    Column {
                        Text(
                            text = "Setting Akhir (Angkat Jaring / Live GPS):",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF64748B)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Lat: $displayEndLat | Long: $displayEndLong",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (endLat != 0.0) ActionCyan else StopRed,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // TOMBOL KALIBRASI ULANG GPS
                OutlinedButton(
                    onClick = { logbookViewModel.updateCurrentLocation(context) },
                    enabled = !isGpsLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ActionCyan)
                ) {
                    if (isGpsLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = ActionCyan,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Memperbarui GPS...",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = ActionCyan
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.GpsFixed,
                            contentDescription = "Kalibrasi GPS",
                            tint = ActionCyan,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Kalibrasi Ulang GPS (Setting Akhir)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryNavy
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 3. TOMBOL SELESAI SETTING
            Button(
                onClick = {
                    logbookViewModel.finishSetting(context)
                    onNavigateToNextStep()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = StopRed,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Anchor,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Selesai Setting & Catat Tangkapan",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}