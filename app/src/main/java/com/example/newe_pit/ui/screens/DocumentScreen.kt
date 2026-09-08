package com.example.newe_pit.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.newe_pit.data.model.DocStatusType
import com.example.newe_pit.data.model.VesselDocItem
import com.example.newe_pit.ui.theme.*


/**
 * Layar Dompet Dokumen Kapal (Document Wallet)
 * Fokus menampilkan daftar perizinan resmi KKP dengan filter kategori dan pratinjau PDF.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentScreen(
    onNavigateBack: () -> Unit = {}
) {
    var selectedCategoryTab by remember { mutableStateOf("Semua") }
    var selectedDocPreview by remember { mutableStateOf<VesselDocItem?>(null) }
    var toastMessage by remember { mutableStateOf<String?>(null) }

    // Data Simulasi Dokumen Siklus e-PIT KKP
    val documentList = remember {
        listOf(
            VesselDocItem(
                id = "DOC-SLO-01",
                title = "Surat Laik Operasi (SLO)",
                docNumber = "SLO/KKP-DJPSDKP/2026/0821",
                issuer = "Satwas PSDKP PPN Ambon",
                validityDate = "28 Agustus 2026",
                statusLabel = "Aktif",
                statusType = DocStatusType.ACTIVE,
                category = "Aktif",
                note = "Pemeriksaan fisik & kelengkapan VMS dinyatakan Laik Laut."
            ),
            VesselDocItem(
                id = "DOC-SPB-02",
                title = "Surat Persetujuan Berlayar (SPB)",
                docNumber = "SPB.PP/SYH-DOBO/VIII/2026/0194",
                issuer = "Syahbandar di Pelabuhan Perikanan Dobo",
                validityDate = "25 Agustus 2026",
                statusLabel = "Aktif",
                statusType = DocStatusType.ACTIVE,
                category = "Aktif",
                note = "Tujuan melaut: WPP-NRI 718 (Laut Arafura)."
            ),
            VesselDocItem(
                id = "DOC-PHP-03",
                title = "Tagihan PHP Pascaproduksi (PNBP)",
                docNumber = "BILL-KKP-8202608240091",
                issuer = "Direktorat Jenderal Perikanan Tangkap",
                validityDate = "Jatuh Tempo: 31 Agustus 2026",
                statusLabel = "Menunggu Bayar",
                statusType = DocStatusType.PENDING,
                category = "Proses",
                note = "Perhitungan mandiri LHP trip pendaratan Dobo.",
                paymentAmount = "Rp 4.250.000"
            ),
            VesselDocItem(
                id = "DOC-SIPI-04",
                title = "SIPI Pascaproduksi (Izin Tangkap)",
                docNumber = "12.24.0029.01.718.009",
                issuer = "Kementerian Kelautan dan Perikanan",
                validityDate = "19 September 2026",
                statusLabel = "Sisa 12 Hari",
                statusType = DocStatusType.EXPIRING,
                category = "Aktif",
                note = "Alat tangkap: Purse Seine Pelagis Kecil. Segera ajukan perpanjangan."
            ),
            VesselDocItem(
                id = "DOC-STBLKK-05",
                title = "STBLKK (Bukti Lapor Kedatangan)",
                docNumber = "STBLKK/PPN-AMB/2026/0711",
                issuer = "Pelabuhan Pendaratan PPN Ambon",
                validityDate = "Selesai (Trip #11)",
                statusLabel = "Arsip",
                statusType = DocStatusType.ACTIVE,
                category = "Riwayat",
                note = "Telah diverifikasi petugas pangkalan pendaratan ikan."
            )
        )
    }

    val filteredDocuments = remember(selectedCategoryTab) {
        if (selectedCategoryTab == "Semua") {
            documentList
        } else {
            documentList.filter { it.category == selectedCategoryTab }
        }
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
                        text = "Dokumen Kapal",
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
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Segmented Filter Tabs
                item {
                    val tabs = listOf("Semua", "Aktif", "Proses", "Riwayat")
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        tabs.forEach { tab ->
                            val isSelected = selectedCategoryTab == tab
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) ActionCyan else Color.Transparent)
                                    .clickable { selectedCategoryTab = tab }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = tab,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) PrimaryNavy else Color(0xFF64748B)
                                )
                            }
                        }
                    }
                }

                // Daftar Kartu Dokumen
                items(filteredDocuments) { doc ->
                    VesselDocumentCard(
                        doc = doc,
                        onViewPdf = { selectedDocPreview = doc },
                        onDownloadOffline = {
                            toastMessage = "Dokumen ${doc.title} disimpan secara offline."
                        }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(70.dp))
                }
            }
        }
    }

    // Modal Pratinjau Dokumen PDF
    selectedDocPreview?.let { doc ->
        DocumentPdfPreviewDialog(
            doc = doc,
            onDismiss = { selectedDocPreview = null },
            onDownloadSuccess = {
                selectedDocPreview = null
                toastMessage = "Berhasil mengunduh salinan PDF ${doc.title}."
            }
        )
    }

    // Floating Notification Banner
    toastMessage?.let { msg ->
        LaunchedEffect(msg) {
            kotlinx.coroutines.delay(2400)
            toastMessage = null
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Surface(
                color = PrimaryNavy,
                shape = RoundedCornerShape(12.dp),
                shadowElevation = 6.dp
            ) {
                Text(
                    text = msg,
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                )
            }
        }
    }
}

/**
 * Komponen Kartu Satuan Dokumen Perizinan
 */
