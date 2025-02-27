package com.example.examsos

import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.examsos.api.RetrofitClient
import com.example.examsos.dataValue.TriviaCategory
import com.google.android.material.slider.Slider
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.sanojpunchihewa.glowbutton.GlowButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

class ActivitySettings : AppCompatActivity() {

    private val myTag = "Rachel'sTag"
    private val api = RetrofitClient.api
    private lateinit var categoryList: List<TriviaCategory>

    private val currentUser = FirebaseAuth.getInstance().currentUser
    private val db = FirebaseFirestore.getInstance()
    private val userDocRef = currentUser?.let { db.collection("users").document(it.uid) }

    // Declare variables
    private lateinit var levelRadioGroup: RadioGroup
    private lateinit var questionSlider: Slider
    private lateinit var typeSpinner: Spinner
    private lateinit var categorySpinner: Spinner
    private lateinit var musicToggleSwitch: Switch
    private lateinit var musicVolumeSlider: Slider
    private lateinit var deleteAccountButton: GlowButton
    private lateinit var updateDailyConfigButton: GlowButton
    private lateinit var timeEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val mToolbar = findViewById<Toolbar>(R.id.settings_toolbar)
        setSupportActionBar(mToolbar)

        // Initialize UI components
        levelRadioGroup = findViewById(R.id.level_radio_group)
        questionSlider = findViewById(R.id.number_of_questions_slider)
        typeSpinner = findViewById(R.id.type_spinner)
        categorySpinner = findViewById(R.id.category_spinner)
        musicToggleSwitch = findViewById(R.id.music_toggle_switch)
        musicVolumeSlider = findViewById(R.id.music_volume_slider)
        deleteAccountButton = findViewById(R.id.delete_account_button)
        updateDailyConfigButton = findViewById(R.id.update_daily_quiz_configuration_button)
        timeEditText = findViewById(R.id.editTextTime)


        // Fetch trivia categories
        fetchTriviaCategories()

        // Set up button listeners
        deleteAccountButton.setOnClickListener {
            showDeleteConfirmationDialog()
        }

        updateDailyConfigButton.setOnClickListener {
            saveSettingsToFirestore()
        }

        timeEditText.setOnClickListener {
            showTimePickerDialog(timeEditText)
        }

        timeEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                showTimePickerDialog(timeEditText)
            }
        }

        musicToggleSwitch.setOnCheckedChangeListener { _, isChecked ->
            userDocRef?.update("isMusicEnabled", isChecked)
                ?.addOnSuccessListener { Log.i(myTag, "Music toggle updated.") }
                ?.addOnFailureListener { Log.e(myTag, "Music toggle update failed.") }
        }

        musicVolumeSlider.addOnChangeListener { _, value, _ ->
            userDocRef?.update("musicVolume", value.toInt())
                ?.addOnSuccessListener { Log.i(myTag, "Music volume updated.") }
                ?.addOnFailureListener { Log.e(myTag, "Music volume update failed.") }
        }

    }

    private fun fetchTriviaCategories() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = api.getCategories()
                categoryList = response.trivia_categories
                Log.d(myTag, "Categories fetched: $categoryList")

                runOnUiThread { initializeDefaultValues() }
            } catch (e: Exception) {
                Log.e(myTag, "Error fetching trivia categories", e)
            }
        }
    }

    private fun initializeDefaultValues() {
        userDocRef?.get()?.addOnSuccessListener { document ->
            if (document.exists()) {
                // Retrieve Firestore fields or set defaults
                val selectedTime = document.getString("selectedTime") ?: "00:00"
                val selectedDifficulty = document.getString("selectedDifficulty") ?: "easy"
                val numberOfQuestions = document.getLong("numberOfQuestions")?.toFloat() ?: 5f
                val selectedType = document.getString("selectedType") ?: "Any Type"
                val selectedCategory = document.getString("selectedCategory") ?: categoryList.firstOrNull()?.name ?: "Any Category"
                val isMusicEnabled = document.getBoolean("isMusicEnabled") ?: true
                val musicVolume = document.getLong("musicVolume")?.toFloat() ?: 50f

                // Populate UI components from Firestore
                timeEditText.setText(selectedTime)

                val difficultyId = when (selectedDifficulty) {
                    "easy" -> R.id.level_easy
                    "medium" -> R.id.level_medium
                    "hard" -> R.id.level_hard
                    else -> R.id.level_easy
                }
                levelRadioGroup.check(difficultyId)

                questionSlider.value = numberOfQuestions
                musicToggleSwitch.isChecked = isMusicEnabled
                musicVolumeSlider.value = musicVolume

                // Populate spinners
                val quizTypes = arrayOf("Any Type", "Multiple Choice", "True/False")
                val adapterType = ArrayAdapter(this, android.R.layout.simple_spinner_item, quizTypes)
                adapterType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                typeSpinner.adapter = adapterType
                typeSpinner.setSelection(quizTypes.indexOf(selectedType).takeIf { it >= 0 } ?: 0)

                val quizCategories = categoryList.map { it.name }
                val adapterCategory = ArrayAdapter(this, android.R.layout.simple_spinner_item, quizCategories)
                adapterCategory.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                categorySpinner.adapter = adapterCategory
                categorySpinner.setSelection(quizCategories.indexOf(selectedCategory).takeIf { it >= 0 } ?: 0)
            } else {
                Log.w(myTag, "No user document found. Using default values.")
                setDefaultValues()
            }
        }?.addOnFailureListener { e ->
            Log.e(myTag, "Error fetching user settings. Using default values.", e)
            setDefaultValues()
        }
    }

    // set default values
    private fun setDefaultValues() {
        timeEditText.setText("00:00")
        levelRadioGroup.check(R.id.level_easy)
        questionSlider.value = 5f
        musicToggleSwitch.isChecked = true
        musicVolumeSlider.value = 50f

        // Populate spinners
        val quizTypes = arrayOf("Any Type", "Multiple Choice", "True/False")
        val adapterType = ArrayAdapter(this, android.R.layout.simple_spinner_item, quizTypes)
        adapterType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        typeSpinner.adapter = adapterType
        typeSpinner.setSelection(0)

        val quizCategories = categoryList.map { it.name }
        val adapterCategory = ArrayAdapter(this, android.R.layout.simple_spinner_item, quizCategories)
        adapterCategory.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = adapterCategory
        categorySpinner.setSelection(0)
    }


    private fun showDeleteConfirmationDialog() {
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Delete Account")
            .setMessage("Are you sure you want to delete your account? This action cannot be undone. I am sad to see you LEAF ;(")
            .setPositiveButton("Yes") { _, _ -> showReAuthenticationDialog() }
            .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }

    private fun showReAuthenticationDialog() {
        // Create a dialog view with input fields
        val dialogView = layoutInflater.inflate(R.layout.dialog_reauth, null)
        val emailInput = dialogView.findViewById<EditText>(R.id.email_input)
        val passwordInput = dialogView.findViewById<EditText>(R.id.password_input)

        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Delete Account")
            .setMessage("Please enter your email and password to proceed.")
            .setView(dialogView)
            .setPositiveButton("Delete Account") { _, _ ->
                val email = emailInput.text.toString()
                val password = passwordInput.text.toString()

                // Validate inputs
                if (email.isNotEmpty() && password.isNotEmpty()) {
                    reAuthenticateAndDelete(email, password)
                } else {
                    Toast.makeText(this, "Please fill in both fields.", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }


    private fun reAuthenticateAndDelete(email: String, password: String) {
        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser != null) {
            val credential = EmailAuthProvider.getCredential(email, password)
            currentUser.reauthenticate(credential).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(myTag, "User re-authenticated successfully.")
                    deleteAccount() // Proceed with account deletion
                } else {
                    Log.e(myTag, "Re-authentication failed.", task.exception)
                    Toast.makeText(
                        this@ActivitySettings,
                        "Re-authentication failed. Please check your credentials.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            Toast.makeText(this, "No user is currently logged in.", Toast.LENGTH_SHORT).show()
        }
    }


    private fun deleteAccount() {
        currentUser?.let { user ->
            userDocRef?.delete()?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(myTag, "User data deleted from Firestore.")
                    user.delete().addOnCompleteListener { deleteTask ->
                        if (deleteTask.isSuccessful) {
                            Log.d(myTag, "User account deleted.")
                            Toast.makeText(this, "Account deleted successfully.", Toast.LENGTH_SHORT).show()
                            navigateToLoginScreen()
                        } else {
                            Log.e(myTag, "Error deleting user account.", deleteTask.exception)
                            Toast.makeText(this, "Failed to delete account. Try again later.", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Log.e(myTag, "Error deleting user data from Firestore.", task.exception)
                    Toast.makeText(this, "Failed to delete user data. Try again later.", Toast.LENGTH_SHORT).show()
                }
            }
        } ?: run {
            Log.e(myTag, "No user logged in.")
            Toast.makeText(this, "No user logged in. Cannot delete account.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun navigateToLoginScreen() {
        val intent = Intent(this, ActivityLogin::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.sub_tool_bar_layout, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.upButton -> {
                finish()
                Log.i(myTag, "Back button clicked")
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showTimePickerDialog(editText: EditText) {
        // Get current time as default
        val calendar = Calendar.getInstance()
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = calendar.get(Calendar.MINUTE)

        // Show TimePickerDialog
        val timePickerDialog = TimePickerDialog(
            this,
            { _, hourOfDay, minute ->
                // Format and set selected time into EditText
                val formattedTime = String.format("%02d:%02d", hourOfDay, minute)
                editText.setText(formattedTime)
            },
            currentHour,
            currentMinute,
            true // Use 24-hour format; set to false for 12-hour format
        )

        timePickerDialog.show()
    }


    private fun saveSettingsToFirestore() {
        // Get data from the UI components
        val selectedTime = timeEditText.text.toString()
        val selectedDifficulty = when (levelRadioGroup.checkedRadioButtonId) {
            R.id.level_easy -> "easy"
            R.id.level_medium -> "medium"
            R.id.level_hard -> "hard"
            else -> "Unknown"
        }
        val numberOfQuestions = questionSlider.value.toInt()
        val selectedType = typeSpinner.selectedItem.toString()
        val selectedCategory = categorySpinner.selectedItem.toString()
        val isMusicEnabled = musicToggleSwitch.isChecked
        val musicVolume = musicVolumeSlider.value.toInt()

        // Ensure userDocRef is not null
        if (userDocRef == null) {
            Log.e(myTag, "UserDocRef is null. Ensure a user is logged in.")
            Toast.makeText(this, "Error: No user logged in.", Toast.LENGTH_SHORT).show()
            return
        }

        // Fetch Firestore document
        userDocRef.get()
            .addOnSuccessListener { document ->
                val dataMap = mapOf(
                    "selectedTime" to selectedTime,
                    "selectedDifficulty" to selectedDifficulty,
                    "numberOfQuestions" to numberOfQuestions,
                    "selectedType" to selectedType,
                    "selectedCategory" to selectedCategory,
                    "isMusicEnabled" to isMusicEnabled,
                    "musicVolume" to musicVolume
                )

                if (document.exists()) {
                    // Document exists, update it
                    userDocRef.update(dataMap)
                        .addOnSuccessListener {
                            Log.i(myTag, "Settings updated successfully.")
                            Toast.makeText(this, "Settings saved!", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            Log.e(myTag, "Failed to update settings.", e)
                            Toast.makeText(this, "Failed to save settings. Try again.", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    // Document does not exist, create it
                    userDocRef.set(dataMap)
                        .addOnSuccessListener {
                            Log.i(myTag, "New settings document created successfully.")
                            Toast.makeText(this, "Settings saved!", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            Log.e(myTag, "Failed to create settings document.", e)
                            Toast.makeText(this, "Failed to save settings. Try again.", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.e(myTag, "Error fetching user document.", e)
                Toast.makeText(this, "Error: Could not retrieve user settings.", Toast.LENGTH_SHORT).show()
            }
    }


}
