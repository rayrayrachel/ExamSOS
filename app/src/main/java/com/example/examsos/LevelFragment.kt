package com.example.examsos

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment

class LevelFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_level, container, false)


        val buttonClick = view.findViewById<Button>(R.id.enter_level_button_id)
        buttonClick.setOnClickListener {

            val intent = Intent(requireContext(), QuizActivity::class.java)
            startActivity(intent)
        }

        return view
    }
}
