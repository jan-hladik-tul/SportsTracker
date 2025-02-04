// File: app/src/main/java/com/example/soccerstats/data/MatchDatabase.kt
package com.example.sportstracker.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

// Increase the version number from 1 to 2 (or higher) when schema changes.
@Database(entities = [Match::class], version = 4, exportSchema = false)
abstract class MatchDatabase : RoomDatabase() {
    abstract fun matchDao(): MatchDao

    companion object {
        @Volatile
        private var INSTANCE: MatchDatabase? = null

        // Define the migration from version 1 to version 2.
        val MIGRATION_3_4: Migration = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Add the new 'note' column to the 'matches' table.
                database.execSQL("ALTER TABLE matches ADD COLUMN newColumn TEXT")
            }
        }

        fun getDatabase(context: Context): MatchDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MatchDatabase::class.java,
                    "match_database"
                )
                    .fallbackToDestructiveMigration()  // WARNING: This will erase all existing data!
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
