package com.example.examsos

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

/**
 * MainActivity is the entry point of the application.
 * It handles user interactions, displays the main UI,
 * and manages the action bar menu items.
 */

class MainActivity : AppCompatActivity() {
    private val myTag = "RachelsTag"

    /**
     * Called when the activity is first created.
     * Initializes the UI components and sets up the action bar.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *        previously being shut down, this Bundle contains the data it
     *        most recently supplied in onSaveInstanceState(Bundle).
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mToolbar = findViewById<Toolbar>(R.id.main_toolbar)
        setSupportActionBar(mToolbar)

        val buttonClick = findViewById<Button>(R.id.button3)
        buttonClick.setOnClickListener {
            val intent = Intent(this, QuizActivity::class.java)
            startActivity(intent)
        }

        Log.i(myTag, "*** MainActivity: In onCreate")
    }

    /**
     * Called to inflate the menu items for the action bar.
     *
     * @param menu The options menu in which you place your items.
     * @return true if the menu is created; false otherwise.
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.tool_bar_layout, menu)
        return super.onCreateOptionsMenu(menu)
    }

    /**
     * Called when an item in the options menu is selected.
     *
     * @param item The menu item that was selected.
     * @return true if the item was handled; false otherwise.
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.logout -> {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                Log.i(myTag, "Logout clicked")
                true
            }
            R.id.more -> {
                Log.i(myTag, "More clicked")
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * Called when the activity is becoming visible to the user.
     */
    override fun onStart() {
        super.onStart()
        Log.i(myTag, "*** MainActivity: In onStart")
    }

    /**
     * Called when the activity is no longer visible to the user.
     */
    override fun onStop() {
        super.onStop()
        Log.i(myTag, "*** MainActivity: In onStop")
    }

    /**
     * Called when the activity is partially obscured by another activity.
     */
    override fun onPause() {
        super.onPause()
        Log.i(myTag, "*** MainActivity: In onPause")
    }

    /**
     * Called when the activity is being restarted after having been stopped.
     */
    override fun onRestart() {
        super.onRestart()
        Log.i(myTag, "*** MainActivity: In onRestart")
    }

    /**
     * Called when the activity will start interacting with the user.
     */
    override fun onResume() {
        super.onResume()
        Log.i(myTag, "*** MainActivity: In onResume")
    }

    /**
     * Called when the activity is finishing or being destroyed by the system.
     */
    override fun onDestroy() {
        super.onDestroy()
        Log.i(myTag, "*** MainActivity: In onDestroy")
    }
}
