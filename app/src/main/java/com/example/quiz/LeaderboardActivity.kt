package com.example.quiz

import android.os.Bundle
import android.util.Log
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import java.text.SimpleDateFormat
import java.util.Locale

class LeaderboardActivity : AppCompatActivity() {

    // Declare variables
    private lateinit var leaderboardTableLayout: TableLayout
    private lateinit var quizViewModel: QuizViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.score_board) // Set the layout file for this activity

        // Initialize the TableLayout
        leaderboardTableLayout = findViewById(R.id.leaderboardTableLayout)

        // Initialize ViewModel using ViewModelProvider
        quizViewModel = ViewModelProvider(this).get(QuizViewModel::class.java)

        // Set the application context for QuizViewModel
        quizViewModel.setContext(applicationContext)

        // Call the function to display leaderboard
        displayLeaderboard()
    }

    // Function to display the leaderboard
    private fun displayLeaderboard() {
        try {
            // Get the TextView for displaying the high score
            val highScoreTextView = findViewById<TextView>(R.id.highScoreTextView)
            // Get the high score from the ViewModel
            val highScore = quizViewModel.getHighScore()
            // Set the text for high score TextView
            highScoreTextView.text = "High Score - $highScore"

            // Get all player names from the ViewModel
            val playerNames = quizViewModel.getAllPlayerNames()
            // Iterate through each player
            for (playerName in playerNames) {
                // Get scores for the current player
                val scores = quizViewModel.getPlayerScores(playerName)
                // Iterate through each score entry
                for (scoreEntry in scores) {
                    // Extract score and date-time from score entry
                    val score = scoreEntry.substringAfter(" - ")
                    val dateTime = formatDate(scoreEntry.substringBefore(" - "))
                    // Add a leaderboard entry for the current player
                    addLeaderboardEntry(playerName, dateTime, score)
                }
            }
        } catch (e: Exception) {
            // Log any errors that occur while displaying the leaderboard
            Log.e("LeaderboardActivity", "Error displaying leaderboard: ${e.message}")
            // Handle error (e.g., show error message to the user)
        }
    }

    // Function to add a leaderboard entry to the TableLayout
    private fun addLeaderboardEntry(name: String, dateTime: String, score: String) {
        // Create a new TableRow
        val tableRow = TableRow(this)

        // Create TextViews for player name, date-time, and score
        val nameTextView = TextView(this).apply {
            text = name
            // Set layout parameters
            layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)
            setPadding(8, 8, 8, 8)
        }
        val dateTextView = TextView(this).apply {
            text = dateTime
            layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)
            setPadding(8, 8, 8, 8)
        }
        val scoreTextView = TextView(this).apply {
            text = score
            layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)
            setPadding(8, 8, 8, 8)
        }

        // Add TextViews to the TableRow
        tableRow.addView(nameTextView)
        tableRow.addView(dateTextView)
        tableRow.addView(scoreTextView)

        // Add the TableRow to the TableLayout
        leaderboardTableLayout.addView(tableRow)
    }

    // Function to format date from one format to another
    private fun formatDate(dateString: String): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val date = dateFormat.parse(dateString)
        val outputFormat = SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.getDefault())
        return outputFormat.format(date)
    }
}
