package com.example.examsos

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.examsos.adapter.TabsPagerAdapter

/**
 * MainActivity is the entry point of the application.
 * It handles user interactions, displays the main UI,
 * and manages the action bar menu items.
 */
class MainActivity : AppCompatActivity() {
    private val myTag = "RachelsTag"

    // Declare ImageButton variables at the class level
    private lateinit var homeButton: ImageButton
    private lateinit var levelsButton: ImageButton
    private lateinit var notesButton: ImageButton
    private lateinit var notificationsButton: ImageButton

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

        val imageView = findViewById<ImageView>(R.id.imageView)

        // Load the GIF from raw resources
        Glide.with(this)
            .asGif()
            .load(R.raw.tree_healthy)
            .into(imageView)

        val viewPager = findViewById<ViewPager2>(R.id.viewPager)
        val adapter = TabsPagerAdapter(this)
        viewPager.adapter = adapter

        // Initialize the ImageButtons
        homeButton = findViewById(R.id.nav_home)

        levelsButton = findViewById(R.id.nav_levels)
        notesButton = findViewById(R.id.nav_notes)
        notificationsButton = findViewById(R.id.nav_notifications)

        homeButton.setOnClickListener {
            viewPager.currentItem = 0 // Set to the first fragment
        }

        levelsButton.setOnClickListener {
            viewPager.currentItem = 1 // Set to the second fragment
        }

        notesButton.setOnClickListener {
            viewPager.currentItem = 2 // Set to the third fragment
        }

        notificationsButton.setOnClickListener {
            viewPager.currentItem = 3 // Set to the fourth fragment
        }

        // Register the page change callback
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                updateButtonColors(position)
            }
        })

        Log.i(myTag, "*** MainActivity: In onCreate")
    }

    private fun updateButtonColors(selectedPosition: Int) {
        // Reset all buttons to default color
        homeButton.setBackgroundColor(getColor(R.color.white))
        levelsButton.setBackgroundColor(getColor(R.color.white))
        notesButton.setBackgroundColor(getColor(R.color.white))
        notificationsButton.setBackgroundColor(getColor(R.color.white))
        // Reset all button icons to default color
        homeButton.setColorFilter(getColor(R.color.theme_primary))
        levelsButton.setColorFilter(getColor(R.color.theme_primary))
        notesButton.setColorFilter(getColor(R.color.theme_primary))
        notificationsButton.setColorFilter(getColor(R.color.theme_primary))

        // Change the color of the selected button and icon to teal and white
        when (selectedPosition) {
            0 -> {homeButton.setBackgroundColor(getColor(R.color.theme_secondary))
                homeButton.setColorFilter(getColor(R.color.white))
            }
            1 -> {levelsButton.setBackgroundColor(getColor(R.color.theme_secondary))
                levelsButton.setColorFilter(getColor(R.color.white))
            }
            2 -> {notesButton.setBackgroundColor(getColor(R.color.theme_secondary))
                notesButton.setColorFilter(getColor(R.color.white))
            }
            3 -> {notificationsButton.setBackgroundColor(getColor(R.color.theme_secondary))
                notificationsButton.setColorFilter(getColor(R.color.white))
            }

        }
    }

    /**
     * Called to inflate the menu items for the action bar.
     *
     * @param menu The options menu in which you place your items.
     * @return true if the menu is created; false otherwise.
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.tool_bar_layout, menu)
        val upButton = menu?.findItem(R.id.upButton)
        upButton?.isVisible = false
        menu?.let {
            for (i in 0 until it.size()) {
                val item = it.getItem(i)
                item.icon?.setTint(getColor(R.color.white))
            }
        }
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
            R.id.refresh -> {
                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
                Log.i(myTag, "restart clicked")
                true
            }
            R.id.settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                Log.i(myTag, "Settings clicked")
                true
            }
            R.id.about_us -> {
                val intent = Intent(this, AboutUsActivity::class.java)
                startActivity(intent)
                Log.i(myTag, "About Us clicked")
                true
            }
            R.id.announcement -> {
                val intent = Intent(this, AnnouncementActivity::class.java)
                startActivity(intent)
                Log.i(myTag, "Announcement clicked")
                true
            }

            R.id.logout -> {
                val intent = Intent(this, LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
                Log.i(myTag, "Logout clicked")
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
