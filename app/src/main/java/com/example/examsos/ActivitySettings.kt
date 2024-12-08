package com.example.examsos

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.examsos.api.RetrofitClient
import com.example.examsos.dataValue.TriviaCategory
import com.google.android.material.slider.Slider
import com.sanojpunchihewa.glowbutton.GlowButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
class ActivitySettings : AppCompatActivity(){

    private val myTag = "Rachel'sTag"
    private val api = RetrofitClient.api
    private lateinit var categoryList: List<TriviaCategory>

    // Declare variables
    private lateinit var levelRadioGroup: RadioGroup
    private lateinit var questionSlider: Slider
    private lateinit var typeSpinner: Spinner
    private lateinit var categorySpinner: Spinner
    private lateinit var musicToggleSwitch: Switch
    private lateinit var musicVolumeSlider: Slider
    private lateinit var deleteAccountButton: GlowButton
    private lateinit var updateDailyConfig: GlowButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val mToolbar = findViewById<Toolbar>(R.id.settings_toolbar)
        setSupportActionBar(mToolbar)

        levelRadioGroup = findViewById(R.id.level_radio_group)
        questionSlider = findViewById(R.id.number_of_questions_slider)
        typeSpinner = findViewById(R.id.type_spinner)
        categorySpinner = findViewById(R.id.category_spinner)
        musicToggleSwitch = findViewById(R.id.music_toggle_switch)
        musicVolumeSlider = findViewById(R.id.music_volume_slider)

        deleteAccountButton = findViewById(R.id.delete_account_button)
        updateDailyConfig = findViewById(R.id.update_daily_quiz_configuration_button)


        // Fetch trivia categories
        fetchTriviaCategories()

        // Set the delete button listener
        deleteAccountButton.setOnClickListener {
            showDeleteConfirmationDialog()
        }

        updateDailyConfig.setOnClickListener {
        //TODO: update button logic
        }
    }

    private fun fetchTriviaCategories() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.api.getCategories()
                categoryList = response.trivia_categories

                Log.d(myTag, "Categories fetched: $categoryList")

                runOnUiThread {
                    initializeDefaultValues()
                }

            } catch (e: Exception) {
                Log.e(myTag, "Error fetching trivia categories", e)
            }
        }
    }

    private fun showDeleteConfirmationDialog() {
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Delete Account")
        builder.setMessage("Are you sure you want to delete your account? This action cannot be undone.")

        builder.setPositiveButton("Yes") { dialog, _ ->
            //TODO: call API to delete account
            deleteAccount()
        }

        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()  // Dismiss
        }

        val dialog = builder.create()
        dialog.show()
    }

    private fun deleteAccount() {
        // TODO: Implement account deletion logic
    }

    private fun initializeDefaultValues() {
        levelRadioGroup.check(R.id.level_easy)
        questionSlider.value = 5f
        musicToggleSwitch.isChecked = true
        musicVolumeSlider.value = 50f

        // Populate spinners
        val quizTypes = arrayOf("Any Type", "Multiple Choice", "True/False")
        val adapterType = ArrayAdapter(this, android.R.layout.simple_spinner_item, quizTypes)
        adapterType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        typeSpinner.adapter = adapterType

        val quizCategories = categoryList.map { it.name }
        val adapterCategory = ArrayAdapter(this, android.R.layout.simple_spinner_item, quizCategories)
        adapterCategory.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = adapterCategory
    }

    private fun addListeners() {
        // Add listeners for difficulty, slider, music toggle
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
