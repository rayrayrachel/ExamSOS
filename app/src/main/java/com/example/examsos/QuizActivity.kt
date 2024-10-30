package com.example.examsos
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity


class QuizActivity : AppCompatActivity(){

    private val myTag = "RachelsTag"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)

        Log.i(myTag, "*** QuizActivity: In onCreate")

    }

    override fun onStart() {
        super.onStart()
        Log.i(myTag, "*** QuizActivity: In onStart")
    }

    override fun onStop() {
        super.onStop()
        Log.i(myTag, "*** QuizActivity: In onStop")
    }

    override fun onPause() {
        super.onPause()
        Log.i(myTag, "*** QuizActivity: In onPause")
    }

    override fun onRestart() {
        super.onRestart()
        Log.i(myTag, "*** QuizActivity: In onRestart")
    }

    override fun onResume() {
        super.onResume()
        Log.i(myTag, "*** QuizActivity: In onResume")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(myTag, "*** QuizActivity: In onDestroy")
    }

}





