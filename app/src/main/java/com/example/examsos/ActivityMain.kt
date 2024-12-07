package com.example.examsos

import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.examsos.adapter.TabsPagerAdapter
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration


/**
 * ActivityMain is the entry point of the application.
 * It handles user interactions, displays the main UI,
 * and manages the action bar menu items.
 */
class ActivityMain : AppCompatActivity() {
    private val myTag = "Rachel'sTag"

    // Declare ImageButton variables at the class level
    private lateinit var homeButton: ImageButton
    private lateinit var levelsButton: ImageButton
    private lateinit var notesButton: ImageButton
    private lateinit var notificationsButton: ImageButton
    private lateinit var welcomeMessage: TextView
    private lateinit var loginDaysTextView: TextView

    private val currentUser = FirebaseAuth.getInstance().currentUser
    private val db = FirebaseFirestore.getInstance()
    private val userDocRef = currentUser?.let { db.collection("users").document(it.uid) }

    private var loginDatesListener: ListenerRegistration? = null

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
        mToolbar.overflowIcon?.setTint(resources.getColor(android.R.color.white, theme))

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

        welcomeMessage = findViewById(R.id.login_welcome_msg_text_view)

        //Initialize the textvies
        loginDaysTextView = findViewById(R.id.dayCountText)

// get username from firestore
        fetchUsername()

        homeButton.setOnClickListener {
            viewPager.currentItem = 0 // Set to the first fragment
            Log.i(myTag,"*** Home Fragment Button Clicked: In Home Fragment")
        }

        levelsButton.setOnClickListener {
            viewPager.currentItem = 1 // Set to the second fragment
            Log.i(myTag, "*** Home Fragment Button Clicked: In Level Fragment")
        }

        notesButton.setOnClickListener {
            viewPager.currentItem = 2 // Set to the third fragment
            Log.i(myTag, "*** Home Fragment Button Clicked: In Notes Fragment")
        }

        notificationsButton.setOnClickListener {
            viewPager.currentItem = 3 // Set to the fourth fragment
            Log.i(myTag,"*** Home Fragment Button Clicked: In Notification Fragment")
        }

