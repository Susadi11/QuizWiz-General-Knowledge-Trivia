package com.example.quiz

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar

class PlayActivity : AppCompatActivity() {

    // Declare variables
    private lateinit var quizViewModel: QuizViewModel
    private lateinit var cptQuestion: TextView
    private lateinit var textQuestion: TextView
    private lateinit var btnChoose1: Button
    private lateinit var btnChoose2: Button
    private lateinit var btnChoose3: Button
    private lateinit var btnChoose4: Button
    private lateinit var btnNext: Button
    private lateinit var playerNameTextView: TextView
    private lateinit var timerTextView: TextView

    private var scorePlayer = 0
    private var isClickBtn = false
    private lateinit var selectedAnswer: String

    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var mediaPlayerIncorrect: MediaPlayer
    private var countDownTimer: CountDownTimer? = null
    private var timeLeftInMillis: Long = 120000 // 2 minutes in milliseconds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.play)

        // Initialize UI elements
        playerNameTextView = findViewById(R.id.playerNameTextView)
        playerNameTextView.text = intent.getStringExtra("player_name") ?: ""
        cptQuestion = findViewById(R.id.cpt_question)
        textQuestion = findViewById(R.id.text_question)
        btnChoose1 = findViewById(R.id.btn_choose1)
        btnChoose2 = findViewById(R.id.btn_choose2)
        btnChoose3 = findViewById(R.id.btn_choose3)
        btnChoose4 = findViewById(R.id.btn_choose4)
        btnNext = findViewById(R.id.btn_next)
        timerTextView = findViewById(R.id.timerTextView)

        // Initialize ViewModel
        quizViewModel = ViewModelProvider(this).get(QuizViewModel::class.java)

        // Set the context for ViewModel
        quizViewModel.setContext(applicationContext)

        // Initialize MediaPlayer
        mediaPlayer = MediaPlayer.create(this, R.raw.correct)
        mediaPlayerIncorrect = MediaPlayer.create(this, R.raw.incorrect)

        startGame()
        // Load the first question
        loadQuestion()

        // Set click listener for the next button
        btnNext.setOnClickListener {
            if (isClickBtn) {
                checkAnswer(selectedAnswer)
                if (quizViewModel.moveToNextQuestion()) {
                    loadQuestion()
                    resetButtonBackgrounds()
                } else {
                    finishGame()
                }
            } else {
                // If the user hasn't selected an answer, proceed to the next question with a deduction of 2 points
                checkAnswer(null)
                if (quizViewModel.moveToNextQuestion()) {
                    loadQuestion()
                    resetButtonBackgrounds()
                } else {
                    finishGame()
                }
            }
        }

        // Show highest score Snackbar
        showHighestScoreSnackbar()
    }

    // Function to start the game
    private fun startGame() {
        quizViewModel.selectRandomQuestions(20)
        quizViewModel.playerName = playerNameTextView.text.toString() // Set the player name
        playerNameTextView.text = quizViewModel.playerName // Update player name TextView
        loadQuestion() // Load the first question
        startTimer()
    }

    // Function to load a question
    private fun loadQuestion() {
        // Display question number out of 20
        cptQuestion.text = "${quizViewModel.currentQuestionIndex + 1}/20" // Display question number out of 20
        textQuestion.text = quizViewModel.getCurrentQuestion()
        // Shuffle choices
        val choices = quizViewModel.getChoicesForCurrentQuestion().shuffled() // Shuffle choices
        btnChoose1.text = choices[0]
        btnChoose2.text = choices[1]
        btnChoose3.text = choices[2]
        btnChoose4.text = choices[3]

        isClickBtn = false
    }

    // Function to check the answer
    private fun checkAnswer(answer: String?) {
        if (answer != null) {
            if (quizViewModel.checkAnswer(answer)) {
                scorePlayer += 10 // Correct answer adds 10 points
                Toast.makeText(this@PlayActivity, "Correct! +10 points", Toast.LENGTH_SHORT).show()
                playCorrectSound()
            } else {
                scorePlayer -= 5 // Incorrect answer deducts 5 points
                Toast.makeText(this@PlayActivity, "Incorrect! -5 points", Toast.LENGTH_SHORT).show()
                playIncorrectSound()
            }
        } else {
            // Deduct 2 points if the user skips the question
            scorePlayer -= 2
            Toast.makeText(this@PlayActivity, "Skipped! -2 points", Toast.LENGTH_SHORT).show()
        }
    }

    private fun playCorrectSound() {
        val sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE)
        val isSoundEnabled = sharedPreferences.getBoolean("sound_enabled", true)

        if (isSoundEnabled) {
            mediaPlayer.start()
        }
    }

    private fun playIncorrectSound() {
        val sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE)
        val isSoundEnabled = sharedPreferences.getBoolean("sound_enabled", true)

        if (isSoundEnabled) {
            mediaPlayerIncorrect.start()
        }
    }

    // Function to finish the game
    private fun finishGame() {
        countDownTimer?.cancel()
        val highScore = quizViewModel.getHighScore()
        if (scorePlayer > highScore) {
            quizViewModel.setHighScore(scorePlayer)
            showHighScorePopup()
        }
        val intent = Intent(this@PlayActivity, ResultActivity::class.java)
        intent.putExtra("player_name", playerNameTextView.text.toString())
        intent.putExtra("Result", scorePlayer)
        startActivity(intent)
        finish()
    }

    // Function to show a popup for new high score
    private fun showHighScorePopup() {
        val alertDialog = AlertDialog.Builder(this)
            .setTitle("New High Score!")
            .setMessage("Congratulations! You have set a new high score of $scorePlayer points.")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        alertDialog.show()
    }

    // Function to start the timer
    private fun startTimer() {
        countDownTimer = object : CountDownTimer(timeLeftInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
                updateCountDownText()
            }

            override fun onFinish() {
                timeLeftInMillis = 0
                updateCountDownText()
                finishGame()
            }
        }.start()
    }

    // Function to update the timer TextView
    private fun updateCountDownText() {
        val seconds = (timeLeftInMillis / 1000).toInt()
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        val timeFormatted = String.format("%02d:%02d", minutes, remainingSeconds)
        timerTextView.text = timeFormatted
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
        mediaPlayer.release()
        mediaPlayerIncorrect.release()
    }

    // Function called when a choice button is clicked
    fun ClickChoose(view: View) {
        if (view is Button) {
            resetButtonBackgrounds()
            view.setBackgroundResource(R.drawable.btn_choose_colour)
            isClickBtn = true
            selectedAnswer = view.text.toString()
        }
    }

    // Function to reset the background of all choice buttons
    private fun resetButtonBackgrounds() {
        btnChoose1.setBackgroundResource(R.drawable.btn_style)
        btnChoose2.setBackgroundResource(R.drawable.btn_style)
        btnChoose3.setBackgroundResource(R.drawable.btn_style)
        btnChoose4.setBackgroundResource(R.drawable.btn_style)
    }

    // Function to show the highest score Snackbar
    private fun showHighestScoreSnackbar() {
        val highestScore = quizViewModel.getHighScore()
        val snackbar = Snackbar.make(
            findViewById(android.R.id.content),
            "Highest Score: $highestScore",
            Snackbar.LENGTH_LONG
        )
        snackbar.setAction("Dismiss") {
            snackbar.dismiss()
        }
        snackbar.show()
    }
}