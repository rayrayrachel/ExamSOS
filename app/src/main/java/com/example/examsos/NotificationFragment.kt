
package com.example.examsos

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class NotificationFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var notificationAdapter: NotificationAdapter
    private lateinit var notificationList: ArrayList<NotificationDataClass>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_notification, container, false)

        val buttonClick = view.findViewById<Button>(R.id.check_all_notification_button)
        buttonClick.setOnClickListener {

            val intent = Intent(requireContext(), NotesActivity::class.java)
            startActivity(intent)
            Toast.makeText(requireContext(), getString(R.string.toast_message_check_all_notification_button), Toast.LENGTH_SHORT).show()

        }

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.notification_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)

        // Create dummy data and set the adapter
        notificationList = getDummyNotifications()
        notificationAdapter = NotificationAdapter(notificationList)
        recyclerView.adapter = notificationAdapter

        return view
    }

    private fun getDummyNotifications(): ArrayList<NotificationDataClass> {
        return arrayListOf(
            NotificationDataClass("New Study Material Available!", "We have uploaded new practice questions for the Biology exam based on your last quiz performance."),
            NotificationDataClass("Reminder: Upcoming Deadline", "You have a homework assignment due tomorrow for the Math course. Don't forget to submit!"),
            NotificationDataClass("Personalized Study Suggestions", "Based on your recent activity, we suggest focusing on Geometry to improve your scores."),
            NotificationDataClass("Mock Test Results", "Your results for the recent Chemistry mock test are ready. Check your dashboard for details."),
            NotificationDataClass("Live Tutoring Available", "You have a live tutoring session scheduled for 4 PM today. Prepare your questions!"),
            NotificationDataClass("New Video Tutorial", "A new video tutorial on 'Effective Study Techniques' has been added. Watch it to improve your study habits."),
            NotificationDataClass("Daily Study Reminder", "Remember to spend at least 30 minutes reviewing your notes today. Consistency is key!"),
            NotificationDataClass("New Group Study Invitation", "You have been invited to join a group study session for the upcoming History exam. Would you like to join?"),
            NotificationDataClass("Achievement Unlocked!", "Congratulations! You’ve completed 10 quizzes. Keep up the great work!"),
            NotificationDataClass("Feedback Request", "We’d love to hear your thoughts on our recent features. Please take a moment to give us feedback.")
        )
    }

}
