package com.example.examsos

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.examsos.adapter.QuizRecordsAdapter
import com.example.examsos.dataValue.QuizRecord
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class FragmentNotes : Fragment() {

    private val myTag = "Rachel's Tag"

    private val db = FirebaseFirestore.getInstance()
    private val currentUser = FirebaseAuth.getInstance().currentUser
    private val userDocRef = currentUser?.let { db.collection("users").document(it.uid) }

    private lateinit var categoryTextView: TextView
    private lateinit var timestampTextView: TextView
    private lateinit var difficultyTextView: TextView
    private lateinit var attemptedQuestionsTextView: TextView
    private lateinit var livesRemainingTextView: TextView
    private lateinit var scoreTextView: TextView
    private lateinit var winTextView: TextView
    private lateinit var notificationIcon: ImageView
    private lateinit var recentNoteCardView: CardView

    private lateinit var attemptNumberCount: TextView
    private lateinit var highScoreCount: TextView
    private lateinit var totalScoreCount: TextView


    private lateinit var quizRecordsList: List<QuizRecord>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_notes, container, false)

        val buttonClick = view.findViewById<Button>(R.id.go_to_notes_button)
        buttonClick.setOnClickListener {
            val intent = Intent(requireContext(), ActivityNotes::class.java)
            startActivity(intent)
            Toast.makeText(requireContext(), getString(R.string.toast_message_go_to_notes_button), Toast.LENGTH_SHORT).show()
        }

        // Initialize views
        categoryTextView = view.findViewById(R.id.categoryTextView)
        timestampTextView = view.findViewById(R.id.timestamp)
        difficultyTextView = view.findViewById(R.id.difficultyTextView)
        attemptedQuestionsTextView = view.findViewById(R.id.attemptedQuestionNumberTextView)
        livesRemainingTextView = view.findViewById(R.id.livesRemainingTextView)
        scoreTextView = view.findViewById(R.id.scoreTextView)
        winTextView = view.findViewById(R.id.winTextView)
        notificationIcon = view.findViewById(R.id.note_item_icon)
        recentNoteCardView = view.findViewById(R.id.recentNoteCard)
        attemptNumberCount = view.findViewById(R.id.attemptNumberCount)
        highScoreCount = view.findViewById(R.id.highScoreCount)
        totalScoreCount = view.findViewById(R.id.totalScoreCount)


        // Fetch quiz records and display the most recent
        fetchQuizRecords()

        return view
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

                if (quizRecords.isNotEmpty()) {
                    quizRecordsList = quizRecords

                    // Get the most recent record
                    val recentRecord = quizRecordsList.maxByOrNull { it.timestamp }

                    val totalAttempts = quizRecordsList.size
                    attemptNumberCount.text = totalAttempts.toString()

                    // Calculate highest score
                    val highestScore = quizRecordsList.maxByOrNull { it.score }?.score ?: 0
                    highScoreCount.text = highestScore.toString()


                    if (recentRecord != null) {
                        categoryTextView.text = recentRecord.category
                        timestampTextView.text = "Accessed Date: ${recentRecord.timestamp}"
                        difficultyTextView.text = "Difficulty: ${recentRecord.difficulty}"
                        attemptedQuestionsTextView.text = "Attempted Questions: ${recentRecord.totalQuestions}"
                        livesRemainingTextView.text = "Lives Remaining: ${recentRecord.livesRemaining}"
                        scoreTextView.text = "Score: ${recentRecord.score}"
                        winTextView.text = if (recentRecord.isWin) "COMPLETED" else "LOSS"

                        // Set icon based on win status
                        if (recentRecord.isWin) {
                            notificationIcon.setImageResource(R.drawable.good_leaf)
                            winTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.theme_primary))
                            recentNoteCardView.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.mint_green))


                        } else {
                            notificationIcon.setImageResource(R.drawable.bad_leaf)
                            winTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
                            recentNoteCardView.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.medium_gray))
                        }
                    }
                } else {
                    Log.e(myTag, "No quiz records found")
                }

                userDocRef.get().addOnSuccessListener { document ->
                    val cumulatedScore = document.getLong("score") ?: 0L
                    totalScoreCount.text = cumulatedScore.toString()
                }
            }
            ?.addOnFailureListener { exception ->
                Log.e(myTag, "Error fetching records", exception)
            }
    }

    private fun displayQuizRecords(quizRecordsList: List<QuizRecord>) {
        for (record in quizRecordsList) {
            Log.d(myTag, "Timestamp: ${record.timestamp}")
            Log.d(myTag, "Score: ${record.score}")
            Log.d(myTag, "Difficulty: ${record.difficulty}")
            Log.d(myTag, "Total Questions: ${record.totalQuestions}")
            Log.d(myTag, "Lives Remaining: ${record.livesRemaining}")
            Log.d(myTag, "Category: ${record.category}")
            Log.d(myTag, "Is Win: ${record.isWin}")
        }
    }
}

