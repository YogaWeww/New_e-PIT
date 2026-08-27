package com.example.newe_pit.data.repository

import com.example.newe_pit.data.model.CatchItem
import com.example.newe_pit.data.model.FishSpecies
import com.example.newe_pit.data.model.HaulRecord
import com.example.newe_pit.data.model.SyncStatus
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Repositori untuk Pengelolaan e-Logbook Perikanan.
 * Mengelola katalog spesies, pencatatan tawur/hauling, dan keranjang draft tangkapan.
 */
class LogbookRepository {

    // Dummy Katalog Spesies Ikan WPP 718
    private val speciesCatalog = listOf(
        FishSpecies("SKJ", "Cakalang", "Katsuwonus pelamis"),
        FishSpecies("BLT", "Tongkol Krakau", "Auxis thazard"),
        FishSpecies("YFT", "Tuna Madidihang", "Thunnus albacares"),
        FishSpecies("UDG", "Udang Windu", "Penaeus monodon"),
        FishSpecies("SWG", "Ikan Swanggi", "Priacanthus tayenus")
    )

    // Dummy Histori Hauling
    private val _haulHistory = MutableStateFlow(
        listOf(
            HaulRecord(
                id = 12,
                haulNumber = 12,
                timestampFormatted = "24 Agustus 2026",
                totalWeightKg = 1500,
                totalQuantityCount = 15,
                syncStatus = SyncStatus.SYNCED
            ),
            HaulRecord(
                id = 11,
                haulNumber = 11,
                timestampFormatted = "17 Agustus 2026",
                totalWeightKg = 2000,
                totalQuantityCount = 20,
                syncStatus = SyncStatus.SYNCED
            )
        )
    )
    val haulHistory: Flow<List<HaulRecord>> = _haulHistory.asStateFlow()

    // Draft item keranjang untuk tawur aktif
    private val _cartItems = MutableStateFlow<List<CatchItem>>(emptyList())
    val cartItems: Flow<List<CatchItem>> = _cartItems.asStateFlow()

    fun getSpeciesCatalog(): List<FishSpecies> = speciesCatalog

    fun addItemToCart(item: CatchItem) {
        val currentList = _cartItems.value.toMutableList()
        currentList.add(item)
        _cartItems.value = currentList
    }

    fun removeItemFromCart(itemId: String) {
        _cartItems.value = _cartItems.value.filter { it.id != itemId }
    }

    fun clearCart() {
        _cartItems.value = emptyList()
    }

    /**
     * Menyimpan data 1 tawur penuh secara permanen (simulasi simpan offline/online)
     */
    suspend fun saveCurrentHaul(): Result<HaulRecord> {
        delay(700)
        val items = _cartItems.value
        if (items.isEmpty()) {
            return Result.failure(Exception("Keranjang tangkapan masih kosong."))
        }

        val totalWeight = items.sumOf { it.weightKg }
        val totalQty = items.sumOf { it.quantityCount }
        val nextId = (_haulHistory.value.maxOfOrNull { it.id } ?: 0) + 1

        val newRecord = HaulRecord(
            id = nextId,
            haulNumber = nextId,
            timestampFormatted = "Hari ini, 08:30 WIB",
            totalWeightKg = totalWeight,
            totalQuantityCount = totalQty,
            catchItems = items,
            syncStatus = SyncStatus.SYNCED
        )

        val updatedHistory = listOf(newRecord) + _haulHistory.value
        _haulHistory.value = updatedHistory
        clearCart()

        return Result.success(newRecord)
    }
}