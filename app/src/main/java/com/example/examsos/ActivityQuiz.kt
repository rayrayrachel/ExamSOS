package com.example.examsos

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class ActivityQuiz : AppCompatActivity(){

    private val myTag = "RachelsTag"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)

        val mToolbar = findViewById<Toolbar>(R.id.quiz_toolbar)
        setSupportActionBar(mToolbar)
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.sub_tool_bar_layout, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.upButton -> {
                finish()
                Log.i(myTag, "back clicked")
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}