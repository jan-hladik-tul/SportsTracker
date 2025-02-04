// File: app/src/main/java/com/example/soccerstats/data/MatchRepository.kt
package com.example.sportstracker.data

class MatchRepository(private val matchDao: MatchDao) {
    fun getAllMatches() = matchDao.getAllMatches()
    suspend fun insertMatch(match: Match) = matchDao.insertMatch(match)
    suspend fun deleteMatch(match: Match) = matchDao.deleteMatch(match)
    suspend fun deleteAllMatches() = matchDao.deleteAllMatches()
    suspend fun updateMatch(match: Match) {
        matchDao.updateMatch(match)
    }

}
