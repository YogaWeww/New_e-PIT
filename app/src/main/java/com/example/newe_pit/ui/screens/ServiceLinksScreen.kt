package com.example.newe_pit.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.newe_pit.data.model.ServiceLinkItem
import com.example.newe_pit.ui.theme.*

/**
 * Layar Link Layanan Resmi KKP & Organisasi Perikanan Regional (RFMOs)
 * Menyediakan tautan langsung ke portal perizinan, satu data, serta RFMOs.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceLinksScreen(
    onNavigateBack: () -> Unit = {}
) {
    val context = LocalContext.current

    // Data Tautan Resmi berdasarkan Panduan e-PIT KKP
    val serviceLinks = remember {
        listOf(
            // Kategori Web KKP
            ServiceLinkItem(
                id = "KKP-01",
                title = "Kementerian Kelautan dan Perikanan (KKP)",
                category = "Web KKP",
                url = "https://kkp.go.id",
                description = "Portal informasi resmi kementerian dan kebijakan PIT nasional"
            ),
            ServiceLinkItem(
                id = "KKP-02",
                title = "Layanan Kapal Perikanan",
                category = "Web KKP",
                url = "https://kapal.kkp.go.id",
                description = "Layanan pendaftaran buku kapal dan izin kelaikan operasional"
            ),
            ServiceLinkItem(
                id = "KKP-03",
                title = "Perizinan Usaha Penangkapan Ikan (SILAT)",
                category = "Web KKP",
                url = "https://perizinan.kkp.go.id",
                description = "Pengajuan dan perpanjangan SIPI / SIKPI terintegrasi OSS"
            ),
            ServiceLinkItem(
                id = "KKP-04",
                title = "Satu Data KKP (One Data KKP)",
                category = "Web KKP",
                url = "https://satudata.kkp.go.id",
                description = "Portal basis data statistik dan potensi produksi perikanan"
            ),

            // Kategori Web RFMOs (Regional Fisheries Management Organisations)
            ServiceLinkItem(
                id = "RFMO-01",
                title = "Commission for the Conservation of Southern Bluefin Tuna (CCSBT)",
                category = "Web RFMOs",
                url = "https://www.ccsbt.org",
                description = "Regulasi konservasi penangkapan tuna sirip biru selatan"
            ),
            ServiceLinkItem(
                id = "RFMO-02",
                title = "Indian Ocean Tuna Commission (IOTC)",
                category = "Web RFMOs",
                url = "https://iotc.org",
                description = "Pengelolaan perikanan tuna kawasan Samudera Hindia"
            ),
            ServiceLinkItem(
                id = "RFMO-03",
                title = "Inter-American Tropical Tuna Commission (IATTC)",
                category = "Web RFMOs",
                url = "https://www.iattc.org",
                description = "Konvensi konservasi tuna laut tropis dan perairan pasifik"
            )
        )
    }

    val kkpLinks = remember(serviceLinks) { serviceLinks.filter { it.category == "Web KKP" } }
    val rfmoLinks = remember(serviceLinks) { serviceLinks.filter { it.category == "Web RFMOs" } }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = NeutralCanvas
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = {
                    Text(
                        text = "Link Layanan",
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

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header Bagian Web KKP
                item {
                    Text(
                        text = "PORTAL RESMI KKP",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF94A3B8),
                        letterSpacing = 1.sp
                    )
                }

                items(kkpLinks) { link ->
                    ServiceLinkCard(
                        item = link,
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link.url))
                            context.startActivity(intent)
                        }
                    )
                }

                // Header Bagian RFMOs
                item {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "ORGANISASI PENGELOLA PERIKANAN REGIONAL (RFMOs)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF94A3B8),
                        letterSpacing = 1.sp
                    )
                }

                items(rfmoLinks) { link ->
                    ServiceLinkCard(
                        item = link,
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link.url))
                            context.startActivity(intent)
                        }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }
}

@Composable
private fun ServiceLinkCard(
    item: ServiceLinkItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, CardBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .background(ActionCyan.copy(alpha = 0.12f), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (item.category == "Web KKP") Icons.Default.Link else Icons.Default.Public,
                        contentDescription = null,
                        tint = ActionCyan,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Column {
                    Text(
                        text = item.title,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryNavy
                    )
                    item.description?.let { desc ->
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = desc,
                            fontSize = 11.sp,
                            color = Color(0xFF64748B),
                            lineHeight = 15.sp
                        )
                    }
                }
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                contentDescription = "Buka Tautan",
                tint = Color(0xFF94A3B8),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}