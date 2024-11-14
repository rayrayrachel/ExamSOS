package com.example.examsos
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class ActivityController : AppCompatActivity() {

    private val myTag = "Rachel'sTag"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser

        if (currentUser != null) {
            Log.d(myTag, "Current user is: ${currentUser.email}")
        } else {
            Log.d(myTag, "No user is currently signed in")
        }

        val intent = if (currentUser != null) {
            Intent(this, ActivityMain::class.java)
        } else {
            Intent(this, ActivityLogin::class.java)
        }
        startActivity(intent)
        finish()
    }
}
