package com.example.examsos
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class ActivityController : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val mAuth = FirebaseAuth.getInstance()
        val intent = if (mAuth.currentUser != null) {
            Intent(this, ActivityMain::class.java)
        } else {
            Intent(this, ActivityLogin::class.java)
        }
        startActivity(intent)
        finish()
    }
}
