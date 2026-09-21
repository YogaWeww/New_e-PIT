package com.example.newe_pit.data.remote

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * Helper class untuk mengisi data dummy kapal ke Cloud Firestore secara otomatis.
 */
object FirestoreSeeder {

    suspend fun seedVesselsData(): Result<String> {
        val db = FirebaseFirestore.getInstance()

        val dummyVessels = listOf(
            mapOf(
                "bkpNumber" to "A00029",
                "vesselName" to "KMN. DIGITALISASI 01",
                "grossTonnage" to 48,
                "ownerName" to "PT. BA***",
                "captainName" to "SUPRIYANTO",
                "sipiNumber" to "12.24.0029.01.718.009",
                "selarMark" to "GT. 48 No. 1234/Bc",
                "homePort" to "PPN Ambon",
                "wppRegion" to "WPP-NRI 718 (Arafura)",
                "gearType" to "Purse Seine Pelagis Kecil",
                "dimensionStr" to "18.5 m x 5.2 m x 2.1 m",
                "enginePower" to "350 HP",
                "engineBrand" to "Yanmar 6HYM-WET",
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
                "captainName" to "BUDI SANTOSO",
                "sipiNumber" to "12.24.0030.01.718.010",
                "selarMark" to "GT. 60 No. 5678/Bc",
                "homePort" to "PP. Dobo",
                "wppRegion" to "WPP-NRI 718 (Arafura)",
                "gearType" to "Jaring Insang Gulung",
                "dimensionStr" to "22.0 m x 6.0 m x 2.8 m",
                "enginePower" to "450 HP",
                "engineBrand" to "Mitsubishi S6B3",
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
                "captainName" to "HENDERIKUS",
                "sipiNumber" to "12.24.0088.01.718.011",
                "selarMark" to "GT. 30 No. 9101/Bc",
                "homePort" to "PPN Tual",
                "wppRegion" to "WPP-NRI 718 (Arafura)",
                "gearType" to "Rawai Tuna",
                "dimensionStr" to "15.0 m x 4.5 m x 1.8 m",
                "enginePower" to "280 HP",
                "engineBrand" to "Caterpillar C7",
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
                db.collection("vessels").document(bkpNumber).set(vessel, com.google.firebase.firestore.SetOptions.merge()).await()
            }
            Result.success("Berhasil mengunggah ${dummyVessels.size} data kapal ke Cloud Firestore!")
        } catch (e: Exception) {
            Result.failure(Exception(e.localizedMessage ?: "Gagal mengunggah data seeder."))
        }
    }
}