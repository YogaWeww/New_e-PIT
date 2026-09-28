package com.example.newe_pit.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.MoveToInbox
import androidx.compose.material.icons.filled.QueryStats
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
import androidx.core.content.ContextCompat
import com.example.newe_pit.R
import com.example.newe_pit.ui.components.EPITCardContainer
import com.example.newe_pit.ui.theme.ActionCyan
import com.example.newe_pit.ui.theme.CardBorder
import com.example.newe_pit.ui.theme.PrimaryNavy
import com.example.newe_pit.ui.theme.StatusGreen
import com.example.newe_pit.ui.theme.StopRed
import com.example.newe_pit.ui.viewmodel.LogbookViewModel

/**
 * Layar Logbook Step 1: Persiapan Mulai Setting & Pembacaan GPS Asli Perangkat.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogbookStep1Screen(
    logbookViewModel: LogbookViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToNextStep: () -> Unit,
    onNavigateToTransshipment: () -> Unit
) {
    val context = LocalContext.current
    val startLat by logbookViewModel.startLatitude.collectAsState()
    val startLong by logbookViewModel.startLongitude.collectAsState()
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

    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasLocationPermission = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        if (hasLocationPermission) {
            logbookViewModel.updateCurrentLocation(context)
        }
    }

    LaunchedEffect(Unit) {
        if (!hasLocationPermission) {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        } else {
            logbookViewModel.updateCurrentLocation(context)
        }
    }

    val isGpsLocked = isGpsHardwareActive && startLat != 0.0

    val headerTitle = when {
        !isGpsHardwareActive -> "GPS HP NONAKTIF"
        isGpsLoading -> "MEMPROSES KALIBRASI GPS"
        isGpsLocked -> "GPS AKTIF & TERKUNCI"
        else -> "SINYAL BELUM TERKUNCI"
    }

    val headerTitleColor = when {
        !isGpsHardwareActive -> StopRed
        isGpsLocked -> PrimaryNavy
        else -> ActionCyan
    }

    val badgeText = when {
        !isGpsHardwareActive -> "GPS Mati"
        isGpsLoading -> "Memproses..."
        isGpsLocked -> "Akurasi ± 4m"
        else -> "Belum Terkunci"
    }

    val badgeColor = when {
        !isGpsHardwareActive || (!isGpsLocked && !isGpsLoading) -> StopRed
        isGpsLocked -> StatusGreen
        else -> ActionCyan
    }

    val latDisplay = when {
        !isGpsHardwareActive -> "GPS Nonaktif"
        isGpsLoading -> "Memperbarui..."
        startLat != 0.0 -> String.format("%.6f %s", Math.abs(startLat), if (startLat >= 0) "N" else "S")
        else -> "Tekan Kalibrasi"
    }

    val longDisplay = when {
        !isGpsHardwareActive -> "GPS Nonaktif"
        isGpsLoading -> "Memperbarui..."
        startLong != 0.0 -> String.format("%.6f %s", Math.abs(startLong), if (startLong >= 0) "E" else "W")
        else -> "Tekan Kalibrasi"
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Logbook (Persiapan)",
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

            // 2. KARTU STATUS GPS
            EPITCardContainer {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = headerTitle,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = headerTitleColor,
                        letterSpacing = 0.5.sp
                    )

                    Surface(
                        color = badgeColor.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .background(badgeColor, RoundedCornerShape(3.dp))
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = badgeText,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = badgeColor
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF1F5F9), RoundedCornerShape(12.dp))
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Latitude:", fontSize = 12.sp, color = Color(0xFF64748B))
                        Text(
                            text = latDisplay,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (!isGpsHardwareActive) StopRed else PrimaryNavy,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Longitude:", fontSize = 12.sp, color = Color(0xFF64748B))
                        Text(
                            text = longDisplay,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (!isGpsHardwareActive) StopRed else PrimaryNavy,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Wilayah WPP:", fontSize = 12.sp, color = Color(0xFF64748B))
                        Text(
                            text = "WPP-NRI 718",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = ActionCyan
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

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
                            text = "Kalibrasi Ulang GPS",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryNavy
                        )
                    }
                }
            }

            // 3. AKSI OPSIONAL
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onNavigateToTransshipment,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
                ) {
                    Icon(
                        imageVector = Icons.Default.MoveToInbox,
                        contentDescription = null,
                        tint = ActionCyan,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Alih Muatan",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryNavy
                    )
                }

                OutlinedButton(
                    onClick = { /* Handle Sampling */ },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
                ) {
                    Icon(
                        imageVector = Icons.Default.QueryStats,
                        contentDescription = null,
                        tint = ActionCyan,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Sampling",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryNavy
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // 4. TOMBOL UTAMA MULAI SETTING
            Button(
                onClick = {
                    logbookViewModel.startSetting(context)
                    onNavigateToNextStep()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = ActionCyan,
                    contentColor = PrimaryNavy
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
                        imageVector = Icons.Default.Flag,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Mulai Setting (Turun Jaring)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}