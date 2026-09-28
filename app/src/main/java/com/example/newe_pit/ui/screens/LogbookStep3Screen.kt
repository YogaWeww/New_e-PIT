package com.example.newe_pit.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SetMeal
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.newe_pit.ui.components.EPITCardContainer
import com.example.newe_pit.ui.components.EPITPrimaryButton
import com.example.newe_pit.ui.components.EPITStepperControl
import com.example.newe_pit.ui.components.EmptySearchState
import com.example.newe_pit.ui.theme.ActionCyan
import com.example.newe_pit.ui.theme.CardBorder
import com.example.newe_pit.ui.theme.PrimaryNavy
import com.example.newe_pit.ui.viewmodel.LogbookViewModel

/**
 * Layar Logbook Step 3: Pemilihan Spesies & Input Berat/Jumlah Tangkapan (Nilai Awal 0).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogbookStep3Screen(
    logbookViewModel: LogbookViewModel,
    onNavigateBack: () -> Unit,
    onOpenCart: () -> Unit
) {
    val speciesCatalog by logbookViewModel.speciesCatalog.collectAsState()
    val cartItems by logbookViewModel.cartItems.collectAsState()
    val userMessage by logbookViewModel.userMessage.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedSpecies by remember { mutableStateOf("Cakalang") }

    // Nilai Awal Berat & Jumlah Tangkapan Dihitung dari 0
    var weightKg by remember { mutableStateOf(0) }
    var quantityCount by remember { mutableStateOf(0) }

    val filteredSpecies = remember(searchQuery, speciesCatalog) {
        if (searchQuery.isBlank()) {
            speciesCatalog
        } else {
            speciesCatalog.filter {
                it.name.contains(searchQuery, ignoreCase = true) ||
                        it.code.contains(searchQuery, ignoreCase = true) ||
                        it.scientificName.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(userMessage) {
        userMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            logbookViewModel.clearUserMessage()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Detail & Cart Tangkapan",
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
                actions = {
                    Box(modifier = Modifier.padding(end = 8.dp)) {
                        IconButton(onClick = onOpenCart) {
                            Icon(
                                imageVector = Icons.Default.SetMeal,
                                contentDescription = "Keranjang",
                                tint = PrimaryNavy
                            )
                        }
                        if (cartItems.isNotEmpty()) {
                            Badge(
                                containerColor = ActionCyan,
                                contentColor = PrimaryNavy,
                                modifier = Modifier.align(Alignment.TopEnd)
                            ) {
                                Text(
                                    text = cartItems.size.toString(),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                )
                            }
                        }
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
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Cari jenis ikan / kode FAO...", fontSize = 13.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF64748B)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ActionCyan,
                    unfocusedBorderColor = CardBorder,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "KATALOG SPESIES WPP 718",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF94A3B8),
                    letterSpacing = 1.sp
                )
                Text(
                    text = "${filteredSpecies.size} Jenis",
                    fontSize = 11.sp,
                    color = ActionCyan,
                    fontWeight = FontWeight.SemiBold
                )
            }

            if (filteredSpecies.isEmpty()) {
                EmptySearchState(
                    searchQuery = searchQuery,
                    onResetSearchClick = { searchQuery = "" },
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(210.dp)
                        .background(Color.White, RoundedCornerShape(12.dp))
                        .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
                        .padding(8.dp)
                ) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(filteredSpecies) { species ->
                            val isSelected = selectedSpecies == species.name
                            Card(
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) Color(0xFFE0F7FA) else Color(0xFFF8FAFC)
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(
                                        1.5.dp,
                                        if (isSelected) ActionCyan else CardBorder,
                                        RoundedCornerShape(10.dp)
                                    )
                                    .clickable { selectedSpecies = species.name }
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.SetMeal,
                                        contentDescription = null,
                                        tint = ActionCyan,
                                        modifier = Modifier.size(22.dp)
                                    )
                                    Column {
                                        Text(
                                            text = species.name,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = PrimaryNavy
                                        )
                                        Text(
                                            text = species.code,
                                            fontSize = 10.sp,
                                            color = Color(0xFF64748B)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            EPITCardContainer {
                Text(
                    text = "INPUT TANGKAPAN: $selectedSpecies",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = PrimaryNavy
                )

                Spacer(modifier = Modifier.height(10.dp))

                EPITStepperControl(
                    label = "Estimasi Berat Tangkapan",
                    value = weightKg,
                    onValueChange = { weightKg = if (it < 0) 0 else it },
                    unit = "Kg",
                    quickSteps = listOf(10, 50)
                )

                Spacer(modifier = Modifier.height(12.dp))

                EPITStepperControl(
                    label = "Jumlah Tangkapan",
                    value = quantityCount,
                    onValueChange = { quantityCount = if (it < 0) 0 else it },
                    unit = "Ekor",
                    quickSteps = listOf(1, 10)
                )
            }

            EPITPrimaryButton(
                text = "Tambah ke Keranjang",
                enabled = weightKg > 0,
                onClick = {
                    logbookViewModel.addItemToCart(
                        speciesName = selectedSpecies,
                        weightKg = weightKg,
                        quantityCount = quantityCount
                    )
                    // Reset input setelah dimasukkan ke keranjang
                    weightKg = 0
                    quantityCount = 0
                }
            )

            if (cartItems.isNotEmpty()) {
                OutlinedButton(
                    onClick = onOpenCart,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Lihat Keranjang (${cartItems.size} Jenis | ${cartItems.sumOf { it.weightKg }} kg)",
                        fontWeight = FontWeight.Bold,
                        color = PrimaryNavy
                    )
                }
            }
        }
    }
}