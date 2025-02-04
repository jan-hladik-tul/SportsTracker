package com.example.sportstracker

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.sportstracker.R
import com.example.sportstracker.data.Match

class MatchAdapter(
    private val onItemClick: (Match) -> Unit,
    // Mapping from team names to their logo URI strings
    private val teamLogos: Map<String, String>
) : ListAdapter<Match, MatchAdapter.MatchViewHolder>(DiffCallback()) {

    inner class MatchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTeams: TextView = itemView.findViewById(R.id.tvTeams)
        val tvScore: TextView = itemView.findViewById(R.id.tvScore)
        val ivTeamALogo: ImageView = itemView.findViewById(R.id.ivTeamALogo)
        val ivTeamBLogo: ImageView = itemView.findViewById(R.id.ivTeamBLogo)

        fun bind(match: Match) {
            tvTeams.text = "${match.teamA} vs ${match.teamB}"
            tvScore.text = "${match.scoreA} - ${match.scoreB}"
            itemView.setOnClickListener { onItemClick(match) }

            // Load logo for Team A if available
            val logoUriA = teamLogos[match.teamA]
            if (!logoUriA.isNullOrEmpty()) {
                try {
                    ivTeamALogo.setImageURI(Uri.parse(logoUriA))
                } catch (e: SecurityException) {
                    e.printStackTrace()
                    ivTeamALogo.setImageResource(R.drawable.ic_launcher_foreground)
                }
            } else {
                ivTeamALogo.setImageResource(R.drawable.ic_launcher_foreground)
            }

            // Load logo for Team B if available
            val logoUriB = teamLogos[match.teamB]
            if (!logoUriB.isNullOrEmpty()) {
                try {
                    ivTeamBLogo.setImageURI(Uri.parse(logoUriB))
                } catch (e: SecurityException) {
                    e.printStackTrace()
                    ivTeamBLogo.setImageResource(R.drawable.ic_launcher_foreground)
                }
            } else {
                ivTeamBLogo.setImageResource(R.drawable.ic_launcher_foreground)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Match>() {
        override fun areItemsTheSame(oldItem: Match, newItem: Match): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Match, newItem: Match): Boolean =
            oldItem == newItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_match, parent, false)
        return MatchViewHolder(view)
    }

    override fun onBindViewHolder(holder: MatchViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
