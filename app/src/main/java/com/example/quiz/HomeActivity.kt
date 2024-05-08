package com.example.quiz

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home)

        // Find the "Play" button and set an onclick listener
        val buttonPlay = findViewById<Button>(R.id.button5)
        buttonPlay.setOnClickListener {
            // Show the custom dialog to enter name
            showEnterNameDialog()
        }

        // Find the "Settings" button and set an onclick listener
        val settingButton = findViewById<Button>(R.id.setting_btn)
        settingButton.setOnClickListener {
            // Start the SettingActivity
            val intent = Intent(this, SettingActivity::class.java)
            startActivity(intent)
        }

        // Find the "Score" button and set an onclick listener
        val scoreButton = findViewById<Button>(R.id.score_btn)
        scoreButton.setOnClickListener {
            // Start the LeaderboardActivity
            val intent = Intent(this, LeaderboardActivity::class.java)
            startActivity(intent)
        }

        // Find the "Privacy" TextView and set a click listener
        val privacyTextView = findViewById<TextView>(R.id.textView2)
        privacyTextView.setOnClickListener {
            // Show privacy policy dialog
            showPrivacyPolicyDialog()
        }
    }

    private fun showEnterNameDialog() {
        // Create an AlertDialog builder
        val dialogBuilder = AlertDialog.Builder(this)
        // Inflate the dialog_enter_name layout
        val dialogView = layoutInflater.inflate(R.layout.dialog_enter_name, null)
        // Find the EditText for entering the name
        val editTextName = dialogView.findViewById<EditText>(R.id.editTextName)

        dialogBuilder.apply {
            setTitle("Enter Your Name")
            setView(dialogView)
            setPositiveButton("OK") { dialog, which ->
                // Get the entered name and start the PlayActivity with the name
                val playerName = editTextName.text.toString().trim()
                startPlayActivity(playerName)
            }
            setNegativeButton("Cancel") { dialog, which ->
                // Dismiss the dialog
                dialog.dismiss()
            }
        }

        // Create and show the dialog
        val dialog = dialogBuilder.create()
        dialog.show()
    }

    private fun startPlayActivity(playerName: String) {
        // Create an Intent to start the PlayActivity
        val intent = Intent(this, PlayActivity::class.java)
        // Put the player name as an extra
        intent.putExtra("player_name", playerName)
        startActivity(intent)
    }

    private fun showPrivacyPolicyDialog() {
        val dialogBuilder = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.privacy, null)

        dialogBuilder.apply {
            setView(dialogView)
            setPositiveButton("Close") { dialog, which ->
                dialog.dismiss()
            }
        }

        val dialog = dialogBuilder.create()
        dialog.show()
    }

}