package com.example.examsos

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat


class MainActivity : AppCompatActivity() {
    private val myTag = "RachelsTag"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val buttonClick = findViewById<Button>(R.id.button3)
        buttonClick.setOnClickListener {
            val intent = Intent(this, QuizActivity::class.java)
            startActivity(intent)
        }

        Log.i(myTag, "*** Ray: In onCreate")

    }

    override fun onStart() {
        super.onStart()
        Log.i(myTag, "*** Ray: In onStart")
    }

    override fun onStop() {
        super.onStop()
        Log.i(myTag, "*** Ray: In onStop")
    }

    override fun onPause() {
        super.onPause()
        Log.i(myTag, "*** Ray: In onPause")
    }

    override fun onRestart() {
        super.onRestart()
        Log.i(myTag, "*** Ray: In onRestart")
    }

    override fun onResume() {
        super.onResume()
        Log.i(myTag, "*** Ray: In onResume")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(myTag, "*** Ray: In onDestroy")
    }

}

//Test Commit