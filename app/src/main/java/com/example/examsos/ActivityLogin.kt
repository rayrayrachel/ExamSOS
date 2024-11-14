package com.example.examsos

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

/**
 * ActivityLogin is responsible for user authentication.
 * It handles the UI for logging in and any associated actions.
 */
class ActivityLogin : AppCompatActivity() {

    private val myTag = "Rachel'sTag"

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText:EditText
    private lateinit var loginButton:Button
    private lateinit var singUpButton: Button


    private var mAuth = FirebaseAuth.getInstance()
    private var currentUser = mAuth.currentUser

    /**
     * Called when the activity is first created.
     * Initializes the UI components for the login screen.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *        previously being shut down, this Bundle contains the data it
     *        most recently supplied in onSaveInstanceState(Bundle).
     *        Note: Otherwise it is null.
     */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        Log.i(myTag, "*** ActivityLogin: In onCreate")



        //calling email edit text field
        emailEditText = findViewById<EditText>(R.id.editTextTextEmailAddress)

        //calling password edit text and visible button
        passwordEditText = findViewById<EditText>(R.id.editTextTextPassword)
        val showHideButton = findViewById<ImageButton>(R.id.hide_password_button)
        var isPasswordVisible = false



        //calling login button
        loginButton = findViewById<Button>(R.id.login_button)
        singUpButton = findViewById<Button>(R.id.signup_button)

        //button password show hide listener
        showHideButton.setOnClickListener {
            isPasswordVisible = !isPasswordVisible

            if (isPasswordVisible) {
                passwordEditText.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                showHideButton.setImageResource(R.drawable.ic_visibility)
            } else {
                passwordEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                showHideButton.setImageResource(R.drawable.ic_visibility_off)
            }

            passwordEditText.setSelection(passwordEditText.text.length)
        }

        //button login listener
        loginButton.setOnClickListener { _->loginClick()  }

        //button signup listener
        singUpButton.setOnClickListener{ v ->signUpClick(v) }
    }

    private fun loginClick() {
        Log.i(myTag, "*** ActivityLogin: Login Button Clicked")

        val email = emailEditText.text.toString()
        val password = passwordEditText.text.toString()

        // Check if email and password are empty
        if (email.isEmpty() || password.isEmpty()) {
            displayMessage(findViewById(android.R.id.content), "Please enter both email and password")
            return
        }

        // Check if the email is valid
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            displayMessage(findViewById(android.R.id.content), "Please enter a valid email address")
            return
        }

        // Sign in Firebase Authentication
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.i(myTag, "*** ActivityLogin: Login successful")
                    val intent = Intent(this, ActivityMain::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Log.e(myTag, "*** ActivityLogin: Login failed: ${task.exception?.message}")
                    displayMessage(findViewById(android.R.id.content), "Login failed: ${task.exception?.message}")
                }
            }
    }


    private fun signUpClick(view: View) {
        Log.i(myTag, "*** ActivityLogin: Signup Button Clicked")

        val email = emailEditText.text.toString()
        val password = passwordEditText.text.toString()

        // Check if the email and password are empty
        if (email.isEmpty() || password.isEmpty()) {
            displayMessage(view, "Please fill in both email and password")
            return
        }

        // Check if the email is valid
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            displayMessage(view, "Please enter a valid email address")
            return
        }

        // Check if the password is more than 6 character
        if (password.length < 6) {
            displayMessage(view, "Password must be at least 6 characters long")
            return
        }

        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.i(myTag, "*** ActivityLogin: User registration successful")
                    val intent = Intent(this, ActivityMain::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Log.e(myTag, "*** ActivityLogin: Registration failed: ${task.exception?.message}")
                    displayMessage(view, "Registration failed: ${task.exception?.message}")
                }
            }
    }


    private fun displayMessage(view: View, msgTxt : String){
        val sb = Snackbar.make(view, msgTxt, Snackbar.LENGTH_SHORT)
        sb.show()
    }

    //TODO hide keyboard when pressing other thing than text view
    fun showKeyboard(activity: Activity, editText: EditText) {
        val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
    }

    // Hide keyboard
    fun hideKeyboard(activity: Activity) {
        val view = activity.currentFocus
        if (view != null) {
            val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }


    /**
     * Called when the activity is becoming visible to the user.
     */
    override fun onStart() {
        super.onStart()
        Log.i(myTag, "*** ActivityLogin: In onStart")
    }

    /**
     * Called when the activity is no longer visible to the user.
     */
    override fun onStop() {
        super.onStop()
        Log.i(myTag, "*** ActivityLogin: In onStop")
    }

    /**
     * Called when the activity is partially obscured by another activity.
     */
    override fun onPause() {
        super.onPause()
        Log.i(myTag, "*** ActivityLogin: In onPause")
    }

    /**
     * Called when the activity is being restarted after having been stopped.
     */
    override fun onRestart() {
        super.onRestart()
        Log.i(myTag, "*** ActivityLogin: In onRestart")
    }

    /**
     * Called when the activity will start interacting with the user.
     */
    override fun onResume() {
        super.onResume()
        Log.i(myTag, "*** ActivityLogin: In onResume")
    }

    /**
     * Called when the activity is finishing or being destroyed by the system.
     */
    override fun onDestroy() {
        super.onDestroy()
        Log.i(myTag, "*** ActivityLogin: In onDestroy")
    }


}
