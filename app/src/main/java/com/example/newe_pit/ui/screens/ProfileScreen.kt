package com.example.newe_pit.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.newe_pit.R
import com.example.newe_pit.data.model.UserRole
import com.example.newe_pit.ui.theme.*
import com.example.newe_pit.ui.viewmodel.AuthViewModel

/**
 * Layar Profil Kapal e-PIT Mobile
 * Menampilkan data teknis kapal, perizinan, mesin, dimensi,
 * serta akses cepat menuju Link Layanan Resmi KKP & Pusat Bantuan Helpdesk.
 */
@Composable
fun ProfileScreen(
    authViewModel: AuthViewModel,
    onNavigateToServiceLinks: () -> Unit = {},
    onNavigateToHelpSupport: () -> Unit = {},
    onSignOut: () -> Unit = {}
) {
    val userSession by authViewModel.userSession.collectAsState()
    var showLogoutDialog by remember { mutableStateOf(false) }

    // Data kapal (sesuai dokumen BKP & SIPI resmi di pangkalan)
    val vesselName = if (userSession.vesselName.isNotBlank()) userSession.vesselName else "KMN. DIGITALISASI 01"
    val noregBkp = if (userSession.noregBkp.isNotBlank()) userSession.noregBkp else "A000029"
    val userRoleLabel = when (userSession.role) {
        UserRole.CAPTAIN -> "Nakhoda Aktif"
        UserRole.SHIP_OWNER -> "Pemilik Kapal"
        else -> "Nakhoda Kapal"
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = NeutralCanvas
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top App Bar
            Surface(
                color = Color.White,
                shadowElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Profil Kapal",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryNavy
                    )
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // 1. KARTU HEADER: FOTO & IDENTITAS KAPAL
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, CardBorder),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.gambar_kapal),
                                    contentDescription = "Foto Kapal",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(68.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .border(1.5.dp, ActionCyan, RoundedCornerShape(14.dp))
                                )

                                Spacer(modifier = Modifier.width(14.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Surface(
                                        color = ActionCyan.copy(alpha = 0.15f),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = userRoleLabel,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = PrimaryNavy,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Text(
                                        text = vesselName,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = PrimaryNavy
                                    )

                                    Text(
                                        text = "eBKP: $noregBkp",
                                        fontSize = 12.sp,
                                        color = Color(0xFF64748B),
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }
                    }
                }

                // 2. KARTU IDENTITAS & LEGALITAS PERIZINAN
                item {
                    ProfileSectionCard(
                        title = "Legalitas & Perizinan",
                        icon = Icons.Default.Description
                    ) {
                        ProfileInfoRow(label = "Nama Kapal", value = vesselName)
                        ProfileInfoRow(label = "No. Izin (SIPI)", value = "12.24.0029.01.718.009", isMonospace = true)
                        ProfileInfoRow(label = "Tanda Selar", value = "GT. 48 No. 1234/Bc", isMonospace = true)
                        ProfileInfoRow(label = "Nama Pemilik", value = "PT. BA***")
                        ProfileInfoRow(label = "Nama Nakhoda", value = "SUPRIYANTO")
                    }
                }

                // 3. KARTU OPERASIONAL & WILAYAH
                item {
                    ProfileSectionCard(
                        title = "Operasional Pangkalan",
                        icon = Icons.Default.Anchor
                    ) {
                        ProfileInfoRow(label = "Pelabuhan Pangkalan", value = "PPN Ambon")
                        ProfileInfoRow(label = "Wilayah Pengelolaan", value = "WPP-NRI 718 (Arafura)")
                        ProfileInfoRow(label = "Jenis Alat Tangkap", value = "Purse Seine Pelagis Kecil")
                    }
                }

                // 4. KARTU SPESIFIKASI TEKNIS & MESIN
                item {
                    ProfileSectionCard(
                        title = "Spesifikasi Fisik & Mesin",
                        icon = Icons.Default.Build
                    ) {
                        ProfileInfoRow(label = "Ukuran Tonase", value = "48 GT")
                        ProfileInfoRow(label = "Dimensi (P x L x D)", value = "18.5 m x 5.2 m x 2.1 m")
                        ProfileInfoRow(label = "Daya Mesin Pokok", value = "350 HP")
                        ProfileInfoRow(label = "Merk Mesin", value = "Yanmar 6HYM-WET")
                    }
                }

                // 5. KARTU PUSAT BANTUAN & LAYANAN RESMI
                item {
                    ProfileSectionCard(
                        title = "Pusat Bantuan & Layanan",
                        icon = Icons.Default.HelpCenter
                    ) {
                        ProfileNavigationRow(
                            title = "Link Layanan Resmi",
                            subtitle = "Portal KKP, perizinan SILAT, & Web RFMOs",
                            icon = Icons.Default.Public,
                            onClick = onNavigateToServiceLinks
                        )

                        HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

                        ProfileNavigationRow(
                            title = "Bantuan & Helpdesk",
                            subtitle = "Hubungi Helpdesk KKP via WhatsApp & Email",
                            icon = Icons.Default.SupportAgent,
                            onClick = onNavigateToHelpSupport
                        )
                    }
                }

                // 6. TOMBOL KELUAR (LOGOUT)
                item {
                    Spacer(modifier = Modifier.height(4.dp))

                    Button(
                        onClick = { showLogoutDialog = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = StopRed.copy(alpha = 0.12f),
                            contentColor = StopRed
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Keluar",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Keluar Akun",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "e-PIT Mobile v1.0.0 — DJPT KKP",
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(70.dp))
                }
            }
        }
    }

    // Modal Dialog Konfirmasi Keluar
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            icon = {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .background(StopRed.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Logout,
                        contentDescription = null,
                        tint = StopRed,
                        modifier = Modifier.size(24.dp)
                    )
                }
            },
            title = {
                Text(
                    text = "Konfirmasi Keluar",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryNavy
                )
            },
            text = {
                Text(
                    text = "Pastikan seluruh data hasil tangkapan di laut telah disimpan atau disinkronkan sebelum keluar dari akun kapal.",
                    fontSize = 12.sp,
                    color = Color(0xFF64748B),
                    lineHeight = 18.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        authViewModel.signOut()
                        onSignOut()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StopRed),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(text = "Ya, Keluar", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showLogoutDialog = false },
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
 * Kontainer Kartu Bagian Profil
 */
@Composable
private fun ProfileSectionCard(
    title: String,
    icon: ImageVector,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, CardBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 10.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = ActionCyan,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryNavy
                )
            }

            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

            Column(
                modifier = Modifier.padding(top = 10.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                content = content
            )
        }
    }
}

/**
 * Baris Navigasi Menu Profil
 */
@Composable
private fun ProfileNavigationRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(ActionCyan.copy(alpha = 0.12f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = ActionCyan,
                    modifier = Modifier.size(20.dp)
                )
            }

            Column {
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryNavy
                )
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = Color(0xFF64748B)
                )
            }
        }

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = Color(0xFFCBD5E1),
            modifier = Modifier.size(20.dp)
        )
    }
}

/**
 * Baris Informasi Kunci-Nilai (Label & Value)
 */
@Composable
private fun ProfileInfoRow(
    label: String,
    value: String,
    isMonospace: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color(0xFF64748B)
        )

        Text(
            text = value,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = PrimaryNavy,
            fontFamily = if (isMonospace) FontFamily.Monospace else FontFamily.Default
        )
    }
}