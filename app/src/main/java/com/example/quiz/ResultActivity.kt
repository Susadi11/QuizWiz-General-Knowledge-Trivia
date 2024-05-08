package com.example.quiz

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class ResultActivity : AppCompatActivity() {

    private lateinit var textView: TextView
    private lateinit var userName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.result)

        // Initialize TextView
        textView = findViewById(R.id.textView)

        // Retrieve score and player name from intent
        val score = intent.getIntExtra("Result", 0)
        val playerName = intent.getStringExtra("player_name")

        // Display score
        textView.text = "Score: $score"

        // Save score with timestamp if player name is not null
        if (playerName != null) {
            userName = playerName
            saveScoreWithTimestamp(userName, score)
        }

        // Set click listener for restart button
        findViewById<TextView>(R.id.btn_restart).setOnClickListener {
            val intent = Intent(this@ResultActivity, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    // Function to save score with timestamp to SharedPreferences
    private fun saveScoreWithTimestamp(userName: String, score: Int) {
        val sharedPreferences = getSharedPreferences("Leaderboard", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val timeStamp = dateFormat.format(Date())

        val scoreEntry = "$timeStamp - Score: $score"

        val currentScores = sharedPreferences.getStringSet(userName, HashSet()) ?: HashSet()
        currentScores.add(scoreEntry)

        editor.putStringSet(userName, currentScores)
        editor.apply()
    }
}
