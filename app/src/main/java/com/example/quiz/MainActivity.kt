package com.example.quiz

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.CheckBox
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    // Declare variables
    private lateinit var checkBox: CheckBox
    private lateinit var startButton: Button
    private lateinit var exitButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set the layout for the activity
        setContentView(R.layout.splash_screen)

        // Use Handler to delay the execution of code by 5 seconds
        Handler(Looper.getMainLooper()).postDelayed({
            // After 5 seconds, change the layout to instructions layout
            setContentView(R.layout.instructions)

            // Initialize UI elements
            checkBox = findViewById(R.id.check)
            startButton = findViewById(R.id.start_btn)
            exitButton = findViewById(R.id.exit_button)

            // Set click listener for start button
            startButton.setOnClickListener {
                // Check if the checkbox is checked
                if (checkBox.isChecked) {
                    // If checked, navigate to the home activity
                    navigateToHome()
                }
            }

            // Set click listener for exit button
            exitButton.setOnClickListener {
                // Finish the activity, effectively closing the application
                finish()
            }
        }, 5000) // 5000 milliseconds = 5 seconds
    }

    // Function to navigate to the home activity
    private fun navigateToHome() {
        // Create an intent to start the home activity
        val intent = Intent(this, HomeActivity::class.java)
        // Start the home activity
        startActivity(intent)
        // Finish current activity to prevent going back to splash screen
        finish()
    }
}
