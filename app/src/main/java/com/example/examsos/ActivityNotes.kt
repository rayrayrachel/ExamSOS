package com.example.examsos

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class ActivityNotes : AppCompatActivity(){

    private val myTag = "RachelsTag"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note)

        val mToolbar = findViewById<Toolbar>(R.id.notes_toolbar)
        setSupportActionBar(mToolbar)
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.tool_bar_layout, menu)
        val refreshButton = menu?.findItem(R.id.refresh)
        refreshButton?.isVisible = false
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.upButton -> {
                finish()
                Log.i(myTag, "Up Button clicked")
                true
            }

            R.id.settings -> {
                val intent = Intent(this, ActivitySettings::class.java)
                startActivity(intent)
                Log.i(myTag, "Settings clicked")
                true
            }

            R.id.about_us -> {
                val intent = Intent(this, ActivityAboutUs::class.java)
                startActivity(intent)
                Log.i(myTag, "About Us clicked")
                true
            }

            R.id.announcement -> {
                val intent = Intent(this, ActivityAnnouncement::class.java)
                startActivity(intent)
                Log.i(myTag, "Announcement clicked")
                true
            }

            R.id.logout -> {
                val intent = Intent(this, ActivityLogin::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
                Log.i(myTag, "Settings clicked")
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}