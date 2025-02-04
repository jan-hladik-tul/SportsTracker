// File: app/src/main/java/com/example/soccerstats/data/Match.kt
package com.example.sportstracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "matches")
data class Match(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val teamA: String,
    val teamB: String,
    val scoreA: Int,
    val scoreB: Int,
    val date: String,
    val note: String? = null,
    // New field to store a memory image URI (as a String)
    val memoryUri: String? = null
)

