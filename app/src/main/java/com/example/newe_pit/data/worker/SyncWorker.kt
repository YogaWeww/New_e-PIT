package com.example.newe_pit.data.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.newe_pit.data.local.EPITDatabase
import com.example.newe_pit.data.model.SyncStatus
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * CoroutineWorker yang berjalan di latar belakang untuk mengunggah
 * data hauling berstatus OFFLINE_PENDING dari Room DB ke Cloud Firestore,
 * lengkap dengan atribut `noregBkp` pengikat identitas kapal.
 */
class SyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    private val database = EPITDatabase.getDatabase(appContext)
    private val haulDao = database.haulDao()
    private val firestore = FirebaseFirestore.getInstance()

    override suspend fun doWork(): Result {
        return try {
            val pendingHauls = haulDao.getAllHaulsWithItems().filter {
                it.haulRecord.syncStatus == SyncStatus.OFFLINE_PENDING
            }

            if (pendingHauls.isEmpty()) {
                return Result.success()
            }

            for (haulWithItems in pendingHauls) {
                val haulRecord = haulWithItems.haulRecord
                val catchItems = haulWithItems.catchItems

                val haulMap = hashMapOf(
                    "noregBkp" to haulRecord.noregBkp,
                    "haulNumber" to haulRecord.haulNumber,
                    "timestampFormatted" to haulRecord.timestampFormatted,
                    "totalWeightKg" to haulRecord.totalWeightKg,
                    "totalQuantityCount" to haulRecord.totalQuantityCount,
                    "startLatitude" to haulRecord.startLatitude,
                    "startLongitude" to haulRecord.startLongitude,
                    "endLatitude" to haulRecord.endLatitude,
                    "endLongitude" to haulRecord.endLongitude,
                    "wppRegion" to haulRecord.wppRegion,
                    "soakDurationMinutes" to haulRecord.soakDurationMinutes,
                    "catchItems" to catchItems.map { item ->
                        mapOf(
                            "id" to item.id,
                            "speciesName" to item.speciesName,
                            "weightKg" to item.weightKg,
                            "quantityCount" to item.quantityCount
                        )
                    }
                )

                // Simpan ke koleksi 'haul_records' di Cloud Firestore
                firestore.collection("haul_records")
                    .document("HAUL_${haulRecord.id}")
                    .set(haulMap)
                    .await()

                // Perbarui status di Room DB lokal menjadi SYNCED
                haulDao.updateSyncStatus(haulRecord.id, SyncStatus.SYNCED)
            }

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}