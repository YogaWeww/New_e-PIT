package com.example.newe_pit.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.newe_pit.ui.theme.*

/**
 * Komponen Reusable Universal Empty State untuk Aplikasi e-PIT Mobile.
 * Menampilkan pesan informatif, ilustrasi ikon maritim, dan tombol aksi opsional
 * saat data atau daftar dalam keadaan kosong (Null / Empty Data State).
 */
@Composable
fun EPITEmptyState(
    title: String,
    description: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    onActionClick: (() -> Unit)? = null,
    secondaryActionLabel: String? = null,
    onSecondaryActionClick: (() -> Unit)? = null,
    iconBackgroundColor: Color = ActionCyan.copy(alpha = 0.12f),
    iconTint: Color = ActionCyan,
    compactMode: Boolean = false
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(if (compactMode) 16.dp else 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(if (compactMode) 52.dp else 72.dp)
                    .clip(CircleShape)
                    .background(iconBackgroundColor)
                    .border(1.dp, iconTint.copy(alpha = 0.3f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconTint,
                    modifier = Modifier.size(if (compactMode) 28.dp else 36.dp)
                )
            }

            Spacer(modifier = Modifier.height(if (compactMode) 12.dp else 16.dp))

            Text(
                text = title,
                fontSize = if (compactMode) 14.sp else 16.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryNavy,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = description,
                fontSize = if (compactMode) 11.sp else 12.sp,
                color = Color(0xFF64748B),
                textAlign = TextAlign.Center,
                lineHeight = if (compactMode) 16.sp else 18.sp,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            if (actionLabel != null && onActionClick != null) {
                Spacer(modifier = Modifier.height(if (compactMode) 14.dp else 20.dp))

                EPITPrimaryButton(
                    text = actionLabel,
                    onClick = onActionClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(if (compactMode) 44.dp else 48.dp)
                )
            }

            if (secondaryActionLabel != null && onSecondaryActionClick != null) {
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = onSecondaryActionClick,
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryNavy),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryNavy),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp)
                ) {
                    Text(
                        text = secondaryActionLabel,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

/**
 * Preset Empty State: Keranjang Tangkapan Logbook Kosong
 */
@Composable
fun EmptyCartState(
    onAddSpeciesClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    EPITEmptyState(
        title = "Keranjang Tangkapan Masih Kosong",
        description = "Belum ada jenis ikan yang ditambahkan ke tawur ini. Silakan pilih spesies dari katalog WPP 718.",
        icon = Icons.Default.ShoppingCartCheckout,
        actionLabel = "Tambah Hasil Tangkapan",
        onActionClick = onAddSpeciesClick,
        iconBackgroundColor = ActionCyan.copy(alpha = 0.15f),
        iconTint = ActionCyan,
        modifier = modifier
    )
}

/**
 * Preset Empty State: Riwayat Hauling Kosong
 */
@Composable
fun EmptyHaulHistoryState(
    onStartSettingClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    EPITEmptyState(
        title = "Belum Ada Catatan Hauling",
        description = "Anda belum melakukan pencatatan penurunan alat tangkap (setting/tawur) pada trip pelayaran ini.",
        icon = Icons.Default.Anchor,
        actionLabel = "Mulai Setting (Turun Jaring)",
        onActionClick = onStartSettingClick,
        iconBackgroundColor = Color(0xFFE0F7FA),
        iconTint = PrimaryNavy,
        modifier = modifier
    )
}

/**
 * Preset Empty State: Hasil Pencarian Spesies Katalog Tidak Ditemukan
 */
@Composable
fun EmptySearchState(
    searchQuery: String,
    onResetSearchClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    EPITEmptyState(
        title = "Spesies \"$searchQuery\" Tidak Ditemukan",
        description = "Pastikan nama lokal, nama perdagangan, atau kode FAO spesies sudah benar dalam katalog WPP 718.",
        icon = Icons.Default.SearchOff,
        actionLabel = "Reset Pencarian",
        onActionClick = onResetSearchClick,
        iconBackgroundColor = Color(0xFFFEF3C7),
        iconTint = Color(0xFFD97706),
        compactMode = true,
        modifier = modifier
    )
}

/**
 * Preset Empty State: Dokumen Kapal Kosong
 */
@Composable
fun EmptyDocumentState(
    filterCategory: String,
    onRefreshClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    EPITEmptyState(
        title = "Tidak Ada Dokumen $filterCategory",
        description = "Seluruh dokumen perizinan kapal (SLO, SPB, SIPI, STBLKK) akan otomatis ditampilkan di sini setelah diverifikasi syahbandar.",
        icon = Icons.Default.FolderOff,
        actionLabel = "Muat Ulang Dokumen",
        onActionClick = onRefreshClick,
        iconBackgroundColor = Color(0xFFF1F5F9),
        iconTint = InactiveGray,
        modifier = modifier
    )
}

/**
 * Preset Empty State: Pusat Notifikasi Kosong
 */
@Composable
fun EmptyNotificationState(
    modifier: Modifier = Modifier
) {
    EPITEmptyState(
        title = "Tidak Ada Notifikasi Baru",
        description = "Pemberitahuan mengenai masa berlaku dokumen, peringatan cuaca BMKG, dan status sinkronisasi akan muncul di sini.",
        icon = Icons.Default.NotificationsNone,
        iconBackgroundColor = StatusGreen.copy(alpha = 0.15f),
        iconTint = StatusGreen,
        compactMode = true,
        modifier = modifier
    )
}

/**
 * Preset Empty State: Alih Muatan Kosong
 */
@Composable
fun EmptyTransshipmentState(
    onSelectPartnerClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    EPITEmptyState(
        title = "Belum Ada Transaksi Alih Muat",
        description = "Pilih kapal penangkap mitra yang terdaftar dalam dokumen SIKPI aktif untuk mulai melakukan serah terima muatan di laut.",
        icon = Icons.Default.MoveToInbox,
        actionLabel = "Pilih Kapal Mitra SIKPI",
        onActionClick = onSelectPartnerClick,
        iconBackgroundColor = ActionCyan.copy(alpha = 0.15f),
        iconTint = ActionCyan,
        modifier = modifier
    )
}

/**
 * Preset Empty State: Antrean Sinkronisasi Offline Bersih (Semua Data Terkirim)
 */
@Composable
fun EmptyOfflineSyncState(
    modifier: Modifier = Modifier
) {
    EPITEmptyState(
        title = "Semua Data Berhasil Terkirim",
        description = "Tidak ada laporan tertunda. Seluruh data hasil tangkapan di HP telah tersinkronisasi ke server pusat KKP.",
        icon = Icons.Default.CloudDone,
        iconBackgroundColor = StatusGreen.copy(alpha = 0.15f),
        iconTint = StatusGreen,
        compactMode = true,
        modifier = modifier
    )
}