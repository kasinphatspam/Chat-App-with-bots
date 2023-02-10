package com.android.studio.azne

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.multidex.MultiDex
import com.android.studio.azne.method.AuthManager
import com.android.studio.azne.method.ToastManager
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_sign_in.*

class SignInActivity : AppCompatActivity() {

    private lateinit var toastManager: ToastManager
    private lateinit var mAuth: FirebaseAuth
    private lateinit var authManager: AuthManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        init()

        confirmButton.setOnClickListener {
            if(checkNotNull()){
                val email = emailEditText.text.toString()
                val password = passwordEditText.text.toString()
                authManager.signIn(email,password)
            }else{
                toastManager.notNull()
            }
        }

        signUpTextView.setOnClickListener{
            val intent = Intent(this,SignUpActivity::class.java)
            startActivity(intent)
        }
    }

    private fun init() {
        mAuth = FirebaseAuth.getInstance()
        toastManager = ToastManager(this)
        authManager = AuthManager(this,this)
    }

    private fun checkNotNull(): Boolean {
        return (emailEditText.length() != 0 && passwordEditText.length() != 0)
    }

    override fun onStart() {
        super.onStart()

        if(mAuth.currentUser != null){
            val intent = Intent(this,MainActivity::class.java)
            intent.putExtra("userUid",mAuth.currentUser!!.uid)
            startActivity(intent)
        }
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase)
        MultiDex.install(baseContext)
    }
}
