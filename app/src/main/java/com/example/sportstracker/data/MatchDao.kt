// File: app/src/main/java/com/example/soccerstats/data/MatchDao.kt
package com.example.sportstracker.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.lifecycle.LiveData
import androidx.room.Update

@Dao
interface MatchDao {
    @Query("SELECT * FROM matches")
    fun getAllMatches(): LiveData<List<Match>>

    @Insert
    suspend fun insertMatch(match: Match)

    @Delete
    suspend fun deleteMatch(match: Match)

    @Update
    suspend fun updateMatch(match: Match)

    @Query("DELETE FROM matches")
    suspend fun deleteAllMatches()
}
