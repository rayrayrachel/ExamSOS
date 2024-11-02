package com.example.examsos

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment

class FragmentLevels : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_levels, container, false)

        setupCardClickListeners(view)

        return view
    }

    private fun setupCardClickListeners(view: View) {
        view.findViewById<CardView>(R.id.fragment_level_category_humanities).setOnClickListener {
            // Handle click for preschool card
            Toast.makeText(requireContext(), getString(R.string.toast_message_fragment_level_category_humanities), Toast.LENGTH_SHORT).show()

            startActivity(Intent(requireContext(), ActivityQuizSelection::class.java))
        }

        view.findViewById<CardView>(R.id.fragment_level_category_STEM).setOnClickListener {
            // Handle click for middle school card
            Toast.makeText(requireContext(), getString(R.string.toast_message_fragment_level_category_stem), Toast.LENGTH_SHORT).show()
            startActivity(Intent(requireContext(), ActivityQuizSelection::class.java))
        }

        view.findViewById<CardView>(R.id.fragment_level_category_Literature).setOnClickListener {
            // Handle click for high school card
            Toast.makeText(requireContext(), getString(R.string.toast_message_fragment_level_category_literature), Toast.LENGTH_SHORT).show()

            startActivity(Intent(requireContext(), ActivityQuizSelection::class.java))
        }

        view.findViewById<CardView>(R.id.fragment_level_category_biology).setOnClickListener {
            // Handle click for advanced card
            Toast.makeText(requireContext(), getString(R.string.toast_message_fragment_level_category_biology), Toast.LENGTH_SHORT).show()

            startActivity(Intent(requireContext(), ActivityQuizSelection::class.java))
        }
    }
}
