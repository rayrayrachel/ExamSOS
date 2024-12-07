package com.example.examsos

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.sanojpunchihewa.glowbutton.GlowButton

class ActivityResults : AppCompatActivity() {

    private val myTag = "Rachel'sTag"


    // Declare UI elements
    private lateinit var resultMessage: TextView
    private lateinit var leafImage: ImageView
    private lateinit var difficultyDisplay: TextView
    private lateinit var numberOfQuestionsDisplay: TextView
    private lateinit var livesRemainingDisplay: TextView
    private lateinit var scoreDisplay: TextView
    private lateinit var levelSelectionButton: GlowButton
    private lateinit var playAgainButton: GlowButton

    private var totalScore: Int = 0
    private var difficulty: String = "easy"
    private var totalQuestions: Int = 0
    private var livesRemaining: Int = 0
    private var isWin: Boolean = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results)

        val mToolbar = findViewById<Toolbar>(R.id.results_toolbar)
        setSupportActionBar(mToolbar)

        // Bind UI elements
        resultMessage = findViewById(R.id.result_message)
        leafImage = findViewById(R.id.leaf_image)
        difficultyDisplay = findViewById(R.id.difficulty_display)
        numberOfQuestionsDisplay = findViewById(R.id.number_of_questions_display)
        livesRemainingDisplay = findViewById(R.id.live_remaining)
        scoreDisplay = findViewById(R.id.score_display)
        levelSelectionButton = findViewById(R.id.level_selection_button)
        playAgainButton = findViewById(R.id.play_again_button)

        totalScore = intent.getIntExtra("TOTAL_SCORE", 0)
        difficulty = intent.getStringExtra("DIFFICULTY") ?: "hard"
        totalQuestions = intent.getIntExtra("TOTAL_QUESTIONS", 0)
        livesRemaining = intent.getIntExtra("LIVES_REMAINING", 0)
        isWin = intent.getBooleanExtra("IS_WIN", false)


        Log.i(myTag, "DIFFICULTY $difficulty")
        Log.i(myTag, "TOTAL_QUESTIONS $totalQuestions")
        Log.i(myTag, "LIVES_REMAINING $livesRemaining")
        Log.i(myTag, "TOTAL_SCORE $totalScore")
        Log.i(myTag, "IS_WIN $isWin")

        updateUI()


        levelSelectionButton.setOnClickListener {
            finish()
        }

        playAgainButton.setOnClickListener {
            //TODO redirect to Activity Quiz
            finish()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.sub_tool_bar_layout, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.upButton -> {
                finish()
                Log.i(myTag, "back clicked")
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }


    private fun updateUI() {
        // Set difficulty
        difficultyDisplay.text = getString(R.string.result_difficulty)+ difficulty

        // Set number of questions
        numberOfQuestionsDisplay.text = getString(R.string.result_number_of_questions)+ "$totalQuestions"

        // Set remaining lives
        livesRemainingDisplay.text = getString(R.string.result_lives_remaining_msg)+ "$livesRemaining"

        // Set score
        scoreDisplay.text = getString(R.string.result_total_leaves) + "$totalScore"

        // Check if the user won or lost
        if (isWin) {
            resultMessage.text = getString(R.string.result_winning_message)
            leafImage.setImageResource(R.drawable.good_leaf)
        } else {
            resultMessage.text = getString(R.string.result_losing_message)
            leafImage.setImageResource(R.drawable.bad_leaf)
        }
    }
}



