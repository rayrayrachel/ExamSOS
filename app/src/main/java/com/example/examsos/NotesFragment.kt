package com.example.examsos

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment

class NotesFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_notes, container, false)


        val buttonClick = view.findViewById<Button>(R.id.go_to_notes_button)
        buttonClick.setOnClickListener {

            val intent = Intent(requireContext(), NotesActivity::class.java)
            startActivity(intent)
            Toast.makeText(requireContext(), getString(R.string.toast_message_go_to_notes_button), Toast.LENGTH_SHORT).show()

        }

        return view
    }
}
