// File: app/src/main/java/com/example/soccerstats/viewmodel/MatchViewModel.kt
package com.example.sportstracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.sportstracker.data.Match
import com.example.sportstracker.data.MatchDatabase
import com.example.sportstracker.data.MatchRepository
import kotlinx.coroutines.launch

class MatchViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: MatchRepository

    // LiveData that automatically updates when the database changes
    val matches: LiveData<List<Match>>

    init {
        val dao = MatchDatabase.getDatabase(application).matchDao()
        repository = MatchRepository(dao)
        matches = repository.getAllMatches() // Directly assign LiveData
    }

    fun addMatch(match: Match) {
        viewModelScope.launch {
            repository.insertMatch(match)
            // No need to manually refresh, LiveData observes database changes.
        }
    }

    fun deleteMatch(match: Match) {
        viewModelScope.launch {
            repository.deleteMatch(match)
            // LiveData will update automatically.
        }
    }
    fun updateMatch(match: Match) {
        viewModelScope.launch {
            repository.updateMatch(match)
        }
    }
    fun clearAllMatches() {
        viewModelScope.launch {
            repository.deleteAllMatches()
        }
    }
}
