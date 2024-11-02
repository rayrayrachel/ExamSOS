package com.example.examsos

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {

        val view = inflater.inflate(R.layout.fragment_home, container, false)


        val buttonClick = view.findViewById<Button>(R.id.quick_start_button_id)
        buttonClick.setOnClickListener {

            val intent = Intent(requireContext(), QuizActivity::class.java)
            startActivity(intent)
            Toast.makeText(requireContext(), getString(R.string.toast_quick_start_button), Toast.LENGTH_SHORT).show()
        }

        return view
    }
}
