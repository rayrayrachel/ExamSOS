package com.example.examsos

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore


class ActivitySignup : AppCompatActivity() {

    private val myTag = "Rachel'sTag"

    private lateinit var usernameText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText:EditText
    private lateinit var loginButton:Button
    private lateinit var singUpButton: Button

    private var mAuth = FirebaseAuth.getInstance()
    private val db = Firebase.firestore

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
        setContentView(R.layout.activity_signup)
        Log.i(myTag, "*** ActivityLogin: In onCreate")

        usernameText = findViewById<EditText>(R.id.editUsername)

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

        val intent = Intent(this, ActivityLogin::class.java)
        startActivity(intent)
        finish()
    }


    private fun signUpClick(view: View) {
        Log.i(myTag, "*** ActivityLogin: Signup Button Clicked")

        val username = usernameText.text.toString()
        val email = emailEditText.text.toString()
        val password = passwordEditText.text.toString()


        if (username.isEmpty()) {
            displayMessage(view, "Please fill in username")
            return
        }

        if (email.isEmpty() || password.isEmpty()) {
            displayMessage(view, "Please fill in both email and password")
            return
        }

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
                    val userId = task.result?.user?.uid ?: return@addOnCompleteListener
                    val user = hashMapOf(
                        "name" to username,
                        "email" to email
                    )

                    db.collection("users")
                        .document(userId)
                        .set(user)
                        .addOnSuccessListener {
                            Log.d(myTag, "User document created with UID: $userId")
                            displayMessage(view, "Account Registered! Welcome, $userId.")
                            val intent = Intent(this, ActivityMain::class.java)
                            startActivity(intent)
                            finish()
                        }
                        .addOnFailureListener { e ->
                            Log.w(myTag, "Error saving user data", e)
                            displayMessage(view, "Failed to save user data. Try again.")
                        }
                } else {
                    Log.e(myTag, "*** ActivitySignup: Registration failed: ${task.exception?.message}")
                    displayMessage(view, "Registration failed: ${task.exception?.message}")
                }
            }
    }


    private fun displayMessage(view: View, msgTxt : String){
        val sb = Snackbar.make(view, msgTxt, Snackbar.LENGTH_SHORT)
        sb.show()
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val view = currentFocus
            if (view is EditText) {
                val outRect = Rect()
                view.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    hideKeyboard(this)
                    view.clearFocus()
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }

    private fun showKeyboard(activity: Activity, editText: EditText) {
        val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
    }

    // Hide keyboard
    private fun hideKeyboard(activity: Activity) {
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
        Log.i(myTag, "*** ActivitySignup: In onStart")
    }

    /**
     * Called when the activity is no longer visible to the user.
     */
    override fun onStop() {
        super.onStop()
        Log.i(myTag, "*** ActivitySignup: In onStop")
    }

    /**
     * Called when the activity is partially obscured by another activity.
     */
    override fun onPause() {
        super.onPause()
        Log.i(myTag, "*** ActivitySignup: In onPause")
    }

    /**
     * Called when the activity is being restarted after having been stopped.
     */
    override fun onRestart() {
        super.onRestart()
        Log.i(myTag, "*** ActivitySignup: In onRestart")
    }

    /**
     * Called when the activity will start interacting with the user.
     */
    override fun onResume() {
        super.onResume()
        Log.i(myTag, "*** ActivitySignup: In onResume")
    }

    /**
     * Called when the activity is finishing or being destroyed by the system.
     */
    override fun onDestroy() {
        super.onDestroy()
        Log.i(myTag, "*** ActivitySignup: In onDestroy")
    }


}
