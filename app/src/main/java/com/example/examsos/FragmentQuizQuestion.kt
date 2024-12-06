package com.example.examsos

import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment

class FragmentQuizQuestion : Fragment() {

    private val myTag = "Rachel'sTag"

    private var questionText: String? = null
    private var options: List<String>? = null
    private var correctAnswer: String? = null
    private var questionNumber: Int = 1
    //TODO handle true and false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            // Get the arguments passed to the fragment
            questionText = it.getString(ARG_QUESTION)
            options = it.getStringArrayList(ARG_OPTIONS)
            correctAnswer = it.getString(ARG_CORRECT_ANSWER)
            questionNumber = it.getInt(ARG_QUESTION_NUMBER)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate layout
        val view = inflater.inflate(R.layout.fragment_quiz_question, container, false)

        // Initialize views
        val questionNumberText = view.findViewById<TextView>(R.id.quiz_question_number)
        val questionHeaderText = view.findViewById<TextView>(R.id.quiz_selection_header)

        // CardViews
        val option1Card = view.findViewById<CardView>(R.id.quiz_multi_top_right_answer)
        val option2Card = view.findViewById<CardView>(R.id.quiz_multi_top_left_answer)
        val option3Card = view.findViewById<CardView>(R.id.quiz_multi_bottom_left_answer)
        val option4Card = view.findViewById<CardView>(R.id.quiz_multi_bottom_right_answer)

        // TextViews inside CardViews
        val option1Text = option1Card.findViewById<TextView>(R.id.option_text1)
        val option2Text = option2Card.findViewById<TextView>(R.id.option_text2)
        val option3Text = option3Card.findViewById<TextView>(R.id.option_text3)
        val option4Text = option4Card.findViewById<TextView>(R.id.option_text4)

        // Set question number
        questionNumberText.text = "Question $questionNumber"

        // Set the question header
        questionHeaderText.text = Html.fromHtml(questionText).toString()

        // Decode the options
        options?.let {
            if (it.size >= 4) {
                option1Text.text = Html.fromHtml(it[0]).toString()
                option2Text.text = Html.fromHtml(it[1]).toString()
                option3Text.text = Html.fromHtml(it[2]).toString()
                option4Text.text = Html.fromHtml(it[3]).toString()
            }
        }

        // Set click listeners for answer options
        option1Card.setOnClickListener { handleAnswerSelection(option1Text.text.toString()) }
        option2Card.setOnClickListener { handleAnswerSelection(option2Text.text.toString()) }
        option3Card.setOnClickListener { handleAnswerSelection(option3Text.text.toString()) }
        option4Card.setOnClickListener { handleAnswerSelection(option4Text.text.toString()) }

        return view
    }

    /**
     * Handle answer selection logic.
     */
    private fun handleAnswerSelection(selectedOption: String) {
        val isCorrect = selectedOption == correctAnswer
        if (isCorrect) {
            Log.i(myTag, "Correct Answer")
            (activity as? ActivityQuiz)?.onAnswerSelected(true) // Move to next question if correct
        } else {
            Log.i(myTag, "Wrong Answer")
            (activity as? ActivityQuiz)?.onAnswerSelected(false) // Incorrect answer, check leaf logic and go to lose page
            //TODO losing page
        }
    }

    companion object {
        private const val ARG_QUESTION = "question"
        private const val ARG_OPTIONS = "options"
        private const val ARG_CORRECT_ANSWER = "correct_answer"
        private const val ARG_QUESTION_NUMBER = "question_number"

        /**
         * Create a new instance of the fragment with the provided question and options.
         */
        @JvmStatic
        fun newInstance(question: String, options: List<String>, correctAnswer: String, questionNumber: Int) =
            FragmentQuizQuestion().apply {
                arguments = Bundle().apply {
                    putString(ARG_QUESTION, question)
                    putStringArrayList(ARG_OPTIONS, ArrayList(options))
                    putString(ARG_CORRECT_ANSWER, correctAnswer)
                    putInt(ARG_QUESTION_NUMBER, questionNumber)
                }
            }
    }
}
