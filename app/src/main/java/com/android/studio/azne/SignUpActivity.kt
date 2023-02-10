package com.android.studio.azne

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.multidex.MultiDex
import com.android.studio.azne.method.AuthManager
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : AppCompatActivity() {

    private lateinit var authManager: AuthManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        init()

        confirmButton.setOnClickListener {
            if(checkNotNull()){
                val email = emailEditText.text.toString()
                val password = passwordEditText.text.toString()
                val username = usernameEditText.text.toString()
                val telephone = telephoneEditText.text.toString()

                authManager.signUp(email,password,username,telephone)
            }
        }
    }

    private fun checkNotNull(): Boolean {

        return (emailEditText.length() != 0 &&
                passwordEditText.length() != 0 &&
                usernameEditText.length() != 0 &&
                telephoneEditText.length() != 0)
    }

    private fun init(){
        authManager = AuthManager(this,this)
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase)
        MultiDex.install(baseContext)
    }
}
