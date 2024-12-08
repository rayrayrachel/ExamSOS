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
    private var questionType: String? = null
    private var options: List<String>? = null
    private var correctAnswer: String? = null
    private var questionNumber: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            // Get the arguments passed to the fragment
            questionText = it.getString(ARG_QUESTION)
            options = it.getStringArrayList(ARG_OPTIONS)
            correctAnswer = it.getString(ARG_CORRECT_ANSWER)
            questionType = it.getString(ARG_QUESTION_TYPE)
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
        when (questionType) {
            "boolean" -> {
                // Hide unused options for boolean type
                option3Card.visibility = View.GONE
                option4Card.visibility = View.GONE

                option1Text.text = getString(R.string.quiz_option_true)
                option2Text.text = getString(R.string.quiz_option_false)

                option1Card.setOnClickListener { handleAnswerSelection("True") }
                option2Card.setOnClickListener { handleAnswerSelection("False") }

            }
            "multiple" -> {
                // Show all four options for multiple-choice
                option1Text.text = options!![0]
                option2Text.text = options!![1]
                option3Text.text = options!![2]
                option4Text.text = options!![3]

                option1Card.setOnClickListener { handleAnswerSelection(option1Text.text.toString()) }
                option2Card.setOnClickListener { handleAnswerSelection(option2Text.text.toString()) }
                option3Card.setOnClickListener { handleAnswerSelection(option3Text.text.toString()) }
                option4Card.setOnClickListener { handleAnswerSelection(option4Text.text.toString()) }
            }
        }

        return view
    }

    /**
     * Handle answer selection logic.
     */
    private fun handleAnswerSelection(selectedOption: String) {
        val isCorrect = selectedOption == correctAnswer
        if (isCorrect) {
            (activity as? ActivityQuiz)?.onAnswerSelected(true) // Move to next question if correct
        } else {
            Log.i(myTag, "Wrong Answer")
            (activity as? ActivityQuiz)?.onAnswerSelected(false) // Incorrect answer, check leaf logic and go to lose page
        }
    }

    companion object {
        private const val ARG_QUESTION = "question"
        private const val ARG_QUESTION_TYPE = "question_type"
        private const val ARG_OPTIONS = "options"
        private const val ARG_CORRECT_ANSWER = "correct_answer"
        private const val ARG_QUESTION_NUMBER = "question_number"

        /**
         * Create a new instance of the fragment with the provided question and options.
         */
        @JvmStatic
        fun newInstance(
            question: String,
            options: List<String>,
            correctAnswer: String,
            type: String,
            questionNumber: Int
        ) = FragmentQuizQuestion().apply {
            arguments = Bundle().apply {
                putString(ARG_QUESTION, question)
                putString(ARG_QUESTION_TYPE, type)
                putStringArrayList(ARG_OPTIONS, ArrayList(options))
                putString(ARG_CORRECT_ANSWER, correctAnswer)
                putInt(ARG_QUESTION_NUMBER, questionNumber)
            }
        }

    }
}
