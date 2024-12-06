package com.example.examsos.dataValue

import android.os.Parcel
import android.os.Parcelable

data class QuizQuestion(
    val category: String,
    val type: String,
    val difficulty: String,
    val question: String,
    val options: ArrayList<String>?, // Includes both correct and incorrect answers
    val correctAnswer: String
) : Parcelable {

    constructor(triviaQuestion: TriviaQuestion) : this(
        triviaQuestion.category,
        triviaQuestion.type,
        triviaQuestion.difficulty,
        triviaQuestion.question,
        triviaQuestion.incorrect_answers?.let {
            ArrayList(it).apply { add(triviaQuestion.correct_answer) } // Add correct answer to the options
        },
        triviaQuestion.correct_answer
    )

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.createStringArrayList(),
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(category)
        parcel.writeString(type)
        parcel.writeString(difficulty)
        parcel.writeString(question)
        parcel.writeStringList(options)
        parcel.writeString(correctAnswer)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<QuizQuestion> {
        override fun createFromParcel(parcel: Parcel): QuizQuestion {
            return QuizQuestion(parcel)
        }

        override fun newArray(size: Int): Array<QuizQuestion?> {
            return arrayOfNulls(size)
        }
    }
}
