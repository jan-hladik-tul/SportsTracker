package com.example.sportstracker

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sportstracker.R
import com.example.sportstracker.TeamAdapter

class TeamsActivity : AppCompatActivity() {

    // Variables for image picking.
    private var selectedLogoUri: Uri? = null
    private var currentLogoImageView: ImageView? = null

    // Use OpenDocument to get persistable permission.
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        uri?.let {
            contentResolver.takePersistableUriPermission(it, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            selectedLogoUri = it
            currentLogoImageView?.setImageURI(it)
        }
    }

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TeamAdapter
    private val teamsKey = "teams"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_teams)

        recyclerView = findViewById(R.id.recyclerViewTeams)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = TeamAdapter(getTeamsList(), ::onTeamDeleted, ::onTeamUpdated)
        recyclerView.adapter = adapter

        // The button acting as the FAB to add a new team.
        val fab: Button = findViewById(R.id.fabAddTeam)
        fab.setOnClickListener {
            showAddTeamDialogWithLogo()
        }
    }

    private fun getTeamsList(): MutableList<String> {
        val prefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        return prefs.getStringSet(teamsKey, emptySet())?.toMutableList() ?: mutableListOf()
    }

    private fun saveTeamsList(teamsList: List<String>) {
        val prefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        prefs.edit().putStringSet(teamsKey, teamsList.toSet()).apply()
    }

    private fun showAddTeamDialogWithLogo() {
        // Reset the selected URI.
        selectedLogoUri = null

        val dialogView = layoutInflater.inflate(R.layout.dialog_add_team, null)
        val etTeamName = dialogView.findViewById<EditText>(R.id.etTeamName)
        currentLogoImageView = dialogView.findViewById(R.id.ivTeamLogo)
        val btnSelectLogo = dialogView.findViewById<Button>(R.id.btnSelectLogo)

        // Launch the image picker using OpenDocument.
        btnSelectLogo.setOnClickListener {
            pickImageLauncher.launch(arrayOf("image/*"))
        }

        AlertDialog.Builder(this)
            .setTitle("Add New Team")
            .setView(dialogView)
            .setPositiveButton("Add") { dialog, _ ->
                val teamName = etTeamName.text.toString().trim()
                if (teamName.isEmpty()) {
                    Toast.makeText(this, "Please enter a team name.", Toast.LENGTH_SHORT).show()
                } else {
                    val logoUriString = selectedLogoUri?.toString() ?: ""
                    val teamEntry = "$teamName|$logoUriString"
                    val teamsSet = getTeamsList().toMutableSet()
                    teamsSet.add(teamEntry)
                    saveTeamsList(teamsSet.toList())
                    adapter.updateList(teamsSet.toList())
                    Toast.makeText(this, "Team added: $teamName", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel", null)
            .create()
            .show()
    }

    private fun onTeamDeleted(teamEntry: String) {
        val teamsList = getTeamsList()
        teamsList.remove(teamEntry)
        saveTeamsList(teamsList)
        adapter.updateList(teamsList)
        Toast.makeText(this, "Team deleted", Toast.LENGTH_SHORT).show()
    }

    private fun onTeamUpdated(oldEntry: String, newEntry: String) {
        val teamsList = getTeamsList()
        val index = teamsList.indexOf(oldEntry)
        if (index != -1) {
            teamsList[index] = newEntry
            saveTeamsList(teamsList)
            adapter.updateList(teamsList)
            Toast.makeText(this, "Team updated", Toast.LENGTH_SHORT).show()
        }
    }
}
