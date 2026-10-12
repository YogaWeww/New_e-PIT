package com.example.newe_pit.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.newe_pit.ui.theme.ActionCyan
import com.example.newe_pit.ui.theme.PrimaryNavy
import com.example.newe_pit.ui.theme.StatusGreen
import com.example.newe_pit.ui.theme.StopRed

/**
 * Banner Komponen UI Indikator Status Online/Offline dan Antrean Sinkronisasi Laporan.
 */
@Composable
fun SyncStatusBanner(
    pendingCount: Int,
    isOnline: Boolean,
    onSyncNowClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // 1. INDIKATOR METODE KONEKSI JARINGAN (ONLINE / OFFLINE)
        Surface(
            color = if (isOnline) Color(0xFFECFDF5) else Color(0xFFFEF2F2),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = if (isOnline) StatusGreen.copy(alpha = 0.4f) else StopRed.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(10.dp)
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(
                                color = if (isOnline) StatusGreen else StopRed,
                                shape = CircleShape
                            )
                    )
                    Text(
                        text = if (isOnline) "Mode Online (Sinyal Terhubung)" else "Mode Offline (Di Luar Jangkauan)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isOnline) Color(0xFF065F46) else Color(0xFF991B1B)
                    )
                }

                Icon(
                    imageVector = if (isOnline) Icons.Default.CloudDone else Icons.Default.CloudOff,
                    contentDescription = null,
                    tint = if (isOnline) StatusGreen else StopRed,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        // 2. BANNER ANTREAN SINKRONISASI LOGBOOK (Hanya tampil jika ada data pending)
        AnimatedVisibility(
            visible = pendingCount > 0,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            Surface(
                color = Color(0xFFE0F7FA),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, ActionCyan.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(ActionCyan.copy(alpha = 0.2f), RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CloudSync,
                                contentDescription = null,
                                tint = PrimaryNavy,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Column {
                            Text(
                                text = "$pendingCount Laporan Menunggu Sinkronisasi",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryNavy
                            )
                            Text(
                                text = if (isOnline) "Sinyal terhubung, siap dikirim ke cloud." else "Tersimpan di HP (Akan dikirim saat ada sinyal).",
                                fontSize = 10.sp,
                                color = Color(0xFF475569)
                            )
                        }
                    }

                    if (isOnline) {
                        Surface(
                            color = ActionCyan,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.clickable { onSyncNowClick() }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Sync,
                                    contentDescription = null,
                                    tint = PrimaryNavy,
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    text = "Kirim Sekarang",
                                    fontSize = 10.sp,
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
