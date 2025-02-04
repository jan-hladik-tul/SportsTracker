package com.example.sportstracker

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.sportstracker.MatchListFragment
import com.example.sportstracker.TeamsActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Show welcome message using SharedPreferences.
        val prefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val favoriteTeam = prefs.getString("favorite_team", null)
        val welcomeMessage = if (favoriteTeam != null) {
            "Welcome fan of $favoriteTeam!"
        } else {
            "Welcome sports fan!"
        }
        Toast.makeText(this, welcomeMessage, Toast.LENGTH_SHORT).show()

        // Add the MatchListFragment if not already added.
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, MatchListFragment())
                .commit()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_set_favorite_team -> {
                showFavoriteTeamDialog()
                true
            }
            R.id.menu_manage_teams -> {
                // Launch TeamsActivity
                startActivity(Intent(this, TeamsActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showFavoriteTeamDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_set_favorite_team, null)
        val spinnerFavoriteTeam = dialogView.findViewById<Spinner>(R.id.spinnerFavoriteTeam)

        // Retrieve teams from SharedPreferences (stored as "TeamName|logoUri")
        val prefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val teamsSet = prefs.getStringSet("teams", emptySet())
        // Extract just the team names:
        val teamsList = teamsSet?.mapNotNull { entry ->
            entry.split("|").getOrNull(0)?.trim()?.takeIf { it.isNotEmpty() }
        }?.sorted() ?: emptyList()

        if (teamsList.isEmpty()) {
            Toast.makeText(this, "No teams available. Please add teams first.", Toast.LENGTH_SHORT).show()
            return
        }

        // Convert the list to an array.
        val teamsArray = teamsList.toTypedArray()
        val adapterTeams = ArrayAdapter(this, android.R.layout.simple_spinner_item, teamsArray)
        adapterTeams.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerFavoriteTeam.adapter = adapterTeams

        AlertDialog.Builder(this)
            .setTitle("Set Favorite Team")
            .setView(dialogView)
            .setPositiveButton("Set") { dialog, _ ->
                val selectedTeam = spinnerFavoriteTeam.selectedItem?.toString() ?: ""
                if (selectedTeam.isNotEmpty()) {
                    prefs.edit().putString("favorite_team", selectedTeam).apply()
                    Toast.makeText(this, "Favorite team set to $selectedTeam", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Please select a team.", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel", null)
            .create()
            .show()
    }
}
