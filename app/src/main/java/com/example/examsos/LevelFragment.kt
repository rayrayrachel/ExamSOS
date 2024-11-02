package com.example.examsos

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment

class LevelFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_levels, container, false)

        setupCardClickListeners(view)

        return view
    }

    private fun setupCardClickListeners(view: View) {
        view.findViewById<CardView>(R.id.fragment_level_category_preschool).setOnClickListener {
            // Handle click for preschool card
            Toast.makeText(requireContext(), getString(R.string.toast_message_fragment_level_category_preschool), Toast.LENGTH_SHORT).show()

            startActivity(Intent(requireContext(), QuizActivity::class.java))
        }

        view.findViewById<CardView>(R.id.fragment_level_category_middleschool).setOnClickListener {
            // Handle click for middle school card
            Toast.makeText(requireContext(), getString(R.string.toast_message_fragment_level_category_middleschool), Toast.LENGTH_SHORT).show()
            startActivity(Intent(requireContext(), QuizActivity::class.java))
        }

        view.findViewById<CardView>(R.id.fragment_level_category_highschool).setOnClickListener {
            // Handle click for high school card
            Toast.makeText(requireContext(), getString(R.string.toast_message_fragment_level_category_highschool), Toast.LENGTH_SHORT).show()

            startActivity(Intent(requireContext(), QuizActivity::class.java))
        }

        view.findViewById<CardView>(R.id.fragment_level_category_advanced).setOnClickListener {
            // Handle click for advanced card
            Toast.makeText(requireContext(), getString(R.string.toast_message_fragment_level_category_advanced), Toast.LENGTH_SHORT).show()

            startActivity(Intent(requireContext(), QuizActivity::class.java))
        }
    }
}
