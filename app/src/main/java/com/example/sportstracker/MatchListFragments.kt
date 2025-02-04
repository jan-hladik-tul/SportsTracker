package com.example.sportstracker

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sportstracker.R
import com.example.sportstracker.data.Match
import com.example.sportstracker.viewmodel.MatchViewModel

class MatchListFragment : Fragment() {

    private lateinit var matchViewModel: MatchViewModel
    private lateinit var adapter: MatchAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment (ensure fragment_match_list.xml contains the RecyclerView and the Add Match button)
        return inflater.inflate(R.layout.fragment_match_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        val logosMap = getTeamLogosMap()

        adapter = MatchAdapter({ match ->
            // This code will run when a match item is clicked.
            // Create an Intent to launch the detail activity.
            val intent = Intent(requireContext(), MatchDetailActivity::class.java)
            // Pass the match id (or any necessary data) via extras.
            intent.putExtra("match_id", match.id)
            startActivity(intent)
        }, logosMap)

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        matchViewModel = ViewModelProvider(this).get(MatchViewModel::class.java)
        matchViewModel.matches.observe(viewLifecycleOwner, Observer { matches ->
            adapter.submitList(matches)
        })

        view.findViewById<Button>(R.id.btnAddMatch)?.setOnClickListener {
            showAddMatchDialog()
        }
    }


    /**
     * Displays an AlertDialog with a custom layout (dialog_add_match.xml) to add a new match.
     * The dialog uses two Spinners for selecting teams (populated from SharedPreferences),
     * plus EditTexts for scores, date, and an optional note.
     */
    private fun showAddMatchDialog() {
        // Inflate the custom layout for the dialog
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_match, null)
        val spinnerTeamA = dialogView.findViewById<Spinner>(R.id.spinnerTeamA)
        val spinnerTeamB = dialogView.findViewById<Spinner>(R.id.spinnerTeamB)
        val etScoreA = dialogView.findViewById<EditText>(R.id.etScoreA)
        val etScoreB = dialogView.findViewById<EditText>(R.id.etScoreB)
        val etDate = dialogView.findViewById<EditText>(R.id.etDate)
        val etNote = dialogView.findViewById<EditText>(R.id.etNote)

        // Retrieve teams from SharedPreferences; assume teams are stored simply as team names.
        val prefs = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val teamsSet = prefs.getStringSet("teams", emptySet())
        val teamsList = teamsSet?.map { entry ->
            // Split by "|" and take the first part (the team name)
            entry.split("|").getOrNull(0)?.trim() ?: ""
        }?.filter { it.isNotEmpty() }?.sorted() ?: listOf()


        // If no teams exist, prompt the user to add teams first.
        if (teamsList.isEmpty()) {
            Toast.makeText(context, "No teams available. Please add teams first.", Toast.LENGTH_SHORT).show()
            return
        }

        // Create an ArrayAdapter to populate the spinners
        val adapterTeams = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, teamsList)
        adapterTeams.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTeamA.adapter = adapterTeams
        spinnerTeamB.adapter = adapterTeams

        // Build and display the AlertDialog
        AlertDialog.Builder(requireContext())
            .setTitle("Add Match")
            .setView(dialogView)
            .setPositiveButton("Add") { dialog, _ ->
                // Retrieve selected team names
                val teamA = spinnerTeamA.selectedItem?.toString() ?: ""
                val teamB = spinnerTeamB.selectedItem?.toString() ?: ""

                // Validate that two different teams are selected
                if (teamA.isEmpty() || teamB.isEmpty() || teamA == teamB) {
                    Toast.makeText(context, "Please select two different teams.", Toast.LENGTH_SHORT).show()
                } else {
                    // Retrieve and parse scores; default to 0 if not provided
                    val scoreA = etScoreA.text.toString().toIntOrNull() ?: 0
                    val scoreB = etScoreB.text.toString().toIntOrNull() ?: 0
                    // Retrieve date and note
                    val date = etDate.text.toString()
                    val note = etNote.text.toString()

                    // Create a new Match object (assuming your Match data class has these fields)
                    val newMatch = Match(
                        teamA = teamA,
                        teamB = teamB,
                        scoreA = scoreA,
                        scoreB = scoreB,
                        date = date,
                        note = if (note.isNotBlank()) note else null
                    )
                    // Add the match via the ViewModel
                    matchViewModel.addMatch(newMatch)
                    Toast.makeText(context, "Match added", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel", null)
            .create()
            .show()
    }

    /**
     * Builds and returns a mapping of team names to their logo URIs.
     * Assumes that teams are stored in SharedPreferences under the key "teams"
     * in the format "TeamName|logoUri". If no logoUri is stored, it may be an empty string.
     */
    private fun getTeamLogosMap(): Map<String, String> {
        val prefs = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val teamsSet = prefs.getStringSet("teams", emptySet())
        val map = mutableMapOf<String, String>()
        teamsSet?.forEach { entry ->
            // Split each entry by "|" to separate team name and logo URI.
            val parts = entry.split("|")
            if (parts.size == 2) {
                val teamName = parts[0].trim()
                val logoUri = parts[1].trim()
                if (teamName.isNotEmpty()) {
                    map[teamName] = logoUri
                }
            }
        }
        return map
    }
}
