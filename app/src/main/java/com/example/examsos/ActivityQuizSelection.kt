package com.example.examsos

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.examsos.adapter.CategoryAdapter
import com.example.examsos.api.RetrofitClient
import com.example.examsos.dataValue.QuizQuestion
import com.example.examsos.dataValue.TriviaCategory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ActivityQuizSelection : AppCompatActivity() {

    private val myTag = "Rachel'sTag"
    val api = RetrofitClient.api

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_selection)

        val mToolbar = findViewById<Toolbar>(R.id.quiz_selection_toolbar)
        setSupportActionBar(mToolbar)

        Log.i(myTag, "*** QuizActivity: In onCreate")

        // Set up the RecyclerView
        fetchTriviaCategories()

        setupQuizTypeSpinner()
    }

    /**
     * Fetch the trivia categories and update RecyclerView with the data.
     */
    private fun fetchTriviaCategories() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.api.getCategories()
                val categoryList = response.trivia_categories

                Log.d(myTag, "Categories fetched: $categoryList")

                runOnUiThread {
                    setupRecyclerViewWithData(categoryList)
                }
            } catch (e: Exception) {
                Log.e(myTag, "Error fetching trivia categories", e)
            }
        }
    }

    /**
     * Set up the RecyclerView with category data and handle category selection.
     */
    private fun setupRecyclerViewWithData(data: List<TriviaCategory>) {
        val recyclerView = findViewById<RecyclerView>(R.id.level_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val adapter = CategoryAdapter(data) { selectedCategory ->

            // Fetch selected difficulty level
            val difficulty = when (findViewById<RadioGroup>(R.id.level_radio_group).checkedRadioButtonId) {
                R.id.level_easy -> "easy"
                R.id.level_medium -> "medium"
                R.id.level_hard -> "hard"
                else -> "easy"
            }

            // Fetch the number of question
            val questionSlider = findViewById<com.google.android.material.slider.Slider>(R.id.number_of_questions_slider)
            var numberOfQuestions = questionSlider.value.toInt()

            // Fetch selected quiz type
            val quizType = findViewById<Spinner>(R.id.category_spinner).selectedItem?.toString()?.let { selected ->
                when (selected) {
                    "Any Type" -> null
                    "Multiple Choice" -> "multiple"
                    "True/False" -> "boolean"
                    else -> null
                }
            }

            // Log the selected values
            Log.d(myTag, "Category: ${selectedCategory.name}, Difficulty: $difficulty, Questions: $numberOfQuestions, Type: $quizType")

            // Fetch available questions count for selected category and difficulty
            fetchCategoryQuestionCount(selectedCategory.id, difficulty, quizType) { availableCount ->
                // Ensure requested number of questions doesn't exceed available count
                if (numberOfQuestions > availableCount) {
                    numberOfQuestions = availableCount
                    runOnUiThread {
                        Toast.makeText(
                            this@ActivityQuizSelection,
                            "Only $availableCount questions available. Adjusting your selection.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                // Proceed with fetching the questions
                fetchQuestions(selectedCategory, difficulty, numberOfQuestions, quizType)
            }
        }

        recyclerView.adapter = adapter
    }
    /**
     * Fetch the available question count for a specific category and difficulty level.
     */
    private fun fetchCategoryQuestionCount(categoryId: Int, difficulty: String, quizType: String?, callback: (Int) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Fetch the category's data from the API
                val response = RetrofitClient.api.getCategoryQuestionCount(categoryId)

                // Log the raw response
                Log.d(myTag, "Raw response: $response")

                // Retrieve the total question count based on difficulty
                val availableCount = when (difficulty) {
                    "easy" -> response.category_question_count.total_easy_question_count
                    "medium" -> response.category_question_count.total_medium_question_count
                    "hard" -> response.category_question_count.total_hard_question_count
                    else -> response.category_question_count.total_question_count
                }

                // Handle the case where no questions are available for the category
                if (availableCount == 0) {
                    runOnUiThread {
                        Toast.makeText(
                            this@ActivityQuizSelection,
                            "No questions available for this category and difficulty level.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    callback(0)  // No questions available, call back 0
                    return@launch
                }

                // Check if quiz type is selected
                if (quizType != null) {
                    //the api only return question count for different difficulty without quiz type,
                // so when quiz type is not null, it has different count, and affect the logic,
                // since the api does not provide, all i could do is to not proceed
                }

                // Return the available count to the callback
                callback(availableCount)

            } catch (e: Exception) {
                Log.e(myTag, "Error fetching category question count", e)
                callback(0)  // If there's an error, assume no questions are available
            }
        }
    }

    /**
     * Fetch the questions based on selected options.
     */private fun fetchQuestions(selectedCategory: TriviaCategory, difficulty: String, numberOfQuestions: Int, quizType: String?) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = api.getQuestions(
                    amount = numberOfQuestions,
                    category = selectedCategory.id,
                    difficulty = difficulty,
                    type = quizType
                )
                Log.d(myTag, "Questions fetched: ${response.results}")

                // Check if no questions were gotten from the API
                if (response.results.isEmpty()) {
                    runOnUiThread {
                        Toast.makeText(
                            this@ActivityQuizSelection,
                            "Not enough questions available for this selection.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    // Process the questions and prepare them as QuizQuestion
                    val questions = response.results.map { triviaQuestion ->
                        // Create QuizQuestion by mapping TriviaQuestion data
                        QuizQuestion(
                            category = triviaQuestion.category,
                            type = triviaQuestion.type,
                            difficulty = triviaQuestion.difficulty,
                            question = triviaQuestion.question,
                            options = ArrayList((triviaQuestion.incorrect_answers + triviaQuestion.correct_answer).shuffled()), // Shuffling the answers
                            correctAnswer = triviaQuestion.correct_answer
                        )
                    }

                    // Pass the QuizQuestion list to ActivityQuiz
                    runOnUiThread {
                        val intent = Intent(this@ActivityQuizSelection, ActivityQuiz::class.java)
                        intent.putParcelableArrayListExtra("QUESTIONS", ArrayList(questions)) // Pass as Parcelable
                        intent.putExtra("DIFFICULTY_SELECTED", difficulty)
                        startActivity(intent)
                    }
                }

            } catch (e: Exception) {
                Log.e(myTag, "Failed to fetch questions", e)
                runOnUiThread {
                    Toast.makeText(
                        this@ActivityQuizSelection,
                        "Failed to fetch questions. Please try again.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    /**
     * Set up the quiz type spinner.
     */
    private fun setupQuizTypeSpinner() {
        val categorySpinner = findViewById<Spinner>(R.id.category_spinner)

        val quizTypeList = listOf(
            "Any Type",
            "Multiple Choice",
            "True/False"
        )

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            quizTypeList
        )

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        categorySpinner.adapter = adapter

        categorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedType = parent?.getItemAtPosition(position).toString()
                if (position != 0) {
                     Toast.makeText(this@ActivityQuizSelection, "Selected: $selectedType", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
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
    override fun onStart() {
        super.onStart()
        Log.i(myTag, "*** QuizActivity: In onStart")
    }

    override fun onStop() {
        super.onStop()
        Log.i(myTag, "*** QuizActivity: In onStop")
    }

    override fun onPause() {
        super.onPause()
        Log.i(myTag, "*** QuizActivity: In onPause")
    }

    override fun onRestart() {
        super.onRestart()
        Log.i(myTag, "*** QuizActivity: In onRestart")
    }

    override fun onResume() {
        super.onResume()
        Log.i(myTag, "*** QuizActivity: In onResume")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(myTag, "*** QuizActivity: In onDestroy")
    }
}
