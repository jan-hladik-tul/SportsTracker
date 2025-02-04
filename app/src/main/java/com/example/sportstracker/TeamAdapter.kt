package com.example.sportstracker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView

class TeamAdapter(
    private var teams: List<String>,
    private val onDelete: (String) -> Unit,
    private val onUpdate: (String, String) -> Unit
) : RecyclerView.Adapter<TeamAdapter.TeamViewHolder>() {

    inner class TeamViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTeamName: TextView = itemView.findViewById(R.id.tvTeamName)
        val btnEdit: Button = itemView.findViewById(R.id.btnEditTeam)
        val btnDelete: Button = itemView.findViewById(R.id.btnDeleteTeam)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeamViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_team, parent, false)
        return TeamViewHolder(view)
    }

    override fun getItemCount(): Int = teams.size

    override fun onBindViewHolder(holder: TeamViewHolder, position: Int) {
        val teamEntry = teams[position]
        // Extract the team name (before the "|")
        val teamName = teamEntry.split("|").getOrNull(0) ?: teamEntry
        holder.tvTeamName.text = teamName

        holder.btnDelete.setOnClickListener {
            onDelete(teamEntry)
        }
        holder.btnEdit.setOnClickListener {
            showEditDialog(holder.itemView.context, teamEntry)
        }
    }

    private fun showEditDialog(context: android.content.Context, teamEntry: String) {
        val parts = teamEntry.split("|")
        val currentName = parts.getOrNull(0) ?: ""
        val currentLogo = parts.getOrNull(1) ?: ""
        val editText = EditText(context)
        editText.setText("$currentName|$currentLogo")
        AlertDialog.Builder(context)
            .setTitle("Edit Team")
            .setView(editText)
            .setPositiveButton("Save") { dialog, _ ->
                val newEntry = editText.text.toString().trim()
                if (newEntry.isNotEmpty() && newEntry.contains("|")) {
                    onUpdate(teamEntry, newEntry)
                } else {
                    Toast.makeText(context, "Invalid format. Use TeamName|logoUri", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel", null)
            .create()
            .show()
    }

    fun updateList(newTeams: List<String>) {
        teams = newTeams
        notifyDataSetChanged()
    }
}
