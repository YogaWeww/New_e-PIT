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
 * Repositori Logbook Perikanan terintegrasi dengan Room Database.
 * Mengikat seluruh pencatatan tawur dengan `noregBkp` akun kapal yang aktif.
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

        fun initialize(context: Context): LogbookRepository {
            return INSTANCE ?: synchronized(this) {
                val db = EPITDatabase.getDatabase(context)
                val instance = LogbookRepository(db.haulDao())
                INSTANCE = instance
                instance
            }
        }
    }

    private val _availableSpecies = MutableStateFlow(
        listOf(
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
    )
    val availableSpecies: StateFlow<List<FishSpecies>> = _availableSpecies.asStateFlow()

    private val _currentCart = MutableStateFlow<List<CatchItem>>(emptyList())
    val currentCart: StateFlow<List<CatchItem>> = _currentCart.asStateFlow()

    private val _fallbackHaulHistory = MutableStateFlow<List<HaulRecord>>(emptyList())

    val haulHistory: Flow<List<HaulRecord>> = haulDao?.getAllHaulsWithItemsFlow()?.map { list ->
        list.map { it.toDomainModel() }
    } ?: _fallbackHaulHistory.asStateFlow()

    fun addItemToCart(item: CatchItem) {
        val currentList = _currentCart.value.toMutableList()
        currentList.add(item)
        _currentCart.value = currentList
    }

    fun removeItemFromCart(itemId: String) {
        _currentCart.value = _currentCart.value.filter { it.id != itemId }
    }

    fun clearCart() {
        _currentCart.value = emptyList()
    }

    fun clearHaulHistory() {
        _fallbackHaulHistory.value = emptyList()
    }

    /**
     * Menyimpan data 1 tawur utuh ke Room Database (SQLite Lokal HP)
     * Mengikat pencatatan dengan `noregBkp` akun kapal.
     */
    suspend fun saveCurrentHaul(
        noregBkp: String,
        startLat: Double = 0.0,
        startLong: Double = 0.0,
        endLat: Double = 0.0,
        endLong: Double = 0.0,
        soakMinutes: Long = 0L
    ): Result<HaulRecord> {
        val items = _currentCart.value
        if (items.isEmpty()) {
            return Result.failure(Exception("Keranjang tangkapan masih kosong."))
        }

        val totalWeight = items.sumOf { it.weightKg }
        val totalQty = items.sumOf { it.quantityCount }
        val currentTimeStr = SimpleDateFormat("dd MMMM yyyy, HH:mm 'WIB'", Locale("id", "ID")).format(Date())

        return if (haulDao != null) {
            try {
                val currentHauls = haulDao.getAllHaulsWithItems()
                val nextHaulNumber = (currentHauls.maxOfOrNull { it.haulRecord.haulNumber } ?: 0) + 1

                val haulEntity = HaulRecordEntity(
                    noregBkp = noregBkp,
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

                val insertedHaulId = haulDao.insertHaulRecord(haulEntity)

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

                val savedRecord = HaulRecord(
                    id = insertedHaulId.toInt(),
                    noregBkp = noregBkp,
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
            val nextId = (_fallbackHaulHistory.value.maxOfOrNull { it.id } ?: 0) + 1
            val newRecord = HaulRecord(
                id = nextId,
                noregBkp = noregBkp,
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

    private fun HaulWithCatchItems.toDomainModel(): HaulRecord {
        return HaulRecord(
            id = haulRecord.id.toInt(),
            noregBkp = haulRecord.noregBkp,
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