
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
import com.example.examsos.adapter.NotificationAdapter
import com.example.examsos.dataValue.NotificationDataClass

class FragmentNotifications : Fragment() {

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

            val intent = Intent(requireContext(), ActivityNotes::class.java)
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
            NotificationDataClass(
                "Hey [Name], New Questions",
                "We've added new Biology questions specifically tailored to boost your understanding based on your last quiz results. Check them out and ace your exam!"
            ),
            NotificationDataClass(
                "Hi [Name], Quick Reminder!",
                "Your Math homework is due tomorrow! Make sure to submit it on time. Need any last-minute help?"
            ),
            NotificationDataClass(
                "Study Tip for You, [Name]!",
                "We noticed you've been working hard. To make even greater progress, focusing on Geometry could really improve your scores. Ready to dive in?"
            ),
            NotificationDataClass(
                "[Name], Your Mock Test Results Are In!",
                "Check out your Chemistry mock test results on your dashboard. Take a look and see how you've performed!"
            ),
            NotificationDataClass(
                "Get Ready for Your Live Tutoring Session, [Name]!",
                "You’ve got a live tutoring session today at 4 PM. It’s a great chance to get help with any tough questions. See you there!"
            ),
            NotificationDataClass(
                "A New Video Just for You, [Name]!",
                "We've uploaded a video on 'Effective Study Techniques' that could be a game-changer for your study routine. Watch it and sharpen your skills!"
            ),
            NotificationDataClass(
                "Stay Consistent, [Name]!",
                "Just a friendly nudge: spend at least 30 minutes reviewing your notes today. Small, daily efforts lead to big wins!"
            ),
            NotificationDataClass(
                "Want to Join a Study Group, [Name]?",
                "You’ve been invited to a group study session for the upcoming History exam. It’s a great way to collaborate. Want to join in?"
            ),
            NotificationDataClass(
                "Way to Go, [Name]! Achievement Unlocked!",
                "Congratulations! You’ve completed 10 quizzes. That’s fantastic progress. Keep pushing forward!"
            ),
            NotificationDataClass(
                "We Value Your Input, [Name]!",
                "We’d love to get your thoughts on our recent features. Your feedback helps us improve. Care to share?"
            )      )
    }

}
