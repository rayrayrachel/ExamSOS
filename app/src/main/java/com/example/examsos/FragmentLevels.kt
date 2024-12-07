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
        view.findViewById<CardView>(R.id.fragment_category_custom).setOnClickListener {
            handleCardClick("custom", getString(R.string.toast_message_fragment_category_custom))
        }

        view.findViewById<CardView>(R.id.fragment_category_daily).setOnClickListener {
            handleCardClick("daily", getString(R.string.toast_message_fragment_category_daily))
        }

        view.findViewById<CardView>(R.id.fragment_category_random).setOnClickListener {
            handleCardClick("random", getString(R.string.toast_message_fragment_category_random))
        }

        view.findViewById<CardView>(R.id.fragment_category_marathon).setOnClickListener {
            handleCardClick("marathon", getString(R.string.fragment_category_marathon))
        }
    }

    private fun handleCardClick(cardType: String, toastMessage: String) {

        Toast.makeText(requireContext(), toastMessage, Toast.LENGTH_SHORT).show()

        val intent = Intent(requireContext(), ActivityQuizSelection::class.java)
        intent.putExtra("CARD TYPE", cardType)
        startActivity(intent)
    }

}