        // Register the page change callback
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                updateButtonColors(position)
            }
        })

        checkLoginStreak()

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
                val intent = Intent(this, ActivityMain::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
                Log.i(myTag, "restart clicked")
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
                Firebase.auth.signOut()
                finish()
                Log.i(myTag, "Logout clicked")
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun checkLoginStreak() {
        val localTimestamp = System.currentTimeMillis()
        Log.i(myTag, "Local timestamp for debugging: $localTimestamp")

        userDocRef?.get()
            ?.addOnSuccessListener { document ->
                if (document.exists()) {
                    // Get the current streak and last login data
                    val lastLogin = document.getLong("lastLogin") ?: 0
                    val streak = document.getLong("streak") ?: 0
                    val currentScore = document.getLong("score") ?: 0 // Fetch existing score

                    val lastLoginCalendar = Calendar.getInstance().apply {
                        timeInMillis = lastLogin
                    }
                    val currentLoginCalendar = Calendar.getInstance().apply {
                        timeInMillis = localTimestamp
                    }

                    // Check if the user logged in on a new day
                    val isNewDay = currentLoginCalendar.get(Calendar.YEAR) != lastLoginCalendar.get(Calendar.YEAR) ||
                            currentLoginCalendar.get(Calendar.DAY_OF_YEAR) != lastLoginCalendar.get(Calendar.DAY_OF_YEAR)

                    // Update streak based on login timing
                    val newStreak = if (isNewDay) {
                        if (currentLoginCalendar.get(Calendar.DAY_OF_YEAR) - lastLoginCalendar.get(Calendar.DAY_OF_YEAR) == 1) {
                            streak + 1 // Increment streak if consecutive day
                        } else {
                            1 // Reset streak if non-consecutive
                        }
                    } else {
                        streak // Same day, no change
                    }

                    // Update score if it’s a new day
                    val updatedScore = if (isNewDay) {
                        currentScore + 10
                    } else {
                        currentScore
                    }

                    // Update Firestore
                    userDocRef.update(
                        mapOf(
                            "streak" to newStreak,
                            "lastLogin" to localTimestamp,
                            "loginDates" to FieldValue.arrayUnion(localTimestamp),
                            "score" to updatedScore
                        )
                    ).addOnSuccessListener {
                        Log.i(myTag, "Login streak, dates, and score updated successfully.")
                    }.addOnFailureListener { e ->
                        Log.e(myTag, "Failed to update login streak, dates, and score.", e)
                    }

                } else {
                    Log.w(myTag, "User document does not exist. Creating a new document.")

                    // Create a new document with initial values
                    userDocRef.set(
                        mapOf(
                            "streak" to 1,
                            "lastLogin" to localTimestamp,
                            "loginDates" to listOf(localTimestamp),
                            "score" to 10 // Initial score on first login
                        )
                    ).addOnSuccessListener {
                        Log.i(myTag, "New user document created successfully with initial values.")
                    }.addOnFailureListener { e ->
                        Log.e(myTag, "Failed to create new user document.", e)
                    }
                }
            }
            ?.addOnFailureListener { e ->
                Log.e(myTag, "Error fetching user document.", e)
            }
    }



    private fun listenForLoginDatesUpdates() {
        if (currentUser != null) {
            loginDatesListener = userDocRef?.addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    Log.e(myTag, "Error listening to login dates updates", exception)
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    // Get the loginDates list from Firestore
                    val loginDates = snapshot.get("loginDates") as? List<Long> ?: emptyList()

                    // convert timestamps to a set of unique days
                    val uniqueDays = loginDates.map { timestamp ->
                        val calendar = Calendar.getInstance()
                        calendar.timeInMillis = timestamp
                        // Extract the year and day of the year to ensure unique days
                        "${calendar.get(Calendar.YEAR)}-${calendar.get(Calendar.DAY_OF_YEAR)}"
                    }.toSet()

                    // Get the count of the unique days
                    val uniqueDaysCount = uniqueDays.size

                    // Update the textview
                    loginDaysTextView.text = "$uniqueDaysCount"
                    Log.i(myTag, "Login days updated: $uniqueDaysCount")
                } else {
                    Log.w(myTag, "Document does not exist")
                    loginDaysTextView.text = "0"
                }
            }
        } else {
            Log.w(myTag, "User not logged in")
            loginDaysTextView.text = "0"
        }
    }

    private fun fetchUsername() {

        if (currentUser != null) {
            Log.i(myTag, "Current User UID: ${currentUser.uid}")
        } else {
            Log.e(myTag, "No user is logged in")
        }

        if (currentUser != null) {

            userDocRef?.get()
                ?.addOnSuccessListener { document ->
                    if (document.exists()) {
                        val username = document.getString("name") // Field in Firestore
                        welcomeMessage.text = "Welcome, $username!" // Update the TextView

                    } else {
                        Log.w(myTag, "No such document")
                        welcomeMessage.text = "Welcome!" // Default message
                    }
                }
                ?.addOnFailureListener { exception ->
                    Log.e(myTag, "Error fetching username", exception)
                    welcomeMessage.text = "Welcome!" // Default message
                }
        } else {
            Log.w(myTag, "User not logged in")
            welcomeMessage.text = "Welcome!" // Default message
        }
    }



    /**
     * Called when the activity is becoming visible to the user.
     */
    override fun onStart() {
        super.onStart()

        listenForLoginDatesUpdates()
        Log.i(myTag, "*** ActivityMain: In onStart")
    }

    /**
     * Called when the activity is no longer visible to the user.
     */
    override fun onStop() {
        super.onStop()

        loginDatesListener?.remove()

        Log.i(myTag, "*** ActivityMain: In onStop")
    }

    /**
     * Called when the activity is partially obscured by another activity.
     */
    override fun onPause() {
        super.onPause()
        Log.i(myTag, "*** ActivityMain: In onPause")
    }

    /**
     * Called when the activity is being restarted after having been stopped.
     */
    override fun onRestart() {
        super.onRestart()
        Log.i(myTag, "*** ActivityMain: In onRestart")
    }

    /**
     * Called when the activity will start interacting with the user.
     */
    override fun onResume() {
        super.onResume()
        Log.i(myTag, "*** ActivityMain: In onResume")
    }

    /**
     * Called when the activity is finishing or being destroyed by the system.
     */
    override fun onDestroy() {
        super.onDestroy()
        Log.i(myTag, "*** ActivityMain: In onDestroy")
    }
}
