package com.example.shiftr

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val account = GoogleSignIn.getLastSignedInAccount(this)
        val firebaseUser = Firebase.auth.currentUser
        val intent = Intent(
            this,
            if (account != null && firebaseUser != null) MainActivity::class.java else LoginActivity::class.java
        )
        startActivity(intent)
        finish()
    }
}