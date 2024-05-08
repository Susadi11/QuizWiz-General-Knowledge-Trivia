package com.example.quiz

import android.content.Context
import androidx.lifecycle.ViewModel
import java.io.BufferedReader
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.*

class QuizViewModel : ViewModel() {

    private lateinit var context: Context

    // Lists to hold questions, answers, choices, and indices
    private val questions = mutableListOf<String>()
    private val answers = mutableListOf<String>()
    private val chooseList = mutableListOf<List<String>>()
    private val usedQuestionIndices = mutableSetOf<Int>()
    private val questionIndices = mutableListOf<Int>()

    // Variables for managing quiz state
    var currentQuestionIndex = 0
    var playerName: String = ""
    var score: Int = 0

    // Function to set the context
    fun setContext(context: Context) {
        this.context = context.applicationContext // Use application context to avoid memory leaks
        loadQuestionsFromTxt()
    }

    // Function to load questions from a text file
    fun loadQuestionsFromTxt() {
        val inputStream = context.resources.openRawResource(R.raw.quiz_questions)
        val reader = BufferedReader(InputStreamReader(inputStream))
        var line: String?
        var question: String
        var answer: String
        var choices: MutableList<String>
        var index = 1 // Start from 1

        // Read each line from the file
        while (reader.readLine().also { line = it } != null) {
            if (!line.isNullOrEmpty()) {
                // Split each line to get question, choices, and answer
                val parts = line!!.split("?")
                if (parts.size >= 2) {
                    question = parts[0].trim() + "?" // Extract question
                    val restOfLine = parts.drop(1).joinToString("?").trim()
                    val choicesAndAnswer = restOfLine.split(",")
                    answer = choicesAndAnswer.last().trim() // Extract answer
                    choices = choicesAndAnswer.dropLast(1).map { it.trim() }.toMutableList() // Extract choices
                    // Add question, answer, choices, and index to their respective lists
                    questions.add(question)
                    answers.add(answer)
                    chooseList.add(choices)
                    questionIndices.add(index) // Add the unique index to the list
                    index++ // Increment the index
                }
            }
        }
        reader.close()
    }

    // Function to select random questions
    fun selectRandomQuestions(count: Int) {
        usedQuestionIndices.clear()
        val randomIndices = mutableListOf<Int>()
        while (randomIndices.size < count) {
            val randomIndex = (1 until questions.size).random()
            if (!randomIndices.contains(randomIndex)) {
                randomIndices.add(randomIndex)
            }
        }
        usedQuestionIndices.addAll(randomIndices)
    }

    // Function to get the index of the next random question
    fun getNextRandomQuestionIndex(): Int {
        var index = (0 until questions.size).random()
        while (index in usedQuestionIndices) {
            index = (0 until questions.size).random()
        }
        usedQuestionIndices.add(index)
        return index
    }

    // Function to get all player names from SharedPreferences
    fun getAllPlayerNames(): Set<String> {
        val sharedPreferences = context.getSharedPreferences("Leaderboard", Context.MODE_PRIVATE)
        return sharedPreferences.all.keys
    }

    // Function to get scores for a player from SharedPreferences
    fun getPlayerScores(playerName: String): List<String> {
        val sharedPreferences = context.getSharedPreferences("Leaderboard", Context.MODE_PRIVATE)
        return sharedPreferences.getStringSet(playerName, emptySet())?.toList() ?: emptyList()
    }

    // Function to get the current question
    fun getCurrentQuestion(): String {
        return questions[usedQuestionIndices.elementAt(currentQuestionIndex)]
    }

    // Function to check the answer for the current question
    fun checkAnswer(answer: String): Boolean {
        return answer.equals(answers[usedQuestionIndices.elementAt(currentQuestionIndex)], ignoreCase = true)
    }

    // Function to get choices for the current question
    fun getChoicesForCurrentQuestion(): List<String> {
        return chooseList[usedQuestionIndices.elementAt(currentQuestionIndex)]
    }

    // Function to move to the next question
    fun moveToNextQuestion(): Boolean {
        if (currentQuestionIndex < usedQuestionIndices.size - 1) {
            currentQuestionIndex++
            return true
        } else {
            calculateFinalScore()
            return false
        }
    }

    // Function to calculate the final score
    private fun calculateFinalScore() {
        score = 0
        for (i in usedQuestionIndices) {
            if (checkAnswer(answers[i])) {
                score += 10 // Correct answer adds 10 points
            } else {
                score -= 5 // Incorrect answer deducts 5 points
            }
        }
        saveScoreWithTimestamp(playerName, score) // Save score to leaderboard
    }

    // Function to save score with timestamp to SharedPreferences
    private fun saveScoreWithTimestamp(playerName: String, score: Int) {
        val sharedPreferences = context.getSharedPreferences("Leaderboard", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val timeStamp = dateFormat.format(Date())

        val scoreEntry = "$timeStamp - Score: $score"

        val currentScores = sharedPreferences.getStringSet(playerName, mutableSetOf()) ?: mutableSetOf()
        currentScores.add(scoreEntry)

        editor.putStringSet(playerName, currentScores)
        editor.apply()
    }

    // Function to get the highest score from all players
    fun getHighScore(): Int {
        var highScore = 0
        val allPlayerScores = mutableListOf<Int>()

        for (playerName in getAllPlayerNames()) {
            for (scoreEntry in getPlayerScores(playerName)) {
                val score = scoreEntry.substringAfter(" - Score: ").toIntOrNull() ?: 0
                allPlayerScores.add(score)
            }
        }

        if (allPlayerScores.isNotEmpty()) {
            highScore = allPlayerScores.maxOrNull() ?: 0
        }

        return highScore
    }

    // Function to set the high score
    fun setHighScore(newHighScore: Int) {
        val sharedPreferences = context.getSharedPreferences("HighScore", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt("highScore", newHighScore)
        editor.apply()
    }

    // Function to reset the quiz
    fun resetQuiz() {
        currentQuestionIndex = 0
        score = 0
    }

    // Function to get the total number of questions
    fun getTotalQuestions(): Int {
        return questions.size
    }
}
