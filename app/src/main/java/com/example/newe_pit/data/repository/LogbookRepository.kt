package com.example.newe_pit.data.repository

import android.content.Context
import com.example.newe_pit.data.local.CatchItemEntity
import com.example.newe_pit.data.local.EPITDatabase
import com.example.newe_pit.data.local.HaulDao
import com.example.newe_pit.data.local.HaulRecordEntity
import com.example.newe_pit.data.local.HaulWithCatchItems
import com.example.newe_pit.data.model.CatchItem
import com.example.newe_pit.data.model.FishSpecies
import com.example.newe_pit.data.model.HaulRecord
import com.example.newe_pit.data.model.SyncStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Repositori Logbook Perikanan terintegrasi dengan Room Database (SQLite Lokal).
 * Mengelola keranjang tangkapan di memori dan penyimpanan permanen ke Room DB.
 */
class LogbookRepository(
    private val haulDao: HaulDao? = null
) {

    companion object {
        @Volatile
        private var INSTANCE: LogbookRepository? = null

        val instance: LogbookRepository
            get() = INSTANCE ?: synchronized(this) {
                val instance = LogbookRepository()
                INSTANCE = instance
                instance
            }

        /**
         * Inisialisasi Repositori dengan Context Aplikasi untuk menghubungkan Room Database
         */
        fun initialize(context: Context): LogbookRepository {
            return INSTANCE ?: synchronized(this) {
                val db = EPITDatabase.getDatabase(context)
                val instance = LogbookRepository(db.haulDao())
                INSTANCE = instance
                instance
            }
        }
    }

    // Katalog 12 Spesies Ikan WPP-NRI 718
    private val speciesCatalog = listOf(
        FishSpecies("SKJ", "Cakalang", "Katsuwonus pelamis"),
        FishSpecies("BLT", "Tongkol Krakau", "Auxis thazard"),
        FishSpecies("YFT", "Tuna Madidihang", "Thunnus albacares"),
        FishSpecies("BET", "Tuna Mata Besar", "Thunnus obesus"),
        FishSpecies("UDG", "Udang Windu", "Penaeus monodon"),
        FishSpecies("SWG", "Ikan Swanggi", "Priacanthus tayenus"),
        FishSpecies("TGR", "Tenggiri", "Scomberomorus commerson"),
        FishSpecies("KRP", "Kerapu Macan", "Epinephelus fuscoguttatus"),
        FishSpecies("LYG", "Ikan Layang", "Decapterus russelli"),
        FishSpecies("LMR", "Ikan Lemuru", "Sardinella lemuru"),
        FishSpecies("BWL", "Bawal Hitam", "Parastromateus niger"),
        FishSpecies("KPA", "Kakap Merah", "Lutjanus campechanus")
    )

    // Draft keranjang tangkapan untuk tawur aktif (In-Memory Draft)
    private val _cartItems = MutableStateFlow<List<CatchItem>>(emptyList())
    val cartItems: StateFlow<List<CatchItem>> = _cartItems.asStateFlow()

    // Internal In-Memory Fallback jika Room belum diinisialisasi
    private val _fallbackHaulHistory = MutableStateFlow<List<HaulRecord>>(emptyList())

    /**
     * Observable Stream untuk Riwayat Hauling (Membaca secara real-time dari Room DB)
     */
    val haulHistory: Flow<List<HaulRecord>> = haulDao?.getAllHaulsWithItemsFlow()?.map { list ->
        list.map { it.toDomainModel() }
    } ?: _fallbackHaulHistory.asStateFlow()

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

    fun clearHaulHistory() {
        _fallbackHaulHistory.value = emptyList()
    }

    /**
     * Menyimpan data 1 tawur utuh ke Room Database (SQLite Lokal HP)
     */
    suspend fun saveCurrentHaul(
        startLat: Double = -6.178564,
        startLong: Double = 106.831934,
        endLat: Double = -6.179120,
        endLong: Double = 106.834110,
        soakMinutes: Long = 135L
    ): Result<HaulRecord> {
        val items = _cartItems.value
        if (items.isEmpty()) {
            return Result.failure(Exception("Keranjang tangkapan masih kosong."))
        }

        val totalWeight = items.sumOf { it.weightKg }
        val totalQty = items.sumOf { it.quantityCount }
        val currentTimeStr = SimpleDateFormat("dd MMMM yyyy, HH:mm 'WIB'", Locale("id", "ID")).format(Date())

        return if (haulDao != null) {
            try {
                // 1. Hitung Nomor Haul Berikutnya dari Room DB
                val currentHauls = haulDao.getAllHaulsWithItems()
                val nextHaulNumber = (currentHauls.maxOfOrNull { it.haulRecord.haulNumber } ?: 0) + 1

                // 2. Buat Entitas Utama Haul Record
                val haulEntity = HaulRecordEntity(
                    haulNumber = nextHaulNumber,
                    timestampFormatted = currentTimeStr,
                    totalWeightKg = totalWeight,
                    totalQuantityCount = totalQty,
                    startLatitude = startLat,
                    startLongitude = startLong,
                    endLatitude = endLat,
                    endLongitude = endLong,
                    wppRegion = "WPP-NRI 718",
                    soakDurationMinutes = soakMinutes,
                    syncStatus = SyncStatus.OFFLINE_PENDING
                )

                // 3. Simpan Haul Entity ke Room & Dapatkan Primary Key ID
                val insertedHaulId = haulDao.insertHaulRecord(haulEntity)

                // 4. Map & Simpan Item Tangkapan terkait
                val catchEntities = items.map { item ->
                    CatchItemEntity(
                        id = item.id,
                        haulRecordId = insertedHaulId,
                        speciesName = item.speciesName,
                        weightKg = item.weightKg,
                        quantityCount = item.quantityCount
                    )
                }
                haulDao.insertCatchItems(catchEntities)

                // 5. Model Domain Hasil
                val savedRecord = HaulRecord(
                    id = insertedHaulId.toInt(),
                    haulNumber = nextHaulNumber,
                    timestampFormatted = currentTimeStr,
                    totalWeightKg = totalWeight,
                    totalQuantityCount = totalQty,
                    catchItems = items,
                    startLatitude = startLat,
                    startLongitude = startLong,
                    endLatitude = endLat,
                    endLongitude = endLong,
                    wppRegion = "WPP-NRI 718",
                    soakDurationMinutes = soakMinutes,
                    syncStatus = SyncStatus.OFFLINE_PENDING
                )

                clearCart()
                Result.success(savedRecord)
            } catch (e: Exception) {
                Result.failure(e)
            }
        } else {
            // Fallback In-Memory jika DB belum diinisialisasi
            val nextId = (_fallbackHaulHistory.value.maxOfOrNull { it.id } ?: 0) + 1
            val newRecord = HaulRecord(
                id = nextId,
                haulNumber = nextId,
                timestampFormatted = currentTimeStr,
                totalWeightKg = totalWeight,
                totalQuantityCount = totalQty,
                catchItems = items,
                startLatitude = startLat,
                startLongitude = startLong,
                endLatitude = endLat,
                endLongitude = endLong,
                wppRegion = "WPP-NRI 718",
                soakDurationMinutes = soakMinutes,
                syncStatus = SyncStatus.OFFLINE_PENDING
            )
            _fallbackHaulHistory.value = listOf(newRecord) + _fallbackHaulHistory.value
            clearCart()
            Result.success(newRecord)
        }
    }

    /**
     * Konversi dari Room Relation ke Domain Model HaulRecord
     */
    private fun HaulWithCatchItems.toDomainModel(): HaulRecord {
        return HaulRecord(
            id = haulRecord.id.toInt(),
            haulNumber = haulRecord.haulNumber,
            timestampFormatted = haulRecord.timestampFormatted,
            totalWeightKg = haulRecord.totalWeightKg,
            totalQuantityCount = haulRecord.totalQuantityCount,
            catchItems = catchItems.map { entity ->
                CatchItem(
                    id = entity.id,
                    speciesName = entity.speciesName,
                    weightKg = entity.weightKg,
                    quantityCount = entity.quantityCount
                )
            },
            startLatitude = haulRecord.startLatitude,
            startLongitude = haulRecord.startLongitude,
            endLatitude = haulRecord.endLatitude,
            endLongitude = haulRecord.endLongitude,
            wppRegion = haulRecord.wppRegion,
            soakDurationMinutes = haulRecord.soakDurationMinutes,
            syncStatus = haulRecord.syncStatus
        )
    }
}