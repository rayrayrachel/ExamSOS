package com.example.examsos

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import java.text.SimpleDateFormat
import java.util.*

class FragmentHome : Fragment() {

    private val myTag = "Rachel's Tag" // For logging
    private val dayToImageMap = mutableMapOf<String, ImageView>() // Mapping days to image view
    private val currentUser = FirebaseAuth.getInstance().currentUser // Current user
    private val db = FirebaseFirestore.getInstance() // Firestore instance
    private val userDocRef = currentUser?.let { db.collection("users").document(it.uid) }

    private var snapshotListener: ListenerRegistration? = null // Firestore listener reference


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Map day names to their image views
        dayToImageMap["Monday"] = view.findViewById(R.id.leaf_monday)
        dayToImageMap["Tuesday"] = view.findViewById(R.id.leaf_tuesday)
        dayToImageMap["Wednesday"] = view.findViewById(R.id.leaf_wednesday)
        dayToImageMap["Thursday"] = view.findViewById(R.id.leaf_thursday)
        dayToImageMap["Friday"] = view.findViewById(R.id.leaf_friday)
        dayToImageMap["Saturday"] = view.findViewById(R.id.leaf_saturday)
        dayToImageMap["Sunday"] = view.findViewById(R.id.leaf_sunday)

        // Quick Start button
        val buttonClick = view.findViewById<Button>(R.id.quick_start_button_id)
        buttonClick.setOnClickListener {
            val intent = Intent(requireContext(), ActivityQuiz::class.java)
            startActivity(intent)
            Toast.makeText(requireContext(), getString(R.string.toast_quick_start_button), Toast.LENGTH_SHORT).show()
        }

        // Start listening to Firestore
        startListeningToFirestore()

        return view
    }

    /**
     * Start listening to Firestore
     */
    private fun startListeningToFirestore() {
        snapshotListener = userDocRef?.addSnapshotListener { document, exception ->
            if (exception != null) {
                Log.e(myTag, "Error listening to Firestore updates", exception)
                return@addSnapshotListener
            }

            if (document != null && document.exists()) {
                // Fetch the loginDates array from Firestore
                val loginDates = document.get("loginDates") as? List<Long> ?: emptyList()
                val loggedInDays = getDaysFromTimestamps(loginDates)

                // Update UI with good or bad leave
                updateLeaves(loggedInDays)
            } else {
                Log.w(myTag, "User document does not exist.")
            }
        }
    }

    /**
     * Stop listening to Firestore.
     */
    private fun stopListeningToFirestore() {
        snapshotListener?.remove()
    }

    /**
     * Converts a list of timestamp .
     */
    private fun getDaysFromTimestamps(timestamps: List<Long>): Set<String> {
        val calendar = Calendar.getInstance()
        calendar.timeZone = TimeZone.getDefault()
        val dayFormat = SimpleDateFormat("EEEE", Locale.getDefault())

        // Get the start of current week
        calendar.firstDayOfWeek = Calendar.MONDAY
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfWeek = calendar.timeInMillis

        // Get the end of current week
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
        calendar.add(Calendar.DAY_OF_YEAR, 1) // Add 1 to include full day
        val endOfWeek = calendar.timeInMillis

        return timestamps.filter { it in startOfWeek until endOfWeek }
            .map { timestamp ->
                val date = Date(timestamp)
                dayFormat.format(date) // Convert to day name
            }.toSet()
    }

    /**
     * Updates the UI by setting good leaf or bad leaf icons based on logged-in days.
     */
    private fun updateLeaves(loggedInDays: Set<String>) {
        for ((day, imageView) in dayToImageMap) {
            if (loggedInDays.contains(day)) {
                imageView.setImageResource(R.drawable.good_leaf) // Set good leaf
            } else {
                imageView.setImageResource(R.drawable.bad_leaf) // Set bad leaf
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Stop listening to Firestore
        stopListeningToFirestore()
    }
}
