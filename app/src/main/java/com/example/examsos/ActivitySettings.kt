package com.example.examsos

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.examsos.api.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ActivitySettings : AppCompatActivity(){

    private val myTag = "Rachel'sTag"
//    val api = RetrofitClient.api

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val mToolbar = findViewById<Toolbar>(R.id.settings_toolbar)
        setSupportActionBar(mToolbar)

//        CoroutineScope(Dispatchers.IO).launch {
//            try {
//                val response = api.getQuestions(amount = 10, category = null, difficulty = "easy", type = "multiple")
//                Log.d(myTag, "Questions: ${response.results}")
//            } catch (e: Exception) {
//                Log.e(myTag, "Failed to fetch questions", e)
//            }
//            }
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
