package com.example.newe_pit.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import com.example.newe_pit.data.model.HaulRecord
import com.example.newe_pit.data.model.SyncStatus
import com.example.newe_pit.ui.components.EPITCardContainer
import com.example.newe_pit.ui.components.EmptyHaulHistoryState
import com.example.newe_pit.ui.components.StatusBadge
import com.example.newe_pit.ui.theme.*
import com.example.newe_pit.ui.viewmodel.LogbookViewModel

/**
 * Layar Khusus Riwayat Hauling (Daftar Catatan Tawur Perjalanan Trip)
 * Menampilkan ringkasan statistik total berat, status sinkronisasi,
 * serta detail spesies dan koordinat GPS per penarikan jaring.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HaulHistoryScreen(
    logbookViewModel: LogbookViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToLogbook: () -> Unit
) {
    val haulHistory by logbookViewModel.haulHistory.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = NeutralCanvas
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = {
                    Text(
                        text = "Riwayat Hauling Trip",
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

            if (haulHistory.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    EmptyHaulHistoryState(
                        onStartSettingClick = onNavigateToLogbook,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item {
                        EPITCardContainer {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "TOTAL HASIL TAWUR TRIP INI",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF94A3B8),
                                        letterSpacing = 1.sp
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "${haulHistory.sumOf { it.totalWeightKg }} kg",
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = PrimaryNavy
                                    )
                                }
                                Surface(
                                    color = ActionCyan.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text(
                                        text = "${haulHistory.size} Tawur",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PrimaryNavy,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }
                    }

                    items(haulHistory) { haul ->
                        HaulHistoryDetailCard(haul = haul)
                    }
                }
            }
        }
    }
}

/**
 * Kartu Detail Item Hauling dengan Fitur Expand/Collapse
 */
@Composable
private fun HaulHistoryDetailCard(haul: HaulRecord) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { isExpanded = !isExpanded },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, CardBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(ActionCyan.copy(alpha = 0.12f), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.SetMeal,
                            contentDescription = null,
                            tint = ActionCyan,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column {
                        Text(
                            text = "Tawur #${haul.haulNumber}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryNavy
                        )
                        Text(
                            text = haul.timestampFormatted,
                            fontSize = 11.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }

                StatusBadge(
                    text = if (haul.syncStatus == SyncStatus.SYNCED) "Terkirim" else "Offline",
                    isActive = haul.syncStatus == SyncStatus.SYNCED
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF8FAFC), RoundedCornerShape(10.dp))
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "Total Berat", fontSize = 10.sp, color = Color(0xFF64748B))
                    Text(text = "${haul.totalWeightKg} kg", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PrimaryNavy)
                }
                Column {
                    Text(text = "Total Jumlah", fontSize = 10.sp, color = Color(0xFF64748B))
                    Text(text = "${haul.totalQuantityCount} ekor", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PrimaryNavy)
                }
                Column {
                    Text(text = "Perendaman", fontSize = 10.sp, color = Color(0xFF64748B))
                    Text(text = "${haul.soakDurationMinutes} min", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = ActionCyan)
                }
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier.padding(top = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    HorizontalDivider(color = Color(0xFFF1F5F9))

                    Text(
                        text = "RINCIAN TANGKAPAN SPESIES",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF94A3B8)
                    )

                    if (haul.catchItems.isEmpty()) {
                        Text(text = "• Ringkasan spesies tersimpan", fontSize = 12.sp, color = Color(0xFF475569))
                    } else {
                        haul.catchItems.forEach { item ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "• ${item.speciesName}", fontSize = 12.sp, color = Color(0xFF475569))
                                Text(
                                    text = "${item.weightKg} kg (${item.quantityCount} ekor)",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryNavy
                                )
                            }
                        }
                    }

                    if (haul.startLatitude != 0.0) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "KOORDINAT GPS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF94A3B8)
                        )
                        Text(
                            text = "Awal: ${haul.startLatitude}, ${haul.startLongitude}\nAkhir: ${haul.endLatitude}, ${haul.endLongitude}",
                            fontSize = 10.sp,
                            color = Color(0xFF64748B),
                            lineHeight = 14.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isExpanded) "Sembunyikan Rincian" else "Lihat Rincian Spesies",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = ActionCyan
                )
                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = ActionCyan,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}