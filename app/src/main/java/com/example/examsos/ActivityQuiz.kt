package com.example.examsos

import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.Menu
import android.view.MenuItem
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
        //TODO handle True and False Logic
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
            //TODO go to new activity
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
            //TODO check leave logic
        }
    }

    fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
