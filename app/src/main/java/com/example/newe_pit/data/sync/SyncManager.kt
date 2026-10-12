package com.example.newe_pit.data.sync

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.example.newe_pit.data.worker.SyncWorker
import java.util.concurrent.TimeUnit

/**
 * SyncManager: Pengelola dan penjadwal tugas pengiriman latar belakang (WorkManager).
 */
class SyncManager(context: Context) {

    private val workManager = WorkManager.getInstance(context.applicationContext)

    companion object {
        const val SYNC_WORK_NAME = "epit_offline_hauls_sync_work"
    }

    /**
     * Menjadwalkan atau memicu proses sinkronisasi antrean data hauling.
     */
    fun scheduleSyncNow() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncWorkRequest = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(constraints)
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                WorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .build()

        workManager.enqueueUniqueWork(
            SYNC_WORK_NAME,
            ExistingWorkPolicy.KEEP,
            syncWorkRequest
        )
    }

    fun cancelSync() {
        workManager.cancelUniqueWork(SYNC_WORK_NAME)
    }
}