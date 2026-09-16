package com.example.newe_pit.data.remote

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * Helper class untuk mengisi data dummy kapal ke Cloud Firestore secara otomatis.
 */
object FirestoreSeeder {

    /**
     * Mengunggah beberapa data kapal contoh ke koleksi 'vessels' di Firestore.
     */
    suspend fun seedVesselsData(): Result<String> {
        val db = FirebaseFirestore.getInstance()

        // Daftar data kapal dummy yang akan diunggah ke cloud
        val dummyVessels = listOf(
            mapOf(
                "bkpNumber" to "A00029",
                "vesselName" to "KMN. DIGITALISASI 001",
                "grossTonnage" to 48,
                "ownerName" to "PT. BA***",
                "sloActive" to true,
                "spbActive" to true,
                "totalQuotaKg" to 10000.0,
                "remainingQuotaKg" to 5101.0,
                "isActivated" to false
            ),
            mapOf(
                "bkpNumber" to "A00030",
                "vesselName" to "KMN. SAMUDERA BAHARI 02",
                "grossTonnage" to 60,
                "ownerName" to "PT. LAUTAN EMAS",
                "sloActive" to true,
                "spbActive" to true,
                "totalQuotaKg" to 15000.0,
                "remainingQuotaKg" to 12000.0,
                "isActivated" to false
            ),
            mapOf(
                "bkpNumber" to "B00088",
                "vesselName" to "KMN. BINTANG LAUT 08",
                "grossTonnage" to 30,
                "ownerName" to "CV. MARITIM JAYA",
                "sloActive" to true,
                "spbActive" to false,
                "totalQuotaKg" to 8000.0,
                "remainingQuotaKg" to 3400.0,
                "isActivated" to false
            )
        )

        return try {
            for (vessel in dummyVessels) {
                val bkpNumber = vessel["bkpNumber"] as String
                // Menggunakan bkpNumber sebagai Document ID
                db.collection("vessels").document(bkpNumber).set(vessel).await()
            }
            Result.success("Berhasil mengunggah ${dummyVessels.size} data kapal ke Cloud Firestore!")
        } catch (e: Exception) {
            Result.failure(Exception(e.localizedMessage ?: "Gagal mengunggah data seeder."))
        }
    }
}