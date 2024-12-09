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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ActivityQuizSelection : AppCompatActivity() {

    private val myTag = "Rachel'sTag"
    private lateinit var cardType : String
    private lateinit var categoryList: List<TriviaCategory>
    private val api = RetrofitClient.api

    private val currentUser = FirebaseAuth.getInstance().currentUser
    private val db = FirebaseFirestore.getInstance()
    private val userDocRef = currentUser?.let { db.collection("users").document(it.uid) }

    override fun onCreate(savedInstanceState: Bundle?) {
        cardType = intent.getStringExtra("CARD TYPE").toString()

        super.onCreate(savedInstanceState)

        Log.i(myTag, "*** QuizActivity: In onCreate")


        if (cardType == "custom") {
            runOnUiThread {
                setContentView(R.layout.activity_quiz_selection)

                val mToolbar = findViewById<Toolbar>(R.id.quiz_selection_toolbar)
                setSupportActionBar(mToolbar)

                fetchTriviaCategories()
                setupQuizTypeSpinner()
            }
        }
        else {
            fetchTriviaCategories()
            finish()
        }
    }

    /**
     * Fetch the trivia categories and update RecyclerView with the data.
     */
    private fun fetchTriviaCategories() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.api.getCategories()
                categoryList = response.trivia_categories

                Log.d(myTag, "Categories fetched: $categoryList")

                when (cardType) {
                    "custom" -> {
                        Log.i(myTag, "SELECTED CUSTOM CARD (show customise quiz selections, i.e. user can select their own quiz parameters)")

                        runOnUiThread {
                            setupRecyclerViewWithData(categoryList)
                        }

                    }
                    "daily" -> {
                        Log.i(myTag, "SELECTED DAILY CARD (i.e. set parameters as user configuration in settings, if user hasn't set it before, use default)")
                        runOnUiThread {
                        handleDailyQuiz(categoryList)
                        }
                    }
                    "random" -> {
                        Log.i(myTag, "SELECTED RANDOM CARD (i.e. randomize parameters)")
                        handleRandomQuiz(categoryList)
                    }
                    "marathon" -> {
                        Log.i(myTag, "SELECTED MARATHON CARD (i.e. setting question size larger than api that can be fetched )")
                        handleMarathonQuiz()
                    }
                    else -> {
                        // Handle default case
                    }
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
            Log.d(myTag, "Customised Category: ${selectedCategory.name}, Difficulty: $difficulty, Number Of Questions: $numberOfQuestions, Type: $quizType")

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
                    Log.w(myTag, "The API doesn't return total count of each difficulty's specific type, it only count for all types.")
                }

                // Return the available count to the callback
                callback(availableCount)

            } catch (e: Exception) {
                Log.e(myTag, "Error fetching category question count", e)
                callback(0)  // If there's an error, assume no questions are available
            }
        }
    }

    private fun handleRandomQuiz(data: List<TriviaCategory>){
        val randomCategory = data.random()
        var randomNumberOfQuestions = (5..50).random()
        val randomDifficulty = listOf("easy", "medium", "hard").random()
        //    val randomQuizType = listOf("multiple", "boolean", null).random()
        val randomQuizType = null


        Log.d(myTag, "Random Generated Category: ${randomCategory.name}, Difficulty: $randomDifficulty, Number Of Questions: $randomNumberOfQuestions, Type: $randomQuizType")

        fetchCategoryQuestionCount(randomCategory.id, randomDifficulty, randomQuizType) { availableCount ->
            // Ensure requested number of questions doesn't exceed available count
            if (randomNumberOfQuestions > availableCount) {
                randomNumberOfQuestions = availableCount
            }
            // Proceed with fetching the questions
            fetchQuestions(randomCategory, randomDifficulty, randomNumberOfQuestions, randomQuizType)
        }

        //finish after transitioning
        runOnUiThread {
            finish()
        }
    }

    private fun handleDailyQuiz(data: List<TriviaCategory>) {
        fetchDailyQuizSettings { difficulty, numberOfQuestions, quizType, categoryId ->
            val selectedCategory = data.find { it.id == categoryId } ?: data.random()
            var adjustedNumberOfQuestions = numberOfQuestions
            Log.d(
                myTag,
                "Daily Quiz Settings: Category=${selectedCategory.name}, Difficulty=$difficulty, Number Of Questions=$numberOfQuestions, Type=$quizType"
            )
            fetchCategoryQuestionCount(selectedCategory.id, difficulty, null) { availableCount ->
                if (adjustedNumberOfQuestions > availableCount) {
                    adjustedNumberOfQuestions = availableCount
                }
                fetchQuestions(selectedCategory, difficulty, adjustedNumberOfQuestions, quizType)
            }
            runOnUiThread {
                finish()
            }
        }
    }


    private fun handleMarathonQuiz() {
        val marathonCategoryId = 9 // General Knowledge category ID
        val totalQuestionsPerDifficulty = 50
        val difficulties = listOf("easy", "medium", "hard")
        var fetchCount = 0

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val allQuestions = mutableListOf<QuizQuestion>()

                // Define the selected category (General Knowledge category)
                val selectedCategory = TriviaCategory(id = marathonCategoryId, name = "General Knowledge")

                for (difficulty in difficulties) {

                    val response = api.getQuestions(
                        amount = totalQuestionsPerDifficulty,
                        category = selectedCategory.id,
                        difficulty = difficulty,
                        type = null
                    )

                    if (response.results.isEmpty()) {
                        Log.e(myTag, "No questions fetched for difficulty $difficulty")
                        return@launch
                    }

                    // Convert TriviaQuestions to QuizQuestions
                    val quizQuestions = response.results.map { triviaQuestion ->
                        QuizQuestion(
                            category = triviaQuestion.category,
                            type = triviaQuestion.type,
                            difficulty = triviaQuestion.difficulty,
                            question = triviaQuestion.question,
                            options = ArrayList((triviaQuestion.incorrect_answers + triviaQuestion.correct_answer).shuffled()),
                            correctAnswer = triviaQuestion.correct_answer
                        )
                    }
                    // Add fetched questions to the list
                    allQuestions.addAll(quizQuestions)

                    fetchCount++

                    if(fetchCount<3) {
                        Log.i(myTag, "Fetched 50 Questions $fetchCount time, Wait 5 Seconds")
                        delay(5000)     //have to wait 5 seconds between fetch API
                    }
                    else
                    {
                        Log.i(myTag, "Fetched 50 Questions $fetchCount time")
                    }

                }

                // Once all questions are fetched, pass them to the Activity Quiz
                runOnUiThread {
                    val intent = Intent(this@ActivityQuizSelection, ActivityQuiz::class.java)
                    intent.putParcelableArrayListExtra("QUESTIONS", ArrayList(allQuestions))
                    intent.putExtra("DIFFICULTY_SELECTED", "marathon")
                    startActivity(intent)
                }
            } catch (e: Exception) {
                Log.e(myTag, "Error fetching marathon questions", e)
                runOnUiThread {
                    Toast.makeText(
                        this@ActivityQuizSelection,
                        "Failed to fetch Marathon questions. Please try again.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    /**
     * Fetch the questions based on selected options.
     */

    private fun fetchQuestions(selectedCategory: TriviaCategory, difficulty: String, numberOfQuestions: Int, quizType: String?) {
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

    //fetch stored daily quiz preference
    private fun fetchDailyQuizSettings(callback: (String, Int, String?, Int) -> Unit) {
        userDocRef?.get()
            ?.addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    // Fetch from Firestore
                    val difficulty = document.getString("selectedDifficulty") ?: "easy"
                    val numberOfQuestions = document.getLong("numberOfQuestions")?.toInt() ?: 5
                    val quizType = when (document.getString("selectedType")) {
                        "Any Type" -> null
                        "Multiple Choice" -> "multiple"
                        "True/False" -> "boolean"
                        else -> null
                    }
                    val categoryName = document.getString("selectedCategory") ?: "General Knowledge"

                    val categoryId = categoryList.find { it.name == categoryName }?.id ?: 9  // Default to category ID 9

                    Log.d(
                        myTag,
                        "Fetched daily quiz settings: Difficulty=$difficulty, Questions=$numberOfQuestions, Type=$quizType, Category=$categoryId"
                    )
                    // Return the fetched settings through the callback
                    callback(difficulty, numberOfQuestions, quizType, categoryId)
                } else {
                    Log.d(myTag, "No user-specific quiz settings found, using defaults.")
                    callback("easy", 5, null, 9)  // Default settings
                }
            }
            ?.addOnFailureListener { exception ->
                Log.e(myTag, "Error fetching quiz settings", exception)
                callback("easy", 5, null, 9)  // Default settings
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
