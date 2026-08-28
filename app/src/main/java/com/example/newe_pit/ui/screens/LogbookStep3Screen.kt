package com.example.newe_pit.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Phonelink
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
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
import com.example.newe_pit.ui.theme.ActionCyan
import com.example.newe_pit.ui.theme.CardBorder
import com.example.newe_pit.ui.theme.CardSurface
import com.example.newe_pit.ui.theme.PrimaryNavy
import com.example.newe_pit.ui.theme.StopRed
import com.example.newe_pit.ui.viewmodel.LogbookViewModel

/**
 * Layar Logbook Step 3: Form Input Spesies & Stepper Kontrol Berat/Jumlah.
 * Dilengkapi Shopping Cart Metaphor Badge untuk meninjau hasil tangkapan.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogbookStep3Screen(
    logbookViewModel: LogbookViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToCart: () -> Unit
) {
    val cartItems by logbookViewModel.cartItems.collectAsState()
    val userMessage by logbookViewModel.userMessage.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedSpecies by remember { mutableStateOf("Cakalang") }
    var weightKg by remember { mutableIntStateOf(300) }
    var quantityCount by remember { mutableIntStateOf(3) }

    val filteredSpecies = remember(searchQuery) {
        logbookViewModel.speciesCatalog.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
                    it.code.contains(searchQuery, ignoreCase = true)
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
                        text = "Detail Tangkapan",
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
                    Box(
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .clickable { onNavigateToCart() },
                        contentAlignment = Alignment.TopEnd
                    ) {
                        IconButton(onClick = onNavigateToCart) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = "Keranjang",
                                tint = PrimaryNavy,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                        if (cartItems.isNotEmpty()) {
                            Box(
                                modifier = Modifier
                                    .size(18.dp)
                                    .background(StopRed, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = cartItems.size.toString(),
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Cari jenis ikan...", fontSize = 13.sp) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = Color(0xFF94A3B8)
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = ActionCyan,
                    unfocusedBorderColor = CardBorder
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = "KATALOG SPESIES PILIHAN WPP 718",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF94A3B8),
                letterSpacing = 1.sp
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                filteredSpecies.take(2).forEach { species ->
                    val isSelected = selectedSpecies == species.name
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) Color(0xFFE0F7FA) else CardSurface
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .border(
                                1.5.dp,
                                if (isSelected) ActionCyan else CardBorder,
                                RoundedCornerShape(12.dp)
                            )
                            .clickable { selectedSpecies = species.name }
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Phonelink,
                                contentDescription = null,
                                tint = ActionCyan,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = species.name,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryNavy
                            )
                        }
                    }
                }
            }

            EPITCardContainer {
                Text(
                    text = "Spesies Terpilih: $selectedSpecies",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = ActionCyan
                )

                Spacer(modifier = Modifier.height(12.dp))

                EPITStepperControl(
                    label = "Berat (kg)",
                    value = weightKg,
                    onValueChange = { weightKg = it },
                    stepSizes = listOf(10, 50)
                )

                Spacer(modifier = Modifier.height(8.dp))

                EPITStepperControl(
                    label = "Jumlah (ekor)",
                    value = quantityCount,
                    onValueChange = { quantityCount = it },
                    stepSizes = listOf(1, 5)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            EPITPrimaryButton(
                text = "Tambah ke Daftar Tangkapan",
                icon = Icons.Default.AddShoppingCart,
                onClick = {
                    logbookViewModel.addItemToCart(selectedSpecies, weightKg, quantityCount)
                }
            )
        }
    }
}