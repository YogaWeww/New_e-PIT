package com.example.newe_pit.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.newe_pit.data.model.NotificationItem
import com.example.newe_pit.data.model.NotificationType
import com.example.newe_pit.ui.theme.*

/**
 * Layar Pusat Notifikasi & Informasi (Notification Screen)
 * Menyediakan pengingat masa berlaku izin, status sinkronisasi offline, dan info cuaca WPP 718.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    onNavigateBack: () -> Unit = {}
) {
    // Data simulasi notifikasi operasional pelayaran KKP
    val notificationList = remember {
        listOf(
            NotificationItem(
                id = "NOTIF-01",
                title = "SIPI Segera Kedaluwarsa",
                description = "Masa berlaku izin tangkap (SIPI) WPP-NRI 718 tersisa 12 hari lagi. Segera ajukan perpanjangan dokumen.",
                timestamp = "Hari ini, 07:30 WIB",
                type = NotificationType.DOCUMENT_EXPIRY,
                isUnread = true
            ),
            NotificationItem(
                id = "NOTIF-02",
                title = "Sinkronisasi Hauling Berhasil",
                description = "Data tangkapan Tawur #11 dan #12 telah berhasil disinkronkan ke server pusat Direktorat Jenderal Perikanan Tangkap.",
                timestamp = "Kemarin, 18:45 WIB",
                type = NotificationType.SYNC_STATUS,
                isUnread = false
            ),
            NotificationItem(
                id = "NOTIF-03",
                title = "Peringatan Dini Gelombang BMKG",
                description = "Waspada tinggi gelombang 1.5 - 2.5 meter di perairan WPP-NRI 718 (Laut Arafura) dalam 24 jam ke depan.",
                timestamp = "24 Agustus 2026",
                type = NotificationType.WEATHER_WARNING,
                isUnread = false
            ),
            NotificationItem(
                id = "NOTIF-04",
                title = "Verifikasi BKP Sukses",
                description = "Nomor Buku Kapal Perikanan KMN. DIGITALISASI 01 (48 GT) terverifikasi valid oleh sistem pangkalan.",
                timestamp = "20 Agustus 2026",
                type = NotificationType.SYNC_STATUS,
                isUnread = false
            )
        )
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = NeutralCanvas
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top App Bar yang Konsisten
            TopAppBar(
                title = {
                    Text(
                        text = "Notifikasi & Informasi",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryNavy
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(notificationList) { notif ->
                    NotificationCard(item = notif)
                }

                item {
                    Spacer(modifier = Modifier.height(70.dp))
                }
            }
        }
    }
}

/**
 * Kartu Satuan Item Notifikasi
 */
@Composable
private fun NotificationCard(item: NotificationItem) {
    val (iconBg, iconTint, iconVector) = when (item.type) {
        NotificationType.DOCUMENT_EXPIRY -> Triple(Color(0xFFFEE2E2), StopRed, Icons.Default.Warning)
        NotificationType.SYNC_STATUS -> Triple(Color(0xFFD1FAE5), StatusGreen, Icons.Default.CloudDone)
        NotificationType.WEATHER_WARNING -> Triple(Color(0xFFE0F7FA), ActionCyan, Icons.Default.Water)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (item.isUnread) Color.White else Color(0xFFF8FAFC)
        ),
        border = BorderStroke(
            width = if (item.isUnread) 1.5.dp else 1.dp,
            color = if (item.isUnread) ActionCyan.copy(alpha = 0.5f) else CardBorder
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (item.isUnread) 3.dp else 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Ikon Jenis Notifikasi
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(iconBg, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = iconVector,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(22.dp)
                )
            }

            // Teks Keterangan
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item.title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryNavy
                    )
                    if (item.isUnread) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(ActionCyan, RoundedCornerShape(4.dp))
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = item.description,
                    fontSize = 12.sp,
                    color = Color(0xFF475569),
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = item.timestamp,
                    fontSize = 10.sp,
                    color = Color(0xFF94A3B8)
                )
            }
        }
    }
}