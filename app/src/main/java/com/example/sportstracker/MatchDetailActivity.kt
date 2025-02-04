package com.example.sportstracker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.sportstracker.data.Match
import com.example.sportstracker.viewmodel.MatchViewModel

class MatchDetailActivity : AppCompatActivity() {

    private lateinit var matchViewModel: MatchViewModel
    private var match: Match? = null

    // For storing the newly selected memory URI
    private var selectedMemoryUri: Uri? = null
    private var ivMemory: ImageView? = null

    // Register an Activity Result Launcher using OpenDocument (which returns an Array<String> for MIME types)
    private val pickMemoryLauncher = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        uri?.let {
            // Obtain persistent permission so we can access the URI later.
            contentResolver.takePersistableUriPermission(it, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            selectedMemoryUri = it

            // Update the ImageView (with a try-catch to avoid crashing in case of a SecurityException)
            try {
                ivMemory?.setImageURI(it)
            } catch (e: SecurityException) {
                e.printStackTrace()
                ivMemory?.setImageResource(R.drawable.ic_add_photo)
            }

            // Update the match record with the new memory URI
            match?.let { currentMatch ->
                val updatedMatch = currentMatch.copy(memoryUri = it.toString())
                matchViewModel.updateMatch(updatedMatch)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_match_detail)

        // Retrieve UI elements
        val tvTeams = findViewById<TextView>(R.id.tvTeamsDetail)
        val tvScore = findViewById<TextView>(R.id.tvScoreDetail)
        val tvDate = findViewById<TextView>(R.id.tvDateDetail)
        val tvNote = findViewById<TextView>(R.id.tvNoteDetail)
        val btnAddMemory = findViewById<Button>(R.id.btnAddMemory)
        val btnDelete = findViewById<Button>(R.id.btnDeleteMatch)
        ivMemory = findViewById(R.id.ivMemory)

        // Initialize the ViewModel
        matchViewModel = ViewModelProvider(this).get(MatchViewModel::class.java)

        // Retrieve match id from intent extras.
        val matchId = intent.getIntExtra("match_id", -1)
        if (matchId == -1) {
            Toast.makeText(this, "Invalid match", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Observe match data; update the UI when the match record changes.
        matchViewModel.matches.observe(this, Observer { matches ->
            match = matches.find { it.id == matchId }
            match?.let { m ->
                tvTeams.text = "${m.teamA} vs ${m.teamB}"
                tvScore.text = "${m.scoreA} - ${m.scoreB}"
                tvDate.text = m.date
                tvNote.text = if (m.note.isNullOrBlank()) "No note provided" else m.note

                // Load the memory image if available; otherwise use a placeholder.
                if (!m.memoryUri.isNullOrEmpty()) {
                    try {
                        ivMemory?.setImageURI(Uri.parse(m.memoryUri))
                    } catch (e: SecurityException) {
                        e.printStackTrace()
                        ivMemory?.setImageResource(R.drawable.ic_add_photo)
                    }
                } else {
                    ivMemory?.setImageResource(R.drawable.ic_add_photo)
                }
            }
        })

        // When the Add Memory button is clicked, launch the image picker.
        btnAddMemory.setOnClickListener {
            // Launch the picker with an array of MIME types.
            pickMemoryLauncher.launch(arrayOf("image/*"))
        }

        // Delete the match when the Delete button is clicked.
        btnDelete.setOnClickListener {
            match?.let { currentMatch ->
                matchViewModel.deleteMatch(currentMatch)
                Toast.makeText(this, "Match deleted", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}
