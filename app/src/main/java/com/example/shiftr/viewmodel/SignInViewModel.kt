package com.example.shiftr.viewmodel

import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

class SignInViewModel : ViewModel() {

    val googleSignInOptions: GoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken("145123241245-qeld4bjknovdn7b4ppag2m5so0t6e9lm.apps.googleusercontent.com")
        .requestEmail()
        .build()

}