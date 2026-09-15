package com.example.newe_pit.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

/**
 * Instansi Utama Room Database e-PIT Mobile
 */
@Database(
    entities = [HaulRecordEntity::class, CatchItemEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(EPITTypeConverters::class)
abstract class EPITDatabase : RoomDatabase() {

    abstract fun haulDao(): HaulDao

    companion object {
        @Volatile
        private var INSTANCE: EPITDatabase? = null

        fun getDatabase(context: Context): EPITDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    EPITDatabase::class.java,
                    "epit_mobile_database.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}