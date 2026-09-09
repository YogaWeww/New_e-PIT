package com.example.newe_pit.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.newe_pit.data.model.QuotaDetailData
import com.example.newe_pit.ui.components.EPITCardContainer
import com.example.newe_pit.ui.theme.*

/**
 * Layar Rincian Kuota PIT (Penangkapan Ikan Terukur)
 * Mengadaptasi tampilan resmi e-PIT dengan kartu metrik Kuota, Realisasi, dan Sisa,
 * serta rincian kontribusi spesies.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuotaDetailScreen(
    onNavigateBack: () -> Unit = {}
) {
    val quotaData = remember { QuotaDetailData() }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = NeutralCanvas
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = {
                    Text(
                        text = "Rincian Kuota Tangkap",
                        fontSize = 17.sp,
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

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Info Wilayah & Legalitas Kuota
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = PrimaryNavy),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = null,
                                tint = ActionCyan,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = quotaData.wppArea,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Kategori: ${quotaData.quotaCategory}",
                            fontSize = 11.sp,
                            color = Color(0xFF94A3B8)
                        )
                        Text(
                            text = "Masa Berlaku: ${quotaData.validityPeriod}",
                            fontSize = 11.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }
                }

                // 1. Kartu Kuota (Biru Utama)
                QuotaMetricCard(
                    title = "KUOTA TAHUNAN",
                    value = "${quotaData.totalQuotaKg.toInt()} Kg",
                    bgColor = Color(0xFF3B82F6),
                    textColor = Color.White
                )

                // 2. Kartu Realisasi (Biru Toska / Cyan)
                QuotaMetricCard(
                    title = "REALISASI TANGKAPAN",
                    value = "${quotaData.realizedQuotaKg.toInt()} Kg",
                    bgColor = Color(0xFF0EA5E9),
                    textColor = Color.White
                )

                // 3. Kartu Sisa Kuota (Hijau Status)
                QuotaMetricCard(
                    title = "SISA KUOTA TERSISA",
                    value = "${quotaData.remainingQuotaKg.toInt()} Kg",
                    bgColor = StatusGreen,
                    textColor = Color.White
                )

                // Rincian Breakdown Spesies Penyerap Kuota
                EPITCardContainer {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PieChart,
                            contentDescription = null,
                            tint = ActionCyan,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "KONTRIBUSI SPESIES PENYERAP KUOTA",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF94A3B8),
                            letterSpacing = 1.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    quotaData.speciesBreakdown.forEach { item ->
                        Column(modifier = Modifier.padding(vertical = 4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = item.speciesName, fontSize = 12.sp, color = PrimaryNavy, fontWeight = FontWeight.Medium)
                                Text(text = "${item.weightKg.toInt()} kg (${item.percentage}%)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PrimaryNavy)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            LinearProgressIndicator(
                                progress = { (item.percentage / 100).toFloat() },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = ActionCyan,
                                trackColor = Color(0xFFE2E8F0)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}

@Composable
private fun QuotaMetricCard(
    title: String,
    value: String,
    bgColor: Color,
    textColor: Color
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = textColor.copy(alpha = 0.85f),
                letterSpacing = 1.2.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = textColor
            )
        }
    }
}