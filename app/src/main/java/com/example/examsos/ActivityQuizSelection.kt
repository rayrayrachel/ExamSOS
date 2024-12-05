package com.example.examsos

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.examsos.adapter.LevelAdapter
import com.example.examsos.dataValue.LevelDataClass
import com.example.examsos.R
import com.example.examsos.adapter.CategoryAdapter
import com.example.examsos.api.RetrofitClient
import com.example.examsos.dataValue.TriviaCategory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class ActivityQuizSelection : AppCompatActivity() {

    private val myTag = "Rachel'sTag"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_selection)

        val mToolbar = findViewById<Toolbar>(R.id.sub_toolbar)
        setSupportActionBar(mToolbar)

        Log.i(myTag, "*** QuizActivity: In onCreate")

        // Set up the RecyclerView
        fetchTriviaCategories()

        setupQuizTypeSpinner()
    }

    /**
     * Sets up the RecyclerView with sample data and the LevelAdapter.
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



    private fun setupRecyclerViewWithData(categories: List<TriviaCategory>) {
        val recyclerView = findViewById<RecyclerView>(R.id.level_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val adapter = CategoryAdapter(categories)
        recyclerView.adapter = adapter
    }




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
                if (position != 0) { // Ignore the placeholder item
                 //   Toast.makeText(this@MainActivity, "Selected: $selectedType", Toast.LENGTH_SHORT).show()
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