@Composable
private fun VesselDocumentCard(
    doc: VesselDocItem,
    onViewPdf: () -> Unit,
    onDownloadOffline: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, CardBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(ActionCyan.copy(alpha = 0.12f), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = null,
                            tint = ActionCyan,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = doc.title,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryNavy
                        )
                        Text(
                            text = doc.docNumber,
                            fontSize = 11.sp,
                            color = Color(0xFF64748B),
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                val (badgeBg, badgeText) = when (doc.statusType) {
                    DocStatusType.ACTIVE -> Color(0xFFD1FAE5) to StatusGreen
                    DocStatusType.PENDING -> Color(0xFFFEF3C7) to Color(0xFFD97706)
                    DocStatusType.EXPIRING -> Color(0xFFFEE2E2) to StopRed
                }

                Surface(
                    color = badgeBg,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = doc.statusLabel,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = badgeText,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF8FAFC), RoundedCornerShape(10.dp))
                    .padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "Penerbit:", fontSize = 11.sp, color = Color(0xFF64748B))
                    Text(text = doc.issuer, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = PrimaryNavy)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "Masa Berlaku:", fontSize = 11.sp, color = Color(0xFF64748B))
                    Text(text = doc.validityDate, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PrimaryNavy)
                }
                if (doc.paymentAmount != null) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "Nominal Tagihan:", fontSize = 11.sp, color = Color(0xFF64748B))
                        Text(text = doc.paymentAmount, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFFD97706))
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onViewPdf,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryNavy)
                ) {
                    Icon(imageVector = Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Lihat PDF", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onDownloadOffline,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ActionCyan.copy(alpha = 0.2f),
                        contentColor = PrimaryNavy
                    )
                ) {
                    Icon(imageVector = Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(16.dp), tint = ActionCyan)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Simpan Offline", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

/**
 * Modal Dialog Pratinjau Dokumen PDF Resmi KKP
 */
@Composable
private fun DocumentPdfPreviewDialog(
    doc: VesselDocItem,
    onDismiss: () -> Unit,
    onDownloadSuccess: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Pratinjau Dokumen PDF",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryNavy
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Tutup")
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp),
                    color = Color(0xFFF1F5F9),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, CardBorder)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "KEMENTERIAN KELAUTAN DAN PERIKANAN",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryNavy,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = doc.title.uppercase(),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = ActionCyan,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "Nomor: ${doc.docNumber}",
                                fontSize = 9.sp,
                                color = Color(0xFF64748B),
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White, RoundedCornerShape(8.dp))
                                .padding(10.dp)
                        ) {
                            Text(text = "Kapal: KMN. DIGITALISASI 01 (48 GT)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text(text = "Penerbit: ${doc.issuer}", fontSize = 10.sp, color = Color(0xFF475569))
                            Text(text = "Masa Berlaku: ${doc.validityDate}", fontSize = 10.sp, color = Color(0xFF475569))
                            doc.note?.let {
                                Text(text = "Keterangan: $it", fontSize = 9.sp, color = Color(0xFF64748B))
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = StatusGreen, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Ditandatangani elektronik (BSrE BSSN)", fontSize = 9.sp, color = StatusGreen, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(text = "Tutup", fontSize = 12.sp)
                    }

                    Button(
                        onClick = onDownloadSuccess,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = ActionCyan, contentColor = PrimaryNavy),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Unduh PDF", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}