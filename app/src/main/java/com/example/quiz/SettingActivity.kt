package com.example.quiz

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.materialswitch.MaterialSwitch

class SettingActivity : AppCompatActivity() {
    private lateinit var soundSwitch: MaterialSwitch

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.setting)

        soundSwitch = findViewById(R.id.soundSwitch)

        // Load sound settings state from shared preferences
        val sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE)
        val isSoundEnabled = sharedPreferences.getBoolean("sound_enabled", true)

        // Set the initial state of the sound switch
        soundSwitch.isChecked = isSoundEnabled

        soundSwitch.setOnCheckedChangeListener { _, isChecked ->
            handleSoundToggle(isChecked)
        }
    }

    private fun handleSoundToggle(isChecked: Boolean) {
        val sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("sound_enabled", isChecked)
        editor.apply()
    }
}
