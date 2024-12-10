package com.example.examsos

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.examsos.adapter.QuizRecordsAdapter
import com.example.examsos.dataValue.QuizRecord
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ActivityNotes : AppCompatActivity() {

    private val myTag = "Rachel's Tag"

    private val db = FirebaseFirestore.getInstance()
    private val currentUser = FirebaseAuth.getInstance().currentUser
    private val userDocRef = currentUser?.let { db.collection("users").document(it.uid) }

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: QuizRecordsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note)

        val mToolbar = findViewById<Toolbar>(R.id.notes_toolbar)
        setSupportActionBar(mToolbar)

        // Check user authentication
        if (currentUser == null) {
            Toast.makeText(this, "User not authenticated!", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.quizRecordsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Fetch quiz records
        fetchQuizRecords()
    }

    private fun fetchQuizRecords() {
        userDocRef?.collection("quizRecords")
            ?.get()
            ?.addOnSuccessListener { querySnapshot ->
                val quizRecords = mutableListOf<QuizRecord>()

                for (document in querySnapshot.documents) {
                    try {
                        val record = document.toObject(QuizRecord::class.java)
                        if (record != null) {
                            quizRecords.add(record)
                        } else {
                            Log.e(myTag, "Null record parsed: ${document.id}")
                        }
                    } catch (e: Exception) {
                        Log.e(myTag, "Error parsing document: ${document.data}", e)
                    }
                }


                adapter = QuizRecordsAdapter(quizRecords)
                recyclerView.adapter = adapter


                displayQuizRecords(quizRecords)
            }
            ?.addOnFailureListener { exception ->
                Log.e(myTag, "Error fetching records", exception)
                Toast.makeText(this, "Failed to fetch quiz records.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun displayQuizRecords(quizRecords: List<QuizRecord>) {
        for (record in quizRecords) {
            Log.d(myTag, "Timestamp: ${record.timestamp}")
            Log.d(myTag, "Score: ${record.score}")
            Log.d(myTag, "Difficulty: ${record.difficulty}")
            Log.d(myTag, "Total Questions: ${record.totalQuestions}")
            Log.d(myTag, "Lives Remaining: ${record.livesRemaining}")
            Log.d(myTag, "Category: ${record.category}")
            Log.d(myTag, "Is Win: ${record.isWin}")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.sub_tool_bar_layout, menu)
        val refreshButton = menu?.findItem(R.id.refresh)
        refreshButton?.isVisible = false
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.upButton -> {
                finish()
                Log.i(myTag, "Up Button clicked")
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
