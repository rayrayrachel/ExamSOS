package com.example.examsos

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.examsos.dataValue.QuizQuestion

class ActivityQuiz : AppCompatActivity() {

    private val myTag = "Rachel'sTag"

    // Track the current question index
    private var currentQuestionIndex = 0
    private var questions: ArrayList<QuizQuestion>? = null

    private lateinit var progressBar: ProgressBar
    private var livesLeft = 3
    private var totalScore = 0
    private var difficulty: String? = null

    private var isUserWin = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)

        // Set up the toolbar
        val mToolbar = findViewById<Toolbar>(R.id.quiz_toolbar)
        setSupportActionBar(mToolbar)

        // Initialize ProgressBar
        progressBar = findViewById(R.id.quiz_progress_bar)

        // Get the questions passed from the previous activity
        questions = intent.getParcelableArrayListExtra("QUESTIONS")

        difficulty = intent.getStringExtra("DIFFICULTY_SELECTED")
        Log.i(myTag, "Difficulty: $difficulty")

        questions!!.forEachIndexed { index, question ->
            val decodedCorrectAnswer = Html.fromHtml(question.correctAnswer).toString()
            Log.i(myTag, "Correct answer for Question #$index: $decodedCorrectAnswer")
        }

        if (questions != null && questions!!.isNotEmpty()) {
           // updateProgressBar()
            loadQuestion(currentQuestionIndex)
        } else {
            Log.e(myTag, "No questions passed to ActivityQuiz.")
        }
    }

    private fun updateProgressBar() {
        val totalQuestions = (questions?.size ?: 1) + 1
        val progress = ((currentQuestionIndex + 1) / totalQuestions.toFloat() * 100).toInt()
        progressBar.progress = progress
        Log.i(myTag, "Progress updated to $progress%")
    }

    // Inflate the options menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.sub_tool_bar_layout, menu)
        return super.onCreateOptionsMenu(menu)
    }

    // Handle the toolbar item selection
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.upButton -> {
                finish()
                Log.i(myTag, "Back clicked")
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // Load the next question into the Fragment
    private fun loadQuestion(questionIndex: Int) {
        if (questionIndex < questions!!.size) {

            val question = questions!![questionIndex]
            val type = question.type

            val decodedQuestion = Html.fromHtml(question.question).toString()
            val decodedOptions = question.options!!.map { Html.fromHtml(it).toString() }
            val decodedCorrectAnswer = Html.fromHtml(question.correctAnswer).toString()

            val quizFragment = FragmentQuizQuestion.newInstance(
                decodedQuestion,
                decodedOptions,
                decodedCorrectAnswer,
                type,
                questionIndex + 1 // Pass the question number
            )

            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, quizFragment) // Replace fragment with new question
                .commit()
        } else {
            showToast("Quiz completed!")
            Log.i(myTag, "Quiz completed!")
            updateTotalScore()

            isUserWin = true

            val intent = Intent(this, ActivityResults::class.java)
            intent.putExtra("TOTAL_SCORE", totalScore)
            intent.putExtra("DIFFICULTY", difficulty)
            intent.putExtra("TOTAL_QUESTIONS", currentQuestionIndex)
            intent.putExtra("LIVES_REMAINING", livesLeft)
            intent.putExtra("IS_WIN", isUserWin)
            startActivity(intent)
            finish()
        }
    }

    /**
     * Called when the user select an answer in the quiz fragment.
     */
    fun onAnswerSelected(isCorrect: Boolean) {
        if (isCorrect) {
            Log.i(myTag, "Correct answer selected!")
            showToast("Correct answer!")
            currentQuestionIndex++
            updateProgressBar()
            loadQuestion(currentQuestionIndex)
        } else {
            Log.i(myTag, "Incorrect answer selected.")
            showToast("Incorrect answer. Try again!")

            // Reduce lives on incorrect answer
            livesLeft--
            updateLivesUI()

            // Check if lives are exhausted
            if (livesLeft <= 0) {
                showToast("No lives left. Game Over!")

                isUserWin = false

                val intent = Intent(this, ActivityResults::class.java)
                intent.putExtra("TOTAL_SCORE", 0)
                intent.putExtra("DIFFICULTY", difficulty)
                intent.putExtra("TOTAL_QUESTIONS", currentQuestionIndex)
                intent.putExtra("LIVES_REMAINING", livesLeft)
                intent.putExtra("IS_WIN", isUserWin)
                startActivity(intent)

                finish()
            }
        }
    }

    private fun updateLivesUI() {
        when (livesLeft) {
            3 -> {
                findViewById<ImageView>(R.id.quiz_first_life).setImageResource(R.drawable.good_leaf)
                findViewById<ImageView>(R.id.quiz_second_life).setImageResource(R.drawable.good_leaf)
                findViewById<ImageView>(R.id.quiz_third_life).setImageResource(R.drawable.good_leaf)
            }
            2 -> {
                findViewById<ImageView>(R.id.quiz_first_life).setImageResource(R.drawable.good_leaf)
                findViewById<ImageView>(R.id.quiz_second_life).setImageResource(R.drawable.good_leaf)
                findViewById<ImageView>(R.id.quiz_third_life).setImageResource(R.drawable.bad_leaf)
            }
            1 -> {
                findViewById<ImageView>(R.id.quiz_first_life).setImageResource(R.drawable.good_leaf)
                findViewById<ImageView>(R.id.quiz_second_life).setImageResource(R.drawable.bad_leaf)
                findViewById<ImageView>(R.id.quiz_third_life).setImageResource(R.drawable.bad_leaf)
            }
            0 -> {
                findViewById<ImageView>(R.id.quiz_first_life).setImageResource(R.drawable.bad_leaf)
                findViewById<ImageView>(R.id.quiz_second_life).setImageResource(R.drawable.bad_leaf)
                findViewById<ImageView>(R.id.quiz_third_life).setImageResource(R.drawable.bad_leaf)
            }
        }
    }

    private fun calculateBaseScore(questionIndex: Int, difficulty: String): Int {
        val baseScore = when (difficulty) {
            "easy" -> questionIndex
            "medium" -> (questionIndex ) * 2
            "hard" -> (questionIndex ) * 3
            else -> 0
        }
        return baseScore
    }

    private fun calculateLivesBonus(): Int {
        return when (livesLeft) {
            3 -> (currentQuestionIndex ) * 2
            2 -> currentQuestionIndex
            else -> 0
        }
    }
    private fun calculateQuestionsBonus(): Int {
        var questionsBonus = 0
        val n = currentQuestionIndex

        if (n > 10) questionsBonus += 2
        if (n > 15) questionsBonus += 4
        if (n > 20) questionsBonus += 8
        if (n > 25) questionsBonus += 16
        if (n > 30) questionsBonus += 32
        if (n > 35) questionsBonus += 64
        if (n > 40) questionsBonus += 128
        if (n > 45) questionsBonus += 256
        return questionsBonus
    }

    private fun updateTotalScore() {
        val difficulty = difficulty
        val baseScore = difficulty?.let { calculateBaseScore(currentQuestionIndex, it) }
        val livesBonus = calculateLivesBonus()
        val questionsBonus = calculateQuestionsBonus()


        if (baseScore != null) {
            totalScore = baseScore + livesBonus + questionsBonus
        }
        Log.i(myTag, "baseScore : $baseScore")
        Log.i(myTag, "livesBonus : $livesBonus")
        Log.i(myTag, "questionsBonus : $questionsBonus")
        Log.i(myTag, "Total score: $totalScore")
    }




    fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
