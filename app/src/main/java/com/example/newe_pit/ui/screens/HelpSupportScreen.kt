package com.example.newe_pit.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.newe_pit.ui.components.EPITCardContainer
import com.example.newe_pit.ui.theme.*

/**
 * Layar Bantuan & Helpdesk Resmi e-PIT KKP
 * Menyediakan tautan cepat ke WhatsApp Helpdesk KKP dan email resmi pit@kkp.go.id.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpSupportScreen(
    onNavigateBack: () -> Unit = {}
) {
    val context = LocalContext.current

    // Kontak resmi dari referensi KKP
    val waNumber1 = "+6281808076775"
    val waNumber2 = "+6285782877383"
    val emailOfficial = "pit@kkp.go.id"

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = NeutralCanvas
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = {
                    Text(
                        text = "Bantuan & Helpdesk",
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
                // Banner Informasi Pusat Bantuan
                item {
                    EPITCardContainer {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .background(ActionCyan.copy(alpha = 0.15f), RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SupportAgent,
                                    contentDescription = null,
                                    tint = ActionCyan,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "Helpdesk e-PIT KKP",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryNavy
                                )
                                Text(
                                    text = "Siaga mendampingi operasional pelayaran nelayan dan pemilik kapal",
                                    fontSize = 11.sp,
                                    color = Color(0xFF64748B),
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }
                }

                // Bagian WhatsApp Resmi
                item {
                    Text(
                        text = "LAYANAN WHATSAPP HELPDESK",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF94A3B8),
                        letterSpacing = 1.sp
                    )
                }

                item {
                    SupportContactCard(
                        title = "WhatsApp Helpdesk 1",
                        value = "+62 818 0807 6775",
                        badgeLabel = "Online",
                        icon = Icons.Default.Chat,
                        iconTint = StatusGreen,
                        onClick = {
                            val uri = Uri.parse("https://wa.me/6281808076775?text=Halo%20Helpdesk%20e-PIT%20KKP,%20saya%20memerlukan%20bantuan%20terkait%20kapal%20saya.")
                            context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                        }
                    )
                }

                item {
                    SupportContactCard(
                        title = "WhatsApp Helpdesk 2",
                        value = "+62 857 8287 7383",
                        badgeLabel = "Online",
                        icon = Icons.Default.Chat,
                        iconTint = StatusGreen,
                        onClick = {
                            val uri = Uri.parse("https://wa.me/6285782877383?text=Halo%20Helpdesk%20e-PIT%20KKP,%20saya%20memerlukan%20bantuan%20terkait%20kapal%20saya.")
                            context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                        }
                    )
                }

                // Bagian Email Resmi
                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "LAYANAN SURAT ELEKTRONIK (EMAIL)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF94A3B8),
                        letterSpacing = 1.sp
                    )
                }

                item {
                    SupportContactCard(
                        title = "Email Resmi e-PIT",
                        value = emailOfficial,
                        badgeLabel = "24 Jam",
                        icon = Icons.Default.Email,
                        iconTint = PrimaryNavy,
                        onClick = {
                            val intent = Intent(Intent.ACTION_SENDTO).apply {
                                data = Uri.parse("mailto:$emailOfficial")
                                putExtra(Intent.EXTRA_SUBJECT, "Permohonan Bantuan Aplikasi e-PIT Mobile")
                            }
                            context.startActivity(intent)
                        }
                    )
                }

                // Card Jam Operasional Pelabuhan
                item {
                    Spacer(modifier = Modifier.height(6.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9)),
                        border = BorderStroke(1.dp, CardBorder)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Schedule,
                                    contentDescription = null,
                                    tint = PrimaryNavy,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "Jam Operasional Layanan",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryNavy
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "• Hari Kerja: Senin – Jumat (08:00 – 16:30 WIB)\n• Pelaporan Kedatangan Darurat / Syahbandar: Siaga 24 Jam di Pelabuhan Pangkalan.",
                                fontSize = 11.sp,
                                color = Color(0xFF475569),
                                lineHeight = 16.sp
                            )
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }
}

@Composable
private fun SupportContactCard(
    title: String,
    value: String,
    badgeLabel: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
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
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(iconTint.copy(alpha = 0.12f), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Column {
                    Text(
                        text = title,
                        fontSize = 11.sp,
                        color = Color(0xFF64748B)
                    )
                    Text(
                        text = value,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryNavy
                    )
                }
            }

            Surface(
                color = if (badgeLabel == "Online") Color(0xFFD1FAE5) else Color(0xFFE2E8F0),
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(
                    text = badgeLabel,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (badgeLabel == "Online") StatusGreen else PrimaryNavy,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }
        }
    }
}