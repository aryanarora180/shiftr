package com.example.shiftr

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.shiftr.model.DataStoreUtils
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    @Inject lateinit var dataStoreUtils: DataStoreUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = Intent(
            this,
            if (dataStoreUtils.isSignedIn()) MainActivity::class.java else LoginActivity::class.java
        )
        startActivity(intent)
        finish()
    }
}